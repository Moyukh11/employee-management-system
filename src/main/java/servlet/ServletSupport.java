package servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Employee;
import util.JsonUtil;

import java.io.IOException;
import java.math.BigDecimal;

final class ServletSupport {
    private ServletSupport() {
    }

    static Employee readEmployee(HttpServletRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getParameter("name"));
        employee.setEmail(request.getParameter("email"));
        employee.setPhone(request.getParameter("phone"));
        employee.setDepartmentId(Integer.parseInt(request.getParameter("departmentId")));
        employee.setSalary(new BigDecimal(request.getParameter("salary")));
        return employee;
    }

    static void json(HttpServletResponse response, String body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }

    static String message(String message) {
        return "{\"message\":" + JsonUtil.quote(message) + "}";
    }
}