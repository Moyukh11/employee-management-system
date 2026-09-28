package servlet;

import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.PasswordUtil;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        if (username == null || !username.trim().matches("[A-Za-z0-9_.-]{3,80}")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, "{\"success\":false,\"message\":\"Username must be 3-80 letters, numbers, dots, underscores, or hyphens\"}");
            return;
        }
        if (password == null || password.length() < 8 || !password.equals(confirmPassword)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletSupport.json(response, "{\"success\":false,\"message\":\"Passwords must match and contain at least 8 characters\"}");
            return;
        }
        try {
            userDAO.create(username.trim(), PasswordUtil.hash(password));
            ServletSupport.json(response, "{\"success\":true,\"message\":\"Account created. You can now sign in.\"}");
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 1062) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                ServletSupport.json(response, "{\"success\":false,\"message\":\"Username is already registered\"}");
                return;
            }
            getServletContext().log("Unable to sign up", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletSupport.json(response, "{\"success\":false,\"message\":\"Unable to create account. Please try again.\"}");
        }
    }
}