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
import com.mfahproj.webapp.models.Exhibition;
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
        // Show edit form for a new member.
        String response = Utils.dynamicNavigator(exchange, "exhibition/edit.html");

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

        // Parse the form data to edit the exhibiton information.
        Map<String, String> form = Utils.parseForm(formData);
        Exhibition obj = Database.getExhibition(Integer.parseInt(form.get("ExhibitionId")));
        obj = EditExhibitionHandler.editObj(obj, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "exhibition/edit.html");
        switch (Database.editExhibition(obj)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", obj.getTitle());
                return;
            default:
                // Could not create collection.
                System.out.printf("%s failed to create.\n", obj.getTitle());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Edits an antifact from the form data provided.
    private static Exhibition editObj(Exhibition obj, Map<String, String> form) {

        if (!StringUtils.isNullOrEmpty(form.get("ExhibitionId"))) {
            obj.setExhibitionId(Integer.parseInt(form.get("ExhibitionId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("Title"))) {
            obj.setTitle(form.get("Title"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("StartDate"))) {
            Date date = EditExhibitionHandler.parseDate(form.get("StartDate"));
            if (date != null) {
                obj.setStartDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("EndDate"))) {
            Date date = EditExhibitionHandler.parseDate(form.get("EndDate"));
            if (date != null) {
                obj.setEndDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("Description"))) {
            obj.setDescription(form.get("Description"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("MuseumId"))) {
            obj.setMuseumId(Integer.parseInt(form.get("MuseumId")));
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
