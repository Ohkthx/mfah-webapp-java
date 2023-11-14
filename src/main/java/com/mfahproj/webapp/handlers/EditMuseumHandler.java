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
import com.mfahproj.webapp.models.Museum;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditMuseumHandler implements HttpHandler {
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
        // Show edit form for a new member.
        String response = Utils.dynamicNavigator(exchange, "museum/edit.html");

        // Edit the placeholders with dynamic text.
        response = response.replace("{{credentials}}", "");

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

        // Parse the form data to edit the Museum information.
        Map<String, String> form = Utils.parseForm(formData);
        Museum obj = Database.getMuseum(Integer.parseInt(form.get("MuseumId")));
        obj = EditMuseumHandler.editObj(obj, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "museum/edit.html");
        switch (Database.editMuseum(obj)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", obj.getName());
                return;
            default:
                // Could not create collection.
                System.out.printf("%s failed to create.\n", obj.getName());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Edits an antifact from the form data provided.
    private static Museum editObj(Museum obj, Map<String, String> form) {

        if (!StringUtils.isNullOrEmpty(form.get("MuseumId"))) {
            obj.setMuseumId(Integer.parseInt(form.get("MuseumId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Name"))) {
            obj.setName(form.get("Name"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Address"))) {
            obj.setAddress(form.get("Address"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("TotalRevenue"))) {
            obj.setTotalRevenue(Integer.parseInt(form.get("TotalRevenue")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("OperationalCost"))) {
            obj.setOperationalCost(Integer.parseInt(form.get("OperationalCost")));
        }

        return obj;
    }

}
