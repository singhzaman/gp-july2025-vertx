package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Machine1Verticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(Machine1Verticle.class);

    @Override
    public void start() throws Exception {
        var config = config();
        var deploymentOptions = new DeploymentOptions()
                .setConfig(config);
        vertx.deployVerticle(new StatisticsVerticle(),
                deploymentOptions);
        vertx.deployVerticle(new EmailVerticle(),
                deploymentOptions);
    }

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions())
            .onSuccess(vertx -> {
                var options = new DeploymentOptions()
                        .setConfig(new JsonObject()
                                .put("port", 8085)
                                .put("connection_string", "mongodb+srv://mehulc:tl9LJE6ep9ld0hMk@cluster0.ufaw51h.mongodb.net/")
                                .put("db_name", "college_db")
                                .put("useObjectId", true)
                                .put("emailHost", "smtp.gmail.com")
                                .put("emailPort", 587)
                                .put("emailUsername", "mehul.chopra.dev@gmail.com")
                                .put("emailPassword", "")
                                .put("adminAppPort", 8087)
                        );
                vertx.deployVerticle(new Machine1Verticle(), options);
            })
            .onFailure(err -> {
                logger.error("Error deploying machine 1 verticle. Error {}", err);
            });
    }
}
