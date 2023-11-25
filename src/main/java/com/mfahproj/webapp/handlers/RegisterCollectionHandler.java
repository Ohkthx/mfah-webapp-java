package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Collection;
import com.mfahproj.webapp.models.Employee;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterCollectionHandler implements HttpHandler {
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
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // No prior session, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Show register form for an employee.
        String response = Utils.dynamicNavigator(exchange, "collection/register.html");

        Utils.sendResponse(exchange, response);
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to create a new user.
        Map<String, String> form = Utils.parseForm(formData);
        Collection collection = RegisterCollectionHandler.createCollection(form);

        String response;
        switch (Database.createCollection(collection)) {
            case SUCCESS:
                // Artifact created
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", collection.getTitle());
                return;
            case DUPLICATE:
                // Duplicate collection detected, refresh the collection register page.
                System.out.printf("%s is a duplicate collection.\n", collection.getTitle());
                response = "<body>"
                        + "    <h4>Collection already exists, please try again.</h4>"
                        + "    <a href='/collection/register'>Register Collection</a>"
                        + "</body>";

                break;
            default:
                // Could not create collection.
                System.out.printf("%s failed to create.\n", collection.getTitle());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        Utils.sendResponse(exchange, response);
    }

    // Creates a new collection from the form data provided.
    private static Collection createCollection(Map<String, String> form) {
        Collection collection = new Collection();

        collection.setTitle(form.get("title"));
        collection.setDescription(form.get("description"));
        collection.setLocationId(Integer.parseInt(form.get("locationId")));
        collection.setExhibitionId(Integer.parseInt(form.get("exhibitionId")));

        Date date = RegisterCollectionHandler.parseDate(form.get("date"));
        if (date != null) {
            collection.setDate(new java.sql.Date(date.getTime()));
        }

        return collection;
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
