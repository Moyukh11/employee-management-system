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

@WebServlet("/addProject")
public class AddProjectServlet extends HttpServlet {
    private final ProjectDAO projectDAO = new ProjectDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Project project = ProjectServletSupport.readProject(request);
            String error = ProjectValidator.validate(project);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletSupport.json(response, ProjectServletSupport.result(false, error));
                return;
            }
            projectDAO.addProject(project);
            response.setStatus(HttpServletResponse.SC_CREATED);
            ServletSupport.json(response, ProjectServletSupport.result(true, "Project added successfully"));
        } catch (IllegalArgumentException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, exception.getMessage()));
        } catch (SQLException exception) {
            getServletContext().log("Unable to add project", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to add project. Please try again."));
        }
    }
}