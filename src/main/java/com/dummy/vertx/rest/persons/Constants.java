package com.dummy.vertx.rest.persons;

/**
 * App global constants.
 */
public interface Constants {
    public static final String DEFAULT_DB_NAME = "persons_db";
    public static final String DEFAULT_PERSONS_COLLECTION_NAME = "persons";
    public static final String DEFAULT_MONGO_HOST = "127.0.0.1";
    public static final int DEFAULT_MONGO_PORT = 27017;
    public static final String CONTENT_TYPE_HEADER = "content-type";
    public static final String APPLICATION_JSON_TYPE = "application/json";
    public static final String MONGO_ID_PROP = "_id";

    public static final String ERROR_CODE_PROP = "errorCode";
    public static final String ERROR_MESSAGE_PROP = "errorMessage";

    public static final String UNKNOWN_ERROR = "Unknown error.";
    public static final String UNKNOWN_DB_ERROR = "Unknown db error.";

    public static final int INTERNAL_SERVER_ERROR_CODE = 500;


    public static final String MONGO_DB_NAME_VERTICLE_CONFIG_KEY = "mongo.db.name";
    public static final String MONGO_HOST_VERTICLE_CONFIG_KEY = "mongo.host";
    public static final String MONGO_PORT_VERTICLE_CONFIG_KEY = "mongo.port";
    public static final String PERSONS_COLLECTION_NAME_VERTICLE_CONFIG_KEY = "persons.collection.name";

    public static final String DB_NAME_MONGO_CONFIG_KEY = "db_name";
    public static final String HOST_MONGO_CONFIG_KEY = "host";
    public static final String PORT_MONGO_CONFIG_KEY = "port";
}
