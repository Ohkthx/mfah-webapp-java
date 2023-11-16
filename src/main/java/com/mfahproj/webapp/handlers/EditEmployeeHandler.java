package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditEmployeeHandler implements HttpHandler {
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
        // Validate the session before sending page.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Show edit form for a new employee.
        String response = Utils.dynamicNavigator(exchange, "employee/edit.html");

        // Updates placeholder values.
        response = response.replace("{{credentials}}", "");
        response = EditEmployeeHandler.setDefaults(employee, response);

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        // Check if a session exists.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to edit the employee information.
        Map<String, String> form = Utils.parseForm(formData);
        employee = EditEmployeeHandler.editEmployee(employee, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "employee/edit.html");
        switch (Database.editEmployee(employee)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", employee.getEmailAddress());
                return;
            default:
                // Could not create employee.
                System.out.printf("%s failed to create.\n", employee.getEmailAddress());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Sets the defaults values for a form.
    private static String setDefaults(Employee employee, String webpage) {
        if (employee == null) {
            return webpage;
        }

        // Replace the placeholder data.
        webpage = webpage.replace("{{firstName}}", employee.getFirstName());
        webpage = webpage.replace("{{lastName}}", employee.getLastName());
        return webpage.replace("{{phoneNumber}}", employee.getPhoneNumber());
    }

    // Edits an employee from the form data provided.
    private static Employee editEmployee(Employee employee, Map<String, String> form) {
        if (!StringUtils.isNullOrEmpty(form.get("firstName"))) {
            employee.setFirstName(form.get("firstName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("lastName"))) {
            employee.setLastName(form.get("lastName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("password"))) {
            employee.setPassword(form.get("password"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("phoneNumber"))) {
            employee.setPhoneNumber(form.get("phoneNumber"));
        }

        return employee;
    }

}
