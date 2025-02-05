import io.restassured.RestAssured;
import models.CheckUserModel;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import models.UpdateUserResponseModel;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static spec.TestSpec.*;

@Tag("Api_tests")
public class ApiTests {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Проверка наличия пользователя")
    void singleUsersHaveTest() {
        step(
                "Проверка наличия пользователя", () ->
                        given(requestSpec)
                                .get("/users/2")
                                .then()
                                .spec(statusCode200Spec));
        Assertions.assertNotNull(requestSpec);
    }


    @Test
    @DisplayName("Проверка отсутствия пользователя")
    void unSingleUsersHaveTest() {
        step(
                "Проверка отсутствия пользователя", () ->
                        given(requestSpec)
                                .get("/users/213")
                                .then()
                                .spec(statusCode404Spec)
                                .body(Matchers.anything()));


    }

    @Test
    @DisplayName("Проверка данный пользователя")
    void singleUsersHaveDataTest() {
        CheckUserModel response = step(
                "Запрос данных пользователя", () ->
                        given(requestSpec)
                                .get("/users/2")
                                .then()
                                .spec(statusCode200Spec)
                                .extract().as(CheckUserModel.class));
        step("Проверяем данных пользователя", () -> {
            assertThat(response.getData().getId()).isEqualTo(2);
            assertThat(response.getData().getEmail()).isEqualTo("janet.weaver@reqres.in");
            assertThat(response.getData().getFirst_name()).isEqualTo("Janet");
            assertThat(response.getData().getLast_name()).isEqualTo("Weaver");


            Assertions.assertEquals(2, response.getData().getId(), "ID пользователя должно быть 2");
            Assertions.assertEquals("janet.weaver@reqres.in", response.getData().getEmail(), "Email пользователя не совпадает");
            Assertions.assertEquals("Janet", response.getData().getFirst_name(), "Имя пользователя не совпадает");
            Assertions.assertEquals("Weaver", response.getData().getLast_name(), "Фамилия пользователя не совпадает");

        });


    }


    @Test
    @DisplayName("Создание пользователя с пустыми данными")
    void createSuccessEmptyUserTest() {
        CreateUserRequestModel userData = new CreateUserRequestModel();
        userData.setName("");
        userData.setJobs("");

        step("Создаем пользователя с пустыми данными", () ->
                given(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(statusCode201Spec));
        Assertions.assertNotNull(userData, "Данные в ответе не должны быть null");
    }

    @Test
    @DisplayName("Создание пользователя с  данными name and jod")
    void createSuccessWithDataUserTest() {

        CreateUserRequestModel userData = new CreateUserRequestModel();

        userData.setName("Alex");
        userData.setJobs("QR");

        CreateUserResponseModel response = step(
                "Создание пользователя",
                () -> given(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(statusCode201Spec)
                        .extract().as(CreateUserResponseModel.class));

        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getName()).isEqualTo("Alex");
            assertThat(response.getJobs()).isEqualTo("QR");
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCreatedAt()).isNotNull();

            Assertions.assertEquals("Alex", response.getName(), "Имя пользователя не совпадает");
            Assertions.assertEquals("QR", response.getJobs(), "Работа пользователя не совпадает");
            Assertions.assertNotNull(response.getId(), "ID пользователя не должен быть null");
            Assertions.assertNotNull(response.getCreatedAt(), "Дата создания не должна быть null");
        });
    }

    @Test
    @DisplayName("Изменение данных пользователя с использованием id из предыдущего запроса")
    void succsessUpdateUserDataTest() {

        CreateUserRequestModel userData = new CreateUserRequestModel();
        userData.setName("Alex");
        userData.setJobs("QR");

        CreateUserResponseModel responseModel = step(
                "Создание пользователя",
                () -> given(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(statusCode201Spec)
                        .extract().as(CreateUserResponseModel.class));

        CreateUserRequestModel updateData = new CreateUserRequestModel();
        updateData.setName("Alexander");
        updateData.setJobs("QA Automation");
        UpdateUserResponseModel response = step(
                "Обновление данных пользователя",
                () -> given(requestSpec)
                        .body(updateData)
                        .when()
                        .put("/users" + "/" + responseModel.getId())
                        .then()
                        .spec(statusCode200Spec)
                        .extract().as(UpdateUserResponseModel.class));

        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getName()).isEqualTo("Alexander");
            assertThat(response.getJobs()).isEqualTo("QA Automation");
            assertThat(response.getUpdatedAt()).isNotNull();

            Assertions.assertEquals("Alexander", response.getName(), "Имя пользователя не совпадает");
            Assertions.assertEquals("QA Automation", response.getJobs(), "Работа пользователя не совпадает");
            assertNotNull(response.getUpdatedAt(), "Дата обновления не должна быть null");
        });

    }
}
