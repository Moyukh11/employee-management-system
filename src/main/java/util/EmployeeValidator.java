package util;

import model.Employee;

import java.util.regex.Pattern;

public final class EmployeeValidator {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+() -]{7,15}$");

    private EmployeeValidator() {
    }

    public static String validate(Employee employee) {
        if (employee.getName() == null || employee.getName().trim().isEmpty()) return "Name is required.";
        if (employee.getEmail() == null || !EMAIL.matcher(employee.getEmail()).matches()) return "Enter a valid email address.";
        if (employee.getPhone() != null && !employee.getPhone().isBlank() && !PHONE.matcher(employee.getPhone()).matches()) return "Enter a valid phone number.";
        if (employee.getDepartmentId() <= 0) return "Select a department.";
        if (employee.getSalary() == null || employee.getSalary().signum() <= 0) return "Salary must be greater than zero.";
        return null;
    }
}