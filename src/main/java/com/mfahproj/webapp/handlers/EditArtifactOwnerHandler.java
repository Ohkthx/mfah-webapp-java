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
import com.mfahproj.webapp.models.ArtifactOwner;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditArtifactOwnerHandler implements HttpHandler {
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
        String response = Utils.dynamicNavigator(exchange, "artifactOwner/edit.html");

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

        // Parse the form data to edit the ArtifactOwner information.
        Map<String, String> form = Utils.parseForm(formData);
        ArtifactOwner owner = Database.getArtifactOwner(Integer.parseInt(form.get("ownerId")));
        owner = EditArtifactOwnerHandler.editArtifactOwner(owner, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "artifact/edit.html");
        switch (Database.editArtifactOwner(owner)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", owner.getName());
                return;
            default:
                // Could not create artifact.
                System.out.printf("%s failed to create.\n", owner.getName());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Edits an antifact from the form data provided.
    private static ArtifactOwner editArtifactOwner(ArtifactOwner owner, Map<String, String> form) {    
        if (!StringUtils.isNullOrEmpty(form.get("ownerId"))) {
            owner.setOwnerId(Integer.parseInt(form.get("ownerId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("name"))) {
            owner.setName(form.get("name"));
        }
        
        if (!StringUtils.isNullOrEmpty(form.get("phoneNum"))) {
            owner.setPhoneNumber(form.get("phoneNum"));
        }
        
        return owner;
    }

}
