package stellaburgers.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import stellaburgers.api.UserApi;
import stellaburgers.dto.User;
import stellaburgers.operators.AuthenticationPage;
import stellaburgers.operators.ForgotPasswordPage;
import stellaburgers.operators.MainPage;
import stellaburgers.operators.RegisterPage;
import stellaburgers.utils.BrowserDrivers;
import stellaburgers.utils.Urls;


import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

@DisplayName("Авторизация пользователя")
public class
AuthorizationTests {
    private WebDriver driver;
    private AuthenticationPage loginPage;
    private MainPage mainPage;
    private RegisterPage registerPage;
    private ForgotPasswordPage forgotPasswordPage;
    private String name;
    private String email;
    private String password;
    private UserApi userApi;
    private User user;

    @Before
    @Step("Запуск браузера, подготовка тестовых данных")
    public void startUp() throws InterruptedException {
        BrowserDrivers browserDrivers = new BrowserDrivers();
        driver = browserDrivers.createDriver();
        driver.get(Urls.MAIN_PAGE);

        loginPage = new AuthenticationPage(driver);
        mainPage = new MainPage(driver);
        registerPage = new RegisterPage(driver);
        forgotPasswordPage = new ForgotPasswordPage(driver);

        name = "name";
        email = "email_" + UUID.randomUUID() + "@gmail.com";
        password = "pass_" + UUID.randomUUID();

        Allure.addAttachment("Имя", name);
        Allure.addAttachment("Email", email);
        Allure.addAttachment("Пароль", password);
        user = new User(email, password, name);
        userApi = new UserApi();
        userApi.createAppUser(user);
    }

    @After
    @Description("Закрытие браузера и удаление данных")
    public void tearDown() {
        driver.quit();
        userApi.deleteTestAppUser(user);
    }

    @Step("Авторизация")
    private void authAppUser() {
        loginPage.setEmail(user.getEmail());
        loginPage.setPassword(user.getPassword());

        loginPage.clickAuthButton();
        loginPage.waitFormSubmitted();
    }

    @Test
    @DisplayName("Вход по кнопке «Войти в аккаунт» на главной")
    public void loginViaMainIsSuccessful() {
        Allure.parameter("Браузер", driver.getClass().getSimpleName());

        mainPage.clickAuthButton();
        loginPage.waitAuthFormVisible();

        authAppUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }

    @Test
    @DisplayName("Вход через кнопку «Личный кабинет»")
    public void loginViaProfileIsSuccessful() {
        Allure.parameter("Браузер", driver.getClass().getSimpleName());

        mainPage.clickLinkToProfile();
        loginPage.waitAuthFormVisible();

        authAppUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }

    @Test
    @DisplayName("Вход через кнопку в форме регистрации")
    public void loginViaRegFormIsSuccessful() {
        Allure.parameter("Браузер", driver.getClass().getSimpleName());

        driver.get(Urls.REGISTER_PAGE);

        registerPage.clickAuthLink();
        loginPage.waitAuthFormVisible();

        authAppUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }

    @Test
    @DisplayName("Вход через кнопку в форме восстановления пароля")
    public void loginViaFromForgotPasswordFormIsSuccessful() {
        Allure.parameter("Браузер", driver.getClass().getSimpleName());

        driver.get(Urls.FORGOT_PASSWORD_PAGE);

        forgotPasswordPage.clickAuthLink();
        loginPage.waitAuthFormVisible();

        authAppUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }
}
