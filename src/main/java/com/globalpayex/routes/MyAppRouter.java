package com.globalpayex.routes;

import com.globalpayex.domain.Book;
import io.vertx.core.Vertx;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class MyAppRouter {

    private static final Logger logger = LoggerFactory
            .getLogger(MyAppRouter.class);

    public static Router init(Vertx vertx, JsonObject config) {
        var router = Router.router(vertx);
        router.get("/hello/:fname")
                .handler(MyAppRouter::handleHello);
        // this will ensure that vertx collects and parses
        // request data in a JsonObject for all POST calls
        router.post().handler(BodyHandler.create());

        router = BooksRoute.init(vertx, router);
        router = StudentsRoute.init(vertx, router, config);
        router.get("/download").handler(routingContext ->
                handleDownload(vertx, routingContext));

        return router;
    }

    private static void handleDownload(Vertx vertx, RoutingContext routingContext) {
        var filePath = "/Users/mehulchopra/Downloads/july2025-training.mp3";

        var response = routingContext
                .response()
                .setStatusCode(200)
                .putHeader("Content-Type", "audio/mp3")
                .putHeader("Content-Disposition", "attachment")
                .setChunked(true);

        var fileFuture = vertx.fileSystem()
                .open(filePath, new OpenOptions().setRead(true));
        fileFuture.onSuccess(asyncFile -> {
            asyncFile.pipeTo(response); // internally ensures streaming back pressure

            // manual backpressure implementation
            /* asyncFile.handler(buffer -> {
                response.write(buffer);
                if (response.writeQueueFull()) {
                    asyncFile.pause();
                }
                response.drainHandler(v -> asyncFile.resume());
            });
            asyncFile.endHandler(v -> {
                response.end();
            }); */
        });
        fileFuture.onFailure(err -> {
            logger.error("Error in reading file {}", err);
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end(err.getMessage());
        });
    }

    private static void handleHello(RoutingContext routingContext) {
        var fname = routingContext.pathParam("fname");
        routingContext
                .response()
                .end("Helloooo " + fname);
    }
}
