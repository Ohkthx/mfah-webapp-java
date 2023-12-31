package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DeleteCollectionHandler implements HttpHandler {
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

        // Extract the CollectionId from the query.
        boolean invalidCollection = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int collectionId = -1;
        try {
            collectionId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidCollection = true;
        }

        System.out.println("Nothing to do here yet for Collection " + collectionId);
        System.out.println("Collection is valid: " + !invalidCollection);

        // Database Deletion here.
        // boolean success = Database.deleteCollection(collectionId);
        // String path = success ? "/success" : "/failure";
        String path = "/accessDeny";
        exchange.getResponseHeaders().add("Location", path);
        exchange.sendResponseHeaders(302, -1);
    }
}
