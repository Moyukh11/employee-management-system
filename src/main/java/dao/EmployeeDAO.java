package dao;

import model.Employee;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private static final String SELECT_BASE = "SELECT e.id, e.name, e.email, e.phone, e.department_id, d.name AS department, e.salary "
            + "FROM employee e LEFT JOIN department d ON e.department_id = d.id ";

    public void addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (name, email, phone, department_id, salary) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            setEmployeeParameters(statement, employee);
            statement.executeUpdate();
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return queryEmployees(SELECT_BASE + "ORDER BY e.id DESC", null);
    }

    public Employee getEmployeeById(int id) throws SQLException {
        List<Employee> employees = queryEmployees(SELECT_BASE + "WHERE e.id = ?", id);
        return employees.isEmpty() ? null : employees.get(0);
    }

    public void updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET name = ?, email = ?, phone = ?, department_id = ?, salary = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            setEmployeeParameters(statement, employee);
            statement.setInt(6, employee.getId());
            statement.executeUpdate();
        }
    }

    public void deleteEmployee(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM employee WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public List<Employee> searchEmployees(String keyword) throws SQLException {
        String sql = SELECT_BASE + "WHERE e.name LIKE ? OR e.email LIKE ? OR d.name LIKE ? ORDER BY e.id DESC";
        String value = "%" + keyword + "%";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setString(2, value);
            statement.setString(3, value);
            return readEmployees(statement);
        }
    }

    public List<Employee> getEmployeesByDepartment(int departmentId) throws SQLException {
        return queryEmployees(SELECT_BASE + "WHERE e.department_id = ? ORDER BY e.id DESC", departmentId);
    }

    private List<Employee> queryEmployees(String sql, Integer id) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (id != null) statement.setInt(1, id);
            return readEmployees(statement);
        }
    }

    private List<Employee> readEmployees(PreparedStatement statement) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getInt("id"));
                employee.setName(resultSet.getString("name"));
                employee.setEmail(resultSet.getString("email"));
                employee.setPhone(resultSet.getString("phone"));
                employee.setDepartmentId(resultSet.getInt("department_id"));
                employee.setDepartmentName(resultSet.getString("department"));
                employee.setSalary(resultSet.getBigDecimal("salary"));
                employees.add(employee);
            }
        }
        return employees;
    }

    private void setEmployeeParameters(PreparedStatement statement, Employee employee) throws SQLException {
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getEmail());
        statement.setString(3, employee.getPhone());
        statement.setInt(4, employee.getDepartmentId());
        statement.setBigDecimal(5, employee.getSalary());
    }
}