package com.paywallet.userservice.user.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paywallet.userservice.user.enums.StateEnum;
@UtilityClass
public class CustomerServiceUtil {

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
}
