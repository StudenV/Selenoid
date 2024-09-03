package Selenoid;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.Map;



public class SelenoidTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static Properties props = new Properties();
    private static int idPreviousProduct;

    @BeforeAll
    public static void setup() {
        try (FileInputStream input = new FileInputStream("src/test/resources/features/application.properties")) {
            props.load(input);
            String driverType = props.getProperty("type.driver");

            ChromeOptions options = new ChromeOptions();
            options.setBinary("D:\\opt\\chrome-win64.chrome.exe");

            if ("remote".equalsIgnoreCase(driverType)) {
                initRemoteDriver();
            } else {
                System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
                driver = new ChromeDriver(options);
                driver.manage().window().maximize();
            }

            // Initialize WebDriverWait with a timeout of 10 seconds
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.get("http://149.154.71.152:8080/food");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initRemoteDriver() {
        try {
            URL remoteUrl = new URL("http://149.154.71.152:4444/wd/hub");
            ChromeOptions options = new ChromeOptions();
            options.setCapability("browserName", "chrome");
            options.setCapability("browserVersion", "109.0");
            options.setCapability("selenoid:options", Map.of(
                    "enableVNC", true,
                    "enableVideo", false
            ));
            driver = new RemoteWebDriver(remoteUrl, options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote URL", e);
        }
    }

    static int findPreviousProductId() {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//tr)[last()]/th")));
            idPreviousProduct = Integer.parseInt(element.getText());
            return idPreviousProduct;
        } catch (Exception e) {
            Assertions.fail("Invalid id value - cannot be converted to int.");
            return 0;
        }
    }

    public static int getIdPreviousProduct() {
        return idPreviousProduct;
    }

    public static WebDriver getDriver() {
        return driver;
    }

    @AfterAll
    public static void endTesting() {
        if (driver != null) {
            driver.quit();
        }
    }
}
