package com.globalpayex;

import com.globalpayex.utils.Series;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexFiboSeriesVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(ComplexFiboSeriesVerticle.class);

    @Override
    public void start() throws Exception {
        var element = config().getInteger("element");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var series = Series.generate(element);
        logger.info("Fibo series for {} is {}", element, series);
    }
}
