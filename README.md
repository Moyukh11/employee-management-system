# Employee Management System

A full-stack Java web application for managing employees, departments, projects, and employee-project assignments. The application uses Jakarta Servlets, JDBC, MySQL, HTML5, CSS3, and vanilla JavaScript, with session-based authentication and role-based access control.

## 🚀 Live Demo

**Live Application:** https://employee-management-system-3giq.onrender.com/

**GitHub Repository:** https://github.com/Moyukh11/employee-management-system

> The application is deployed on Render and uses Aiven MySQL as its cloud database.

---

## ✨ Features

### Employee Management

* Add new employees with server-side and frontend validation
* Edit employee information
* Delete employees with confirmation
* Search employees by name, email, or department
* Filter employees by department
* Display employee salary and department information

### Department Management

* View all departments
* Add and delete departments
* Associate employees with departments
* Display department names using SQL joins

### Project Management

* Create and edit projects
* Delete projects
* Search projects by name, description, or status
* Filter projects by status
* Track project start and end dates

### Employee-Project Assignments

* Assign employees to projects
* Define an employee's role within a project
* Store assignment dates
* View employee-project relationships
* Prevent duplicate employee-project assignments
* Automatically remove assignments when an employee or project is deleted

### Authentication & Security

* Session-based authentication
* Login and logout functionality
* User signup
* ADMIN and HR roles
* Role-based access control
* PBKDF2 password hashing
* Protected API endpoints
* ADMIN-only modification operations
* PreparedStatements to reduce SQL injection risk

### Dashboard

* Total employees
* Total departments
* Total projects
* Total project assignments
* Responsive navigation sidebar
* Desktop sidebar and mobile drawer layout

---

## 🛠️ Tech Stack

| Technology          | Purpose                              |
| ------------------- | ------------------------------------ |
| Java 17+            | Backend programming                  |
| Jakarta Servlet 6   | HTTP request handling                |
| Apache Tomcat 10.1+ | Web server / servlet container       |
| JDBC                | Database connectivity                |
| MySQL 8+            | Relational database                  |
| Aiven MySQL         | Production cloud database            |
| HTML5               | Frontend structure                   |
| CSS3                | Styling and responsive design        |
| JavaScript          | Frontend logic and API communication |
| Fetch API           | AJAX requests                        |
| Docker              | Production deployment                |
| Render              | Cloud application hosting            |

**Build approach:** Manual Java compilation, without Maven or Spring Boot.

---

## 🏗️ Architecture

The application follows a simple MVC-style architecture:

```text
Frontend
   │
   │ Fetch API / HTTP Requests
   ▼
Jakarta Servlets
   │
   ▼
DAO Layer
   │
   ▼
JDBC
   │
   ▼
MySQL Database
```

### Main layers

```text
Model
 ├── Employee
 ├── Department
 ├── Project
 ├── EmployeeProject
 └── User

DAO
 ├── EmployeeDAO
 ├── DepartmentDAO
 ├── ProjectDAO
 ├── EmployeeProjectDAO
 └── UserDAO

Servlet
 ├── Authentication
 ├── Employee operations
 ├── Department operations
 ├── Project operations
 └── Assignment operations

Util
 ├── DBConnection
 ├── PasswordUtil
 ├── EmployeeValidator
 ├── ProjectValidator
 └── JsonUtil

Frontend
 ├── HTML pages
 ├── CSS
 └── JavaScript
```

---

## 📁 Project Structure

```text
EMS/
├── .vscode/
├── build/
│   ├── WEB-INF/
│   ├── classes/
│   ├── css/
│   ├── js/
│   └── *.html
│
├── lib/
│   └── mysql-connector-j.jar
│
├── src/
│   └── main/
│       ├── java/
│       │   ├── dao/
│       │   ├── model/
│       │   ├── servlet/
│       │   └── util/
│       │
│       └── webapp/
│           ├── css/
│           ├── js/
│           ├── WEB-INF/
│           └── *.html
│
├── database.sql
├── Dockerfile
├── README.md
└── .gitignore
```

### Important directories

* `src/main/java/model` — Java model classes
* `src/main/java/dao` — database access logic
* `src/main/java/servlet` — HTTP request handlers
* `src/main/java/util` — database, validation, password, and JSON utilities
* `src/main/webapp` — HTML, CSS, JavaScript, and web configuration
* `build` — compiled classes and deployment-ready web files
* `lib` — external JAR dependencies

---

## 🗄️ Database

The application uses MySQL with the following main tables:

```text
users
department
employee
project
employee_project
```

### Database relationships

```text
department
     │
     │ 1:N
     ▼
employee
     │
     │ N:M
     ▼
employee_project
     ▲
     │ N:M
     │
project
```

The `employee_project` table acts as a junction table between employees and projects.

### Database setup

For local development:

```sql
CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;
```

Then run:

```text
database.sql
```

The database script creates the required tables and default department records.

---

## 🔐 Authentication

The application provides session-based authentication.

### Roles

| Role  | Access                |
| ----- | --------------------- |
| ADMIN | Read and write access |
| HR    | Read-only access      |

Password storage uses **PBKDF2WithHmacSHA256** with salted password hashes.

Protected operations include:

* Adding employees
* Updating employees
* Deleting employees
* Adding projects
* Updating projects
* Deleting projects
* Managing employee-project assignments

Unauthenticated API requests return HTTP `401`, while unauthorized role-based operations return HTTP `403`.

---

## ⚙️ Configuration

Database configuration is centralized in:

```text
src/main/java/util/DBConnection.java
```

The application reads these environment variables:

```text
EMS_DB_URL
EMS_DB_USERNAME
EMS_DB_PASSWORD
```

### Local database

Example:

```text
EMS_DB_URL=jdbc:mysql://localhost:3306/employee_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
EMS_DB_USERNAME=root
EMS_DB_PASSWORD=your_password
```

### Windows PowerShell

```powershell
$env:EMS_DB_USERNAME = "root"
$env:EMS_DB_PASSWORD = "your_password"
```

### Production

The production application uses **Aiven MySQL** through environment variables configured in Render.

The database password is not stored in the source code.

---

## ☁️ Deployment

The production application is deployed using:

```text
GitHub
   │
   ▼
Render
   │
   ▼
Docker + Tomcat 10.1
   │
   ▼
Java Servlet Application
   │
   ▼
Aiven MySQL
```

### Deployment stack

* GitHub — source code repository
* Docker — application container
* Apache Tomcat 10.1 — servlet container
* Render — cloud hosting
* Aiven — cloud MySQL database

### Docker configuration

The application is deployed as the Tomcat root application:

```dockerfile
FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/ROOT

RUN sed -i 's/port="8080"/port="10000"/' /usr/local/tomcat/conf/server.xml

COPY build/ /usr/local/tomcat/webapps/ROOT/

EXPOSE 10000

CMD ["catalina.sh", "run"]
```

The application therefore runs at:

```text
https://employee-management-system-3giq.onrender.com/
```

---

## 🧪 Local Compilation

From the project root:

```bash
rm -rf build/classes
mkdir -p build/classes

javac --release 17 \
-cp "lib/*" \
-d build/classes \
$(find src/main/java -name "*.java")
```

Copy compiled classes:

```bash
rm -rf build/WEB-INF/classes
mkdir -p build/WEB-INF/classes

cp -r build/classes/* build/WEB-INF/classes/
```

Copy frontend files:

```bash
cp -r src/main/webapp/* build/
```

Copy the MySQL connector:

```bash
mkdir -p build/WEB-INF/lib
cp lib/mysql-connector-j.jar build/WEB-INF/lib/
```

---

## 🔌 API Endpoints

### Employee

```text
GET  /listEmployees
GET  /searchEmployee?keyword=Rahul
GET  /editEmployee?id=1
POST /addEmployee
POST /updateEmployee
POST /deleteEmployee?id=1
```

### Department

```text
GET  /listDepartments
POST /addDepartment
POST /deleteDepartment
```

### Project

```text
GET  /listProjects
GET  /searchProject?keyword=Payroll
GET  /editProject?id=1
POST /addProject
POST /updateProject
POST /deleteProject?id=1
```

### Employee-Project Assignment

```text
GET  /listEmployeeProjects
POST /assignEmployeeProject
```

### Authentication

```text
POST /login
POST /logout
POST /signup
GET  /currentUser
POST /changePassword
```

---

## 🔄 Request Flow

Example employee creation flow:

```text
User fills Add Employee form
          │
          ▼
Frontend JavaScript validation
          │
          ▼
Fetch API POST request
          │
          ▼
AddEmployeeServlet
          │
          ▼
EmployeeValidator
          │
          ▼
EmployeeDAO
          │
          ▼
PreparedStatement
          │
          ▼
MySQL
          │
          ▼
JSON response
          │
          ▼
Frontend updates UI
```

---

## 📄 Main Pages

```text
login.html
signup.html
index.html
employees.html
add-employee.html
edit-employee.html
departments.html
projects.html
add-project.html
edit-project.html
assign-project.html
settings.html
```

---

## 🔒 Security Practices

The application implements several basic security practices:

* Password hashing with PBKDF2
* Salted password storage
* Session-based authentication
* Role-based authorization
* PreparedStatements for database queries
* Server-side validation
* Frontend validation
* Environment variables for database credentials
* Database credentials are not hardcoded in `DBConnection.java`

Sensitive local database dump files are excluded through `.gitignore`.

---

## 📊 Current Cloud Database

The deployed Aiven database contains:

```text
Departments:  6
Employees:    3
Projects:     3
Assignments:  6
```

These records are stored in the cloud database and are accessed by the Render deployment through JDBC.

---

## 🚀 Future Improvements

Possible future enhancements include:

* Employee profile photos
* Pagination for large employee lists
* Advanced dashboard analytics
* Export employees to CSV/PDF
* Email notifications
* Audit logs
* Password reset via email
* Admin user management
* Automated CI/CD deployment
* Unit and integration testing

---

## 📝 Notes

* Frontend validation improves user experience, but server-side validation is also enforced.
* DAO classes use `PreparedStatement` for database operations.
* JDBC resources are managed using try-with-resources.
* The application does not use Maven, Spring Boot, Hibernate, or JPA.
* Production database credentials are supplied through Render environment variables.
* Local database dump files are intentionally excluded from Git tracking.

---

## 📜 License

This project is intended for educational, portfolio, and demonstration purposes.
