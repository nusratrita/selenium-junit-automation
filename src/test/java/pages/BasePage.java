package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void jsClick(By locator) {
        WebElement el = waitVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected String textOf(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    /** Accepts a native JS alert/confirm if one pops up within a few seconds. */
    protected void acceptAlertIfPresent() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (RuntimeException ignored) {
            // no alert appeared — nothing to do
        }
    }

    /** Navigate to a root-relative path on the current origin (e.g. "/user"). */
    protected void navigateToPath(String path) {
        String origin = (String) ((JavascriptExecutor) driver)
            .executeScript("return window.location.origin;");
        driver.get(origin + path);
    }
}
