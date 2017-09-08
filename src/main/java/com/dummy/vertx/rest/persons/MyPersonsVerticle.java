package com.dummy.vertx.rest.persons;

import com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception.ErrorUtil;
import com.dummy.vertx.rest.persons.com.dummy.vertx.rest.persons.exception.StateException;
import com.dummy.vertx.rest.persons.service.MyMongoFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;

import static com.dummy.vertx.rest.persons.Constants.APPLICATION_JSON_TYPE;
import static com.dummy.vertx.rest.persons.Constants.CONTENT_TYPE_HEADER;
import static com.dummy.vertx.rest.persons.Constants.DEFAULT_MONGO_HOST;
import static com.dummy.vertx.rest.persons.Constants.DEFAULT_MONGO_PORT;
import static com.dummy.vertx.rest.persons.Constants.HOST_MONGO_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.MONGO_DB_NAME_VERTICLE_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.DB_NAME_MONGO_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.DEFAULT_DB_NAME;
import static com.dummy.vertx.rest.persons.Constants.ERROR_CODE_PROP;
import static com.dummy.vertx.rest.persons.Constants.ERROR_MESSAGE_PROP;
import static com.dummy.vertx.rest.persons.Constants.INTERNAL_SERVER_ERROR_CODE;
import static com.dummy.vertx.rest.persons.Constants.MONGO_HOST_VERTICLE_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.MONGO_ID_PROP;
import static com.dummy.vertx.rest.persons.Constants.DEFAULT_PERSONS_COLLECTION_NAME;
import static com.dummy.vertx.rest.persons.Constants.MONGO_PORT_VERTICLE_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.PERSONS_COLLECTION_NAME_VERTICLE_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.PORT_MONGO_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.UNKNOWN_DB_ERROR;
import static com.dummy.vertx.rest.persons.Constants.UNKNOWN_ERROR;

/**
 * Verticle which runs an HTTP server with the following REST API:
 * <ul>
 *     <li>GET: /persons - to return all available persons as JSON array</li>
 *     <li>GET: /persons/:id - to return a single person filtering by its _id</li>
 *     <li>
 *         POST: /persons - to insert or update new\existing person information.
 *         If the JSON object does not contain _id property or contains one but it's not yet in the collection,
 *         a new record is created. If it does, then the corresponding record JSON gets updated.
 *     </li>
 * </ul>
 * Created by sinal04 on 06/09/2017.
 */
public class MyPersonsVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        //Configure Mongo client
        JsonObject mongoDbConfig = new JsonObject();
        mongoDbConfig.put(DB_NAME_MONGO_CONFIG_KEY,
                config().getString(MONGO_DB_NAME_VERTICLE_CONFIG_KEY, DEFAULT_DB_NAME));

        mongoDbConfig.put(HOST_MONGO_CONFIG_KEY,
                config().getString(MONGO_HOST_VERTICLE_CONFIG_KEY, DEFAULT_MONGO_HOST));

        mongoDbConfig.put(PORT_MONGO_CONFIG_KEY,
                config().getInteger(MONGO_PORT_VERTICLE_CONFIG_KEY, DEFAULT_MONGO_PORT));


        MongoClient mongoClient = MyMongoFactory.createFromJsonConfig(vertx, mongoDbConfig);

        String personsCollectionName = config().getString(PERSONS_COLLECTION_NAME_VERTICLE_CONFIG_KEY,
                DEFAULT_PERSONS_COLLECTION_NAME);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(LoggerHandler.create());


        //Using handler, not blockingHandler as MongoDb is non-blocking


        //Get all persons
        router
                .get("/persons")
                .produces(APPLICATION_JSON_TYPE)
                .handler(routingContext -> {
                    mongoClient.find(personsCollectionName, new JsonObject(), result -> {
                        HttpServerResponse response = routingContext.response();
                        response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);

                        if (result.succeeded()) {
                            JsonArray resArray = result.result() != null ? new JsonArray(result.result()) : new JsonArray();
                            response.end(resArray.encode());
                        } else {
                            ErrorUtil.sendInternalServerErrorResponseJson(result.cause(), response, UNKNOWN_DB_ERROR);
                        }
                    });
                });

        //Get by person id
        router
                .get("/persons/:id")
                .produces(APPLICATION_JSON_TYPE)
                .handler(routingContext -> {
                    String personId = routingContext.request().getParam("id");
                    JsonObject searchObj = new JsonObject().put(MONGO_ID_PROP, personId);
                    mongoClient.findOne(personsCollectionName, searchObj, null, result -> {
                        HttpServerResponse response = routingContext.response();
                        response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);

                        if (result.succeeded()) {
                            JsonObject resObj = result.result();
                            response.end(resObj != null ? resObj.encode() : new JsonObject().encode());
                        } else {
                            ErrorUtil.sendInternalServerErrorResponseJson(result.cause(),
                                    response, UNKNOWN_DB_ERROR);
                        }
                    });
                });

        //POST a new person or person update
        router
                .post("/persons")
                .consumes(APPLICATION_JSON_TYPE)
                .produces(APPLICATION_JSON_TYPE)
                .handler(routingContext -> {
                    JsonObject personJson = routingContext.getBodyAsJson();

                    mongoClient.save(personsCollectionName, personJson, res -> {
                        if (res.succeeded()) {
                            String id = res.result();
                            if (id == null) {//update was done, not insert
                                id = personJson.getString(MONGO_ID_PROP);
                            }

                            if (id == null) {
                                ErrorUtil.sendInternalServerErrorResponseJson(String.format("Could not find object's '%s' property!",
                                        MONGO_ID_PROP), routingContext.response());

                            } else {
                                //Fetch the person from db just to make sure it's really there
                                mongoClient.findOne(personsCollectionName, new JsonObject().put(MONGO_ID_PROP, id), null, findRes -> {
                                    HttpServerResponse response = routingContext.response();
                                    response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);
                                    if (findRes.succeeded() && findRes.result() != null) {
                                        response.end(findRes.result().encode());
                                    } else {
                                        ErrorUtil.sendInternalServerErrorResponseJson(findRes.cause(),
                                                response, UNKNOWN_DB_ERROR);
                                    }
                                });
                            }
                        } else {
                            ErrorUtil.sendInternalServerErrorResponseJson(res.cause(),
                                    routingContext.response(),
                                    UNKNOWN_DB_ERROR);
                        }
                    });
                });


        router.route().failureHandler(failureCtx -> {
            HttpServerResponse response = failureCtx.response();
            response.putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);

            JsonObject responseJson = new JsonObject();
            responseJson.put(ERROR_CODE_PROP, failureCtx.statusCode() > 0 ?
                    failureCtx.statusCode() : INTERNAL_SERVER_ERROR_CODE);

            Throwable exc = failureCtx.failure();
            if (exc != null) {
                responseJson.put(ERROR_MESSAGE_PROP, exc.getMessage());
                if (exc instanceof StateException) {
                    StateException stateExc = (StateException) exc;
                    responseJson.put(ERROR_CODE_PROP, stateExc.getCode());
                }
            } else {
                responseJson.put(ERROR_MESSAGE_PROP, UNKNOWN_ERROR);
            }

            response.setStatusCode(responseJson.getInteger(ERROR_CODE_PROP));
            response.end(responseJson.encode());
        });

        //All other requests should end up here and we return 404
        router.route().handler(ctx -> {
            throw new StateException(String.format("Resource with URI=%s not found",
                    ctx.request().path()), 404);
        });

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(8080, result -> {
            if (result.succeeded()) {
                fut.complete();
            } else {
                fut.fail(result.cause());
            }
        });

    }
}
