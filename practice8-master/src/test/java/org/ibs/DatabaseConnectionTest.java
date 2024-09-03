package org.ibs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;


import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    protected static WebDriver driver;
    protected static Connection connection;
    private static final String DB_URL = "jdbc:h2:mem:testdb";  // URL к базе данных H2
    private static final String USER = "user";                // Имя пользователя
    private static final String PASS = "pass";

    @BeforeAll
    public static void setUp() throws SQLException {
        // Устанавливаем соединение с базой данных H2
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "user", "pass");

        try (Statement stmt = connection.createStatement()) {
            // Создание таблицы FOOD
            stmt.execute("CREATE TABLE FOOD (FOOD_ID INT PRIMARY KEY, FOOD_NAME VARCHAR(255), FOOD_TYPE VARCHAR(255), FOOD_EXOTIC BOOLEAN)");
        }

        System.setProperty("webdriver.chrome.driver", "C:\\Working Project\\autotest\\ibspractice\\src\\test\\resources\\chromedriver.exe");

        // Запускаем Chrome браузер
        driver = new ChromeDriver();

        // Переходим на страницу с SQL-формой
        driver.get("http://localhost:8080/h2-console/login.do?jsessionid=f4af6154a4bdefb893e090d30c4839c6");

        // Ожидаем появления поля ввода пароля и вводим пароль
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        passwordField.sendKeys("pass");

        // Нажимаем кнопку "Connect"
        WebElement connectButton = driver.findElement(By.cssSelector("input.button[value='Connect']"));
        connectButton.click();

        System.out.println("Настройка завершена: соединение с базой данных установлено, браузер запущен и выполнен вход в H2 Console.");
    }

    @Test
    @DisplayName("Проверка выполнения SQL-запроса SELECT * FROM FOOD")
    void testExecuteSelectQuery() {
        // Инициализация WebDriverWait с таймаутом в 30 секунд
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("triggerButton"))).click();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[contains(@id, 'h2iframeTransport')]")));


        // Опциональная пауза для отладки (можно убрать, если не нужна)
        try {
            Thread.sleep(10000); // Пауза на 10 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Поиск iframe на странице
        WebElement iframeElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//iframe[contains(@id, 'h2iframeTransport')]")));
        ;

        // Переключение на найденный iframe
        driver.switchTo().frame(iframeElement);

        // Найти скрытый элемент <textarea> с id "sql" и сделать его видимым через JavaScript
        WebElement hiddenSqlTextArea = driver.findElement(By.id("sql"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", hiddenSqlTextArea);

        // Ожидание видимости и кликабельности элемента <textarea> с id "sql"
        WebElement sqlTextArea = wait.until(ExpectedConditions.elementToBeClickable(By.id("sql")));

        // Клик по элементу для получения фокуса
        sqlTextArea.click();

        // Ввод SQL-запроса в <textarea>
        sqlTextArea.sendKeys("SELECT * FROM FOOD;");

        // Поиск кнопки "Run" и нажатие на неё
        WebElement runButton = driver.findElement(By.cssSelector("input.button[value='Run']"));
        runButton.click();

        // Вывод информации в консоль, что запрос был успешно введён
        System.out.println("SQL-запрос 'SELECT * FROM FOOD;' введён и кнопка 'Run' нажата.");

        // Ожидание появления таблицы с результатами выполнения запроса
        WebElement resultTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultTable")));

        // Вывод информации в консоль, что результаты успешно отображены
        System.out.println("Результаты запроса успешно отображены.");
    }




    @Test
    @DisplayName("Проверка удаления товара")
    void testDeleteFoodItem() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Добавление нового товара для теста
            stmt.executeUpdate("INSERT INTO FOOD (FOOD_ID, FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) " +
                    "VALUES (5, 'Манго', 'FRUIT', TRUE)");

            // Удаление товара
            int rowsAffected = stmt.executeUpdate("DELETE FROM FOOD WHERE FOOD_ID = 5");
            assertEquals(1, rowsAffected, "Должна быть удалена одна строка");

            // Проверка, что товар был удален
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD WHERE FOOD_ID = 5");
            assertFalse(rs.next(), "Товар с FOOD_ID = 5 не должен существовать");
        }

        // Явное ожидание до появления элемента
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement sqlTextArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sql")));

        // Вводим SQL-запрос в браузере и нажимаем кнопку Run
        sqlTextArea.sendKeys("DELETE FROM FOOD WHERE FOOD_ID = 5");

        WebElement runButton = driver.findElement(By.cssSelector("input.button[value='Run']"));
        runButton.click();

        System.out.println("SQL-запрос на удаление введён и кнопка Run нажата.");
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        // Закрываем соединение с базой данных
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        // Закрываем браузер
        if (driver != null) {
            driver.quit();
            System.out.println("Браузер закрыт.");
        }
    }
}
