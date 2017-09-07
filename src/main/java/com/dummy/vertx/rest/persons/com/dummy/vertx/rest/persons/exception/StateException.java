package com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception;

/**
 * App-wide exception class which provides also an error code.
 */
public class StateException extends RuntimeException {

    private int code;

    public StateException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
