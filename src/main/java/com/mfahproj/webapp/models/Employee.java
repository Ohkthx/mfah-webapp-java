package com.mfahproj.webapp.models;

public class Employee {
    // Various access levels for employees.
    public enum AccessLevels {
        Normal,
        Supervisor,
        Manager,
    }

    private int employeeId = -1;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String phoneNumber;
    private String emailAddress;
    private String password;
    private double salary;
    private int museumId = 1;
    private int supervisorId = 1;
    private String accessLevel;
    private java.sql.Date lastLogin;

    // Create a blank employee.
    public Employee() {
    };

    // Used to create a new employee.
    // employeeId is created by AUTOINCREMENT.
    public Employee(String firstName, String lastName, String jobTitle, String phoneNumber,
            String emailAddress, String password, double salary, int museumId, int supervisorId, AccessLevels access) {
        this(-1, -1, firstName, lastName, jobTitle, phoneNumber, emailAddress, password, salary, supervisorId, access,
                new java.sql.Date(System.currentTimeMillis()));
    }

    public Employee(int employeeId, int museumId, String firstName, String lastName, String jobTitle,
            String phoneNumber,
            String emailAddress, String password, double salary, int supervisorId, AccessLevels access,
            java.sql.Date lastLogin) {
        this.setEmployeeId(employeeId);
        this.setMuseumId(museumId);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setJobTitle(jobTitle);
        this.setPhoneNumber(phoneNumber);
        this.setEmailAddress(emailAddress);
        this.setPassword(password);
        this.setSalary(salary);
        this.setSupervisorId(supervisorId);
        this.setAccessLevel(access.name());
        this.setLastLogin(lastLogin);
    }

    public int getEmployeeId() {
        return this.employeeId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getPassword() {
        return this.password;
    }

    public double getSalary() {
        return this.salary;
    }

    public int getMuseumId() {
        return this.museumId;
    }

    public int getSupervisorId() {
        return this.supervisorId;
    }

    public String getAccessLevel() {
        return this.accessLevel;
    }

    public java.sql.Date getLastLogin() {
        return this.lastLogin;
    }

    // Setters
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setMuseumId(int museumId) {
        this.museumId = museumId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void setLastLogin(java.sql.Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}