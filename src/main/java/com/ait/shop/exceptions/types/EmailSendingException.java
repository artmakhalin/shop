package com.ait.shop.exceptions.types;

public class EmailSendingException extends RuntimeException{

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
