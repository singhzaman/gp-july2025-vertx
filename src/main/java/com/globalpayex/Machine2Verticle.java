package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Machine2Verticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(Machine2Verticle.class);

    @Override
    public void start() throws Exception {
        var config = config();
        var deploymentOptions = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(new MyHttpServerVerticle(),
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
                            );
                    vertx.deployVerticle(new Machine2Verticle(), options);
                })
                .onFailure(err -> {
                    logger.error("Error deploying machine 2 verticle. Error {}", err);
                });
    }
}
