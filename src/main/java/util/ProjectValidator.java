package util;

import model.Project;

import java.util.Set;

public final class ProjectValidator {
    private static final Set<String> STATUSES = Set.of("Active", "Completed", "On Hold", "Cancelled");

    private ProjectValidator() {
    }

    public static String validate(Project project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) return "Project name is required";
        if (project.getName().trim().length() > 100) return "Project name must be 100 characters or fewer";
        if (project.getStatus() == null || project.getStatus().isBlank()) return "Status is required";
        if (!STATUSES.contains(project.getStatus())) return "Status must be Active, Completed, On Hold, or Cancelled";
        if (project.getEndDate() != null && project.getStartDate() != null && project.getEndDate().isBefore(project.getStartDate())) return "End date cannot be before start date";
        return null;
    }
}