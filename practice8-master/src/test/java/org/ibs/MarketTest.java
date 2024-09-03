package org.ibs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarketTest {

    private static WebDriver driver;
    private static final Logger logger = Logger.getLogger(MarketTest.class.getName());


    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\practice8-master\\src\\test\\resources\\chromedriver.exe");
        driver = new ChromeDriver();
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

    public void addProduct(String name, String type, boolean isExotic) {
        logger.info("Добавление продукта: " + name + ", Тип: " + type + ", Экзотический: " + isExotic);

        driver.get("http://localhost:8080/food");
        logger.info("Открыта страница с продуктами.");

        // Шаг 1: Кликнуть кнопку "Добавить"
        WebElement addButton = driver.findElement(By.cssSelector("body > div > div.content > div > div.btn-grou.mt-2.mb-2 > button"));
        addButton.click();
        logger.info("Нажата кнопка 'Добавить'.");

        // Ожидание появления модального окна
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editModal")));
        logger.info("Модальное окно добавления появилось.");

        // Шаг 2: Заполнить поля формы
        WebElement nameField = driver.findElement(By.cssSelector("#name"));
        nameField.click();
        nameField.sendKeys(name);
        logger.info("Введено наименование продукта: " + name);

        WebElement typeDropdown = driver.findElement(By.id("type"));
        Select select = new Select(typeDropdown);
        select.selectByValue(type);
        logger.info("Выбран тип продукта: " + type);

        if (isExotic) {
            WebElement exoticCheckbox = driver.findElement(By.cssSelector("#exotic"));
            exoticCheckbox.click();
            logger.info("Отмечен чекбокс 'Экзотический'.");
        }

        // Шаг 3: Кликнуть по кнопке "Сохранить"
        WebElement saveButton = driver.findElement(By.cssSelector("#save"));
        saveButton.click();
        logger.info("Нажата кнопка 'Сохранить'.");

        // Ожидание подтверждения добавления товара в таблицу
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("table"), name));
        logger.info("Товар '" + name + "' добавлен в таблицу.");

        // Проверка, что элемент добавлен
        WebElement table = driver.findElement(By.cssSelector("table"));
        assertTrue(table.getText().contains(name), "Товар '" + name + "' не был добавлен в таблицу.");
        logger.info("Тест на добавление продукта '" + name + "' завершен успешно.");
    }

    @AfterAll
    public static void tearDown() {
        logger.info("Тестирование завершено. Браузер закрыт.");
    }

}