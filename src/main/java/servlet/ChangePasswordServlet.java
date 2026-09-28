package servlet;

import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import util.PasswordUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/changePassword")
public class ChangePasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);

        /*
         * Check whether the user is logged in.
         */
        if (session == null ||
                !(session.getAttribute("userId") instanceof Integer)) {

            sendJson(
                    response,
                    401,
                    false,
                    "Please log in first."
            );

            return;
        }

        String currentPassword =
                request.getParameter("currentPassword");

        String newPassword =
                request.getParameter("newPassword");


        /*
         * Validate input.
         */
        if (currentPassword == null ||
                currentPassword.isEmpty() ||
                newPassword == null ||
                newPassword.isEmpty()) {

            sendJson(
                    response,
                    400,
                    false,
                    "All password fields are required."
            );

            return;
        }


        /*
         * Password must contain at least 8 characters.
         */
        if (newPassword.length() < 8 ||
                newPassword
                        .getBytes(StandardCharsets.UTF_8)
                        .length > 1024) {

            sendJson(
                    response,
                    400,
                    false,
                    "The new password must contain at least 8 characters and be no longer than 1024 UTF-8 bytes."
            );

            return;
        }


        /*
         * New password must be different.
         */
        if (currentPassword.equals(newPassword)) {

            sendJson(
                    response,
                    400,
                    false,
                    "The new password must differ from the current password."
            );

            return;
        }


        int userId =
                (Integer) session.getAttribute("userId");


        try {

            /*
             * Get the currently logged-in user.
             */
            User user =
                    userDAO.getUserById(userId);


            if (user == null ||
                    user.getPasswordHash() == null) {

                sendJson(
                        response,
                        401,
                        false,
                        "User account not found."
                );

                return;
            }


            /*
             * Verify the old password.
             */
            if (!PasswordUtil.matches(
                    currentPassword,
                    user.getPasswordHash())) {

                sendJson(
                        response,
                        400,
                        false,
                        "Current password is incorrect."
                );

                return;
            }


            /*
             * Generate a new secure PBKDF2 hash.
             */
            String newHash =
                    PasswordUtil.hash(newPassword);


            /*
             * Update the password in MySQL.
             */
            boolean updated =
                    userDAO.updatePassword(
                            userId,
                            newHash);


            if (!updated) {

                sendJson(
                        response,
                        500,
                        false,
                        "Password could not be updated."
                );

                return;
            }


            sendJson(
                    response,
                    200,
                    true,
                    "Password changed successfully."
            );


        } catch (Exception exception) {

            getServletContext().log(
                    "Error changing password for user ID "
                            + userId,
                    exception
            );


            sendJson(
                    response,
                    500,
                    false,
                    "An unexpected error occurred."
            );
        }
    }


    private void sendJson(
            HttpServletResponse response,
            int status,
            boolean success,
            String message)
            throws IOException {

        response.setStatus(status);

        String escaped =
                message
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\r", "\\r")
                        .replace("\n", "\\n");

        response.getWriter().write(
                "{\"success\":"
                        + success
                        + ",\"message\":\""
                        + escaped
                        + "\"}"
        );
    }
}
