package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * The cost list shown on /user. Rows live in a standard table; a "Search items..."
 * box filters them, which we use so a freshly-added row can't be hidden by paging.
 */
public class ItemListPage extends BasePage {

    private final By itemRows  = By.cssSelector("table tbody tr");
    private final By searchBox = By.cssSelector("input[placeholder='Search items...']");

    public ItemListPage(WebDriver driver) { super(driver); }

    public int totalItems() {
        return findAll(itemRows).size();
    }

    public boolean containsItemNamed(String name) {
        // Filter via the search box first (best-effort), then scan the rows.
        if (isPresent(searchBox)) {
            try {
                type(searchBox, name);
                Thread.sleep(1200);
            } catch (InterruptedException ignored) {
            } catch (RuntimeException ignored) {}
        }
        for (WebElement row : findAll(itemRows)) {
            if (row.getText().contains(name)) return true;
        }
        return false;
    }

    public List<WebElement> allRows() {
        return findAll(itemRows);
    }
}
