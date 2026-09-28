package model;

import java.time.LocalDate;

public class EmployeeProject {
    private int id;
    private int employeeId;
    private String employeeName;
    private String employeeDepartment;
    private int projectId;
    private String projectName;
    private String role;
    private LocalDate assignedDate;

    public EmployeeProject() {
    }

    public EmployeeProject(int employeeId, int projectId, String role, LocalDate assignedDate) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.role = role;
        this.assignedDate = assignedDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeDepartment() { return employeeDepartment; }
    public void setEmployeeDepartment(String employeeDepartment) { this.employeeDepartment = employeeDepartment; }
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }
}