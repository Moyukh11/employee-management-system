CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(500) NOT NULL,
    role ENUM('ADMIN', 'HR') NOT NULL DEFAULT 'HR'
);

INSERT IGNORE INTO users (username, password_hash, role) VALUES
    ('admin', '120000$WVPhAa2bYRW5Au5vrwaZKg==$qO5cxsVBVdekdKtlcxU70GM1UD0X+QsdXnCaxubQhyc=', 'ADMIN'),
    ('hr', '120000$kzNq3xWIctGUXvJ2lCFdLg==$Sq1ySjMqf8awYccMCMFbIFuqpVaCJ5rKSVeV3dl2Wis=', 'HR');

CREATE TABLE IF NOT EXISTS department (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS employee (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15),
    department_id INT,
    salary DECIMAL(10,2),
    FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE IF NOT EXISTS project (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'Active'
);

CREATE TABLE IF NOT EXISTS employee_project (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    project_id INT NOT NULL,
    role VARCHAR(100) NOT NULL,
    assigned_date DATE NOT NULL,
    UNIQUE KEY unique_employee_project (employee_id, project_id),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

INSERT IGNORE INTO department (name) VALUES
    ('IT'), ('HR'), ('Finance'), ('Sales'), ('Marketing');