package Selenoid;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class WedTest {
    private static WebDriver driver;
    private static final Logger logger = Logger.getLogger(WedTest.class.getName());
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\practice8-master\\src\\test\\resources\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        logger.info("Настройка завершена, запущен браузер Chrome.");
    }

    @Test
    public void testAddProduct() {
        logger.info("Тестирование добавления обычного продукта.");
        addProduct("Морковь", "VEGETABLE", false);
    }

    @Test
    public void testAddExoticProduct() {
        logger.info("Тестирование добавления экзотического продукта.");
        addProduct("Банан", "FRUIT", true);
    }

    private void addProduct(String name, String type, boolean isExotic) {
        logger.info("Добавление продукта: " + name + ", Тип: " + type + ", Экзотический: " + isExotic);

        driver.get("http://localhost:8080/food");
        logger.info("Открыта страница с продуктами.");

        // Шаг 1: Кликнуть кнопку "Добавить"
        clickAddButton();

        // Ожидание появления модального окна
        waitUntilModalIsVisible();

        // Шаг 2: Заполнить поля формы
        fillProductName(name);
        selectProductType(type);
        if (isExotic) {
            markAsExotic();
        }

        // Шаг 3: Кликнуть по кнопке "Сохранить"
        saveProduct();

        // Ожидание подтверждения добавления товара в таблицу
        waitUntilProductIsAdded(name);

        // Проверка, что элемент добавлен
        assertProductAdded(name);
        logger.info("Тест на добавление продукта '" + name + "' завершен успешно.");
    }

    private void clickAddButton() {
        WebElement addButton = driver.findElement(By.cssSelector("body > div > div.content > div > div.btn-grou.mt-2.mb-2 > button"));
        addButton.click();
        logger.info("Нажата кнопка 'Добавить'.");
    }

    private void waitUntilModalIsVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editModal")));
        logger.info("Модальное окно добавления появилось.");
    }

    private void fillProductName(String name) {
        WebElement nameField = driver.findElement(By.cssSelector("#name"));
        nameField.click();
        nameField.sendKeys(name);
        logger.info("Введено наименование продукта: " + name);
    }

    private void selectProductType(String type) {
        WebElement typeDropdown = driver.findElement(By.id("type"));
        Select select = new Select(typeDropdown);
        select.selectByValue(type);
        logger.info("Выбран тип продукта: " + type);
    }

    private void markAsExotic() {
        WebElement exoticCheckbox = driver.findElement(By.cssSelector("#exotic"));
        exoticCheckbox.click();
        logger.info("Отмечен чекбокс 'Экзотический'.");
    }

    private void saveProduct() {
        WebElement saveButton = driver.findElement(By.cssSelector("#save"));
        saveButton.click();
        logger.info("Нажата кнопка 'Сохранить'.");
    }

    private void waitUntilProductIsAdded(String name) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("table"), name));
        logger.info("Товар '" + name + "' добавлен в таблицу.");
    }

    private void assertProductAdded(String name) {
        WebElement table = driver.findElement(By.cssSelector("table"));
        assertTrue(table.getText().contains(name), "Товар '" + name + "' не был добавлен в таблицу.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        logger.info("Тестирование завершено. Браузер закрыт.");
    }
}
