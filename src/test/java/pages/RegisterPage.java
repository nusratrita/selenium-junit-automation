package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegisterPage extends BasePage {

    public static final String PATH = "/register";

    private final By firstNameInput = By.cssSelector("input[placeholder*='First Name' i], input[name='firstName'], input[id='firstName']");
    private final By lastNameInput  = By.cssSelector("input[placeholder*='Last Name' i], input[name='lastName'], input[id='lastName']");
    private final By emailInput     = By.cssSelector("input[type='email'], input[placeholder*='Email' i], input[name='email']");
    private final By passwordInput  = By.cssSelector("input[type='password'], input[placeholder*='Password' i], input[name='password']");
    private final By phoneInput     = By.cssSelector("input[placeholder*='Phone' i], input[name='phoneNumber'], input[name='phone']");
    private final By addressInput   = By.cssSelector("input[placeholder*='Address' i], textarea[placeholder*='Address' i], input[name='address']");
    private final By genderRadios   = By.cssSelector("input[type='radio']");
    private final By termsCheckbox  = By.cssSelector("input[type='checkbox']");
    private final By submitButton   = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'register')]");
    private final By successToast   = By.xpath("//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'congratulation') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'registered successfully') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'check your email') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'verification') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'success')]");

    public RegisterPage(WebDriver driver) { super(driver); }

    public RegisterPage open(String baseUrl) {
        driver.get(baseUrl + PATH);
        return this;
    }

    public RegisterPage fillFirstName(String firstName) { type(firstNameInput, firstName); return this; }
    public RegisterPage fillLastName(String lastName)   { type(lastNameInput, lastName); return this; }
    public RegisterPage fillEmail(String email)         { type(emailInput, email); return this; }
    public RegisterPage fillPassword(String pwd)        { type(passwordInput, pwd); return this; }
    public RegisterPage fillPhone(String phone)         { type(phoneInput, phone); return this; }
    public RegisterPage fillAddress(String address) {
        if (isPresent(addressInput)) type(addressInput, address);
        return this;
    }

    public RegisterPage selectGender(String gender) {
        // First radio = Male, second radio = Female (per page layout).
        int index = "female".equalsIgnoreCase(gender) ? 1 : 0;
        java.util.List<WebElement> radios = findAll(genderRadios);
        if (!radios.isEmpty() && radios.size() > index) {
            jsClickElement(radios.get(index));
        }
        return this;
    }

    public RegisterPage acceptTerms() {
        if (isPresent(termsCheckbox)) {
            WebElement cb = driver.findElement(termsCheckbox);
            if (!cb.isSelected()) jsClickElement(cb);
        }
        return this;
    }

    public void submit() { click(submitButton); }

    public boolean isRegistrationSuccessMessageShown() {
        return isPresent(successToast);
    }

    private void jsClickElement(WebElement el) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("arguments[0].click();", el);
    }
}
