package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterEmployeeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            get(exchange);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            post(exchange);
        }
    }

    // Handles GET requests from the client.
    private void get(HttpExchange exchange) throws IOException {
        // Show register form for a new member.
        String response = Utils.dynamicNavigator(exchange, "employee/register.html");

        // Edit the placeholders with dynamic text.
        response = response.replace("{{credentials}}", "");

        Utils.sendResponse(exchange, response);
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to create a new user.
        Map<String, String> form = Utils.parseForm(formData);
        Employee employee = RegisterEmployeeHandler.createEmployee(form);

        // Load register form.
        String response = Utils.dynamicNavigator(exchange, "employee/register.html");
        switch (Database.createEmployee(employee)) {
            case SUCCESS:
                // Redirect to success page.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", employee.getEmailAddress());
                return;
            case DUPLICATE:
                // Duplicate employee detected, point them to login page.
                System.out.printf("%s is a duplicate employee.\n", employee.getEmailAddress());
                response = response.replace("{{credentials}}", "<b style='color:red;'>Member already exists.</b>");
                break;
            default:
                // Could not create employee.
                System.out.printf("%s failed to create.\n", employee.getEmailAddress());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        Utils.sendResponse(exchange, response);
    }

    // Creates a new employee from the form data provided.
    private static Employee createEmployee(Map<String, String> form) {
        Employee employee = new Employee();

        employee.setFirstName(form.get("firstName"));
        employee.setLastName(form.get("lastName"));
        employee.setJobTitle(form.get("jobTitle"));
        employee.setPhoneNumber(form.get("phoneNumber"));
        employee.setEmailAddress(form.get("emailAddress"));
        employee.setPassword(form.get("password"));
        employee.setAccessLevel(form.get("accessLevel"));
        employee.setLastLogin(new java.sql.Date(System.currentTimeMillis()));

        // Try to parse salary.
        try {
            employee.setSalary(Double.parseDouble(form.get("salary").replace(",", "")));
        } catch (Exception e) {
            System.err.println("Unable to parse salary.");
            return null;
        }

        // Try to parse museumId.
        try {
            employee.setMuseumId(Integer.parseInt(form.get("museumId")));
        } catch (Exception e) {
            System.err.println("Unable to parse Museum Id.");
            return null;
        }

        // Try to parse supervisorId.
        try {
            employee.setSupervisorId(Integer.parseInt(form.get("supervisorId")));
        } catch (Exception e) {
            System.err.println("Unable to parse Supervisor Id.");
            return null;
        }

        return employee;
    }

}
