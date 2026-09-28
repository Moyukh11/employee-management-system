package servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        String role = session == null ? null : (String) session.getAttribute("role");
        if (role == null) {
            if (isApi(path)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ServletSupport.json(httpResponse, "{\"success\":false,\"message\":\"Please log in first\"}");
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
            }
            return;
        }
        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) && !"ADMIN".equalsIgnoreCase(role) && !"/logout".equals(path)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            if (isApi(path)) ServletSupport.json(httpResponse, "{\"success\":false,\"message\":\"Administrator access is required\"}");
            else httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Administrator access is required");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return "/login.html".equals(path) || "/signup.html".equals(path) || "/login".equals(path) || "/signup".equals(path) || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/favicon");
    }

    private boolean isApi(String path) {
        return path.startsWith("/") && !path.endsWith(".html") && !path.startsWith("/css/") && !path.startsWith("/js/");
    }
}