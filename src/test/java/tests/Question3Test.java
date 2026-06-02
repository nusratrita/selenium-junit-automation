package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import pages.*;
import utils.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Question 3 - DailyFinance end-to-end automation.
 * Site: https://dailyfinance.roadtocareer.net/
 *
 * Steps are ordered with @Order so state flows from one test to the next via
 * {@link TestContext}. Admin credentials are read from -Dadmin.email and
 * -Dadmin.password system properties. The password-reset link is pasted on the
 * terminal mid-run.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Question3Test {

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    void setUp() {
        driver  = DriverFactory.getDriver();
        baseUrl = ConfigReader.baseUrl();
    }

    @AfterAll
    void tearDown() {
        // Leave browser open after the suite, matching the project convention.
        // Uncomment to auto-close: DriverFactory.quitDriver();
    }

    // ------------------------------------------------------------------
    // Step 1 — Register a new user; assert registration success message.
    // ------------------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("TC-01 Register a new user and assert congratulations message")
    void step01_registerNewUser() {
        TestContext.registeredEmail  = RandomDataGenerator.gmailWithTag("nusratrita00");
        TestContext.originalPassword = "Pass@Coder2026";   // fixed password

        RegisterPage register = new RegisterPage(driver)
            .open(baseUrl)
            .fillFirstName("Nusrat")
            .fillLastName("Rita")
            .fillEmail(TestContext.registeredEmail)
            .fillPassword(TestContext.originalPassword)
            .fillPhone("01" + RandomDataGenerator.numeric(9))
            .fillAddress("Dhaka, Bangladesh")
            .selectGender("female")
            .acceptTerms();

        register.submit();
        ScreenshotUtil.capture(driver, "01_register_success");

        System.out.println("Registered email   : " + TestContext.registeredEmail);
        System.out.println("Generated password : " + TestContext.originalPassword);

        assertThat(register.isRegistrationSuccessMessageShown())
            .as("Congratulations / verification email message should appear")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 2 — Negative reset-password tests.
    // ------------------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("TC-02-NEG-A Forgot password with empty email is rejected")
    void step02a_forgotPasswordEmptyEmail() {
        ForgotPasswordPage page = new LoginPage(driver)
            .open(baseUrl)
            .clickForgotPassword()
            .fillEmail("")
            .submit();

        ScreenshotUtil.capture(driver, "02a_forgot_empty_email");

        assertThat(page.errorShown() || page.sendButtonDisabled() || !page.successShown())
            .as("Empty email must not produce a success message")
            .isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("TC-02-NEG-B Forgot password with invalid email format is rejected")
    void step02b_forgotPasswordInvalidFormat() {
        ForgotPasswordPage page = new ForgotPasswordPage(driver)
            .open(baseUrl)
            .fillEmail("not-an-email")
            .submit();

        ScreenshotUtil.capture(driver, "02b_forgot_invalid_format");

        assertThat(page.errorShown() || !page.successShown())
            .as("Malformed email must not produce a success message")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 3 — Input the valid registered Gmail and click Send Reset Link.
    // ------------------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("TC-03 Send reset link for the registered email")
    void step03_sendResetLink() {
        ForgotPasswordPage page = new ForgotPasswordPage(driver)
            .open(baseUrl)
            .fillEmail(TestContext.registeredEmail)
            .submit();

        ScreenshotUtil.capture(driver, "03_reset_link_sent");

        assertThat(page.successShown())
            .as("System should confirm a reset email was sent for " + TestContext.registeredEmail)
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 4 — Retrieve reset link from Gmail (manual paste) and set new password.
    // ------------------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("TC-04 Open reset link from Gmail and set a new password")
    void step04_setNewPassword() {
        String resetUrl = ConsoleHelper.promptForResetLink();
        assertThat(resetUrl)
            .as("A reset URL must be supplied via the console")
            .startsWith("http");

        TestContext.newPassword = "Pass@Reset2026";   // fixed new password (differs from original to prove the reset changed it)

        ResetPasswordPage page = new ResetPasswordPage(driver)
            .openLink(resetUrl)
            .setNewPassword(TestContext.newPassword);

        page.submit();
        ScreenshotUtil.capture(driver, "04_password_reset");

        System.out.println("New password is: " + TestContext.newPassword);
        assertThat(page.successShown() || driver.getCurrentUrl().contains("/login"))
            .as("Reset password flow should land on success state or login")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 5 — Login with the new password.
    // ------------------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("TC-05 Login with the new password succeeds")
    void step05_loginWithNewPassword() {
        DashboardPage dashboard = new LoginPage(driver)
            .open(baseUrl)
            .fillEmail(TestContext.registeredEmail)
            .fillPassword(TestContext.newPassword)
            .submitExpectingSuccess();

        ScreenshotUtil.capture(driver, "05_login_with_new_pwd");

        assertThat(dashboard.isLoaded())
            .as("Dashboard should load after a successful login")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 6 — Add 2 items and assert they both appear on the list.
    // ------------------------------------------------------------------
    @Test
    @Order(7)
    @DisplayName("TC-06 Add 2 items (mandatory only + all fields) and verify list")
    void step06_addTwoItems() {
        TestContext.firstItemName  = "FullItem_" + RandomDataGenerator.alphanumeric(5);
        TestContext.secondItemName = "MinItem_"  + RandomDataGenerator.alphanumeric(5);

        DashboardPage dash = new DashboardPage(driver);

        dash.openAddItem().saveAllFields(
            TestContext.firstItemName,
            RandomDataGenerator.numeric(3),
            "Auto-added item with all fields filled",
            "2026-01-15");

        ScreenshotUtil.capture(driver, "06a_full_item_added");

        dash.openAddItem().saveMandatoryOnly(
            TestContext.secondItemName,
            RandomDataGenerator.numeric(3));

        ScreenshotUtil.capture(driver, "06b_mandatory_item_added");

        ItemListPage list = new ItemListPage(driver);
        assertThat(list.containsItemNamed(TestContext.firstItemName))
            .as("Full-field item should appear in the list")
            .isTrue();
        assertThat(list.containsItemNamed(TestContext.secondItemName))
            .as("Mandatory-only item should appear in the list")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 7 — Update profile email to a new Gmail.
    // ------------------------------------------------------------------
    @Test
    @Order(8)
    @DisplayName("TC-07 Update profile email to a new Gmail address")
    void step07_updateProfileEmail() {
        TestContext.updatedEmail = RandomDataGenerator.gmailWithTag("nusratrita00");

        new DashboardPage(driver)
            .openProfile()
            .updateEmail(TestContext.updatedEmail);

        ScreenshotUtil.capture(driver, "07_profile_email_updated");

        // Verify the change actually persisted: reopen the profile and confirm the
        // email now shows the new address. (openProfile hard-refreshes the SPA form
        // so the update is submitted against a fully-loaded form.)
        String persistedEmail = new DashboardPage(driver).openProfile().currentEmail();
        System.out.println("Updated email saved as: " + persistedEmail);

        assertThat(persistedEmail)
            .as("Profile email should be updated and saved to the new address")
            .isEqualToIgnoringCase(TestContext.updatedEmail);
    }

    // ------------------------------------------------------------------
    // Step 8 — Logout, login with new email, verify old email fails.
    // ------------------------------------------------------------------
    @Test
    @Order(9)
    @DisplayName("TC-08-NEG Login with the previous email fails")
    void step08a_loginWithOldEmailFails() {
        new DashboardPage(driver).logout();

        LoginPage login = new LoginPage(driver)
            .open(baseUrl)
            .fillEmail(TestContext.registeredEmail)
            .fillPassword(TestContext.newPassword)
            .submitExpectingFailure();

        ScreenshotUtil.capture(driver, "08a_old_email_login_fails");

        assertThat(login.errorVisible() || login.stillOnLoginPage())
            .as("Login with the OLD email must fail after the profile email change")
            .isTrue();
    }

    @Test
    @Order(10)
    @DisplayName("TC-08 Login with the updated email succeeds")
    void step08b_loginWithUpdatedEmailSucceeds() {
        DashboardPage dash = new LoginPage(driver)
            .open(baseUrl)
            .fillEmail(TestContext.updatedEmail)
            .fillPassword(TestContext.newPassword)
            .submitExpectingSuccess();

        ScreenshotUtil.capture(driver, "08b_updated_email_login_ok");

        assertThat(dash.isLoaded())
            .as("Dashboard should load with the updated email")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 9 — Logout, login as admin with credentials from the terminal.
    // ------------------------------------------------------------------
    @Test
    @Order(11)
    @DisplayName("TC-09 Admin can log in using credentials passed from the terminal")
    void step09_adminLogin() {
        String adminEmail    = ConfigReader.getRequired("admin.email");
        String adminPassword = ConfigReader.getRequired("admin.password");

        new DashboardPage(driver).logout();

        new LoginPage(driver)
            .open(baseUrl)
            .fillEmail(adminEmail)
            .fillPassword(adminPassword)
            .submitExpectingSuccess();

        ScreenshotUtil.capture(driver, "09_admin_login_ok");

        // Admin lands on the /admin dashboard (a user table), not the /user view.
        assertThat(new AdminUsersPage(driver).isLoaded())
            .as("Admin dashboard (/admin) should load after admin login")
            .isTrue();
    }

    // ------------------------------------------------------------------
    // Step 10 — Search the updated user email on admin dashboard.
    // ------------------------------------------------------------------
    @Test
    @Order(12)
    @DisplayName("TC-10 Admin can search and see the updated user email")
    void step10_adminSearchUpdatedUser() {
        AdminUsersPage admin = new AdminUsersPage(driver)
            .searchBy(TestContext.updatedEmail);

        ScreenshotUtil.capture(driver, "10_admin_search_updated_user");

        assertThat(admin.rowsContain(TestContext.updatedEmail))
            .as("Updated email '%s' should appear in admin user table", TestContext.updatedEmail)
            .isTrue();
    }
}
