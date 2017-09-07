package com.dummy.vertx.rest.persons.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import static com.dummy.vertx.rest.persons.Constants.DB_NAME_MONGO_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.DEFAULT_DB_NAME;
import static com.dummy.vertx.rest.persons.Constants.DEFAULT_MONGO_HOST;
import static com.dummy.vertx.rest.persons.Constants.DEFAULT_MONGO_PORT;
import static com.dummy.vertx.rest.persons.Constants.HOST_MONGO_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.PORT_MONGO_CONFIG_KEY;

/**
 * Factory that keeps details on instantiating MongoDB client instances.
 */
public class MyMongoFactory {

    /**
     * Returns default MongoDB config JSON object.
     *
     * @return
     */
    public static JsonObject getDefaultJsonConfig() {
        return new JsonObject()
                .put(DB_NAME_MONGO_CONFIG_KEY, DEFAULT_DB_NAME)
                .put(HOST_MONGO_CONFIG_KEY, DEFAULT_MONGO_HOST)
                .put(PORT_MONGO_CONFIG_KEY, DEFAULT_MONGO_PORT);
    }

    /**
     * Creates a MongoDB client from the given JSON config.
     *
     * @param vertx
     * @param config
     * @return
     */
    public static MongoClient createFromJsonConfig(Vertx vertx, JsonObject config) {
        return MongoClient.createShared(vertx, config);
    }
}
