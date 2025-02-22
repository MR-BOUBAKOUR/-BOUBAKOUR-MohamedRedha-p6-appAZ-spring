package com.payMyBuddy.exception;

public class SelfAddContactException extends RuntimeException {
    public SelfAddContactException(String message) {
        super(message);
    }
}