package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String DATABASE_URL = getEnvironmentValue("EMS_DB_URL",
            "jdbc:mysql://localhost:3306/employee_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USERNAME = getEnvironmentValue("EMS_DB_USERNAME", "root");
    private static final String PASSWORD = getEnvironmentValue("EMS_DB_PASSWORD", "");

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("MySQL JDBC driver not found. Add mysql-connector-j.jar to Tomcat or WEB-INF/lib.", exception);
        }
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    private static String getEnvironmentValue(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }
}