package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DeleteArtifactHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            get(exchange);
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

        // Extract the ArtifactId from the query.
        boolean invalidArtifact = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int artifactId = -1;
        try {
            artifactId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidArtifact = true;
        }

        System.out.println("Nothing to do here yet for Artifact " + artifactId);
        System.out.println("Artifact is valid: " + !invalidArtifact);

        // Database Deletion here.
        // boolean success = Database.deleteArtifact(artifactId);
        // String path = success ? "/success" : "/failure";
        String path = "/accessDeny";
        exchange.getResponseHeaders().add("Location", path);
        exchange.sendResponseHeaders(302, -1);
    }
}
