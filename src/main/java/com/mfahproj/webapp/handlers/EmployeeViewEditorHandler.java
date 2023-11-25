package com.mfahproj.webapp.handlers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Museum;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.xml.crypto.Data;

public class EmployeeViewEditorHandler implements HttpHandler {

    HashMap<String, String> storeQuery = new HashMap<>();

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
        // Check if a session exists.
        String sessionId = Session.extractSessionId(exchange);
        // employee id number should be passed from EmployeeViewHandler.java
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // get URL from edit function
        String query = exchange.getRequestURI().getQuery();

        // extract only employeeID from URL to parse into editEmployee
        query = query.replaceAll("[^0-9]", "");

        storeQuery.put(sessionId, query);

        String response = Utils.dynamicNavigator(exchange, "employee/employeeViewEditor.html");

        response = response.replace("{{credentials}}",
                "Editing Employee : " + Database.getEmployee(Integer.parseInt(query)).getFirstName() +
                        " " + Database.getEmployee(Integer.parseInt(query)).getLastName());

        //autofill form information
        response = response.replace("{{employeeFirstName}}",Database.getEmployee(Integer.parseInt(query)).getFirstName());
        response = response.replace("{{employeeLastName}}",Database.getEmployee(Integer.parseInt(query)).getLastName());
        response = response.replace("{{jobTitle}}",Database.getEmployee(Integer.parseInt(query)).getJobTitle());
        response = response.replace("{{phoneNumber}}",Database.getEmployee(Integer.parseInt(query)).getPhoneNumber());
        response = response.replace("{{emailAddress}}",Database.getEmployee(Integer.parseInt(query)).getEmailAddress());
        response = response.replace("{{accessLevel}}",Database.getEmployee(Integer.parseInt(query)).getAccessLevel());


        String supervisorOptions = "";
        for (Employee m : Database.getAllSupervisors()) {
            String selected = "";

            if(m.getEmployeeId() == Database.getEmployee(Integer.parseInt(query)).getSupervisorId())
                selected = " selected";
            supervisorOptions += String.format("<option value=%d %s> %s %s</option>", m.getEmployeeId(), selected, m.getFirstName(), m.getLastName());
        }

        response = response.replace("{{supervisorInfo}}", supervisorOptions);

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {

        // Check if a session exists.
        String sessionId = Session.extractSessionId(exchange);
        Employee currentEmployee = Session.getEmployeeSession(sessionId);
        Employee editEmployee;
        if (currentEmployee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // retrieve hashmap to get employeeID
        String query = storeQuery.get(sessionId);

        // set employee to employee that you want to edit
        editEmployee = Database.getEmployee(Integer.parseInt(query));

        // Parse the form data to edit the employee information.
        Map<String, String> form = Utils.parseForm(formData);

        EmployeeViewEditorHandler.editEmployee(editEmployee, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "employee/employeeViewEditor.html");

        switch (Database.editEmployee(editEmployee)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, currentEmployee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s updated.\n", editEmployee.getEmailAddress());
                return;
            default:
                // Could not create employee.
                System.out.printf("%s failed to update.\n", editEmployee.getEmailAddress());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Edits an employee from the form data provided.
    private static Employee editEmployee(Employee employee, Map<String, String> form) {

        if (!StringUtils.isNullOrEmpty(form.get("firstName"))) {
            employee.setFirstName(form.get("firstName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("lastName"))) {
            employee.setLastName(form.get("lastName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("jobTitle"))) {
            employee.setJobTitle(form.get("jobTitle"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("phoneNumber"))) {
            employee.setPhoneNumber(form.get("phoneNumber"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("emailAddress"))) {
            employee.setEmailAddress(form.get("emailAddress"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("accessLevel"))) {
            var input = form.get("accessLevel").toLowerCase();
            if (input.equals("manager") || input.equals("supervisor") || input.equals("normal"))
                employee.setAccessLevel(form.get("accessLevel").toUpperCase());

        }

        if (!StringUtils.isNullOrEmpty(form.get("supervisorId"))) {
            employee.setSupervisorId(Integer.parseInt(form.get("supervisorId")));

        }
        return employee;
    }
}
