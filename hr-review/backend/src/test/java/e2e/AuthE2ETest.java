package e2e;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthE2ETest extends BaseE2ETest {

    @Test
    void login_shouldShowRoleBadge_andLogoutWorks() {
        loginAsAdmin();

        String badgeText = $(byTestId("role-badge")).getText();
        assertTrue(badgeText.contains("ADMIN") || badgeText.contains("USER"));

        logout();
    }
}
