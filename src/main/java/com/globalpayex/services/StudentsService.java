package com.globalpayex.services;

import com.globalpayex.dao.StudentDao;
import com.globalpayex.exceptions.StudentAlreadyExistsException;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class StudentsService {

    private StudentDao studentDao;

    public StudentsService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public Future<List<JsonObject>> getAllStudents(JsonObject queryParamsObj) {

        var orConditions = new JsonArray();
        if (queryParamsObj.containsKey("gender")) {
            orConditions.add(
                    new JsonObject()
                            .put("gender", queryParamsObj
                                    .getString("gender")));
        }
        if (queryParamsObj.containsKey("country")) {
            orConditions.add(
                    new JsonObject()
                            .put("address.country", queryParamsObj
                                    .getString("country")));
        }
        var query = new JsonObject();
        if (!orConditions.isEmpty()) {
            query.put("$or", orConditions);
        }

        var studentsFuture = this.studentDao.findAll(
                query,
                new JsonObject()
        );
        var transformedFuture = studentsFuture.map(studentJsonObjects -> {
            return studentJsonObjects
                    .stream()
                    .map(studentJsonObject -> {
                        var username = studentJsonObject
                                .getString("username");
                        studentJsonObject.put("username", username.toUpperCase());
                        return studentJsonObject;
                    })
                    .collect(Collectors.toList());
        });

        return transformedFuture;
    }

    public Future<JsonObject> getStudentById(String id) {
        var studentFuture = this.studentDao.findById(id);
        var transformedFuture = studentFuture
                .map(studentJsonObject -> {
                    if (studentJsonObject != null) {
                        studentJsonObject.put(
                                "username",
                                studentJsonObject.getString("username").toUpperCase()
                        );
                    }
                    return studentJsonObject;
                });
        return transformedFuture;
    }

    public Future<JsonObject> create(JsonObject data) {
        // check for user with username existing or no
        var existingStudentFuture = this.studentDao.count(
                new JsonObject().put("username", data.getString("username"))
        );
        return existingStudentFuture.compose(studentCount -> {
           if (studentCount == 0) {
               var newStudentFuture = studentDao.insert(data);
               var transformedFuture =  newStudentFuture.map(newStudentId -> {
                   data.put("_id", newStudentId);
                   return data;
               });
               return transformedFuture;
           }
           return Future.failedFuture(
                   new StudentAlreadyExistsException(
                           String.format(
                                   "Student with username %s exists",
                                   data.getString("username")
                           )
                   )
           );
        });
    }

    public Future<JsonObject> authenticate(JsonObject data) {
        var authenticateFuture = this.studentDao.findAll(
                new JsonObject()
                        .put("username", data.getString("username"))
                        .put("password", data.getString("password")),
                new JsonObject()
                        .put("_id", 1)
                        .put("username", 1)
        );
        var transformedFuture = authenticateFuture
                .map(studentJsonObjects -> {
                    if (studentJsonObjects.isEmpty()) {
                        // invalid username or password
                        return null;
                    }
                    return studentJsonObjects.get(0);
                });
        return transformedFuture;
    }
}
