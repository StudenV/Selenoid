package Cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductSteps {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before // Метод будет выполнен перед каждым тестом
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Working Project\\autotest\\ibspractice\\src\\test\\resources\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("I set up")
    public void i_set_up() {
        // Код для инициализации или очистки данных перед тестом
        // Например, это может быть очистка базы данных или установка начального состояния
        System.out.println("Setup step executed.");
    }

    @Given("I am on the product page")
    public void i_am_on_the_product_page() {
        driver.get("http://localhost:8080/food");
    }

    @When("I add a product with name {string} and type {string} and {string}")
    public void i_add_a_product_with_name_and_type_and_exotic(String name, String type, String exotic) {
        // Click "Add" button
        WebElement addButton = driver.findElement(By.cssSelector("body > div > div.content > div > div.btn-grou.mt-2.mb-2 > button"));
        addButton.click();

        // Wait for modal to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editModal")));

        // Fill form
        WebElement nameField = driver.findElement(By.cssSelector("#name"));
        nameField.sendKeys(name);

        WebElement typeDropdown = driver.findElement(By.id("type"));
        Select select = new Select(typeDropdown);
        select.selectByValue(type);

        if (exotic.equals("exotic")) {
            WebElement exoticCheckbox = driver.findElement(By.cssSelector("#exotic"));
            exoticCheckbox.click();
        }

        // Click "Save"
        WebElement saveButton = driver.findElement(By.cssSelector("#save"));
        saveButton.click();
    }

    @Then("the product {string} should be added to the table")
    public void the_product_should_be_added_to_the_table(String name) {
        // Wait for the product to appear in the table
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("table"), name));

        // Verify the product is in the table
        WebElement table = driver.findElement(By.cssSelector("table"));
        assertTrue(table.getText().contains(name), "Product '" + name + "' was not added to the table.");
    }

    @After // Метод будет выполнен после каждого теста
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
