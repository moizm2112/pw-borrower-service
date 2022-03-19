package com.paywallet.userservice.user.services.allowretry;


import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.notification.Notification;
import com.paywallet.userservice.user.model.notification.NotificationType;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.repository.NotificationRepository;
import com.paywallet.userservice.user.util.RequestIdUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.paywallet.userservice.user.constant.RetryAPIConstants.*;
import static com.paywallet.userservice.user.util.NullableWrapperUtil.resolve;


@Data
@Service
@Slf4j
public class NotificationErrorStatus implements AllowRetryService {

    @Autowired
    RequestIdUtil requestIdUtil;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    CustomerRepository customerRepository;

    /***
     * checking for previous request notification error
     *
     * @param requestIdDetails
     * @return
     * @throws RetryException
     */
    @Override
    public String checkForRetryStatus(RequestIdDetails requestIdDetails) throws RetryException {

        Optional<String> decodedRequestID = requestIdUtil.getDecodedRequestID(requestIdDetails.getRequestId());

        if (decodedRequestID.isPresent()) {
            Optional<List<Notification>> notificationList = notificationRepository.findByRequestId(decodedRequestID.get());
            boolean isInvalidEmailId = false;
            boolean isInvalidMobile = false;
            String errorEmailId = "";
            String errorMobile = "";
            log.info(" Notification list : {}, requestID : {}", notificationList, requestIdDetails.getRequestId());

            if (notificationList.isPresent()) {
                for (Notification notification : notificationList.get()) {
                    if (NotificationType.SMS.equals(notification.getType())) {
                        isInvalidMobile = this.validateSMSError(notification);
                        errorMobile = notification.getTo();
                    } else if (NotificationType.EMAIL.equals(notification.getType())) {
                        isInvalidEmailId = this.validateEmailError(notification);
                        errorEmailId = notification.getTo();
                    }
                }
            }

            if (isInvalidEmailId && isInvalidMobile) {
                log.info(" errorMobile : {} errorEmailId : {}  ", errorMobile, errorEmailId);
                checkForUpdatedCustomerDetails(errorMobile, errorEmailId, requestIdDetails);
            }

        }
        return NOT_BOTH_INVALID_EMAIL_MOBILE_MESSAGE;
    }


    /**
     * previous notification is failed with invalid emailID and mobileID
     * checking whether details are updated or not
     *
     * @param oldMobile
     * @param oldEmailId
     * @param requestIdDetails
     * @throws RetryException
     */
    private void checkForUpdatedCustomerDetails(String oldMobile, String oldEmailId, RequestIdDetails requestIdDetails) throws RetryException {

        log.info(" checking whether mobile and email details are updated or not, to allow retry  user ID : {} request id {}", requestIdDetails.getUserId(), requestIdDetails.getRequestId());
        Optional<CustomerDetails> customerDetails = customerRepository.findById(requestIdDetails.getUserId());
        if (customerDetails.isPresent()) {
            Optional<String> customerEmailId = resolve(() -> customerDetails.get().getPersonalProfile().getEmailId());
            Optional<String> customerMobile = resolve(() -> customerDetails.get().getPersonalProfile().getMobileNo());
            log.info(" customerEmailId : {} customerMobile : {}  ", customerEmailId, customerMobile);
            if (customerMobile.isPresent() && customerEmailId.isPresent()) {
                if ((customerEmailId.get() + customerMobile.get()).equalsIgnoreCase(oldEmailId + oldMobile)) {
                    throw new RetryException(new StringBuffer().append(INVALID_EMAIL_MOBILE_MESSAGE).append(REQUEST_ID).append(requestIdDetails.getRequestId()).toString());
                }
            }
        }

    }

    public boolean validateEmailError(Notification notification) {
        Optional<String> errorCode = resolve(() -> notification.getNotificationError().getEmailNotificationError().getEmailErrorDetail().getEvent());
        if (errorCode.isPresent()) {
            return (errorCode.get().equalsIgnoreCase(INVALID_EMAIL_ERROR_CODE_VALUE1) || errorCode.get().equalsIgnoreCase(INVALID_EMAIL_ERROR_CODE_VALUE2));
        }
        return false;
    }


    public boolean validateSMSError(Notification notification) {
        Optional<String> errorCode = resolve(() -> notification.getNotificationError().getSmsNotificationError()
                .getValues().get(INVALID_SMS_ERROR_CODE_KEY));
        if (errorCode.isPresent()) {
            return errorCode.get().equalsIgnoreCase(INVALID_SMS_ERROR_CODE_VALUE);
        }
        return false;
    }
}
