package com.dummy.vertx.rest.persons;

import com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception.ErrorUtil;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.dummy.vertx.rest.persons.Constants.APPLICATION_JSON_TYPE;
import static com.dummy.vertx.rest.persons.Constants.CONTENT_TYPE_HEADER;
import static com.dummy.vertx.rest.persons.Constants.ERROR_CODE_PROP;
import static com.dummy.vertx.rest.persons.Constants.ERROR_MESSAGE_PROP;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception.ErrorUtil}.
 */
public class ErrorUtilTest {

    private static final String TEST_ERROR_STRING = "Test string";
    private static final String UNKNOWN_ERROR_MESSAGE = "Unknown";

    @Test
    public void testSendInternalServerErrorResponseJsonFromString() {
        Map<String, String> headers = new HashMap<>();
        DummyHttpServerResponse response = new DummyHttpServerResponse(headers);
        ErrorUtil.sendInternalServerErrorResponseJson(TEST_ERROR_STRING, response);
        assertEquals(500, response.getStatusCode());
        assertEquals(APPLICATION_JSON_TYPE,
                headers.get(CONTENT_TYPE_HEADER));
        JsonObject errJson = new JsonObject(response.getBody());
        assertEquals(TEST_ERROR_STRING, errJson.getString(ERROR_MESSAGE_PROP));
        assertEquals(500, errJson.getInteger(ERROR_CODE_PROP).intValue());

    }

    @Test
    public void testSendInternalServerErrorResponseJsonFromThrowable() {
        Map<String, String> headers = new HashMap<>();
        DummyHttpServerResponse response = new DummyHttpServerResponse(headers);
        Exception ex = new Exception(TEST_ERROR_STRING);
        ErrorUtil.sendInternalServerErrorResponseJson(ex, response, null);
        assertEquals(500, response.getStatusCode());
        assertEquals(APPLICATION_JSON_TYPE, headers.get(CONTENT_TYPE_HEADER));

        JsonObject errJson = new JsonObject(response.getBody());
        assertEquals(TEST_ERROR_STRING, errJson.getString(ERROR_MESSAGE_PROP));
        assertEquals(500, errJson.getInteger(ERROR_CODE_PROP).intValue());
    }

    @Test
    public void testSendInternalServerErrorResponseJsonFromUnknownErrMessage() {
        Map<String, String> headers = new HashMap<>();
        DummyHttpServerResponse response = new DummyHttpServerResponse(headers);
        Exception ex = new Exception(TEST_ERROR_STRING);
        ErrorUtil.sendInternalServerErrorResponseJson(null, response, UNKNOWN_ERROR_MESSAGE);
        assertEquals(500, response.getStatusCode());
        assertEquals(APPLICATION_JSON_TYPE, headers.get(CONTENT_TYPE_HEADER));

        JsonObject errJson = new JsonObject(response.getBody());
        assertEquals(UNKNOWN_ERROR_MESSAGE, errJson.getString(ERROR_MESSAGE_PROP));
        assertEquals(500, errJson.getInteger(ERROR_CODE_PROP).intValue());
    }

}
