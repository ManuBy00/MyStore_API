package com.mby.myStore.Exceptions;

public class ExpiredJwtToken extends RuntimeException {
    public ExpiredJwtToken(String message) {
        super(message);
    }
}
