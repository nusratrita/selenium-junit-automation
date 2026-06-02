package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * The /add-cost form. Required fields: itemName, amount, purchaseDate (defaults
 * to today) and month (defaults to January). Quantity is a readonly field that
 * defaults to 1 and is changed with the +/- buttons. Remarks is optional.
 */
public class AddItemPage extends BasePage {

    private final By nameInput    = By.id("itemName");
    private final By amountInput  = By.id("amount");
    private final By dateInput    = By.id("purchaseDate");
    private final By monthSelect  = By.id("month");
    private final By remarksInput = By.id("remarks");
    private final By plusButton   = By.xpath("//div[contains(@class,'quantity')]//button[normalize-space(.)='+']");
    private final By submitButton = By.cssSelector("button.submit-button");

    public AddItemPage(WebDriver driver) { super(driver); }

    /** Fills only the mandatory fields (itemName + amount); date/month keep their defaults. */
    public ItemListPage saveMandatoryOnly(String name, String amount) {
        waitVisible(nameInput);
        type(nameInput, name);
        type(amountInput, amount);
        return submitAndReturnToList();
    }

    /** Fills every field on the form. */
    public ItemListPage saveAllFields(String name, String amount, String description, String date) {
        waitVisible(nameInput);
        type(nameInput, name);
        type(amountInput, amount);
        setDate(date);
        new Select(waitVisible(monthSelect)).selectByVisibleText("March");
        if (isPresent(remarksInput)) type(remarksInput, description);
        bumpQuantity();
        return submitAndReturnToList();
    }

    private void setDate(String isoDate) {
        // React-controlled date input: set value through the native setter so React
        // registers the change, rather than relying on locale-sensitive sendKeys.
        WebElement el = waitVisible(dateInput);
        ((JavascriptExecutor) driver).executeScript(
            "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
            "setter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
            "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
            el, isoDate);
    }

    private void bumpQuantity() {
        if (isPresent(plusButton)) {
            try { click(plusButton); } catch (RuntimeException ignored) { /* keep default qty */ }
        }
    }

    private ItemListPage submitAndReturnToList() {
        click(submitButton);
        // The app raises a native "added successfully" alert on submit — accept it.
        acceptAlertIfPresent();
        // Let the POST settle, then land on the list so the new row is visible and
        // the dashboard's "Add Cost" button is available for the next add.
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        navigateToPath("/user");
        return new ItemListPage(driver);
    }
}
