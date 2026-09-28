package servlet;

import dao.DepartmentDAO;
import model.Department;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/listDepartments")
public class ListDepartmentServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            List<Department> departments =
                    departmentDAO.getAllDepartments();

            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < departments.size(); i++) {

                Department department = departments.get(i);

                json.append("{")
                    .append("\"id\":")
                    .append(department.getId())
                    .append(",");

                json.append("\"name\":\"")
                    .append(escapeJson(department.getName()))
                    .append("\",");

                json.append("\"employeeCount\":")
                    .append(department.getEmployeeCount());

                json.append("}");

                if (i < departments.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");

            response.getWriter().write(json.toString());

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to load departments\"}"
            );
        }
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
