package com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception;

/**
 * Created by sinal04 on 07/09/2017.
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
