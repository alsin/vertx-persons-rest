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
 * Created by sinal04 on 06/09/2017.
 */
public class MyMongoFactory {


//    /**
//     *
//     * @param vertx
//     * @param mongoClientConfigFile
//     * @return
//     */
//    public static MongoClient createFromConfig(Vertx vertx, String mongoClientConfigFile) {
//        FileSystem fs = vertx.fileSystem();
//        fs.exists(mongoClientConfigFile, result -> {
//           if (result.succeeded()) {
//               if (result.result()) {
//
//                   //File exists! Try to read & init from it
//                   fs.readFile(mongoClientConfigFile, readResult -> {
//
//
//                   });
//               }
//           } else {
//
//           }
//        });
//        return null;
//    }

    public static JsonObject getDefaultJsonConfig() {
        return new JsonObject()
                .put(DB_NAME_MONGO_CONFIG_KEY, DEFAULT_DB_NAME)
                .put(HOST_MONGO_CONFIG_KEY, DEFAULT_MONGO_HOST)
                .put(PORT_MONGO_CONFIG_KEY, DEFAULT_MONGO_PORT);
    }

    /**
     *
     * @param vertx
     * @return
     */
    public static MongoClient createFromJsonConfig(Vertx vertx, JsonObject config) {
        return MongoClient.createShared(vertx, config);
    }
}
