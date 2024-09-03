package org.ibs;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class IbsTestAutomation {

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "user", "pass");
        try (Statement stmt = connection.createStatement()) {
            // Создание таблицы FOOD
            stmt.execute("CREATE TABLE FOOD (FOOD_ID INT PRIMARY KEY, FOOD_NAME VARCHAR(255), FOOD_TYPE VARCHAR(255), FOOD_EXOTIC BOOLEAN)");
        }
    }

    @Test
    @DisplayName("Проверка добавления нового товара")
    void testAddFoodItem() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Добавление нового товара
            int rowsAffected = stmt.executeUpdate("INSERT INTO FOOD (FOOD_ID, FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) " +
                    "VALUES (5, 'Манго', 'FRUIT', TRUE)");
            assertEquals(1, rowsAffected, "Должна быть добавлена одна строка");

            // Проверка наличия товара в таблице
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD WHERE FOOD_ID = 5");
            assertTrue(rs.next(), "Товар с FOOD_ID = 5 должен существовать");
            assertEquals("Манго", rs.getString("FOOD_NAME"));
            assertEquals("FRUIT", rs.getString("FOOD_TYPE"));
            assertTrue(rs.getBoolean("FOOD_EXOTIC"));
        }
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
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
