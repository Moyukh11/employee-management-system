package servlet;

import dao.DepartmentDAO;
import dao.EmployeeDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Department;
import model.Employee;
import util.JsonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/listEmployees")
public class ListEmployeeServlet extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String department = request.getParameter("departmentId");
            List<Employee> employees = department == null || department.isBlank() ? employeeDAO.getAllEmployees() : employeeDAO.getEmployeesByDepartment(Integer.parseInt(department));
            StringBuilder json = new StringBuilder("{\"employees\":[");
            appendEmployees(json, employees);
            json.append("],\"departments\":[");
            List<Department> departments = departmentDAO.getAllDepartments();
            for (int index = 0; index < departments.size(); index++) {
                if (index > 0) json.append(',');
                Department item = departments.get(index);
                json.append("{\"id\":").append(item.getId()).append(",\"name\":").append(JsonUtil.quote(item.getName())).append('}');
            }
            json.append("]}");
            ServletSupport.json(response, json.toString());
        } catch (NumberFormatException | SQLException exception) {
            getServletContext().log("Unable to list employees", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ServletSupport.message("Unable to load employees. Please try again."));
        }
    }

    static void appendEmployees(StringBuilder json, List<Employee> employees) {
        for (int index = 0; index < employees.size(); index++) {
            if (index > 0) json.append(',');
            Employee employee = employees.get(index);
            json.append("{\"id\":").append(employee.getId())
                    .append(",\"name\":").append(JsonUtil.quote(employee.getName()))
                    .append(",\"email\":").append(JsonUtil.quote(employee.getEmail()))
                    .append(",\"phone\":").append(JsonUtil.quote(employee.getPhone()))
                    .append(",\"departmentId\":").append(employee.getDepartmentId())
                    .append(",\"department\":").append(JsonUtil.quote(employee.getDepartmentName()))
                    .append(",\"salary\":").append(employee.getSalary() == null ? "null" : employee.getSalary().toPlainString()).append('}');
        }
    }
}