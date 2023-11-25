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
import com.mfahproj.webapp.models.Exhibition;
import com.mfahproj.webapp.models.Employee;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterExhibitionHandler implements HttpHandler {
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
        String response = Utils.dynamicNavigator(exchange, "exhibition/register.html");

        Utils.sendResponse(exchange, response);
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to create a new user.
        Map<String, String> form = Utils.parseForm(formData);
        Exhibition exhibition = RegisterExhibitionHandler.createExhibition(form);

        String response;
        switch (Database.createExhibition(exhibition)) {
            case SUCCESS:
                // Exhibition created
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", exhibition.getTitle());
                return;
            case DUPLICATE:
                // Duplicate exhibition detected, refresh the exhibition register page.
                System.out.printf("%s is a duplicate exhibition.\n", exhibition.getTitle());
                response = "<body>"
                        + "    <h4>Exhibition already exists, please try again.</h4>"
                        + "    <a href='/exhibition/register'>Register Exhibition</a>"
                        + "</body>";

                break;
            default:
                // Could not create exhibition.
                System.out.printf("%s failed to create.\n", exhibition.getTitle());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        Utils.sendResponse(exchange, response);
    }

    // Creates a new exhibition from the form data provided.
    private static Exhibition createExhibition(Map<String, String> form) {
        Exhibition exhibition = new Exhibition();

        exhibition.setTitle(form.get("Title"));
        exhibition.setDescription(form.get("Description"));
        exhibition.setMuseumId(Integer.parseInt(form.get("MuseumId")));

        Date sdate = RegisterExhibitionHandler.parseDate(form.get("StartDate"));
        if (sdate != null) {
            exhibition.setStartDate(new java.sql.Date(sdate.getTime()));
        }

        Date edate = RegisterExhibitionHandler.parseDate(form.get("EndDate"));
        if (edate != null) {
            exhibition.setEndDate(new java.sql.Date(edate.getTime()));
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
