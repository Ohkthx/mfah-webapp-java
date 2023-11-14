package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.ArtifactOwner;
import com.mfahproj.webapp.models.Employee;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterArtifactOwnerHandler implements HttpHandler {
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

        // Show register form for an artifact owner.
        String response = Utils.dynamicNavigator(exchange, "artifactOwner/register.html");

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to create a new user.
        Map<String, String> form = Utils.parseForm(formData);
        ArtifactOwner obj = RegisterArtifactOwnerHandler.createObj(form);

        String response;
        switch (Database.createArtifactOwner(obj)) {
            case SUCCESS:
                // Artifact created
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", obj.getName());
                return;
            case DUPLICATE:
                // Duplicate artifactOwner detected, refresh the artifactOwner register page.
                System.out.printf("%s is a duplicate artifact owner.\n", obj.getName());
                response = "<body>"
                        + "    <h4>owner already exists, please try again.</h4>"
                        + "    <a href='/artifactOwner/register'>Register Artifact Owner</a>"
                        + "</body>";

                break;
            default:
                // Could not create artist.
                System.out.printf("%s failed to create.\n", obj.getName());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Creates a new ArtifactOwner from the form data provided.
    private static ArtifactOwner createObj(Map<String, String> form) {
        ArtifactOwner obj = new ArtifactOwner();

        obj.setName(form.get("name"));
        obj.setPhoneNumber(form.get("phoneNum"));

        return obj;
    }

}
