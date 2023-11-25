package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EmployeeViewHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            get(exchange);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            post(exchange);
        }
    }

    private void get(HttpExchange exchange) throws IOException {
        // check if session exists
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);

        // No prior session, send to login page
        if (employee == null) {
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        if (!employee.getAccessLevel().equalsIgnoreCase("MANAGER")) {
            exchange.getResponseHeaders().add("Location", "/accessDeny");
            exchange.sendResponseHeaders(302, -1);
            return;
        }
        // send user to employeeView.html (should list all employees with an option to
        // edit employee data
        String response = Utils.dynamicNavigator(exchange, "employee/employeeView.html");
        response = response.replace("{{employeeDetails}}", EmployeeViewHandler.getEmployeeDetails(sessionId));
        response = response.replace("{{emailAddress}}", employee.getFirstName());
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void post(HttpExchange exchange) throws IOException {

    }

    // get employee details
    private static String getEmployeeDetails(String session) {
        // Map of supervisors to display in the table.
        HashMap<Integer, Employee> supervisors = new HashMap<Integer, Employee>();
        for (Employee supervisor : Database.getAllSupervisors()) {
            supervisors.put(supervisor.getEmployeeId(), supervisor);
        }

        String s = "";
        for (Employee e : Database.getAllEmployees()) {
            // get Employee Supervisor
            // parse employeeId as a parameter through URL
            Employee supervisor = supervisors.get(e.getSupervisorId());
            String supName = String.format("%s %s", supervisor.getFirstName(), supervisor.getLastName());

            s += "<tr>"
                    + String.format("\t<td>%s</td>", e.getFirstName())
                    + String.format("\t<td>%s</td>", e.getLastName())
                    + String.format("\t<td>%s</td>", e.getJobTitle())
                    + String.format("\t<td>%s</td>", e.getPhoneNumber())
                    + String.format("\t<td>%s</td>", e.getEmailAddress())
                    + String.format("\t<td>%s</td>", e.getAccessLevel())
                    + String.format("\t<td>%s</td>", supName)
                    + String.format("\t<td>%s</td>", e.getLastLogin())
                    + String.format("\t<td><a href=\"/employee/employeeViewEditor?employeeId=%s\">Edit</a></td>",
                            e.getEmployeeId())
                    + "</tr>";
        }
        return s;
    }

}
