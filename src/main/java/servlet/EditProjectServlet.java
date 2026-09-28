package servlet;

import dao.ProjectDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Project;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/editProject")
public class EditProjectServlet extends HttpServlet {
    private final ProjectDAO projectDAO = new ProjectDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Project project = projectDAO.getProjectById(Integer.parseInt(request.getParameter("id")));
            if (project == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletSupport.json(response, ProjectServletSupport.result(false, "Project not found"));
                return;
            }
            ServletSupport.json(response, ProjectServletSupport.projectJson(project));
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, "A valid project ID is required"));
        } catch (SQLException exception) {
            getServletContext().log("Unable to load project", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to load project. Please try again."));
        }
    }
}