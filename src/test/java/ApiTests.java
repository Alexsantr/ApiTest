import io.restassured.RestAssured;
import models.CheckUserModel;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import models.UpdateUserResponseModel;

import org.junit.jupiter.api.*;


import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        CheckUserModel response = step(
                "Проверка наличия пользователя", () ->
                        given(requestSpec)
                                .get("/users/1")
                                .then()
                                .spec(statusCode200Spec)
                                .extract().as(CheckUserModel.class));

        step("Проверяем данных пользователя", () ->
        {
            Assertions.assertAll(
                    () -> assertThat(response.getData().getId()).isEqualTo(1),
                    () -> assertThat(response.getData().getEmail()).isEqualTo("george.bluth@reqres.in"),
                    () -> assertThat(response.getData().getFirst_name()).isEqualTo("George"),
                    () -> assertThat(response.getData().getLast_name()).isEqualTo("Bluth")
            );


        });


    }


    @Test
    @DisplayName("Проверка отсутствия пользователя")
    void unSingleUsersHaveTest() {
        String respose = step(
                "Проверка отсутствия пользователя", () ->
                        given(requestSpec)
                                .get("/users/213")
                                .then()
                                .spec(statusCode404Spec)
                                .extract()
                                .asString());
        assertThat(respose).isEqualTo("{}");


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

        });

    }
}
