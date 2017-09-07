package com.dummy.vertx.rest.persons;

/**
 * Created by sinal04 on 07/09/2017.
 */
public interface Constants {
    public static final String PERSONS_TABLE = "persons";
    public static final String CONTENT_TYPE_HEADER = "content-type";
    public static final String APPLICATION_JSON_TYPE = "application/json";
    public static final String MONGO_ID_PROP = "_id";

    public static final String ERROR_CODE_PROP = "errorCode";
    public static final String ERROR_MESSAGE_PROP = "errorMessage";

    public static final String UNKNOWN_ERROR = "Unknown error.";
    public static final String UNKNOWN_DB_ERROR = "Unknown db error.";

    public static final int INTERNAL_SERVER_ERROR_CODE = 500;

}
