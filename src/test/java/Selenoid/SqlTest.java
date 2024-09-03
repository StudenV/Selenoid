package Selenoid;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SqlTest {
    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        // Подключаемся к базе данных H2 в файловом режиме
        connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb", "user", "pass");
        // Очищаем таблицу перед началом тестов
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE FOOD");
        }
    }

    @Test
    @DisplayName("Проверка добавления, выбора и удаления товара")
    void testDatabaseOperations() throws SQLException {
        // Добавляем товар
        addProduct(5, "Банан", "FRUIT", false);

        // Проверяем добавление товара
        checkProductExists(5, "Банан", "FRUIT", false);

        // Удаляем товар
        deleteProduct(5);

        // Проверяем, что товар удален
        checkProductNotExists(5);
    }

    private void addProduct(int id, String name, String type, boolean isExotic) throws SQLException {
        String sql = "INSERT INTO FOOD (FOOD_ID, FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, type);
            pstmt.setBoolean(4, isExotic);
            int rowsAffected = pstmt.executeUpdate();
            assertEquals(1, rowsAffected, "Должна быть добавлена одна строка");
            System.out.println("Товар " + name + " добавлен в таблицу.");
        }
    }

    private void checkProductExists(int id, String expectedName, String expectedType, boolean expectedIsExotic) throws SQLException {
        String sql = "SELECT * FROM FOOD WHERE FOOD_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next(), "Товар с ID " + id + " должен существовать");
            assertEquals(expectedName, rs.getString("FOOD_NAME"), "Название товара не соответствует ожидаемому");
            assertEquals(expectedType, rs.getString("FOOD_TYPE"), "Тип товара не соответствует ожидаемому");
            assertEquals(expectedIsExotic, rs.getBoolean("FOOD_EXOTIC"), "Статус экзотичности не соответствует ожидаемому");
        }
    }

    private void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM FOOD WHERE FOOD_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            assertEquals(1, rowsAffected, "Должна быть удалена одна строка");
            System.out.println("Товар с ID " + id + " удален из таблицы.");
        }
    }

    private void checkProductNotExists(int id) throws SQLException {
        String sql = "SELECT * FROM FOOD WHERE FOOD_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            assertFalse(rs.next(), "Товар с ID " + id + " не должен существовать");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Соединение с базой данных закрыто.");
        }
    }
}
