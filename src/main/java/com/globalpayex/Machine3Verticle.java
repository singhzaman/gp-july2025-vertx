package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Machine3Verticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(Machine3Verticle.class);

    @Override
    public void start() throws Exception {
        var config = config();
        var deploymentOptions = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(new AdminAppVerticle(),
                deploymentOptions);
    }

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions())
                .onSuccess(vertx -> {
                    var options = new DeploymentOptions()
                            .setConfig(new JsonObject()
                                    .put("adminAppPort", 8087)
                            );
                    vertx.deployVerticle(new Machine3Verticle(), options);
                })
                .onFailure(err -> {
                    logger.error("Error deploying machine 3 verticle. Error {}", err);
                });
    }
}
