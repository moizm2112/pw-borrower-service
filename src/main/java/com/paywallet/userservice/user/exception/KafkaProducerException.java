package com.paywallet.userservice.user.exception;

public class KafkaProducerException extends Exception {
    public KafkaProducerException(String message) {
        super(message);
    }
}
