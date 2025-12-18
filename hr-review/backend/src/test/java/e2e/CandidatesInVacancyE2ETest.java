package e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CandidatesInVacancyE2ETest extends BaseE2ETest {

    @Test
    void createCandidate_inVacancy() {
        loginAsAdmin();

        click(By.cssSelector("a[href^='/vacancies/'][href$='/candidates']"));

        String name = "Candidate " + System.currentTimeMillis();
        type(byTestId("cand-create-name"), name);
        $(byTestId("cand-create-status")).sendKeys("NEW");
        click(byTestId("cand-create-submit"));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("table.table"), name
        ));
    }
}
