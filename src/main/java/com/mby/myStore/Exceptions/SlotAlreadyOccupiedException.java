package com.mby.myStore.Exceptions;

public class SlotAlreadyOccupiedException extends RuntimeException {
    public SlotAlreadyOccupiedException(String message) {
        super(message);
    }
}
