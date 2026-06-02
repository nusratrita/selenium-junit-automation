package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ResetPasswordPage extends BasePage {

    // Match password fields by type — the same approach that works on the login
    // form. The reset page renders "New Password" + "Confirm Password", both as
    // input[type='password'] (their name/id attributes are not 'password').
    private final By passwordFields = By.cssSelector("input[type='password']");
    private final By submitBtn      = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'reset') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'update password') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'change password') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'submit')]");
    private final By successToast   = By.xpath("//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'password reset') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'password changed') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'successfully')]");

    public ResetPasswordPage(WebDriver driver) { super(driver); }

    public ResetPasswordPage openLink(String resetUrl) {
        driver.get(resetUrl);
        return this;
    }

    public ResetPasswordPage setNewPassword(String newPwd) {
        // Wait for the first password field to render, then fill every
        // password field on the page (new + confirm) with the same value.
        waitVisible(passwordFields);
        List<WebElement> fields = findAll(passwordFields);
        for (WebElement field : fields) {
            field.clear();
            field.sendKeys(newPwd);
        }
        return this;
    }

    public LoginPage submit() {
        click(submitBtn);
        return new LoginPage(driver);
    }

    public boolean successShown() { return isPresent(successToast); }
}
