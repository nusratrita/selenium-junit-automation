package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Admin dashboard at /admin — shows a paginated table of all users with a
 * "Search by any field or date..." box that filters the rows live.
 */
public class AdminUsersPage extends BasePage {

    private final By searchInput = By.cssSelector("input[type='search'], input[name='search'], input[placeholder*='Search' i]");
    private final By userRows    = By.cssSelector("table tbody tr, .user-row, [data-testid='user-row']");

    public AdminUsersPage(WebDriver driver) { super(driver); }

    /** True once the admin user table/search box has rendered. */
    public boolean isLoaded() {
        try {
            waitVisible(searchInput);
            return driver.getCurrentUrl().contains("/admin");
        } catch (RuntimeException e) {
            return false;
        }
    }

    public AdminUsersPage searchBy(String text) {
        type(searchInput, text);
        // The table filters live as you type — give it a moment to settle.
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        return this;
    }

    public boolean rowsContain(String text) {
        for (WebElement row : findAll(userRows)) {
            if (row.getText().contains(text)) return true;
        }
        return false;
    }
}
