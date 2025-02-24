package com.omdb.exception;

public class DaplicateRecoredException extends RuntimeException{
    public DaplicateRecoredException() {
    }

    public DaplicateRecoredException(String message) {
        super(message);
    }

}
