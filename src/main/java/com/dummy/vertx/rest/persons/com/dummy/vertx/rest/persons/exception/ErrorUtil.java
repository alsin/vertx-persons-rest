package com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import static com.dummy.vertx.rest.persons.Constants.APPLICATION_JSON_TYPE;
import static com.dummy.vertx.rest.persons.Constants.CONTENT_TYPE_HEADER;
import static com.dummy.vertx.rest.persons.Constants.ERROR_CODE_PROP;
import static com.dummy.vertx.rest.persons.Constants.ERROR_MESSAGE_PROP;
import static com.dummy.vertx.rest.persons.Constants.INTERNAL_SERVER_ERROR_CODE;

/**
 * Utility to help with generating and sending error responses.
 */
public class ErrorUtil {

    /**
     * Sends a 505 Internal Server Error JSON object as a response using <code>errorMsg</code>
     * as the <code>errorMessage</code> property of the JSON.
     *
     * @param errorMsg        error cause message
     * @param response        response object to write to
     */
    public static void sendInternalServerErrorResponseJson(String errorMsg, HttpServerResponse response) {
        response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);
        response.setStatusCode(INTERNAL_SERVER_ERROR_CODE);
        response.end(new JsonObject()
                .put(ERROR_CODE_PROP, INTERNAL_SERVER_ERROR_CODE)
                .put(ERROR_MESSAGE_PROP, errorMsg).encode());
    }

    /**
     * Sends a 505 Internal Server Error JSON object as a response using <code>cause.message()</code>
     * as the <code>errorMessage</code> property of the JSON.
     *
     * @param cause               error cause exception
     * @param response            response object to write to
     * @param unknownErrorMessage if <code>cause</code> is <code>null</code> then this message is used as the
     *                            <code>errorMessage</code> property of the response JSON. Unknown error message
     *                            can be different for different layers of the application, e.g. 'Unknown DB error'
     *                            or 'Unknown FS error'
     */
    public static void sendInternalServerErrorResponseJson(Throwable cause, HttpServerResponse response,
                                                           String unknownErrorMessage) {
        response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);
        response.setStatusCode(INTERNAL_SERVER_ERROR_CODE);
        response.end(generateInternalServerErrorJson(cause, unknownErrorMessage).encode());
    }

    private static JsonObject generateInternalServerErrorJson(Throwable cause, String unknownErrorMessage) {
        return new JsonObject()
                .put(ERROR_CODE_PROP, INTERNAL_SERVER_ERROR_CODE)
                .put(ERROR_MESSAGE_PROP, cause != null ? cause.getMessage() : unknownErrorMessage);
    }

}
