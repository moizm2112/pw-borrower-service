package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.dto.EventDTO;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.paywallet.userservice.user.enums.StateEnum;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
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

    public EventDTO prepareEvent(String requestId, String code, String source, String message, String level){
        return EventDTO.builder()
                .requestId(requestId)
                .code(code)
                .source(source)
                .message(message)
                .level(level)
                .time(new Date())
                .build();
    }

    public String hashString(String originalString) {
        if(StringUtils.isBlank(originalString))
            return "";

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA256);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
