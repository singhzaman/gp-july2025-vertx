package com.globalpayex.services

import com.globalpayex.dao.StudentDao
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class StudentsServiceSpec extends Specification {

    def "test getStudentById() when student by id is found"() {
        given:
        var studentDaoStub = Stub(StudentDao)
        studentDaoStub.findById(_) >> Future.succeededFuture(
                new JsonObject()
                .put("_id", "121321")
                .put("username", "mehul25")
                .put("gender", "m")
        )
        var studentsService = new StudentsService(studentDaoStub)
        def usernameActual = new BlockingVariable<String>()
        def genderActual = new BlockingVariable<String>()

        when:
        studentsService.getStudentById("121321")
            .onSuccess(studentJsonObject -> {
                usernameActual.set(studentJsonObject.getString("username"))
                genderActual.set(studentJsonObject.getString("gender"))
            });

        then:
        usernameActual.get() == "MEHUL25"
        genderActual.get() == "m"
    }

    def "test getStudentById() when student by id is not found"() {
        given:
        var studentDaoStub = Stub(StudentDao)
        studentDaoStub.findById(_) >> Future.succeededFuture(null)
        var studentsService = new StudentsService(studentDaoStub)
        def result = new BlockingVariable<JsonObject>()

        when:
        studentsService.getStudentById("121321")
                .onSuccess(studentJsonObject -> {
                    result.set(studentJsonObject)
                });

        then:
        result.get() == null
    }

    def "test create() when for that student username there is no existing student"() {
        given:
        def newStudent = new JsonObject()
            .put("username", "mehul25")
            .put("gender", "m")

        var studentDaoStub = Stub(StudentDao)
        // stub the dao method
        studentDaoStub.count(_) >> Future.succeededFuture(0l)
        studentDaoStub.insert(_) >> Future.succeededFuture("12345")

        var studentsService = new StudentsService(studentDaoStub)
        def idActual = new BlockingVariable<String>()
        def usernameActual = new BlockingVariable<String>()

        when:
        studentsService.create(newStudent)
            .onSuccess(studentJsonObject -> {
               idActual.set(studentJsonObject.getString("_id"))
               usernameActual.set(studentJsonObject.getString("username"))
            });

        then:
        idActual.get() == "12345"
        usernameActual.get() == "mehul25"
    }
}
