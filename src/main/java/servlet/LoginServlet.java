package servlet;

import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            User user = username == null || password == null ? null : userDAO.findByUsername(username.trim());
            if (user == null || !util.PasswordUtil.matches(password, user.getPasswordHash())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ServletSupport.json(response, "{\"success\":false,\"message\":\"Invalid username or password\"}");
                return;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            ServletSupport.json(response, "{\"success\":true,\"message\":\"Login successful\",\"username\":" + util.JsonUtil.quote(user.getUsername()) + ",\"role\":" + util.JsonUtil.quote(user.getRole()) + "}");
        } catch (SQLException exception) {
            getServletContext().log("Unable to log in", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, "{\"success\":false,\"message\":\"Unable to log in. Please try again.\"}");
        }
    }
}