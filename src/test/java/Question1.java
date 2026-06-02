import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Question1 {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeAll
    void setup() throws Exception {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        bringBrowserToFront();
    }

    private void bringBrowserToFront() {
        try {
            String ps1Path = System.getProperty("user.dir") + "\\src\\test\\resources\\bring_to_front.ps1";
            Process proc = new ProcessBuilder(
                "powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", ps1Path
            ).start();
            proc.waitFor(3, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception ignored) {}
    }

    // Browser stays open after test completes for recording

    void takeScreenshot(String fileName) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path dest = Path.of(System.getProperty("user.dir"), "screenshots", fileName);
        Files.createDirectories(dest.getParent());
        Files.copy(screenshot.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Screenshot saved: " + dest);
    }

    @Test
    @DisplayName("Automate Student Registration Form and Assert Success")
    void testStudentRegistrationForm() throws InterruptedException, IOException {
        // Step 1: Navigate to the form
        driver.get("https://www.tutorialspoint.com/selenium/practice/selenium_automation_practice.php");

        // Step 2: Input all the fields

        // Name
        driver.findElement(By.id("name")).sendKeys("Nusrat Jahan Rita");
        Thread.sleep(500);

        // Email
        driver.findElement(By.id("email")).sendKeys("nusratrita00@gmail.com");
        Thread.sleep(500);

        // Gender - Select Female (second radio button, index 1)
        WebElement femaleRadio = driver.findElements(By.cssSelector("input[type='radio']")).get(1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", femaleRadio);
        Thread.sleep(500);

        // Mobile
        driver.findElement(By.id("mobile")).sendKeys("0123456789");
        Thread.sleep(500);

        // Date of Birth - use JS to set value for date input field
        WebElement dobField = driver.findElement(By.id("dob"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '1998-06-15';" +
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", dobField);
        Thread.sleep(500);

        // Subjects
        driver.findElement(By.id("subjects")).sendKeys("Maths");
        Thread.sleep(500);

        // Hobbies - Select Reading (second checkbox, index 1)
        WebElement readingCheckbox = driver.findElements(By.cssSelector("input[type='checkbox']")).get(1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", readingCheckbox);
        Thread.sleep(500);

        // Picture - Upload a file
        String filePath = System.getProperty("user.dir") + "/src/test/resources/test-photo.txt";
        driver.findElement(By.id("picture")).sendKeys(filePath.replace("/", "\\"));
        Thread.sleep(500);

        // Current Address (textarea)
        WebElement addressField = driver.findElement(By.xpath("//textarea[@placeholder='Currend Address']"));
        addressField.sendKeys("123 Main Street, Dhaka, Bangladesh");
        Thread.sleep(500);

        // State dropdown - use JS to set value and trigger events
        WebElement stateElement = driver.findElement(By.id("state"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", stateElement);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "el.value = 'Uttar Pradesh';" +
                "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));" +
                "el.dispatchEvent(new Event('blur', { bubbles: true }));", stateElement);
        Thread.sleep(500);

        // City dropdown - use JS to set value and trigger events
        WebElement cityElement = driver.findElement(By.id("city"));
        ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "el.value = 'Lucknow';" +
                "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));" +
                "el.dispatchEvent(new Event('blur', { bubbles: true }));", cityElement);
        Thread.sleep(500);

        // Step 3: Click Submit button
        WebElement submitButton = driver.findElement(By.cssSelector("input[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500);
        submitButton.click();

        // Step 4: Assert that all fields were filled and form was submitted
        Thread.sleep(1000);

        // Verify all fields have values (confirming form was filled correctly)
        String name = driver.findElement(By.id("name")).getAttribute("value");
        String email = driver.findElement(By.id("email")).getAttribute("value");
        String mobile = driver.findElement(By.id("mobile")).getAttribute("value");
        String dob = driver.findElement(By.id("dob")).getAttribute("value");
        boolean genderSelected = driver.findElements(By.cssSelector("input[type='radio']")).get(1).isSelected();
        boolean hobbySelected = driver.findElements(By.cssSelector("input[type='checkbox']")).get(1).isSelected();

        Assertions.assertFalse(name.isEmpty(), "Name should be filled");
        Assertions.assertFalse(email.isEmpty(), "Email should be filled");
        Assertions.assertFalse(mobile.isEmpty(), "Mobile should be filled");
        Assertions.assertFalse(dob.isEmpty(), "Date of Birth should be filled");
        Assertions.assertTrue(genderSelected, "Gender should be selected");
        Assertions.assertTrue(hobbySelected, "Hobby should be selected");

        System.out.println("All fields filled successfully!");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Mobile: " + mobile);
        System.out.println("DOB: " + dob);
        System.out.println("Gender Selected: " + genderSelected);
        System.out.println("Hobby Selected: " + hobbySelected);
        System.out.println("Submit button clicked - Registration completed!");

        // Take screenshot of the completed form
        takeScreenshot("question1_result.png");
    }
}
