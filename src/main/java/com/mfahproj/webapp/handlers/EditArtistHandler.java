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
import com.mfahproj.webapp.models.Artist;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditArtistHandler implements HttpHandler {
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

        // Extract the ArtistId from the query.
        boolean invalidArtist = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int artistId = -1;
        try {
            artistId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidArtist = true;
        }

        // Obtain the entity from the database.
        Artist artist = Database.getArtist(artistId);
        if (artist == null) {
            invalidArtist = true;
        }

        // Update the credentials to something meaningful if there was an error.
        String response = Utils.dynamicNavigator(exchange, "artist/edit.html");
        if (invalidArtist) {
            response = response.replace("{{credentials}}", "<b style='color:red;'>Invalid Id.</b>");
        } else {
            response = response.replace("{{credentials}}", "");
        }

        // Update the default form data by swapping out the placeholders.
        response = EditArtistHandler.setDefaults(artist, response);

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

        // Extract the ArtistId from the query.
        boolean invalidArtist = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int artistId = -1;
        try {
            artistId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidArtist = true;
        }

        // Obtain the entity from the database.
        Artist artist = Database.getArtist(artistId);
        if (artist == null) {
            invalidArtist = true;
        }

        if (invalidArtist) {
            // Send them to the failure page.
            exchange.getResponseHeaders().add("Location", "/failure");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Parse the form data to edit the artist information.
        Map<String, String> form = Utils.parseForm(formData);
        artist = EditArtistHandler.editArtist(artist, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "artist/edit.html");
        // Update the default form data by swapping out the placeholders.
        response = EditArtistHandler.setDefaults(artist, response);

        switch (Database.editArtist(artist)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("Artist: %s edited.\n", artist.getFirstName());
                return;
            default:
                // Could not create artist.
                System.out.printf("Artist: %s failed to edit.\n", artist.getFirstName());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Sets the defaults values for a form.
    private static String setDefaults(Artist artist, String webpage) {
        if (artist == null) {
            // Create a default artist with blank values. Credentials will show an error.
            artist = new Artist(-1, "", "");
        }

        // Replace the placeholder data.
        webpage = webpage.replace("{{artistId}}", Integer.toString(artist.getArtistId()));
        webpage = webpage.replace("{{firstName}}", artist.getFirstName());
        return webpage.replace("{{lastName}}", artist.getLastName());
    }

    // Edits an antifact from the form data provided.
    private static Artist editArtist(Artist artist, Map<String, String> form) {
        if (!StringUtils.isNullOrEmpty(form.get("artistId"))) {
            artist.setArtistId(Integer.parseInt(form.get("artistId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("firstName"))) {
            artist.setFirstName(form.get("firstName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("lastName"))) {
            artist.setLastName(form.get("lastName"));
        }

        return artist;
    }

}
