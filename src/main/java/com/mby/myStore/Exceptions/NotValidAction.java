package com.mby.myStore.Exceptions;

public class NotValidAction extends RuntimeException {
    public NotValidAction(String message) {
        super(message);
    }
}
