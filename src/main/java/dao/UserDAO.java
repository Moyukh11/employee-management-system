package dao;

import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public void create(String username, String passwordHash) throws SQLException {

        String sql =
                "INSERT INTO users (username, password_hash, role) " +
                "VALUES (?, ?, 'HR')";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);

            statement.executeUpdate();
        }
    }


    public User findByUsername(String username) throws SQLException {

        String sql =
                "SELECT id, username, password_hash, role " +
                "FROM users WHERE username = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("role")
                );
            }
        }
    }


    public User getUserById(int id) throws SQLException {

        String sql =
                "SELECT id, username, password_hash, role " +
                "FROM users WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("role")
                );
            }
        }
    }


    public boolean updatePassword(
            int userId,
            String passwordHash) throws SQLException {

        String sql =
                "UPDATE users SET password_hash = ? " +
                "WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, passwordHash);
            statement.setInt(2, userId);

            return statement.executeUpdate() == 1;
        }
    }
}
