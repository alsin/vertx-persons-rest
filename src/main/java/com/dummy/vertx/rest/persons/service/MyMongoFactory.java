package com.dummy.vertx.rest.persons.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

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

    /**
     *
     * @param vertx
     * @return
     */
    public static MongoClient createDefault(Vertx vertx) {
        JsonObject config = new JsonObject()
                .put("db_name", "persons_db")
                .put("host", "127.0.0.1");
        return MongoClient.createShared(vertx, config);
    }
}
