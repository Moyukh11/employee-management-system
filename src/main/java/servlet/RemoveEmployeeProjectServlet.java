package servlet;

import dao.EmployeeProjectDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/removeEmployeeProject")
public class RemoveEmployeeProjectServlet extends HttpServlet {
    private final EmployeeProjectDAO assignmentDAO = new EmployeeProjectDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (id <= 0) throw new NumberFormatException();
            assignmentDAO.removeEmployeeFromProject(id);
            ServletSupport.json(response, ProjectServletSupport.result(true, "Employee removed from project successfully"));
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, "A valid assignment ID is required"));
        } catch (SQLException exception) {
            getServletContext().log("Unable to remove employee from project", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to remove employee from project"));
        }
    }
}