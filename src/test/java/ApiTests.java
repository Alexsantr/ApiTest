import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class ApiTests {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Проверка наличия пользователя")
    void singleUsersHaveTest() {
        given()
                .log().uri()
                .get("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка отсутствия пользователя")
    void unSingleUsersHaveTest() {
        given()
                .log().uri()
                .get("/users/213")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка данный пользователя")
    void singleUsersHaveDataTest() {
        given()
                .log().uri()
                .get("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.id", is(2))
                .body("data.email", is("janet.weaver@reqres.in"));
    }

    @Test
    @DisplayName("Создание пользователя с пустыми данными")
    void createSuccessEmptyUserTest() {
        String userData = "";
        given()
                .body(userData)
                .when()
                .log().uri()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201);
    }

    @Test
    @DisplayName("Создание пользователя с  данными name and jod")
    void createSuccessWithDataUserTest() {
        String userData = """
                {"name": "Alex",
                "job": "QR"
                }""";
        given()
                .body(userData)
                .contentType(ContentType.JSON)
                .when()
                .log().uri()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("Alex"))
                .body("job", is("QR"));


    }

    @Test
    @DisplayName("Изменение данных пользователя с использованием id из предыдущего запроса")
    void succsessUpdateUserDataTest() {

        String userData = """
                {"name": "Alex","job": "QR"}""";

        String userId = given()
                .body(userData)
                .contentType(ContentType.JSON)
                .when()
                .log().uri()
                .post("/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .extract()
                .path("id");

        String updatedUserData = "{\"name\": \"Alexander\",\"job\": \"QA Automation\"}";

        given()
                .body(updatedUserData)
                .contentType(ContentType.JSON)
                .when()
                .log().uri()
                .patch("/users/" + userId)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", is("Alexander"))
                .body("job", is("QA Automation"));
    }

    @Test
    @DisplayName("Удаление созданого пользователя")
    void SuccessDeleteUserTest() {
        String userData = """
                {"name": "Alex",
                "job": "QR"
                }""";
        String userid = given()
                .body(userData)
                .contentType(ContentType.JSON)
                .when()
                .log().uri()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .log().uri()
                .delete("/users/" + userid)
                .then()
                .log().status()
                .log().body()
                .statusCode(204);


    }
}