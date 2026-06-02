package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    public static final String PATH = "/login";

    private final By emailInput    = By.cssSelector("input[type='email'], input[name='email'], input[id='email']");
    private final By passwordInput = By.cssSelector("input[type='password'], input[name='password']");
    private final By loginButton   = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in')]");
    private final By forgotLink    = By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'forgot') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'reset')]");
    private final By errorMessage  = By.xpath("//*[contains(@class, 'error') or contains(@class, 'invalid') or contains(@role, 'alert')]");

    public LoginPage(WebDriver driver) { super(driver); }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + PATH);
        return this;
    }

    public LoginPage fillEmail(String email) { type(emailInput, email); return this; }
    public LoginPage fillPassword(String pwd) { type(passwordInput, pwd); return this; }

    public DashboardPage submitExpectingSuccess() {
        click(loginButton);
        return new DashboardPage(driver);
    }

    public LoginPage submitExpectingFailure() {
        click(loginButton);
        return this;
    }

    public ForgotPasswordPage clickForgotPassword() {
        click(forgotLink);
        return new ForgotPasswordPage(driver);
    }

    public boolean errorVisible() {
        return isPresent(errorMessage);
    }

    public boolean stillOnLoginPage() {
        return driver.getCurrentUrl().contains(PATH);
    }
}
