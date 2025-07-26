package com.globalpayex.dao;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public class StudentDao {

    private MongoClient mongoClient;

    public static final String COLLECTION_NAME = "students";

    public StudentDao(Vertx vertx, JsonObject config) {
       this.mongoClient = MongoClient.createShared(vertx, config);
    }

    public Future<List<JsonObject>> findAll(JsonObject query, JsonObject projection) {
        return this.mongoClient.findWithOptions(
                COLLECTION_NAME,
                query, new FindOptions().setFields(projection)
        );
    }

    public Future<JsonObject> findById(String id) {
        return
                this.mongoClient.findOne(
                        COLLECTION_NAME,
                        new JsonObject()
                                .put("_id", id),
                        null
                );
    }

    public Future<String> insert(JsonObject data) {
        return this.mongoClient.insert(COLLECTION_NAME, data);
    }

    public Future<Long> count(JsonObject query) {
        return this.mongoClient.count(COLLECTION_NAME, query);
    }
}
