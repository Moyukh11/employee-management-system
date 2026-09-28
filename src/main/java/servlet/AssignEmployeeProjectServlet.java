package servlet;

import dao.EmployeeDAO;
import dao.EmployeeProjectDAO;
import dao.ProjectDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.EmployeeProject;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/assignEmployeeProject")
public class AssignEmployeeProjectServlet extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final EmployeeProjectDAO assignmentDAO = new EmployeeProjectDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            EmployeeProject assignment = EmployeeProjectServletSupport.readAssignment(request);
            if (employeeDAO.getEmployeeById(assignment.getEmployeeId()) == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletSupport.json(response, ProjectServletSupport.result(false, "Employee not found"));
                return;
            }
            if (projectDAO.getProjectById(assignment.getProjectId()) == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletSupport.json(response, ProjectServletSupport.result(false, "Project not found"));
                return;
            }
            if (assignmentDAO.isEmployeeAssignedToProject(assignment.getEmployeeId(), assignment.getProjectId())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                ServletSupport.json(response, ProjectServletSupport.result(false, "Employee is already assigned to this project"));
                return;
            }
            assignmentDAO.assignEmployeeToProject(assignment);
            ServletSupport.json(response, ProjectServletSupport.result(true, "Employee assigned to project successfully"));
        } catch (IllegalArgumentException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, exception.getMessage()));
        } catch (SQLException exception) {
            getServletContext().log("Unable to assign employee to project", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to assign employee to project"));
        }
    }
}