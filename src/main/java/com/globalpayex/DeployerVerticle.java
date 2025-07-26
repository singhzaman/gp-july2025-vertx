package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class DeployerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        var config = config();
        var deploymentOptions = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(new MyHttpServerVerticle(),
                deploymentOptions);
        vertx.deployVerticle(new StatisticsVerticle(),
                deploymentOptions);
        vertx.deployVerticle(new EmailVerticle(),
                deploymentOptions);
        vertx.deployVerticle(new AdminAppVerticle(),
                deploymentOptions);
    }

    public static void main(String[] args) {
        Vertx vertx1 = Vertx.vertx();

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
        vertx1.deployVerticle(new DeployerVerticle(), options);
    }
}
