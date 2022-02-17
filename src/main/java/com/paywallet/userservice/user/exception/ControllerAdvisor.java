package com.paywallet.userservice.user.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ControllerAdvisor {

	/**
	 * Method handles Service unavailable exception
	 * @param serviceNotAvailableException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(ServiceNotAvailableException.class)
	public ResponseEntity<Object> handleServiceNotAvailableException(ServiceNotAvailableException serviceNotAvailableException, HttpServletRequest request) {

		String path = request.getRequestURI();
        log.error("General Custom exception ", serviceNotAvailableException);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.SERVICE_UNAVAILABLE.toString());
        body.put("message", serviceNotAvailableException.getMessage());
        body.put("timestamp", new Date());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	/**
	 * Method handles RequestID not found exception
	 * @param requestIdNotFoundException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(RequestIdNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleRequestIdNotFoundException(RequestIdNotFoundException requestIdNotFoundException, HttpServletRequest request) {

		String path = request.getRequestURI();
        log.error("Request Id not found ", requestIdNotFoundException);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "RequestId not found in the database");
        body.put("timestamp", new Date());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Method handles Notification exception happens while  not found exception
	 * @param SMSAndEmailNotificationException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(SMSAndEmailNotificationException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleSMSAndEmailNotificationException(SMSAndEmailNotificationException smsEmailNotificationException, HttpServletRequest request) {

		String path = request.getRequestURI();
        log.error("SMS and EMail Notification failed ", smsEmailNotificationException);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Exception occured while creating and sending SMS and Email Notification");
        body.put("timestamp", new Date());
        body.put("path", path);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if(smsEmailNotificationException.getMessage().contains("500"))
        	status = HttpStatus.INTERNAL_SERVER_ERROR;
        else if(smsEmailNotificationException.getMessage().contains("401")) 
        	status = HttpStatus.UNAUTHORIZED;
        else
        	status = HttpStatus.BAD_REQUEST;
        body.put("code", status.toString());
        return new ResponseEntity<>(body, status);
	}
	
	
	
	/**
	 * Method handles general customer exception
	 * @param generalCustomException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(GeneralCustomException.class)
	public ResponseEntity<Object> handleGeneralCustomException(GeneralCustomException generalCustomException, HttpServletRequest request) {
		String path = request.getRequestURI();
        log.error("General Custom exception ", generalCustomException);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", generalCustomException.getMessage());
        body.put("timestamp", new Date());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Method handles Fineract API Exception
	 * @param fineractAPIException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(FineractAPIException.class)
	public ResponseEntity<Object> handleGeneralCustomException(FineractAPIException fineractAPIException, HttpServletRequest request) {
		String path = request.getRequestURI();
        log.error("Fineract API exception : ", fineractAPIException);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", fineractAPIException.getMessage());
        body.put("timestamp", new Date());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Method handles customer not found exception
	 * @param customerNotFoundException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(CustomerNotFoundException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleCustomerNotFoundException(CustomerNotFoundException customerNotFoundException, HttpServletRequest request) {
		String path = request.getRequestURI();
        log.error(customerNotFoundException.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", customerNotFoundException.getMessage());
        body.put("timestamp", new Date());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Method handles customer account exception
	 * @param customerAccountException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(CustomerAccountException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleCustomerAccountException(CustomerAccountException customerAccountException, HttpServletRequest request) {
		String path = request.getRequestURI();
        log.error(customerAccountException.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "Customer Account does not exists");
        body.put("timestamp", new Date());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Method handles Create customer exception
	 * @param CreateCustomerException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = CreateCustomerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleCreateCustomerException(CreateCustomerException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.error("Error while creating customer", ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "Create Customer Failed : " + ex.getMessage());
        body.put("timestamp", new Date());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
	
	/**
	 * Method handles method argument not valid exception
	 * @param MethodArgumentNotValidException
	 * @return
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        List<String> list = new ArrayList<String>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            list.add(fieldName + " : " + errorMessage);

        });
        log.error("Method argument validation exception while creating customer ", ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "Method argument validation failed " + " : " + list);
        body.put("timestamp", new Date());
        return new ResponseEntity<Object> (body, HttpStatus.BAD_REQUEST);
    }
	
	
	 /* Method handles method argument not valid exception
	 * @param MethodArgumentNotValidException
	 * @return
	 */
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        log.error("Method argument validation exception HttpMessageNotReadableException ", ex);
        List<String> list = new ArrayList<String>();
        List<Reference> errorField = ((InvalidFormatException) (ex.getCause())).getPath();
        errorField.forEach((error) -> {
            String source = error.getDescription();
            String fieldName = error.getFieldName();
            list.add(source + " : " + ((InvalidFormatException) (ex.getCause())).getOriginalMessage());

        });
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "Method argument validation failed - " + " : " + list);
        body.put("timestamp", new Date());
        return new ResponseEntity<Object> (body, HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(OfferPayAllocationException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleOfferPayAllocationException(OfferPayAllocationException offerPayAllocationException, HttpServletRequest request) {
		String path = request.getRequestURI();
		log.error(offerPayAllocationException.getMessage());
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("code", HttpStatus.BAD_REQUEST.toString());
		body.put("message", offerPayAllocationException.getMessage());
		body.put("timestamp", new Date());
		body.put("path", path);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	
}