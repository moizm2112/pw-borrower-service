package com.paywallet.userservice.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

@Component
public class AESEncryption {

    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    private static final String ALGORITHM = "AES";
    private static final String AES_MODE = "AES/GCM/NoPadding";

    @Value("${aes.enc.key}")
    private String encKey;

    @Value("${aes.enc.iv}")
    private String encIv;


    public String encrypt(String plainText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if(Objects.isNull(plainText) || plainText.isBlank()){
            return plainText;
        }
        SecretKey secretKey = getSecretKeyObject(encKey);
        byte[] ivBytes = getIVBytes(encIv);
        return encrypt(plainText, secretKey, ivBytes);
    }

    public String decrypt(String cipherText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if(Objects.isNull(cipherText) || cipherText.isBlank()){
            return cipherText;
        }
        SecretKey secretKey = getSecretKeyObject(encKey);
        byte[] ivBytes = getIVBytes(encIv);
        return decrypt(cipherText, secretKey, ivBytes);
    }

    private String encrypt(String plainText, SecretKey secretKey, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = generateCipher(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printBase64Binary(cipherText);
    }

    private String decrypt(String cipherText, SecretKey secretKey, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] data = DatatypeConverter.parseBase64Binary(cipherText);
        Cipher cipher = generateCipher(Cipher.DECRYPT_MODE, secretKey,iv);
        byte[] decryptedText = cipher.doFinal(data);
        return new String(decryptedText);
    }

    private Cipher generateCipher(int mode, SecretKey secretKey, byte[] iv) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(mode, keySpec, gcmParameterSpec);
        return cipher;
    }

    private SecretKey getSecretKeyObject(String hexFormattedKey) {
        byte[] rawKeyByte = DatatypeConverter.parseHexBinary(hexFormattedKey);
        return new SecretKeySpec(rawKeyByte, ALGORITHM);
    }

    private byte[] getIVBytes(String hexFormattedIv) {
        return DatatypeConverter.parseHexBinary(hexFormattedIv);
    }

    public String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE);
        byte[] encodedKey = keyGenerator.generateKey().getEncoded();
        String hexBinary = DatatypeConverter.printHexBinary(encodedKey);
        return hexBinary;
    }

    public String generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        String hexBinary = DatatypeConverter.printHexBinary(iv);
        return hexBinary;
    }
}
