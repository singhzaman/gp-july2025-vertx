package com.globalpayex.routes;

import com.globalpayex.dao.StudentDao;
import com.globalpayex.exceptions.StudentAlreadyExistsException;
import com.globalpayex.services.StudentsService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudentsRoute {

    private static final Logger logger = LoggerFactory
            .getLogger(StudentsRoute.class);

    private static StudentsService studentsService;

    public static Router init(
            Vertx vertx,
            Router router,
            JsonObject config) {
        studentsService = new StudentsService(new StudentDao(vertx, config));

        router.get("/students")
                .handler(StudentsRoute::handleGetAllStudents);
        router.get("/students/:id")
                .handler(StudentsRoute::handleGetStudent);
        router.post("/students")
                .handler(routingContext -> handleNewStudent(vertx, routingContext));

        return router;
    }

    private static void handleNewStudent(Vertx vertx, RoutingContext routingContext) {
        var studentData = routingContext.body().asJsonObject();
        var newStudentFuture = studentsService
                .create(studentData);
        newStudentFuture.onSuccess(newStudent -> {
            // send message to destination
            // "new.student"
            vertx.eventBus()
                            .publish(
                                    "new.student",
                                    new JsonObject().put("_id", newStudent.getString("_id"))
                            );

            routingContext
                   .response()
                   .setStatusCode(201)
                   .end(newStudent.encode());

        });
        newStudentFuture.onFailure(err -> {
            if (err instanceof StudentAlreadyExistsException) {
                routingContext
                        .response()
                        .setStatusCode(400)
                        .end(err.getMessage());
            } else {
                routingContext
                        .response()
                        .setStatusCode(500)
                        .end("Something went wrong");
            }
        });
    }

    private static void handleGetStudent(RoutingContext routingContext) {
        var studentId = routingContext.pathParam("id");
        var studentFuture = studentsService.getStudentById(studentId);
        studentFuture.onSuccess(studentJsonObject -> {
           if (studentJsonObject == null) {
               routingContext
                       .response()
                       .setStatusCode(404)
                       .end("Student with id " + studentId + " not found");
           } else {
               routingContext
                       .response()
                       .putHeader("Content-Type", "application/json")
                       .end(studentJsonObject.encode());
           }
        });
        studentFuture.onFailure(err -> {
           routingContext
                   .response()
                   .setStatusCode(500)
                   .end("Something went wrong");
        });
    }

    private static void handleGetAllStudents(RoutingContext routingContext) {
        var queryParamsObj = new JsonObject();
        var gender = routingContext.queryParam("gender");
        if (!gender.isEmpty()) {
            queryParamsObj.put("gender", gender.get(0));
        }

        var country = routingContext.queryParam("country");
        if (!country.isEmpty()) {
            queryParamsObj.put("country", country.get(0));
        }

        // query for all the documents from "students" collection
        var findStudentsFuture = studentsService
                .getAllStudents(queryParamsObj);

        findStudentsFuture.onSuccess(studentsJsonObjects -> {
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonArray(studentsJsonObjects).encode());
        });
        findStudentsFuture.onFailure(err -> {
            logger.error(err.getMessage());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Something went wrong");
        });
    }
}
