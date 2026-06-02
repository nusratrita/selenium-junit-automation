package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtil {

    private static final DateTimeFormatter STAMP =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtil() {}

    public static Path capture(WebDriver driver, String label) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path outDir = Paths.get("screenshots", "question3");
            Files.createDirectories(outDir);
            String name = label + "_" + LocalDateTime.now().format(STAMP) + ".png";
            Path dest = outDir.resolve(name);
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest;
        } catch (Exception e) {
            System.err.println("Screenshot failed: " + e.getMessage());
            return null;
        }
    }
}
