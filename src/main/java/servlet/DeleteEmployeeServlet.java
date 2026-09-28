package servlet;

import dao.EmployeeDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/deleteEmployee")
public class DeleteEmployeeServlet extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            employeeDAO.deleteEmployee(Integer.parseInt(request.getParameter("id")));
            ServletSupport.json(response, ServletSupport.message("Employee deleted successfully."));
        } catch (NumberFormatException | java.sql.SQLException exception) {
            getServletContext().log("Unable to delete employee", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ServletSupport.message("Unable to delete employee. Please try again."));
        }
    }
}