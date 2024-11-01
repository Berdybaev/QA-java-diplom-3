package stellaburgers.api;



import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import stellaburgers.dto.User;
import stellaburgers.dto.UserJson;
import stellaburgers.utils.Urls;

import static io.restassured.RestAssured.given;

public class UserApi {


    private RequestSpecification baseRequest() {
        return new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .setContentType("application/json")
                .setRelaxedHTTPSValidation()
                .build();
    }


    private Response sendRequest(String endpoint, Object body) {
        return given(baseRequest())
                .body(body)
                .when()
                .post(endpoint)
                .thenReturn();
    }

    @Step("Авторизация пользователя")
    public Response loginAppUser(User user) {
        return sendRequest(Urls.LOGIN_API, user);
    }

    @Step("Создание пользователя")
    public void createAppUser(User user) {
        sendRequest(Urls.CREATE_USER_API, user).getStatusCode();
    }

    @Step("Удаление пользователя по токену")
    private void deleteUser(String token) {
        given(baseRequest())
                .auth().oauth2(token)
                .when()
                .delete(Urls.DELETE_USER_API);
    }

    @Step("Удаление тестового пользователя")
    public void deleteTestAppUser(User user) {
        Response response = loginAppUser(user);

        if (response.getStatusCode() == 200) {
            String token = response.body().as(UserJson.class).getAccessToken().split(" ")[1];
            deleteUser(token);
        }
    }
}
