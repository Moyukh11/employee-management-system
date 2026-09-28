package servlet;

import dao.EmployeeDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Employee;
import util.EmployeeValidator;

import java.io.IOException;

@WebServlet("/addEmployee")
public class AddEmployeeServlet extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Employee employee = ServletSupport.readEmployee(request);
            String error = EmployeeValidator.validate(employee);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletSupport.json(response, ServletSupport.message(error));
                return;
            }
            employeeDAO.addEmployee(employee);
            ServletSupport.json(response, ServletSupport.message("Employee added successfully."));
        } catch (NumberFormatException | java.sql.SQLException exception) {
            getServletContext().log("Unable to add employee", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ServletSupport.message("Unable to add employee. Please try again."));
        }
    }
}