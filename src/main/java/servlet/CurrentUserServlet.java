package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/currentUser")
public class CurrentUserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        ServletSupport.json(response, "{\"username\":" + util.JsonUtil.quote((String) session.getAttribute("username")) + ",\"role\":" + util.JsonUtil.quote((String) session.getAttribute("role")) + "}");
    }
}