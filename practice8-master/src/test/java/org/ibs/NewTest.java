package org.ibs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.logging.Logger;

public class NewTest {

    private static final Logger logger = Logger.getLogger(NewTest.class.getName());

    public static void main(String[] args) {
        // Установите путь к chromedriver
        System.setProperty("webdriver.chrome.driver", "C:\\Working Project\\автотест\\ibspractice\\src\\test\\resources\\chromedriver .exe");

        WebDriver driver = new ChromeDriver();

        try {
            driver.get("http://localhost:8080/food");

            // Шаг 1: Кликнуть кнопку "Добавить"
            WebElement addButton = driver.findElement(By.cssSelector("body > div > div.content > div > div.btn-grou.mt-2.mb-2 > button"));
            addButton.click();

            // Используем Duration для ожидания в WebDriverWait
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Шаг 2: Дождаться появления модального окна (без использования переменной)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editModal")));

            // Шаг 3: Кликнуть по полю ввода "Наименование"
            WebElement nameField = driver.findElement(By.cssSelector("#name"));
            nameField.click();

            // Ввести в поле "Наименование" слово "Морковь"
            nameField.sendKeys("Морковь");

            // Шаг 5: Кликнуть по выпадающему списку "Тип"
            WebElement typeDropdown = driver.findElement(By.id("type"));
            typeDropdown.click();

            WebElement vegetableOption = driver.findElement(By.xpath("//option[@value='VEGETABLE']"));
            vegetableOption.click();

            // Шаг 7: Кликнуть по кнопке "Сохранить"
            WebElement saveButton = driver.findElement(By.cssSelector("#save"));
            saveButton.click();

            // Ожидание подтверждения, что новый элемент добавлен в таблицу
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("table"), "Морковь"));

            logger.info("Товар 'Морковь' успешно добавлен.");

            // Дополнительная задержка перед завершением теста
            Thread.sleep(2000);

        } catch (Exception e) {
            logger.severe("Произошла ошибка: " + e.getMessage());
        }
        // Закрытие браузера отключено
        /*
        finally {
            // Закрыть браузер
            driver.quit();
        }
        */
    }
}

