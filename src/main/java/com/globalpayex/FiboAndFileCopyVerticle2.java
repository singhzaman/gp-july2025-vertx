package com.globalpayex;

import com.globalpayex.utils.Series;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class FiboAndFileCopyVerticle2 extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(FiboAndFileCopyVerticle2.class);

    // seperate out each asynchronous concern into its own function
    private Future<Buffer> readFileAsync(String path, FileSystem fileSystem) {
        Promise<Buffer> promise =  Promise.promise();
        fileSystem.readFile(path, (readAsyncResult) -> {
            if (readAsyncResult.succeeded()) {
                var fileContent = readAsyncResult.result();
                promise.complete(fileContent);
            } else {
                promise.fail(readAsyncResult.cause().getMessage());
            }
        });
        return promise.future();
    }

    private Future<Integer> writeFileAsync(String path, Buffer content, int element,
                                           FileSystem fileSystem) {
        Promise<Integer> promise = Promise.promise();
        fileSystem.writeFile(path, content, writeAsyncResult -> {
            if (writeAsyncResult.succeeded()) {
                promise.complete(element);
            } else {
                promise.fail(writeAsyncResult.cause().getMessage());
            }
        });
        return promise.future();
    }

    @Override
    public void start() throws Exception {
        var list = Arrays.asList(4, 6, 8, 15, 20);
        var fileSystem = vertx.fileSystem();
        var path = "src/main/java/com/globalpayex/MultiOperationsVerticle.java";
        var destDirPath = "/Users/mehulchopra/Documents/gp-temp";

        list.forEach(element -> {
            var readFuture = readFileAsync(path, fileSystem);
            var finalFuture = readFuture.compose(buffer -> {
                return writeFileAsync(
                        destDirPath + "/" + element + ".txt",
                        buffer,
                        element,
                        fileSystem
                );
            });
            finalFuture.onSuccess(ele -> {
                logger.info("Copy success for {}", ele);
            });
            finalFuture.onFailure(err -> {
                logger.error("Copy failure");
            });

            var series = Series.generate(element);
            logger.info("Fibo series for {} is {}", element, series);
        });
    }

    public static void main(String[] args) {
        Vertx vertx1 = Vertx.vertx();
        vertx1.deployVerticle(new FiboAndFileCopyVerticle2());
    }
}
