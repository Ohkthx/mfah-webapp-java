package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class EmployeeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Check if a session exists.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee != null) {
            // Valid non-timeout sessions found. Send to employee home page.
            String response = Utils.readResourceFile("employee/employee.html");
            response = response.replace("{{emailAddress}}", employee.getEmailAddress());
            response = response.replace("{{employeeDetails}}", EmployeeHandler.getDetails(employee));

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }

        // No prior session, send to login page.
        exchange.getResponseHeaders().add("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
    }

    // Generates the 'About Me' section.
    private static String getDetails(Employee employee) {
        Employee supervisor = Database.getEmployee(employee.getSupervisorId());
        String supName = String.format("%s %s", supervisor.getFirstName(), supervisor.getLastName());

        return "<ul>"
                + String.format("\t<li>First name: %s</li>", employee.getFirstName())
                + String.format("\t<li>Last name: %s</li>", employee.getLastName())
                + String.format("\t<li>Job title: %s</li>", employee.getJobTitle())
                + String.format("\t<li>Phone number: %s</li>", employee.getPhoneNumber())
                + String.format("\t<li>Email address: %s</li>", employee.getEmailAddress())
                + String.format("\t<li>Access level: %s</li>", employee.getAccessLevel())
                + String.format("\t<li>Supervisor: %s</li>", supName)
                + String.format("\t<li>Last login: %s</li>", employee.getLastLogin())
                + "</ul>";
    }
}
