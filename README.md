# Employee Management System

This project is a Java web application for managing employee records using Jakarta Servlet, JDBC, MySQL, HTML, CSS, and vanilla JavaScript. It is structured as a small MVC-style app with a servlet layer, DAO layer, and static frontend pages.

## Features

- Add new employees with validation
- Edit existing employee records
- Delete employees with a confirmation prompt
- Search employees by name, email, or department
- Filter employees by department
- View summary counts on the dashboard
- Display department names through a `LEFT JOIN` from MySQL

## Tech stack

- Java 17+
- Jakarta Servlet 6 / Tomcat 10.1+
- MySQL 8+
- JDBC
- HTML5, CSS3, plain JavaScript
- No Maven or framework dependency management used in this project

## Project structure

- `src/main/java/model` — `Employee`, `Department`, and `Project` model classes
- `src/main/java/dao` — database access logic for employees, departments, and projects
- `src/main/java/servlet` — request handlers for listing, searching, editing, creating, updating, and deleting employees and projects
- `src/main/java/util` — database connection, validation, and JSON helper utilities
- `src/main/webapp` — frontend pages, styles, scripts, and `WEB-INF/web.xml`
- `database.sql` — schema and seed data for the MySQL database
- `lib/` — local libraries such as `mysql-connector-j.jar`
- `build/` — compiled classes and generated web assets

## Database setup

1. Create the database and tables by running `database.sql` in MySQL.
2. The script creates:
   - `employee_db`
   - `department` table
   - `employee` table with a foreign key to `department`
   - `project` table for project management records
   - `employee_project` join table for employee/project assignments
   - default department records: IT, HR, Finance, Sales, and Marketing

Example:

```sql
CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;
```

## Configuration

Database settings are centralized in `src/main/java/util/DBConnection.java`.

The project reads these environment variables first:

- `EMS_DB_URL`
- `EMS_DB_USERNAME`
- `EMS_DB_PASSWORD`

If they are not defined, it uses the defaults:

- URL: `jdbc:mysql://localhost:3306/employee_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- Username: `root`
- Password: empty string

Example on Windows PowerShell:

```powershell
$env:EMS_DB_USERNAME = "root"
$env:EMS_DB_PASSWORD = "your_password"
```

## Authentication

The application uses session-based authentication with PBKDF2 password hashes. Run `database.sql` once after the authentication changes to create the `users` table and seed the local development accounts:

- `admin` / `Admin@123` — `ADMIN`, full read/write access
- `hr` / `Hr@123` — `HR`, read-only access

Unauthenticated HTML requests redirect to `login.html`; API requests return HTTP 401. Mutating employee, project, and assignment requests require the `ADMIN` role and return HTTP 403 for `HR` users. The dashboard sidebar stays fixed on desktop and slides in as a drawer on smaller screens.

## Deployment steps

1. Install MySQL and create the schema using `database.sql`.
2. Install Tomcat 10.1 or later.
3. Put `mysql-connector-j.jar` in Tomcat's `lib` folder or in the deployed app's `WEB-INF/lib` directory.
4. Compile the Java classes and output them to `build/classes`.
5. Copy the contents of `src/main/webapp` to the web application root, or deploy the project as a WAR/expanded app in Tomcat.
6. Ensure the compiled classes are available under `WEB-INF/classes`.
7. Start Tomcat and open the application in a browser, for example:

```text
http://localhost:8080/EMS/
```

## Example compilation command

Example Windows command from the project root:

```text
javac -cp "C:\path\to\tomcat\lib\jakarta.servlet-api.jar;lib\mysql-connector-j.jar" -d build\classes src\main\java\model\*.java src\main\java\util\*.java src\main\java\dao\*.java src\main\java\servlet\*.java
```

If your Tomcat installation contains the servlet API as a different JAR name, use the matching path from your environment.

## Request flow

The servlet layer exposes these endpoints:

- `GET /listEmployees` — returns employee data and departments for the dashboard/table
- `GET /searchEmployee?keyword=Rahul` — searches by name, email, or department
- `GET /editEmployee?id=1` — loads a single employee for the edit form
- `POST /addEmployee` — inserts a new employee
- `POST /updateEmployee` — updates an employee by ID
- `POST /deleteEmployee?id=1` — deletes an employee by ID
- `GET /listProjects` — returns all projects
- `GET /searchProject?keyword=Payroll` — searches project name, description, and status
- `GET /editProject?id=1` — loads one project for editing
- `POST /addProject` — creates a project
- `POST /updateProject` — updates a project by ID
- `POST /deleteProject?id=1` — deletes a project by ID

Projects can be assigned to multiple employees from the Assignments page or add/edit project form. Assignments are stored in `employee_project` with role and assigned date, and the `employee_project` foreign keys cascade when an employee or project is deleted. For an already-created older join table, migrate it with: `ALTER TABLE employee_project ADD COLUMN id INT PRIMARY KEY AUTO_INCREMENT FIRST, ADD COLUMN role VARCHAR(100) NOT NULL DEFAULT 'Project team member', ADD COLUMN assigned_date DATE NOT NULL DEFAULT (CURRENT_DATE), ADD UNIQUE KEY unique_employee_project (employee_id, project_id);`.

The frontend sends AJAX requests using `fetch()`, and the backend responds with JSON messages for success or validation issues.

## Pages

- `src/main/webapp/index.html` — dashboard with summary cards
- `src/main/webapp/employees.html` — employee directory with search and filtering
- `src/main/webapp/add-employee.html` — form for creating employees
- `src/main/webapp/edit-employee.html` — form for updating employees
- `src/main/webapp/projects.html` — project directory with search and status filtering
- `src/main/webapp/add-project.html` — form for creating projects
- `src/main/webapp/edit-project.html` — form for updating projects

## Notes

- Frontend validation improves user experience, but server-side validation is also enforced in the Java servlet flow.
- The app uses `PreparedStatement` throughout the DAO layer to reduce SQL injection risk.
- Connection, statement, and result set resources are managed with try-with-resources.

## License

This project is intended for learning and local development use.
