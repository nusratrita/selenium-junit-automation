import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Question2 {

    WebDriver driver;

    @BeforeAll
    void setup() throws Exception {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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
    @DisplayName("Scrap table data from DSE and store in text file")
    void testScrapTableData() throws IOException, InterruptedException {
        // Step 1: Navigate to the page
        driver.get("https://dsebd.org/latest_share_price_scroll_by_value.php");

        // Wait for page to fully load
        Thread.sleep(10000);

        // Scroll down to show the table
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 300);");
        Thread.sleep(2000);

        // Use JavaScript to extract all table data at once to avoid StaleElementException
        String script = """
                var result = '';
                var tables = document.querySelectorAll('table');
                var dataTable = null;
                for (var t = 0; t < tables.length; t++) {
                    var rows = tables[t].querySelectorAll('tr');
                    if (rows.length > 5) {
                        dataTable = tables[t];
                        break;
                    }
                }
                if (!dataTable) dataTable = tables[0];
                var rows = dataTable.querySelectorAll('tr');
                for (var i = 0; i < rows.length; i++) {
                    var cells = rows[i].querySelectorAll('th, td');
                    var rowData = [];
                    for (var j = 0; j < cells.length; j++) {
                        rowData.push(cells[j].innerText.trim());
                    }
                    if (rowData.length > 0) {
                        result += rowData.join('\\t') + '\\n';
                    }
                }
                return result;
                """;

        String tableData = (String) ((JavascriptExecutor) driver).executeScript(script);
        Thread.sleep(2000);

        // Step 1: Print all cell values
        System.out.println(tableData);

        // Scroll through the table to show data visually
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 600);");
        Thread.sleep(2000);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 1200);");
        Thread.sleep(2000);

        // Step 2: Store the values in a text file
        String filePath = System.getProperty("user.dir") + "/dse_table_data.txt";
        FileWriter writer = new FileWriter(filePath);
        writer.write(tableData);
        writer.close();

        // Count data rows (exclude header)
        String[] lines = tableData.split("\n");
        int dataRows = lines.length - 1;

        System.out.println("Total data rows scraped: " + dataRows);
        System.out.println("Data saved to: " + filePath);
        Thread.sleep(2000);

        // Assert that data was scraped
        Assertions.assertTrue(dataRows > 0, "Table should have data rows");

        // Take screenshot of the scraped page
        takeScreenshot("question2_result.png");

        // Open the text file to show the saved data
        java.awt.Desktop.getDesktop().open(new File(filePath));
    }
}
