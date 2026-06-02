package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * The logged-in landing page at /user. The top bar has an account icon button
 * that opens a dropdown menu (Profile / Logout) and an "Add Cost" button that
 * navigates to the /add-cost form.
 */
public class DashboardPage extends BasePage {

    private final By accountIcon = By.cssSelector("button[aria-label='account of current user']");
    private final By addCostBtn  = By.cssSelector("button.add-cost-button");
    private final By profileItem = By.xpath("//li[contains(@class,'MuiMenuItem-root') and contains(translate(., 'PROFILE', 'profile'), 'profile')]");
    private final By logoutItem  = By.xpath("//li[contains(@class,'MuiMenuItem-root') and contains(translate(., 'LOGOUT', 'logout'), 'logout')]");

    public DashboardPage(WebDriver driver) { super(driver); }

    public boolean isLoaded() {
        try {
            waitVisible(addCostBtn);
            return true;
        } catch (RuntimeException e) {
            return driver.getCurrentUrl().contains("/user");
        }
    }

    public AddItemPage openAddItem() {
        click(addCostBtn);
        return new AddItemPage(driver);
    }

    public ProfilePage openProfile() {
        click(accountIcon);
        click(profileItem);
        // Hard-refresh once on the profile view: the SPA form must be fully loaded and
        // bound before editing, otherwise an update submitted against the half-bound
        // form is silently dropped (no error, change never saved).
        driver.navigate().refresh();
        return new ProfilePage(driver);
    }

    public LoginPage logout() {
        click(accountIcon);
        click(logoutItem);
        return new LoginPage(driver);
    }
}
