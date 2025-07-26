package com.globalpayex;

import com.globalpayex.routes.MyAppRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHttpServerVerticle extends AbstractVerticle  {

    private static final Logger logger = LoggerFactory
            .getLogger(MyHttpServerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var port = config()
                .getInteger("port");
        vertx
                .createHttpServer()
                .requestHandler(
                        MyAppRouter.init(vertx, config())
                )
                .listen(port, ar -> {
                    if (ar.succeeded()) {
                        var mongoClient = MongoClient.createShared(
                                vertx, config()
                        );
                        logger.info("Connected with mongodb instance {}", mongoClient);

                        logger.info("Server running on port {}. Ready to accept connections",
                                port);
                        startPromise.complete();
                    } else {
                        logger.error("Error in starting up server. Error {}",
                                ar.cause().getMessage());
                        startPromise.fail(ar.cause().getMessage());
                    }
                });
    }

    public static void main(String[] args) {
        Vertx vertx1 = Vertx.vertx();

        var options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("port", 8085)
                        .put("connection_string", "mongodb+srv://mehulc:tl9LJE6ep9ld0hMk@cluster0.ufaw51h.mongodb.net/")
                        .put("db_name", "college_db")
                        .put("useObjectId", true)
                );
        var deployFuture = vertx1
                .deployVerticle(new MyHttpServerVerticle(), options);
        deployFuture.onSuccess(id ->
                logger.info("Verticle deployed!!!"));
        deployFuture.onFailure(err -> {
           logger.error("Error deploying verticle {}", err);
           vertx1.close();
        });
    }
}
