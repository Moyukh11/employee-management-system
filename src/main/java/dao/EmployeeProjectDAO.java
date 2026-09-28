package dao;

import model.EmployeeProject;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeProjectDAO {
    private static final String SELECT_BASE = "SELECT ep.id, ep.employee_id, e.name AS employee_name, d.name AS employee_department, "
            + "ep.project_id, p.name AS project_name, ep.role, ep.assigned_date "
            + "FROM employee_project ep JOIN employee e ON ep.employee_id = e.id "
            + "LEFT JOIN department d ON e.department_id = d.id JOIN project p ON ep.project_id = p.id ";

    public void assignEmployeeToProject(EmployeeProject assignment) throws SQLException {
        String sql = "INSERT INTO employee_project (employee_id, project_id, role, assigned_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignment.getEmployeeId());
            statement.setInt(2, assignment.getProjectId());
            statement.setString(3, assignment.getRole());
            statement.setDate(4, Date.valueOf(assignment.getAssignedDate()));
            statement.executeUpdate();
        }
    }

    public void removeEmployeeFromProject(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM employee_project WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public List<EmployeeProject> getAllAssignments() throws SQLException {
        return queryAssignments(SELECT_BASE + "ORDER BY ep.id DESC");
    }

    public List<EmployeeProject> getAssignmentsByEmployee(int employeeId) throws SQLException {
        return queryAssignments(SELECT_BASE + "WHERE ep.employee_id = ? ORDER BY ep.id DESC", employeeId);
    }

    public List<EmployeeProject> getAssignmentsByProject(int projectId) throws SQLException {
        return queryAssignments(SELECT_BASE + "WHERE ep.project_id = ? ORDER BY ep.id DESC", projectId);
    }

    public List<EmployeeProject> searchAssignments(String keyword) throws SQLException {
        String sql = SELECT_BASE + "WHERE e.name LIKE ? OR p.name LIKE ? OR ep.role LIKE ? ORDER BY ep.id DESC";
        String value = "%" + keyword + "%";
        return queryAssignments(sql, value, value, value);
    }

    public boolean isEmployeeAssignedToProject(int employeeId, int projectId) throws SQLException {
        String sql = "SELECT 1 FROM employee_project WHERE employee_id = ? AND project_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            statement.setInt(2, projectId);
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next(); }
        }
    }

    public int countAssignments() throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM employee_project"); ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private List<EmployeeProject> queryAssignments(String sql, Object... values) throws SQLException {
        List<EmployeeProject> assignments = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < values.length; index++) if (values[index] != null) statement.setObject(index + 1, values[index]);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeProject assignment = new EmployeeProject();
                    assignment.setId(resultSet.getInt("id"));
                    assignment.setEmployeeId(resultSet.getInt("employee_id"));
                    assignment.setEmployeeName(resultSet.getString("employee_name"));
                    assignment.setEmployeeDepartment(resultSet.getString("employee_department"));
                    assignment.setProjectId(resultSet.getInt("project_id"));
                    assignment.setProjectName(resultSet.getString("project_name"));
                    assignment.setRole(resultSet.getString("role"));
                    Date assignedDate = resultSet.getDate("assigned_date");
                    assignment.setAssignedDate(assignedDate == null ? null : assignedDate.toLocalDate());
                    assignments.add(assignment);
                }
            }
        }
        return assignments;
    }
}