package org.ibs;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

public class SeleniumSqlTest {
    private static Connection connection;
    private static WebDriver driver;

    @BeforeAll
    static void setUp() throws SQLException, MalformedURLException {
        // Подключение к базе данных
        connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb", "user", "pass");

        // Настройка WebDriver для Selenoid
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("chrome");
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);

        // Проверка статуса Selenoid
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:4444/wd/hub/status").openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed to connect to Selenoid: HTTP error code : " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to Selenoid: " + e.getMessage(), e);
        }
    }


    @Test
    @DisplayName("Проверка текущего состояния таблицы")
    void testCheckTableState() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD");
            while (rs.next()) {
                System.out.println("FOOD_ID: " + rs.getInt("FOOD_ID") +
                        ", FOOD_NAME: " + rs.getString("FOOD_NAME") +
                        ", FOOD_TYPE: " + rs.getString("FOOD_TYPE") +
                        ", FOOD_EXOTIC: " + rs.getBoolean("FOOD_EXOTIC"));
            }
        }
    }

    @Test
    @DisplayName("Добавляем товар и проверяем через веб-интерфейс")
    void testAddFoodItem() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Добавляем новый товар
            int rowsAffected = stmt.executeUpdate("INSERT INTO FOOD (FOOD_ID, FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) " +
                    "VALUES (5, 'Банан', 'FRUIT', FALSE)");
            assertEquals(1, rowsAffected, "Должна быть добавлена одна строка");

            // Проверяем наличие товара в таблице
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD WHERE FOOD_ID = 5");
            assertTrue(rs.next(), "Товар с FOOD_ID = 5 должен существовать");
            assertEquals("Банан", rs.getString("FOOD_NAME"));
            assertEquals("FRUIT", rs.getString("FOOD_TYPE"));
            assertFalse(rs.getBoolean("FOOD_EXOTIC"));
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Соединение с базой данных закрыто.");
        }
        if (driver != null) {
            driver.quit();
            System.out.println("Браузер закрыт.");
        }
    }
}
