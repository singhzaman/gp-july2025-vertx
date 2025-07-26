package com.globalpayex;

import com.globalpayex.dao.StudentDao;
import com.globalpayex.services.StudentsService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsVerticle extends AbstractVerticle {

    private Map<String, Long> groupedGenderCount;

    private static final Logger logger = LoggerFactory
            .getLogger(StatisticsVerticle.class);

    @Override
    public void start() throws Exception {
        var studentService = new StudentsService(
                new StudentDao(vertx, config())
        );
        // define handler to read message from destination "new.student" on the event bus
        vertx.eventBus()
                .<JsonObject>consumer("new.student", message -> {
                    var newStudent = message.body();
                    logger.info("new student just created with id {}", newStudent.getString("_id"));

                    var studentsFuture = studentService
                            .getAllStudents(new JsonObject());
                    studentsFuture.onSuccess(studentJsonObjects -> {
                        var groupedData = studentJsonObjects
                                .stream()
                                .collect(Collectors.groupingBy(
                                        studentJson -> studentJson.getString("gender"),
                                        Collectors.counting()
                                ));
                        this.groupedGenderCount = groupedData;
                        logger.info("Student statistics as of now is {}", groupedData);
                    });
                    studentsFuture.onFailure(err -> {
                        logger.error("error {}", err.getMessage());
                    });
                });
        vertx.eventBus()
                .<JsonObject>consumer("student.stats", this::handleStudentStatsRequest);

    }

    private void handleStudentStatsRequest(Message<JsonObject> message) {
        var responseJsonObject = new JsonObject()
                .put("male", groupedGenderCount.get("m"))
                .put("female", groupedGenderCount.get("f"));
        message.reply(responseJsonObject);
    }
}
