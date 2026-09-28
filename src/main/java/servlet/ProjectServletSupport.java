package servlet;

import jakarta.servlet.http.HttpServletRequest;
import model.Project;
import util.JsonUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

final class ProjectServletSupport {
    private ProjectServletSupport() {
    }

    static Project readProject(HttpServletRequest request) {
        Project project = new Project();
        project.setName(trim(request.getParameter("name")));
        project.setDescription(trimToNull(request.getParameter("description")));
        project.setStartDate(parseDate(request.getParameter("startDate")));
        project.setEndDate(parseDate(request.getParameter("endDate")));
        project.setStatus(trim(request.getParameter("status")));
        project.setEmployeeIds(parseEmployeeIds(request.getParameterValues("employeeIds")));
        return project;
    }

    static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Enter valid dates in YYYY-MM-DD format");
        }
    }

    static String projectJson(Project project) {
        return "{\"id\":" + project.getId() + ",\"name\":" + JsonUtil.quote(project.getName())
                + ",\"description\":" + JsonUtil.quote(project.getDescription())
                + ",\"startDate\":" + JsonUtil.quote(project.getStartDate() == null ? null : project.getStartDate().toString())
                + ",\"endDate\":" + JsonUtil.quote(project.getEndDate() == null ? null : project.getEndDate().toString())
                + ",\"status\":" + JsonUtil.quote(project.getStatus())
                + ",\"employeeIds\":[" + integerListJson(project.getEmployeeIds()) + "]}";
    }

    static String projectsJson(java.util.List<Project> projects) {
        StringBuilder json = new StringBuilder("{\"projects\":[");
        for (int index = 0; index < projects.size(); index++) {
            if (index > 0) json.append(',');
            json.append(projectJson(projects.get(index)));
        }
        return json.append("]}").toString();
    }

    static String result(boolean success, String message) {
        return "{\"success\":" + success + ",\"message\":" + JsonUtil.quote(message) + "}";
    }

    private static List<Integer> parseEmployeeIds(String[] values) {
        List<Integer> employeeIds = new ArrayList<>();
        if (values == null) return employeeIds;
        for (String value : values) for (String item : value.split(",")) {
            try {
                int employeeId = Integer.parseInt(item.trim());
                if (employeeId > 0 && !employeeIds.contains(employeeId)) employeeIds.add(employeeId);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Employee assignments must contain valid IDs");
            }
        }
        return employeeIds;
    }

    private static String integerListJson(List<Integer> values) {
        StringBuilder json = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) json.append(',');
            json.append(values.get(index));
        }
        return json.toString();
    }

    private static String trim(String value) { return value == null ? null : value.trim(); }
    private static String trimToNull(String value) { String trimmed = trim(value); return trimmed == null || trimmed.isEmpty() ? null : trimmed; }
}