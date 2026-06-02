package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * The profile page (/user/&lt;id&gt;). Fields render read-only until the EDIT button
 * is clicked, which reveals editable inputs plus an UPDATE button. MUI renders the
 * button captions uppercase via CSS, so matching is done case-insensitively.
 */
public class ProfilePage extends BasePage {

    private final By editButton   = By.xpath("//button[contains(translate(., 'EDIT', 'edit'), 'edit')]");
    private final By emailInput   = By.cssSelector("input[name='email']");
    private final By updateButton = By.xpath("//button[contains(translate(., 'UPDATE', 'update'), 'update')]");
    private final By successToast = By.xpath("//*[contains(translate(., 'UPDATED', 'updated'), 'updated') or contains(translate(., 'SUCCESS', 'success'), 'success')]");
    private final By failureMsg   = By.xpath("//*[contains(translate(., 'FAILED TO UPDATE', 'failed to update'), 'failed to update') and not(.//*[contains(translate(., 'FAILED TO UPDATE', 'failed to update'), 'failed to update')])]");

    private String pendingEmail;

    public ProfilePage(WebDriver driver) { super(driver); }

    public ProfilePage updateEmail(String newEmail) {
        click(editButton);

        // React-controlled input: a plain clear()+sendKeys can leave the bound value
        // stale/garbled, so set the value through the native setter and fire input/change
        // so React registers the new email cleanly (mirrors typing it by hand).
        WebElement field = waitVisible(emailInput);
        ((JavascriptExecutor) driver).executeScript(
            "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
            "setter.call(arguments[0], '');" +
            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
            "setter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
            "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
            field, newEmail);

        pendingEmail = newEmail;
        click(updateButton);
        // Profile update may raise a native success alert — accept it if present.
        acceptAlertIfPresent();
        return this;
    }

    public boolean updateSuccessShown() {
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        // A server-side "Failed to update user." message means the save did NOT happen.
        if (isPresent(failureMsg)) return false;
        return isPresent(successToast);
    }

    /** Text of any "Failed..." message shown after Update (empty if none). */
    public String failureText() {
        List<WebElement> f = findAll(failureMsg);
        return f.isEmpty() ? "" : f.get(0).getText().trim();
    }

    /**
     * Current value of the email field. After a page (re)load the SPA fills this field
     * from an async call, so we wait until it is actually populated before reading —
     * re-locating each poll to avoid stale references during the React re-render.
     */
    public String currentEmail() {
        waitVisible(emailInput);
        wait.until(d -> {
            List<WebElement> els = d.findElements(emailInput);
            return !els.isEmpty()
                && els.get(0).getAttribute("value") != null
                && !els.get(0).getAttribute("value").trim().isEmpty();
        });
        return findAll(emailInput).get(0).getAttribute("value").trim();
    }
}
