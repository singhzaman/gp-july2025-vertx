import com.globalpayex.MyHttpServerVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions
import spock.util.concurrent.BlockingVariable

class StudentRoutesSpec extends Specification {

    @Shared
    Vertx vertx

    def setupSpec() {
        def asyncConditions = new AsyncConditions(1)
        this.vertx = Vertx.vertx();
        var options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("port", 8085)
                        .put("connection_string", "mongodb+srv://mehulc:tl9LJE6ep9ld0hMk@cluster0.ufaw51h.mongodb.net/")
                        .put("db_name", "college_db")
                        .put("useObjectId", true)
                );
        var deployFuture = this.vertx
                .deployVerticle(new MyHttpServerVerticle(), options);
        deployFuture.onSuccess(id -> {
            asyncConditions.evaluate {
                assert id != ""
            }
        });
        deployFuture.onFailure(err -> {
            asyncConditions.evaluate {
                assert false
            }
            this.vertx.close();
        });

        asyncConditions.await(5)
    }

    def cleanupSpec() {
        vertx.close();
    }

    def "test POST /students endpoint when student with username does not exist"() {
        given:
        def endpoint = "/students"
        var username = UUID.randomUUID().toString();
        def newStudent = new JsonObject()
            .put("username", username) // this should be generated randomly for every test run
            .put("password", "123")
            .put("gender", "m")
        def idActual = new BlockingVariable<String>(5)
        def usernameActual = new BlockingVariable<String>(5)
        def statusCodeActual = new BlockingVariable<Integer>(5)

        when:
        vertx.createHttpClient()
            .request(HttpMethod.POST, 8085, "localhost", endpoint)
            .onSuccess(request -> {
                request.send(newStudent.encode())
                    .onSuccess(response -> {
                        response.bodyHandler(buffer -> {
                            var newStudentObj = buffer.toJsonObject()
                            idActual.set(newStudentObj.getString("_id"))
                            usernameActual.set(newStudentObj.getString("username"))
                        });
                        statusCodeActual.set response.statusCode()
                    })
            })

        then:
        idActual.get() != ""
        usernameActual.get() == username
        statusCodeActual.get() == 201
    }
}
