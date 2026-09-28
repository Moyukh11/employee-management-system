package model;

import java.math.BigDecimal;

public class Employee {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int departmentId;
    private String departmentName;
    private BigDecimal salary;

    public Employee() {
    }

    public Employee(String name, String email, String phone, int departmentId, BigDecimal salary) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
        this.salary = salary;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
}