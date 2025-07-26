package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TemperatureVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(TemperatureVerticle.class);

    private List<Integer> temperatures = new ArrayList<>();

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(5000, id -> {
            var random = new Random();
            temperatures.add(random.nextInt(31));
        });
        vertx.setPeriodic(15000, id -> {
            var average = temperatures
                    .stream()
                    .collect(Collectors.averagingInt(temp -> temp));
            logger.info("Average is {}", average);
        });
    }

    public static void main(String[] args) {
        Vertx
                .vertx()
                .deployVerticle(new TemperatureVerticle());
    }
}
