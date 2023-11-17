package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Museum;
import com.mfahproj.webapp.models.Employee;
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
        // Validate the session before sending page.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Extract the MuseumId from the query.
        boolean invalidMuseum = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int museumId = -1;
        try {
            museumId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidMuseum = true;
        }

        // Obtain the entity from the database.
        Museum museum = Database.getMuseum(museumId);
        if (museum == null) {
            invalidMuseum = true;
        }

        // Update the credentials to something meaningful if there was an error.
        String response = Utils.dynamicNavigator(exchange, "museum/edit.html");
        if (invalidMuseum) {
            response = response.replace("{{credentials}}", "<b style='color:red;'>Invalid Id.</b>");
        } else {
            response = response.replace("{{credentials}}", "");
        }

        // Update the default form data by swapping out the placeholders.
        response = EditMuseumHandler.setDefaults(museum, response);

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        // Validate the session before sending page.
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

        // Extract the MuseumId from the query.
        boolean invalidMuseum = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int museumId = -1;
        try {
            museumId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidMuseum = true;
        }

        // Obtain the entity from the database.
        Museum museum = Database.getMuseum(museumId);
        if (museum == null) {
            invalidMuseum = true;
        }

        if (invalidMuseum) {
            // Send them to the failure page.
            exchange.getResponseHeaders().add("Location", "/failure");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Parse the form data to edit the museum information.
        Map<String, String> form = Utils.parseForm(formData);
        museum = EditMuseumHandler.editMuseum(museum, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "museum/edit.html");
        // Update the default form data by swapping out the placeholders.
        response = EditMuseumHandler.setDefaults(museum, response);

        switch (Database.editMuseum(museum)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("Museum: %s edited.\n", museum.getName());
                return;
            default:
                // Could not create museum.
                System.out.printf("Museum: %s failed to edit.\n", museum.getName());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Sets the defaults values for a form.
    private static String setDefaults(Museum museum, String webpage) {
        if (museum == null) {
            // Create a default museum with blank values. Credentials will show an error.
            museum = new Museum();
        }

        // Replace the placeholder data.
        webpage = webpage.replace("{{museumId}}", Integer.toString(museum.getMuseumId()));
        return webpage.replace("{{name}}", museum.getName());
    }

    // Edits an antifact from the form data provided.
    private static Museum editMuseum(Museum museum, Map<String, String> form) {

        if (!StringUtils.isNullOrEmpty(form.get("MuseumId"))) {
            museum.setMuseumId(Integer.parseInt(form.get("MuseumId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Name"))) {
            museum.setName(form.get("Name"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Address"))) {
            museum.setAddress(form.get("Address"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("TotalRevenue"))) {
            museum.setTotalRevenue(Integer.parseInt(form.get("TotalRevenue")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("OperationalCost"))) {
            museum.setOperationalCost(Integer.parseInt(form.get("OperationalCost")));
        }

        return museum;
    }

}
