package stellaburgers.tests;

import stellaburgers.api.UserApi;
import stellaburgers.utils.BrowserDrivers;
import stellaburgers.utils.Urls;
import stellaburgers.dto.User;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.WebDriver;
import stellaburgers.operators.RegisterPage;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Регистрация пользователя")
@RunWith(JUnit4.class)
public class RegisterPageTests {
    private WebDriver driver;
    private RegisterPage registerPage;
    private String email, name, password;
    private BrowserDrivers browserDrivers;
    private User user;

    @Before
    @Step("Запуск браузера, подготовка тестовых данных")
    public void startUp() {
        browserDrivers = new BrowserDrivers();
        driver = browserDrivers.createDriver();
        driver.get(Urls.REGISTER_PAGE);
        registerPage = new RegisterPage(driver);

        email = "email_" + UUID.randomUUID() + "@gmail.com";
        name = "name";
        password = "pass_" + UUID.randomUUID();
        user = new User(email, password, name);
        Allure.addAttachment("Имя", name);
        Allure.addAttachment("Email", email);
        Allure.addAttachment("Пароль", password);
    }

    @After
    @Step("Закрытие браузера и очистка данных")
    public void tearDown() {
        driver.quit();
        new UserApi().deleteTestAppUser(user);
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void registerNewAppUserIsSuccessful() {
        Allure.parameter("Браузер", browserDrivers.getBrowserName()); // Используем здесь
        registerPage.setEmail(email);
        registerPage.setName(name);
        registerPage.setPassword(password);
        registerPage.clickRegisterButton();
        registerPage.waitFormSubmitted("Вход");
        checkFormReload();
    }

    @Test
    @DisplayName("Регистрация с коротким паролем")
    public void registerNewAppUserLowPasswordIsFailed() {
        Allure.parameter("Браузер", browserDrivers.getBrowserName());
        registerPage.setEmail(email);
        registerPage.setName(name);
        registerPage.setPassword(password.substring(0, 3));
        registerPage.clickRegisterButton();
        registerPage.waitErrorIsVisible();
        errorMessageCheck();
    }

    @Step("Проверка перезагрузки формы регистрации")
    private void checkFormReload() {
        MatcherAssert.assertThat(
                "Форма регистрации не перезагрузилась",
                driver.getCurrentUrl(),
                containsString("/login")
        );
    }

    @Step("Проверка появления сообщения об ошибке")
    private void errorMessageCheck() {
        MatcherAssert.assertThat(
                "Некорректное сообщение об ошибке",
                registerPage.getErrorMessage(),
                equalTo("Некорректный пароль")
        );
    }
}
