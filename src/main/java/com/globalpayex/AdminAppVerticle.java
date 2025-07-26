package com.globalpayex;

import com.globalpayex.routes.MyAdminRouter;
import com.globalpayex.routes.MyAppRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminAppVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(AdminAppVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var port = config().getInteger("adminAppPort");
        vertx
                .createHttpServer()
                .requestHandler(
                        MyAdminRouter.init(vertx, config())
                )
                .listen(port, ar -> {
                    if (ar.succeeded()) {
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
}
