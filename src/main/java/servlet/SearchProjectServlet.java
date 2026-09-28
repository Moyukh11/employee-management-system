package servlet;

import dao.ProjectDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/searchProject")
public class SearchProjectServlet extends HttpServlet {
    private final ProjectDAO projectDAO = new ProjectDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String keyword = request.getParameter("keyword");
            ServletSupport.json(response, ProjectServletSupport.projectsJson(projectDAO.searchProjects(keyword == null ? "" : keyword.trim())));
        } catch (SQLException exception) {
            getServletContext().log("Unable to search projects", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, ProjectServletSupport.result(false, "Unable to search projects. Please try again."));
        }
    }
}