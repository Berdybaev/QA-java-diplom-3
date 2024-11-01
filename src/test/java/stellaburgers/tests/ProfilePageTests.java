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
import stellaburgers.operators.AuthenticationPage;
import stellaburgers.operators.MainPage;
import stellaburgers.operators.ProfilePage;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;

@DisplayName("Проверки личного кабинета пользователя")
@RunWith(JUnit4.class)
public class ProfilePageTests {
    private WebDriver driver;
    private AuthenticationPage authPage;
    private MainPage mainPage;
    private ProfilePage profilePage;
    private String name, email, password;
    private UserApi userApi;
    private BrowserDrivers browserDrivers;
    private User user;
    @Before
    @Step("Запуск браузера, подготовка тестовых данных")
    public void startUp() {
        browserDrivers = new BrowserDrivers();
        driver = browserDrivers.createDriver();
        driver.get(Urls.MAIN_PAGE);

        authPage = new AuthenticationPage(driver);
        mainPage = new MainPage(driver);
        profilePage = new ProfilePage(driver);

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
    @Step("Закрытие браузера и очистка данных")
    public void tearDown() {
        driver.quit();
        userApi.deleteTestAppUser(user);
    }

    @Step("Процесс авторизации")
    private void authAppUser() {
        authPage.setEmail(user.getEmail());
        authPage.setPassword(user.getPassword());
        authPage.clickAuthButton();
        authPage.waitFormSubmitted();
    }

    @Step("Переход в личный кабинет")
    private void goToProfile() {
        driver.get(Urls.LOGIN_PAGE);
        authPage.waitAuthFormVisible();
        authAppUser();
        mainPage.clickLinkToProfile();
        profilePage.waitAuthFormVisible();
    }

    @Test
    @DisplayName("Проверка перехода по клику на «Личный кабинет»")
    public void testLinkToProfileIsSuccessful() {
        Allure.parameter("Браузер", browserDrivers.getBrowserName());
        goToProfile();

        MatcherAssert.assertThat(
                "Некорректный URL страницы Личного кабинета",
                driver.getCurrentUrl(),
                containsString("/account/profile")
        );
    }

}
