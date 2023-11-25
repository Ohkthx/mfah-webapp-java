package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        // Validate the session before sending page.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Extract the ArtifactOwnerId from the query.
        boolean invalidArtifactOwner = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int artifactOwnerId = -1;
        try {
            artifactOwnerId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidArtifactOwner = true;
        }

        // Obtain the entity from the database.
        ArtifactOwner artifactOwner = Database.getArtifactOwner(artifactOwnerId);
        if (artifactOwner == null) {
            invalidArtifactOwner = true;
        }

        // Update the credentials to something meaningful if there was an error.
        String response = Utils.dynamicNavigator(exchange, "artifactOwner/edit.html");
        if (invalidArtifactOwner) {
            response = response.replace("{{credentials}}", "<b style='color:red;'>Invalid Id.</b>");
        } else {
            response = response.replace("{{credentials}}", "");
        }

        // Update the default form data by swapping out the placeholders.
        response = EditArtifactOwnerHandler.setDefaults(artifactOwner, response);

        Utils.sendResponse(exchange, response);
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

        // Extract the ArtifactOwnerId from the query.
        boolean invalidArtifactOwner = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int artifactOwnerId = -1;
        try {
            artifactOwnerId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidArtifactOwner = true;
        }

        // Obtain the entity from the database.
        ArtifactOwner artifactOwner = Database.getArtifactOwner(artifactOwnerId);
        if (artifactOwner == null) {
            invalidArtifactOwner = true;
        }

        if (invalidArtifactOwner) {
            // Send them to the failure page.
            exchange.getResponseHeaders().add("Location", "/failure");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Parse the form data to edit the artifactOwner information.
        Map<String, String> form = Utils.parseForm(formData);
        artifactOwner = EditArtifactOwnerHandler.editArtifactOwner(artifactOwner, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "artifactOwner/edit.html");
        // Update the default form data by swapping out the placeholders.
        response = EditArtifactOwnerHandler.setDefaults(artifactOwner, response);

        switch (Database.editArtifactOwner(artifactOwner)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("ArtifactOwner: %s edited.\n", artifactOwner.getName());
                return;
            default:
                // Could not create artifactOwner.
                System.out.printf("ArtifactOwner: %s failed to edit.\n", artifactOwner.getName());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        Utils.sendResponse(exchange, response);
    }

    // Sets the defaults values for a form.
    private static String setDefaults(ArtifactOwner artifactOwner, String webpage) {
        if (artifactOwner == null) {
            // Create a default artifactOwner with blank values. Credentials will show an
            // error.
            artifactOwner = new ArtifactOwner(-1, "", "");
        }

        // Replace the placeholder data.
        webpage = webpage.replace("{{artifactOwnerId}}", Integer.toString(artifactOwner.getOwnerId()));
        webpage = webpage.replace("{{name}}", artifactOwner.getName());
        return webpage.replace("{{phoneNumber}}", artifactOwner.getPhoneNumber());
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
