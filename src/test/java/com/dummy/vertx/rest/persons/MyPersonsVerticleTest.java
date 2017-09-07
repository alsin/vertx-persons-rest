package com.dummy.vertx.rest.persons;

import com.dummy.vertx.rest.persons.service.MyMongoFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.dummy.vertx.rest.persons.Constants.ERROR_CODE_PROP;
import static com.dummy.vertx.rest.persons.Constants.ERROR_MESSAGE_PROP;
import static com.dummy.vertx.rest.persons.Constants.MONGO_DB_NAME_VERTICLE_CONFIG_KEY;
import static com.dummy.vertx.rest.persons.Constants.MONGO_ID_PROP;
import static com.dummy.vertx.rest.persons.Constants.PERSONS_COLLECTION_NAME_VERTICLE_CONFIG_KEY;

/**
 * Functional test written on top of JUnit and Vertx-Unit frameworks which tests the REST API
 * provided by {@link MyPersonsVerticle} from the perspective of functionality.
 *
 * Created by sinal04 on 07/09/2017.
 */
@RunWith(VertxUnitRunner.class)
public class MyPersonsVerticleTest {
    private static final String NAME_FIELD = "name";
    private static final String SURNAME_FIELD = "surname";
    private static final String TEST_MONGO_DB_NAME = "test_persons_db";
    private static final String TEST_PERSONS_COLLECTION_NAME = "test.persons";

    private JsonObject adamSmithPerson = new JsonObject().put(NAME_FIELD, "Adam").put(SURNAME_FIELD, "Smith");
    private JsonObject johnDoePerson = new JsonObject().put(NAME_FIELD, "John").put(SURNAME_FIELD, "Doe");
    private JsonObject walterScottPerson = new JsonObject().put(NAME_FIELD, "Walter").put(SURNAME_FIELD, "Scott");
    private List<JsonObject> personsTestInstances = Arrays.asList(johnDoePerson, walterScottPerson);

    private Vertx vertx;

    private void initDb(TestContext context) {
        JsonObject config = new JsonObject()
                .put(Constants.DB_NAME_MONGO_CONFIG_KEY, TEST_MONGO_DB_NAME);
        MongoClient mongoClient = MyMongoFactory.createFromJsonConfig(vertx, config);

        dropTestCollection(context, mongoClient);

    }

    private void dropTestCollection(TestContext context, MongoClient mongoClient) {
        mongoClient.dropCollection(TEST_PERSONS_COLLECTION_NAME, context.asyncAssertSuccess(result -> {
            insertTestPersons(context, mongoClient);
        }));
    }

    private void insertTestPersons(TestContext context, MongoClient mongoClient) {
        personsTestInstances.forEach(person -> {
            mongoClient.insert(TEST_PERSONS_COLLECTION_NAME, person,
                    context.asyncAssertSuccess(result -> {
                        String name = person.getString(NAME_FIELD);

                        if (name.equals(johnDoePerson.getString(NAME_FIELD))) {
                            johnDoePerson.put(MONGO_ID_PROP, result);
                        } else if (name.equals(walterScottPerson.getString(NAME_FIELD))) {
                            walterScottPerson.put(MONGO_ID_PROP, result);
                        }
                    }));
        });
    }

    @Before
    public void setUp(TestContext context) {
        this.vertx = Vertx.vertx();

        JsonObject verticleConfig = new JsonObject()
                .put(MONGO_DB_NAME_VERTICLE_CONFIG_KEY, TEST_MONGO_DB_NAME)
                .put(PERSONS_COLLECTION_NAME_VERTICLE_CONFIG_KEY, TEST_PERSONS_COLLECTION_NAME);
        DeploymentOptions deployOptions = new DeploymentOptions().setConfig(verticleConfig);
        vertx.deployVerticle(MyPersonsVerticle.class.getName(), deployOptions, context.asyncAssertSuccess(result -> {
            initDb(context);
        }));

    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetAll(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/persons",
                response -> {
                    response.bodyHandler(body -> {
                        context.assertEquals(200, response.statusCode());

                        JsonArray personsArray = body.toJsonArray();

                        context.assertNotNull(personsArray);
                        context.assertEquals(personsTestInstances.size(), personsArray.size());

                        JsonObject receivedJohnDoe = null;
                        JsonObject receivedWalterScott = null;
                        for (int i = 0; i < personsArray.size(); i++) {
                            JsonObject jsonObj = personsArray.getJsonObject(i);
                            context.assertNotNull(jsonObj);
                            String personName = jsonObj.getString(NAME_FIELD);
                            context.assertNotNull(personName);
                            if (personName.equals(johnDoePerson.getString(NAME_FIELD))) {
                                receivedJohnDoe = jsonObj;
                            } else if (personName.equals(walterScottPerson.getString(NAME_FIELD))) {
                                receivedWalterScott = jsonObj;
                            } else {
                                context.fail(String.format("Unexpected person name: %s", personName));
                            }
                        }

                        context.assertNotNull(receivedJohnDoe);
                        context.assertNotNull(receivedWalterScott);

                        context.assertEquals(johnDoePerson.getString(SURNAME_FIELD),
                                receivedJohnDoe.getString(SURNAME_FIELD));
                        context.assertEquals(walterScottPerson.getString(SURNAME_FIELD),
                                receivedWalterScott.getString(SURNAME_FIELD));

                        async.complete();
                    });
                });
    }

    @Test
    public void testGetJohnDoe(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/persons/" + johnDoePerson.getString(MONGO_ID_PROP),
                response -> {
                    response.bodyHandler(body -> {
                        context.assertEquals(200, response.statusCode());

                        JsonObject person = body.toJsonObject();
                        context.assertNotNull(person);

                        context.assertEquals(johnDoePerson.getString(NAME_FIELD),
                                person.getString(NAME_FIELD));

                        context.assertEquals(johnDoePerson.getString(SURNAME_FIELD),
                                person.getString(SURNAME_FIELD));


                        async.complete();
                    });
                });
    }

    @Test
    public void testGetNonExistentPerson(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/persons/blah-blah",
                response -> {
                    response.bodyHandler(body -> {
                        context.assertEquals(200, response.statusCode());

                        JsonObject person = body.toJsonObject();
                        context.assertNotNull(person);
                        context.assertTrue(person.isEmpty());

                        async.complete();
                    });
                });
    }

    @Test
    public void testGetNonExistentResource(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/blah-blah",
                response -> {
                    response.bodyHandler(body -> {
                        context.assertEquals(404, response.statusCode());
                        JsonObject error = body.toJsonObject();
                        context.assertNotNull(error);
                        context.assertTrue(error.containsKey(ERROR_CODE_PROP));
                        context.assertEquals(404, error.getInteger(ERROR_CODE_PROP));
                        context.assertTrue(error.containsKey(ERROR_MESSAGE_PROP));
                        async.complete();
                    });
                });
    }


    @Test
    public void testPostAdamSmith(TestContext context) {
        final Async async = context.async();

        HttpClientRequest request = vertx.createHttpClient().post(8080, "localhost", "/persons",
                response -> {
                    response.bodyHandler(body -> {
                        context.assertEquals(200, response.statusCode());
                        JsonObject person = body.toJsonObject();
                        context.assertNotNull(person);

                        context.assertEquals(adamSmithPerson.getString(NAME_FIELD), person.getString(NAME_FIELD));
                        context.assertEquals(adamSmithPerson.getString(SURNAME_FIELD), person.getString(SURNAME_FIELD));
                        context.assertTrue(person.containsKey(MONGO_ID_PROP));

                        vertx.createHttpClient().getNow(8080, "localhost", "/persons",
                                gResponse -> {
                                    gResponse.bodyHandler(gBody -> {
                                        context.assertEquals(200, gResponse.statusCode());

                                        JsonArray personsArray = gBody.toJsonArray();

                                        context.assertNotNull(personsArray);
                                        context.assertEquals(3, personsArray.size());

                                        JsonObject receivedJohnDoe = null;
                                        JsonObject receivedWalterScott = null;
                                        JsonObject receivedAdamSmith = null;
                                        for (int i = 0; i < personsArray.size(); i++) {
                                            JsonObject jsonObj = personsArray.getJsonObject(i);
                                            context.assertNotNull(jsonObj);
                                            String personName = jsonObj.getString(NAME_FIELD);
                                            context.assertNotNull(personName);
                                            if (personName.equals(johnDoePerson.getString(NAME_FIELD))) {
                                                receivedJohnDoe = jsonObj;
                                            } else if (personName.equals(walterScottPerson.getString(NAME_FIELD))) {
                                                receivedWalterScott = jsonObj;
                                            } else if (personName.equals(adamSmithPerson.getString(NAME_FIELD))) {
                                                receivedAdamSmith = jsonObj;
                                            } else {
                                                context.fail(String.format("Unexpected person name: %s", personName));
                                            }
                                        }

                                        context.assertNotNull(receivedJohnDoe);
                                        context.assertNotNull(receivedWalterScott);
                                        context.assertNotNull(receivedAdamSmith);

                                        context.assertEquals(johnDoePerson.getString(SURNAME_FIELD),
                                                receivedJohnDoe.getString(SURNAME_FIELD));
                                        context.assertEquals(walterScottPerson.getString(SURNAME_FIELD),
                                                receivedWalterScott.getString(SURNAME_FIELD));

                                        context.assertEquals(adamSmithPerson.getString(SURNAME_FIELD),
                                                receivedAdamSmith.getString(SURNAME_FIELD));

                                        async.complete();
                                    });
                                });

                    });
                });
        request.putHeader(Constants.CONTENT_TYPE_HEADER, Constants.APPLICATION_JSON_TYPE);
        request.end(adamSmithPerson.encode());
    }

    @Test
    public void testPostWithoutContentTypeHeader(TestContext context) {
        final Async async = context.async();

        HttpClientRequest request = vertx.createHttpClient().post(8080, "localhost", "/persons",
                response -> {
                    response.bodyHandler(body -> {
                        context.assertEquals(404, response.statusCode());
                        JsonObject error = body.toJsonObject();
                        context.assertNotNull(error);
                        context.assertTrue(error.containsKey(ERROR_CODE_PROP));
                        context.assertEquals(404, error.getInteger(ERROR_CODE_PROP));
                        context.assertTrue(error.containsKey(ERROR_MESSAGE_PROP));

                        async.complete();
                    });
                });
        request.end(adamSmithPerson.encode());
    }

}
