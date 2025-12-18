package e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseE2ETest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    protected final String BASE_URL = "http://localhost:5173";
    protected final String ADMIN_EMAIL = "andrew.razin@inbox.ru";
    protected final String ADMIN_PASS  = "123";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1400, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    protected By byTestId(String id) {
        return By.cssSelector("[testid='" + id + "'],[data-testid='" + id + "']");
    }

    protected WebElement $(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement el = $(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected void open(String path) {
        driver.get(BASE_URL + path);
    }

    protected void loginAsAdmin() {
        open("/login");
        type(byTestId("login-email"), ADMIN_EMAIL);
        type(byTestId("login-password"), ADMIN_PASS);
        click(byTestId("login-submit"));

        $(byTestId("vacancy-search"));
    }

    protected void logout() {
        click(byTestId("logout-btn"));
        open("/login");
        $(byTestId("login-email"));
    }
}
