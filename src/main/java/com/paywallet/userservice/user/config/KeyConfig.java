package com.paywallet.userservice.user.config;

public class KeyConfig {
    private static String key;
    private static String iv;

    protected static synchronized void init(String secreteKey, String secreteIv) {
        key = secreteKey;
        iv = secreteIv;
    }

    public static String getKey(){
        return key;
    }

    public static String getIv(){
        return iv;
    }

}
