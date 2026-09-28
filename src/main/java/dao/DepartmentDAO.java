package dao;

import model.Department;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> getAllDepartments() throws SQLException {

        List<Department> departments = new ArrayList<>();

        String sql =
                "SELECT d.id, d.name, COUNT(e.id) AS employee_count " +
                "FROM department d " +
                "LEFT JOIN employee e ON d.id = e.department_id " +
                "GROUP BY d.id, d.name " +
                "ORDER BY d.name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Department department = new Department();

                department.setId(resultSet.getInt("id"));
                department.setName(resultSet.getString("name"));
                department.setEmployeeCount(
                        resultSet.getInt("employee_count")
                );

                departments.add(department);
            }
        }

        return departments;
    }

    public boolean addDepartment(String name) throws SQLException {

        String sql = "INSERT INTO department (name) VALUES (?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteDepartment(int id) throws SQLException {

        String sql = "DELETE FROM department WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }
}
