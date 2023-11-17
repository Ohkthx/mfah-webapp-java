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
import com.mfahproj.webapp.models.Collection;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditCollectionHandler implements HttpHandler {
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

        // Obtain the entity from the database.
        Collection collection = Database.getCollection(collectionId);
        if (collection == null) {
            invalidCollection = true;
        }

        // Update the credentials to something meaningful if there was an error.
        String response = Utils.dynamicNavigator(exchange, "collection/edit.html");
        if (invalidCollection) {
            response = response.replace("{{credentials}}", "<b style='color:red;'>Invalid Id.</b>");
        } else {
            response = response.replace("{{credentials}}", "");
        }

        // Update the default form data by swapping out the placeholders.
        response = EditCollectionHandler.setDefaults(collection, response);

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

        // Obtain the entity from the database.
        Collection collection = Database.getCollection(collectionId);
        if (collection == null) {
            invalidCollection = true;
        }

        if (invalidCollection) {
            // Send them to the failure page.
            exchange.getResponseHeaders().add("Location", "/failure");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Parse the form data to edit the collection information.
        Map<String, String> form = Utils.parseForm(formData);
        collection = EditCollectionHandler.editCollection(collection, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "collection/edit.html");
        // Update the default form data by swapping out the placeholders.
        response = EditCollectionHandler.setDefaults(collection, response);

        switch (Database.editCollection(collection)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s edited.\n", collection.getTitle());
                return;
            default:
                // Could not create collection.
                System.out.printf("%s failed to edit.\n", collection.getTitle());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Sets the defaults values for a form.
    private static String setDefaults(Collection collection, String webpage) {
        if (collection == null) {
            // Create a default collection with blank values. Credentials will show an
            // error.
            collection = new Collection();
        }

        // Replace the placeholder data.
        webpage = webpage.replace("{{collectionId}}", Integer.toString(collection.getCollectionId()));
        webpage = webpage.replace("{{title}}", collection.getTitle());
        webpage = webpage.replace("{{date}}", collection.getDate().toString());
        webpage = webpage.replace("{{description}}", collection.getDescription());
        webpage = webpage.replace("{{locationId}}", Integer.toString(collection.getLocationId()));
        return webpage.replace("{{exhibitionId}}", Integer.toString(collection.getExhibitionId()));
    }

    // Edits an antifact from the form data provided.
    private static Collection editCollection(Collection obj, Map<String, String> form) {
        if (!StringUtils.isNullOrEmpty(form.get("CollectionId"))) {
            obj.setCollectionId(Integer.parseInt(form.get("CollectionId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Title"))) {
            obj.setTitle(form.get("Title"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Date"))) {
            Date date = EditCollectionHandler.parseDate(form.get("Date"));
            if (date != null) {
                obj.setDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("Description"))) {
            obj.setDescription(form.get("Description"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("LocationId"))) {
            obj.setLocationId(Integer.parseInt(form.get("LocationId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("ExhibitionId"))) {
            obj.setExhibitionId(Integer.parseInt(form.get("ExhibitionId")));
        }

        return obj;
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
