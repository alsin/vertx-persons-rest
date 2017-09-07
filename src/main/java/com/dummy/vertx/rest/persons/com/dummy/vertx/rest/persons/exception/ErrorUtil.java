package com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import static com.dummy.vertx.rest.persons.Constants.APPLICATION_JSON_TYPE;
import static com.dummy.vertx.rest.persons.Constants.CONTENT_TYPE_HEADER;
import static com.dummy.vertx.rest.persons.Constants.ERROR_CODE_PROP;
import static com.dummy.vertx.rest.persons.Constants.ERROR_MESSAGE_PROP;
import static com.dummy.vertx.rest.persons.Constants.INTERNAL_SERVER_ERROR_CODE;

/**
 * Created by sinal04 on 07/09/2017.
 */
public class ErrorUtil {

    public static void sendInternalServerErrorResponseJson(String errorMsg, HttpServerResponse response) {
        response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);
        response.setStatusCode(INTERNAL_SERVER_ERROR_CODE);
        response.end(new JsonObject()
                .put(ERROR_CODE_PROP, INTERNAL_SERVER_ERROR_CODE)
                .put(ERROR_MESSAGE_PROP, errorMsg).encode());
    }

    public static void sendInternalServerErrorResponseJson(Throwable cause, HttpServerResponse response,
                                                           String unknownErrorMessage) {
        response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);
        response.setStatusCode(INTERNAL_SERVER_ERROR_CODE);
        response.end(generateInternalServerErrorJson(cause, unknownErrorMessage).encode());
    }

    public static JsonObject generateInternalServerErrorJson(Throwable cause, String unknownErrorMessage) {
        return new JsonObject()
                .put(ERROR_CODE_PROP, INTERNAL_SERVER_ERROR_CODE)
                .put(ERROR_MESSAGE_PROP, cause != null ? cause.getMessage() : unknownErrorMessage);
    }

}
