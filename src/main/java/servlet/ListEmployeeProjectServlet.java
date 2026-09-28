package servlet;

import dao.EmployeeProjectDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/employeeProjects")
public class ListEmployeeProjectServlet extends HttpServlet {
    private final EmployeeProjectDAO assignmentDAO = new EmployeeProjectDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String employeeId = request.getParameter("employeeId");
            String projectId = request.getParameter("projectId");
            String keyword = request.getParameter("keyword");
            if (keyword != null && !keyword.isBlank()) {
                ServletSupport.json(response, EmployeeProjectServletSupport.assignmentsJson(assignmentDAO.searchAssignments(keyword.trim())));
            } else if (employeeId != null && !employeeId.isBlank()) {
                ServletSupport.json(response, EmployeeProjectServletSupport.assignmentsJson(assignmentDAO.getAssignmentsByEmployee(Integer.parseInt(employeeId))));
            } else if (projectId != null && !projectId.isBlank()) {
                ServletSupport.json(response, EmployeeProjectServletSupport.assignmentsJson(assignmentDAO.getAssignmentsByProject(Integer.parseInt(projectId))));
            } else {
                ServletSupport.json(response, EmployeeProjectServletSupport.assignmentsJson(assignmentDAO.getAllAssignments()));
            }
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Employee or project ID must be valid"));
        } catch (SQLException exception) {
            getServletContext().log("Unable to list employee project assignments", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to load assignments"));
        }
    }
}