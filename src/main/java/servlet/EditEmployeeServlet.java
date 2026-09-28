package servlet;

import dao.EmployeeDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Employee;
import util.JsonUtil;

import java.io.IOException;

@WebServlet("/editEmployee")
public class EditEmployeeServlet extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Employee employee = employeeDAO.getEmployeeById(Integer.parseInt(request.getParameter("id")));
            if (employee == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletSupport.json(response, ServletSupport.message("Employee not found."));
                return;
            }
            String json = "{\"id\":" + employee.getId() + ",\"name\":" + JsonUtil.quote(employee.getName()) + ",\"email\":" + JsonUtil.quote(employee.getEmail()) + ",\"phone\":" + JsonUtil.quote(employee.getPhone()) + ",\"departmentId\":" + employee.getDepartmentId() + ",\"salary\":" + employee.getSalary().toPlainString() + "}";
            ServletSupport.json(response, json);
        } catch (NumberFormatException | java.sql.SQLException exception) {
            getServletContext().log("Unable to load employee", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ServletSupport.message("Unable to load employee. Please try again."));
        }
    }
}