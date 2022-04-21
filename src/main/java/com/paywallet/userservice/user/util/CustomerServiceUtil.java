package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.dto.EventDTO;
import io.sentry.Sentry;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.paywallet.userservice.user.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
@Slf4j
public class CustomerServiceUtil {
    private final String SHA256 = "SHA-256";

    public static boolean doesObjectContainField(Object object, String fieldName) {
        Class<?> objectClass = object.getClass();
        for (Field field : objectClass.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean validateCustomerState(String state) throws Exception {
    	List<String> stateList = new ArrayList<String>(Arrays.asList("AL","AK","AS","AZ","AR","CA","CO","CT","DE","DC","FL","GA","GU","HI",
    			"ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY",
    			"NC","ND","MP","OH","OK","OR","PA","PR","RI","SC","SD","TN","TX","UT","VT","VA","VI","WA","WV","WI","WY"));
    	if(stateList.contains(state))
    		return true;
    	return false;
    }

    public String hashString(String originalString) {
        if(StringUtils.isBlank(originalString))
            return "";

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA256);
        } catch (NoSuchAlgorithmException e) {
            Sentry.captureException(e);
            log.error("Error while creating hashing : {}",e.getMessage(),e);
        }
        byte[] hashBytes = md.digest(originalString.getBytes(StandardCharsets.UTF_8));
        String hashedString = Base64.getEncoder().encodeToString(hashBytes);
        return hashedString;

    }

    public String mask(String str) {
        if(Objects.isNull(str)) {
            return "";
        }
        return str.replaceAll("\\d(?!\\d{0,3}$)","X");
    }

}
