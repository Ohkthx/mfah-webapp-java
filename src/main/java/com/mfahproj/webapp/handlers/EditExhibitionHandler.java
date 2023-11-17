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
import com.mfahproj.webapp.models.Exhibition;
import com.mfahproj.webapp.models.Employee;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditExhibitionHandler implements HttpHandler {
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

        // Extract the ExhibitionId from the query.
        boolean invalidExhibition = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int exhibitionId = -1;
        try {
            exhibitionId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidExhibition = true;
        }

        // Obtain the entity from the database.
        Exhibition exhibition = Database.getExhibition(exhibitionId);
        if (exhibition == null) {
            invalidExhibition = true;
        }

        // Update the credentials to something meaningful if there was an error.
        String response = Utils.dynamicNavigator(exchange, "exhibition/edit.html");
        if (invalidExhibition) {
            response = response.replace("{{credentials}}", "<b style='color:red;'>Invalid Id.</b>");
        } else {
            response = response.replace("{{credentials}}", "");
        }

        // Update the default form data by swapping out the placeholders.
        response = EditExhibitionHandler.setDefaults(exhibition, response);

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

        // Extract the ExhibitionId from the query.
        boolean invalidExhibition = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int exhibitionId = -1;
        try {
            exhibitionId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidExhibition = true;
        }

        // Obtain the entity from the database.
        Exhibition exhibition = Database.getExhibition(exhibitionId);
        if (exhibition == null) {
            invalidExhibition = true;
        }

        if (invalidExhibition) {
            // Send them to the failure page.
            exchange.getResponseHeaders().add("Location", "/failure");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Parse the form data to edit the exhibition information.
        Map<String, String> form = Utils.parseForm(formData);
        exhibition = EditExhibitionHandler.editExhibition(exhibition, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "exhibition/edit.html");
        // Update the default form data by swapping out the placeholders.
        response = EditExhibitionHandler.setDefaults(exhibition, response);

        switch (Database.editExhibition(exhibition)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("Exhibition: %s edited.\n", exhibition.getTitle());
                return;
            default:
                // Could not create exhibition.
                System.out.printf("Exhibition: %s failed to edit.\n", exhibition.getTitle());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Sets the defaults values for a form.
    private static String setDefaults(Exhibition artist, String webpage) {
        if (artist == null) {
            // Create a default exhibition with blank values. Credentials will show an
            // error.
            artist = new Exhibition();
        }

        // Replace the placeholder data.
        webpage = webpage.replace("{{exhibitionId}}", Integer.toString(artist.getExhibitionId()));
        webpage = webpage.replace("{{title}}", artist.getTitle());
        webpage = webpage.replace("{{startDate}}", artist.getStartDate().toString());
        webpage = webpage.replace("{{endDate}}", artist.getEndDate().toString());
        webpage = webpage.replace("{{description}}", artist.getDescription());
        return webpage.replace("{{museumId}}", Integer.toString(artist.getMuseumId()));
    }

    // Edits an antifact from the form data provided.
    private static Exhibition editExhibition(Exhibition exhibition, Map<String, String> form) {

        if (!StringUtils.isNullOrEmpty(form.get("ExhibitionId"))) {
            exhibition.setExhibitionId(Integer.parseInt(form.get("ExhibitionId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Title"))) {
            exhibition.setTitle(form.get("Title"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("StartDate"))) {
            Date date = EditExhibitionHandler.parseDate(form.get("StartDate"));
            if (date != null) {
                exhibition.setStartDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("EndDate"))) {
            Date date = EditExhibitionHandler.parseDate(form.get("EndDate"));
            if (date != null) {
                exhibition.setEndDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("Description"))) {
            exhibition.setDescription(form.get("Description"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("MuseumId"))) {
            exhibition.setMuseumId(Integer.parseInt(form.get("MuseumId")));
        }

        return exhibition;
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
