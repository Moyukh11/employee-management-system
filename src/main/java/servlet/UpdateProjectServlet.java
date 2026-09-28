package servlet;

import dao.ProjectDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Project;
import util.ProjectValidator;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/updateProject")
public class UpdateProjectServlet extends HttpServlet {
    private final ProjectDAO projectDAO = new ProjectDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, "A valid project ID is required"));
            return;
        }
        try {
            Project project = ProjectServletSupport.readProject(request);
            project.setId(id);
            String error = ProjectValidator.validate(project);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletSupport.json(response, ProjectServletSupport.result(false, error));
                return;
            }
            if (projectDAO.getProjectById(project.getId()) == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletSupport.json(response, ProjectServletSupport.result(false, "Project not found"));
                return;
            }
            projectDAO.updateProject(project);
            ServletSupport.json(response, ProjectServletSupport.result(true, "Project updated successfully"));
        } catch (IllegalArgumentException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, exception.getMessage()));
        } catch (SQLException exception) {
            getServletContext().log("Unable to update project", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to update project. Please try again."));
        }
    }
}