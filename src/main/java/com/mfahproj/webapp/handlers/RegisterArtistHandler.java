package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Artist;
import com.mfahproj.webapp.models.Employee;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterArtistHandler implements HttpHandler {
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
        String response = Utils.dynamicNavigator(exchange, "artist/register.html");

        Utils.sendResponse(exchange, response);
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to create a new user.
        Map<String, String> form = Utils.parseForm(formData);
        Artist obj = RegisterArtistHandler.createObj(form);

        String response;
        switch (Database.createArtist(obj)) {
            case SUCCESS:
                // Artifact created
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", obj.getFirstName());
                return;
            case DUPLICATE:
                // Duplicate artist detected, refresh the artist register page.
                System.out.printf("%s is a duplicate artist.\n", obj.getLastName());
                response = "<body>"
                        + "    <h4>Artist already exists, please try again.</h4>"
                        + "    <a href='/artist/register'>Register Artist</a>"
                        + "</body>";

                break;
            default:
                // Could not create artist.
                System.out.printf("%s failed to create.\n", obj.getFirstName());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        Utils.sendResponse(exchange, response);
    }

    // Creates a new artist from the form data provided.
    private static Artist createObj(Map<String, String> form) {
        Artist obj = new Artist();

        obj.setFirstName(form.get("firstName"));
        obj.setLastName(form.get("lastName"));

        return obj;
    }

}
