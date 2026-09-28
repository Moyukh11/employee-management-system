package servlet;

import dao.ProjectDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/deleteProject")
public class DeleteProjectServlet extends HttpServlet {
    private final ProjectDAO projectDAO = new ProjectDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (projectDAO.getProjectById(id) == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletSupport.json(response, ProjectServletSupport.result(false, "Project not found"));
                return;
            }
            projectDAO.deleteProject(id);
            ServletSupport.json(response, ProjectServletSupport.result(true, "Project deleted successfully"));
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, ProjectServletSupport.result(false, "A valid project ID is required"));
        } catch (SQLException exception) {
            getServletContext().log("Unable to delete project", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to delete project. Please try again."));
        }
    }
}