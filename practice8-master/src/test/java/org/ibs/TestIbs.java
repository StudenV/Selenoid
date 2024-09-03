package org.ibs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestIbs {
    private WebDriver driver;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Working Project\\автотест\\ibspractice\\src\\test\\resources\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @Given("I open the products page")
    public void openProductsPage() {
        driver.get("http://localhost:8080/food");
    }

    @When("I add a product with name {string} and type {string} and exotic status {string}")
    public void addProduct(String name, String type, String isExotic) {
        WebElement addButton = driver.findElement(By.cssSelector("body > div > div.content > div > div.btn-grou.mt-2.mb-2 > button"));
        addButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editModal")));

        WebElement nameField = driver.findElement(By.cssSelector("#name"));
        nameField.click();
        nameField.sendKeys(name);

        WebElement typeDropdown = driver.findElement(By.id("type"));
        Select select = new Select(typeDropdown);
        select.selectByValue(type);

        if (Boolean.parseBoolean(isExotic)) {
            WebElement exoticCheckbox = driver.findElement(By.cssSelector("#exotic"));
            exoticCheckbox.click();
        }

        WebElement saveButton = driver.findElement(By.cssSelector("#save"));
        saveButton.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("table"), name));
    }

    @Then("I should see the product {string} in the product list")
    public void verifyProductAdded(String name) {
        WebElement table = driver.findElement(By.cssSelector("table"));
        assertTrue(table.getText().contains(name), "Товар '" + name + "' не был добавлен в таблицу.");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
