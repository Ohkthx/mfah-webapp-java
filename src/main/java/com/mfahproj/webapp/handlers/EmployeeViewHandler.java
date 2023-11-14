package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

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

    // should add a Database.editEmployee() method here so managers can edit.

    // get employee details
    private static String getEmployeeDetails(String session) {

        String s = "";
        for (int i = 1; i <= Database.getMaxEmployeeID(); i++) {
            Employee employeeDetails = Database.getEmployee(i);
            // get Employee Supervisor
            Employee supervisor = Database.getEmployee(employeeDetails.getSupervisorId());
            String supName = String.format("%s %s", supervisor.getFirstName(), supervisor.getLastName());

            s += "<tr>"
                    + String.format("\t<td>%s</td>", employeeDetails.getFirstName())
                    + String.format("\t<td>%s</td>", employeeDetails.getLastName())
                    + String.format("\t<td>%s</td>", employeeDetails.getJobTitle())
                    + String.format("\t<td>%s</td>", employeeDetails.getPhoneNumber())
                    + String.format("\t<td>%s</td>", employeeDetails.getEmailAddress())
                    + String.format("\t<td>%s</td>", employeeDetails.getAccessLevel())
                    + String.format("\t<td>%s</td>", supName)
                    + String.format("\t<td>%s</td>", employeeDetails.getLastLogin())
                    + String.format("\t<td><a href=\"/employee/employeeViewEditor?employeeId=%s\">Edit</a></td>", i)
                    + "</tr>";
        }
        return s;
    }

}
