package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationsVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(MultiOperationsVerticle.class);

    @Override
    public void start() throws Exception {
        // init code for this verticle
        var a = 9;
        var b = 5;

        // read from file (IO)
        // non blocking IO
        var fileSystem = vertx.fileSystem();

        var path = "src/main/java/com/globalpayex/MultiOperationsVerticle.java";
        // async non blockingIO
        fileSystem.readFile(path, (ar) -> {
           if (ar.succeeded()) {
               var content = ar.result().toString();
               logger.info(content);
           } else {
               logger.error(ar.cause().getMessage());
           }
        });

        var sum = a + b;
        var mul = a * b;

        // simulate
        // complex operation
        // Never ever block ur event loop thread of vertx
        // do not ever writing long running, blocking IO code in the event loop
        // thread of vertx
        // Thread.sleep(30000);

        logger.info("Sum of {} and {} is {}", a, b, sum);
        logger.info("Mul of {} and {} is {}", a, b, mul);
    }

    public static void main(String[] args) {
        Vertx vertx1 = Vertx.vertx();
        vertx1.deployVerticle(new MultiOperationsVerticle());

        logger.info("MultiOperationsVerticle has been deployed!!!");
    }
}
