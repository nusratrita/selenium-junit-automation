package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ForgotPasswordPage extends BasePage {

    public static final String PATH = "/forgot-password";

    private final By emailInput    = By.cssSelector("input[type='email'], input[name='email'], input[id='email']");
    private final By sendLinkBtn   = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'send') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'reset link') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'submit')]");
    private final By successToast  = By.xpath("//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'check your email') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'email sent') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'has been sent') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'link sent') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sent to your')]");
    private final By errorMessage  = By.xpath("//*[contains(@class, 'error') or contains(@class, 'invalid') or contains(@role, 'alert')]");

    public ForgotPasswordPage(WebDriver driver) { super(driver); }

    public ForgotPasswordPage open(String baseUrl) {
        driver.get(baseUrl + PATH);
        return this;
    }

    public ForgotPasswordPage fillEmail(String email) { type(emailInput, email); return this; }

    public ForgotPasswordPage submit() { click(sendLinkBtn); return this; }

    public boolean successShown() { return isPresent(successToast); }
    public boolean errorShown()   { return isPresent(errorMessage); }

    public boolean sendButtonDisabled() {
        return !driver.findElements(sendLinkBtn).isEmpty()
            && driver.findElements(sendLinkBtn).get(0).getAttribute("disabled") != null;
    }
}
