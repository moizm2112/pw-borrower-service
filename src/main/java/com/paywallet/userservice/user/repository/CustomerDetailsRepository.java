package com.paywallet.userservice.user.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.entities.CustomerProvidedDetails;
import com.paywallet.userservice.user.entities.PayrollProfile;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
@Slf4j
public class CustomerDetailsRepository {
    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    CommonUtil commonUtil;

    public CustomerProvidedDetails upsert(CustomerProvidedDetails customerProfile) throws JsonProcessingException {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(customerProfile.getRequestId()));
        Update update = new Update();
        Map<String,String> objectMap = extractColumn(customerProfile);
        objectMap.values().removeIf(Objects::isNull);
        log.info("Customer Provided data : {}",objectMap);
        objectMap.forEach(update::set);
        CustomerProvidedDetails customerProvidedDetails = mongoOperations.findAndModify(query, update, FindAndModifyOptions.options()
                .upsert(true).returnNew(true), CustomerProvidedDetails.class);
        log.info("customerProvidedDetails : {}",customerProvidedDetails);
        return customerProvidedDetails;
    }

    public CustomerProvidedDetails findByRequestId(String requestId) {
        return mongoOperations.findById(requestId, CustomerProvidedDetails.class);
    }

    public Optional<CustomerDetails> findCustomerById(String customerId) {
        return Optional.ofNullable(mongoOperations.findById(customerId, CustomerDetails.class));
    }

    public PayrollProfile findPayrollProviderDetailsById(String customerId) {
        return findCustomerById(customerId)
                .orElseThrow(()-> new CustomerNotFoundException("Payroll provider details not found for the given customerId :"+customerId))
                .getPayrollProvidedDetails();
    }

    public String updatePayrollProfile(String customerId, PayrollProfile payrollProfile) throws JsonProcessingException {
        String status = CommonEnum.CUSTOMER_PAY_ROLL_UPDATE_FAILED_STATUS_MSG.getMessage();
        payrollProfile.setUpdatedAt(commonUtil.getESTTime());
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(customerId));
        Update update = new Update();
        Map<String,String> objectMap = extractColumn(payrollProfile);
        objectMap.values().removeIf(Objects::isNull);
        objectMap.forEach((key, value) -> update.set("payrollProvidedDetails."+key,value));
        UpdateResult updateResult = mongoOperations.updateFirst(query,update,CustomerDetails.class);
        if(updateResult.getModifiedCount() > 0) {
            status = CommonEnum.CUSTOMER_PAY_ROLL_UPDATE_SUCCESS_STATUS_MSG.getMessage();
        }
        return status;
    }

    public Map<String,String> extractColumn(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(obj);
        return mapper.readValue(jsonString, HashMap.class);
    }


}
