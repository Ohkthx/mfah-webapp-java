package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Artifact;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditArtifactHandler implements HttpHandler {
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
        String response = Utils.dynamicNavigator(exchange, "artifact/edit.html");

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

        // Parse the form data to edit the artifact information.
        Map<String, String> form = Utils.parseForm(formData);
        Artifact artifact = Database.getArtifact(Integer.parseInt(form.get("ArtifactId")));
        artifact = EditArtifactHandler.editArtifact(artifact, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "artifact/edit.html");
        switch (Database.editArtifact(artifact)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", artifact.getTitle());
                return;
            default:
                // Could not create artifact.
                System.out.printf("%s failed to create.\n", artifact.getTitle());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Edits an antifact from the form data provided.
    private static Artifact editArtifact(Artifact artifact, Map<String, String> form) {
        if (!StringUtils.isNullOrEmpty(form.get("ArtifactId"))) {
            artifact.setTitle(form.get("ArtifactId"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Title"))) {
            artifact.setTitle(form.get("Title"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("ArtistId"))) {
            artifact.setArtistId(Integer.parseInt(form.get("ArtistId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Date"))) {
            Date date = EditArtifactHandler.parseDate(form.get("Date"));
            if (date != null) {
                artifact.setDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("Place"))) {
            artifact.setPlace(form.get("Place"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Medium"))) {
            artifact.setMedium(form.get("Medium"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Dimensions"))) {
            artifact.setDimensions(form.get("Dimensions"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("CollectionId"))) {
            artifact.setCollectionId(Integer.parseInt(form.get("CollectionId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Description"))) {
            artifact.setDescription(form.get("Description"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("OwnerId"))) {
            artifact.setOwnerId(Integer.parseInt(form.get("OwnerId")));
        }

        return artifact;
    }

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
