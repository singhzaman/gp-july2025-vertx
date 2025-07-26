package com.globalpayex;

import com.globalpayex.utils.Series;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class FiboAndFileCopyVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(FiboAndFileCopyVerticle.class);

    @Override
    public void start() throws Exception {
        var list = Arrays.asList(4, 6, 8, 15, 20);
        var fileSystem = vertx.fileSystem();
        var path = "src/main/java/com/globalpayex/MultiOperationsVerticle.java";
        var destDirPath = "/Users/mehulchopra/Documents/gp-temp";

        list.forEach(element -> {
            // copy
            // non blocking IO
            // async code
            fileSystem.readFile(path, (readAsyncResult) -> {
                if (readAsyncResult.succeeded()) {
                    var content = readAsyncResult.result();
                    fileSystem.writeFile(
                            destDirPath + "/" + element + ".txt",
                            content,
                            (writeAsyncResult) -> {
                                if (writeAsyncResult.succeeded()) {
                                    logger.info("Copy success for {}", element);
                                } else {
                                    logger.error("Copy failure during write for {}",
                                            element);
                                }
                            }
                    );
                } else {
                    logger.error("Copy failure during read for {}", element);
                }
            });

            /* var series = Series.generate(element);
            logger.info("Fibo series for {} is {}", element, series); */

            // complex fibo series generation
            // takes some time
            // Block the event loop
            /* try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            var series = Series.generate(element);
            logger.info("Fibo series for {} is {}", element, series); */
            /* var options = new DeploymentOptions()
                    .setConfig(new JsonObject().put("element", element))
                            .setThreadingModel(ThreadingModel.WORKER);
            vertx
                    .deployVerticle(new ComplexFiboSeriesVerticle(), options); */

            var seriesFuture = vertx
                    .executeBlocking(() -> {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        var series = Series.generate(element);
                        logger.info("Fibo series in blocking", series);
                        return series;
                    }, false);
            seriesFuture.onSuccess(seriesStr -> {
                logger.info("Fibo series for {} is {}", element, seriesStr);
            });
        });
    }

    public static void main(String[] args) {
        Vertx vertx1 = Vertx.vertx();

        /* var options = new DeploymentOptions()
                .setInstances(2); */
        vertx1.deployVerticle(new FiboAndFileCopyVerticle());

        // pass fully qualified name when more than 1 instance
        // vertx1.deployVerticle("com.globalpayex.FiboAndFileCopyVerticle", options);
    }
}
