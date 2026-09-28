package servlet;

import jakarta.servlet.http.HttpServletRequest;
import model.EmployeeProject;
import util.JsonUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

final class EmployeeProjectServletSupport {
    private EmployeeProjectServletSupport() {
    }

    static EmployeeProject readAssignment(HttpServletRequest request) {
        EmployeeProject assignment = new EmployeeProject();
        try {
            assignment.setEmployeeId(Integer.parseInt(request.getParameter("employeeId")));
            assignment.setProjectId(Integer.parseInt(request.getParameter("projectId")));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Employee and project IDs must be valid");
        }
        String role = request.getParameter("role");
        if (role == null || role.trim().isEmpty()) throw new IllegalArgumentException("Role is required");
        assignment.setRole(role.trim());
        String date = request.getParameter("assignedDate");
        if (date == null || date.isBlank()) throw new IllegalArgumentException("Assigned date is required");
        try {
            assignment.setAssignedDate(LocalDate.parse(date));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Assigned date must be valid");
        }
        if (assignment.getEmployeeId() <= 0 || assignment.getProjectId() <= 0) throw new IllegalArgumentException("Employee and project IDs must be valid");
        return assignment;
    }

    static String assignmentsJson(List<EmployeeProject> assignments) {
        StringBuilder json = new StringBuilder("{\"assignments\":[");
        for (int index = 0; index < assignments.size(); index++) {
            if (index > 0) json.append(',');
            EmployeeProject item = assignments.get(index);
            json.append("{\"id\":").append(item.getId())
                    .append(",\"employeeId\":").append(item.getEmployeeId())
                    .append(",\"employeeName\":").append(JsonUtil.quote(item.getEmployeeName()))
                    .append(",\"employeeDepartment\":").append(JsonUtil.quote(item.getEmployeeDepartment()))
                    .append(",\"projectId\":").append(item.getProjectId())
                    .append(",\"projectName\":").append(JsonUtil.quote(item.getProjectName()))
                    .append(",\"role\":").append(JsonUtil.quote(item.getRole()))
                    .append(",\"assignedDate\":").append(JsonUtil.quote(item.getAssignedDate() == null ? null : item.getAssignedDate().toString())).append('}');
        }
        return json.append("]}").toString();
    }
}