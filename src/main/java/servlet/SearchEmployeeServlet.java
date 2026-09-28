package servlet;

import dao.EmployeeDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/searchEmployee")
public class SearchEmployeeServlet extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            StringBuilder json = new StringBuilder("{\"employees\":[");
            ListEmployeeServlet.appendEmployees(json, employeeDAO.searchEmployees(request.getParameter("keyword") == null ? "" : request.getParameter("keyword")));
            json.append("]}");
            ServletSupport.json(response, json.toString());
        } catch (java.sql.SQLException exception) {
            getServletContext().log("Unable to search employees", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ServletSupport.message("Unable to search employees. Please try again."));
        }
    }
}