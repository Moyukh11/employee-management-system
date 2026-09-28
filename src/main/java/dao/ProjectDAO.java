package dao;

import model.Project;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private static final String SELECT_BASE = "SELECT id, name, description, start_date, end_date, status FROM project ";

    public List<Project> getAllProjects() throws SQLException {
        return queryProjects(SELECT_BASE + "ORDER BY id DESC", null);
    }

    public Project getProjectById(int id) throws SQLException {
        List<Project> projects = queryProjects(SELECT_BASE + "WHERE id = ?", id);
        if (projects.isEmpty()) return null;
        Project project = projects.get(0);
        loadEmployeeIds(project);
        return project;
    }

    public void addProject(Project project) throws SQLException {
        String sql = "INSERT INTO project (name, description, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            setProjectParameters(statement, project);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Project ID was not generated");
                project.setId(keys.getInt(1));
            }
            replaceEmployeeAssignments(connection, project.getId(), project.getEmployeeIds());
            connection.commit();
        } catch (SQLException exception) {
            throw exception;
        }
    }

    public void updateProject(Project project) throws SQLException {
        String sql = "UPDATE project SET name = ?, description = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            setProjectParameters(statement, project);
            statement.setInt(6, project.getId());
            statement.executeUpdate();
            replaceEmployeeAssignments(connection, project.getId(), project.getEmployeeIds());
            connection.commit();
        } catch (SQLException exception) {
            throw exception;
        }
    }

    public void deleteProject(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM project WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public List<Project> searchProjects(String keyword) throws SQLException {
        String sql = SELECT_BASE + "WHERE name LIKE ? OR description LIKE ? OR status LIKE ? ORDER BY id DESC";
        String value = "%" + keyword + "%";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setString(2, value);
            statement.setString(3, value);
            return readProjects(statement);
        }
    }

    private List<Project> queryProjects(String sql, Integer id) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (id != null) statement.setInt(1, id);
            return readProjects(statement);
        }
    }

    private List<Project> readProjects(PreparedStatement statement) throws SQLException {
        List<Project> projects = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Project project = new Project();
                project.setId(resultSet.getInt("id"));
                project.setName(resultSet.getString("name"));
                project.setDescription(resultSet.getString("description"));
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");
                project.setStartDate(startDate == null ? null : startDate.toLocalDate());
                project.setEndDate(endDate == null ? null : endDate.toLocalDate());
                project.setStatus(resultSet.getString("status"));
                projects.add(project);
            }
        }
        return projects;
    }

    private void setProjectParameters(PreparedStatement statement, Project project) throws SQLException {
        statement.setString(1, project.getName());
        statement.setString(2, project.getDescription());
        statement.setDate(3, project.getStartDate() == null ? null : Date.valueOf(project.getStartDate()));
        statement.setDate(4, project.getEndDate() == null ? null : Date.valueOf(project.getEndDate()));
        statement.setString(5, project.getStatus());
    }

    private void loadEmployeeIds(Project project) throws SQLException {
        List<Integer> employeeIds = new ArrayList<>();
        String sql = "SELECT employee_id FROM employee_project WHERE project_id = ? ORDER BY employee_id";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, project.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) employeeIds.add(resultSet.getInt("employee_id"));
            }
        }
        project.setEmployeeIds(employeeIds);
    }

    private void replaceEmployeeAssignments(Connection connection, int projectId, List<Integer> employeeIds) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM employee_project WHERE project_id = ?")) {
            delete.setInt(1, projectId);
            delete.executeUpdate();
        }
        if (employeeIds == null || employeeIds.isEmpty()) return;
        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO employee_project (employee_id, project_id, role, assigned_date) VALUES (?, ?, ?, ?)")) {
            for (Integer employeeId : employeeIds) {
                insert.setInt(1, employeeId);
                insert.setInt(2, projectId);
            insert.setString(3, "Project team member");
            insert.setDate(4, Date.valueOf(LocalDate.now()));
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }
}