package com.globalpayex.routes;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAdminRouter {
    private static final Logger logger = LoggerFactory
            .getLogger(MyAdminRouter.class);

    public static Router init(Vertx vertx, JsonObject config) {
        var router = Router.router(vertx);
        router.get("/student-statistics")
                .handler(routingContext -> handleStudentStatistics(vertx,
                        routingContext));

        return router;
    }

    private static void handleStudentStatistics(Vertx vertx, RoutingContext routingContext) {
        vertx.eventBus()
                .<JsonObject>request("student.stats", new JsonObject())
                .onSuccess(message -> {
                    var statsJsonObject = message.body();
                    routingContext
                            .response()
                            .putHeader("Content-Type", "application/json")
                            .end(statsJsonObject.encode());
                })
                .onFailure(err -> {
                    logger.error("Unable to get reply from `student.stats`. Error {}", err);
                    routingContext
                            .response()
                            .setStatusCode(500)
                            .end("Unable to get student statistics");
                });
    }
}
