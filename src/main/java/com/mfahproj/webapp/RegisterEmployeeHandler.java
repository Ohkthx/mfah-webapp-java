package com.mfahproj.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
        // Show register form for a new employee.
        String response = Utils.readResourceFile("register-employee.html");
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to create a new user.
        Map<String, String> form = Utils.parseForm(formData);
        Employee employee = RegisterEmployeeHandler.createEmployee(form);

        String response;
        switch (Database.createEmployee(employee)) {
            case SUCCESS:
                // Create a session for the new employee.
                String sessionId = App.newEmployeeSession(employee);
                exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId);
                exchange.getResponseHeaders().add("Location", "/employee");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", employee.getEmailAddress());
                return;
            case DUPLICATE:
                // Duplicate employee detected, point them to login page.
                System.out.printf("%s is a duplicate employee.\n", employee.getEmailAddress());
                response = "<body>"
                        + "    <h4>Employee already exists, please try to login.</h4>"
                        + "    <a href='/login'>Login</a>"
                        + "</body>";

                break;
            default:
                // Could not create employee.
                System.out.printf("%s failed to create.\n", employee.getEmailAddress());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
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
            employee.setSalary(Double.parseDouble(form.get("salary")));
        } catch (Exception e) {
            return null;
        }

        // Try to parse museumId.
        try {
            employee.setMuseumId(Integer.parseInt(form.get("museumId")));
        } catch (Exception e) {
            return null;
        }

        // Try to parse supervisorId.
        try {
            employee.setSupervisorId(Integer.parseInt(form.get("supervisorId")));
        } catch (Exception e) {
            return null;
        }

        return employee;
    }

    // Parses a date from string into a usable format.
    private static Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
