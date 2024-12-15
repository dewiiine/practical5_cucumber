package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private String url;
    private String user;
    private String password;
    private Connection connection;

    public DatabaseManager() {
        // Загрузка конфигурации из app.properties
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/app.properties")) {
            properties.load(fis);
            String mode = properties.getProperty("db.mode", "local");

            if ("local".equalsIgnoreCase(mode)) {
                this.url = properties.getProperty("db.url.local");
            } else if ("remote".equalsIgnoreCase(mode)) {
                this.url = properties.getProperty("db.url.remote");
            } else {
                throw new IllegalArgumentException("Неверный режим базы данных: " + mode);
            }

            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке конфигурации базы данных", e);
        }
    }

    public Connection getConnection() {
        if (this.connection == null) {
            try {
                this.connection = java.sql.DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка подключения к базе данных", e);
            }
        }
        return this.connection;
    }

    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getFoodCount(String foodName) {
        String sql = "SELECT COUNT(*) FROM FOOD WHERE FOOD_NAME = ?";
        int count = 0;

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, foodName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса getFoodCount", e);
        }

        return count;
    }

    public boolean deleteLastAddedFood(String foodName) {
        String sql = "DELETE FROM FOOD WHERE FOOD_ID = (SELECT MAX(FOOD_ID) FROM FOOD WHERE FOOD_NAME = ?)";
        boolean isDeleted = false;

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, foodName);
            int rowsAffected = preparedStatement.executeUpdate();
            isDeleted = rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса deleteLastAddedFood", e);
        }

        return isDeleted;
    }
}
