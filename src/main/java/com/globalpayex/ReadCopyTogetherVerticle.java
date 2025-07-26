package com.globalpayex;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class ReadCopyTogetherVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(ReadCopyTogetherVerticle.class);

    private static final String FILE_PATH =
            "src/main/java/com/globalpayex/MultiOperationsVerticle.java";

    private Future<Buffer> readFileAsync() {
        Promise<Buffer> promise = Promise.promise();
        vertx
                .fileSystem()
                .readFile(FILE_PATH, ar -> {
                    if (ar.succeeded()) {
                        promise.complete(ar.result());
                    } else {
                        promise.fail(ar.cause().getMessage());
                    }
                });
        return promise.future();
    }

    private Future<Void> copyFileAsync() {
        var destDirPath = "/Users/mehulchopra/Documents/gp-temp";
        Promise<Void> promise = Promise.promise();
        vertx
                .fileSystem()
                .copy(
                        FILE_PATH,
                        destDirPath + "/MultiOperationsVerticle.java",
                        (ar) -> {
                            if (ar.succeeded()) {
                                promise.complete();
                            } else {
                                promise.fail(ar.cause().getMessage());
                            }
                        }
                );
        return promise.future();
    }

    @Override
    public void start() throws Exception {
        // Future<Buffer> f1 = readFileAsync();
        // Future<Void> f2 =  copyFileAsync();

        var config = config();
        var filePath = config.getString("filePath");
        logger.info("File path: {}", filePath);

        var destDirPath = config.getString("destDirPath");
        Future<Buffer> f1 = vertx
                .fileSystem()
                .readFile(filePath);
        Future<Void> f2 = vertx
                .fileSystem()
                .copy(filePath, destDirPath + "/MultiOperationsVerticle.java");

        var compositeFuture = Future.all(f1, f2);
        compositeFuture.onSuccess(handler -> {
            // will be called when all the futures succeed
            var f1Success = handler.resultAt(0);
            logger.info("F1 future result" + f1Success);
        });
        compositeFuture.onFailure(err -> {
            // will be called when any of the future fails
            logger.error(err.getMessage());
        });
    }

    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)) {
            System.out.println("enter ur fav file absolute path: ");
            var filePath = scanner.nextLine();

            Vertx vertx1 = Vertx.vertx();
            DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setConfig(new JsonObject()
                            .put("filePath", filePath)
                            .put("destDirPath", "/Users/mehulchopra/Documents/gp-temp")
                    );

            vertx1.deployVerticle(new ReadCopyTogetherVerticle(), deploymentOptions);
        }
    }
}
