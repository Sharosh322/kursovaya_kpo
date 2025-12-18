package e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class VacanciesE2ETest extends BaseE2ETest {

    @Test
    void vacancies_filters_and_createVacancy() {
        loginAsAdmin();

        type(byTestId("vacancy-search"), "Data");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table")));

        $(byTestId("vacancy-status-filter")).sendKeys("OPEN"); // select
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table")));

        click(byTestId("vacancy-reset"));
        assertEquals("", $(byTestId("vacancy-search")).getAttribute("value"));

        String title = "QA Vacancy " + System.currentTimeMillis();
        type(byTestId("vacancy-create-title"), title);
        $(byTestId("vacancy-create-status")).sendKeys("OPEN");
        click(byTestId("vacancy-create-submit"));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("table.table"), title
        ));
    }
}
