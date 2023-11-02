package com.mfahproj.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jcp.xml.dsig.internal.dom.Utils;

import com.mfahproj.webapp.models.Artifact;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ArtifactHandler implements HttpHandler {
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
        // Show register form for a new artifact.
        String response = Utils.readResourceFile("register-artifact.html");
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
        Artifact artifact = RegisterHandler.createArtifact(form);

        String response;
        switch (Database.createArtifact(artifact)) {
            case SUCCESS:
                // Create a session for the new artifact.
                String sessionId = App.newArtifactSession(artifact);
                exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId);
                exchange.getResponseHeaders().add("Location", "/artifact");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", artifact.getTitle());
                return;
            case DUPLICATE:
                // Duplicate artifact detected, point them to login page.
                System.out.printf("%s is a duplicate artifact.\n", artifact.getTitle());
                response = "<body>"
                        + "    <h4>Artifact already exists, please try again.</h4>"
                        + "    <a href='/register-artifact'>Login</a>"
                        + "</body>";

                break;
            default:
                // Could not create artifact.
                System.out.printf("%s failed to create.\n", artifact.getTitle());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Creates a new artifact from the form data provided.
    private static Artifact createArtifact(Map<String, String> form) {
        Artifact artifact = new Artifact();

        artifact.setTitle(form.get("Title"));
        artifact.setArtistId(form.get("ArtistId"));
        artifact.setDate(form.get("Date"));
        artifact.setPlace(form.get("Place"));
        artifact.setMedium(form.get("Medium"));
        artifact.setDimensions(form.get("Dimensions"));
        artifact.setCollectionId(form.get("CollectionId"));
        artifact.setDescription(form.get("Description"));
        artifact.setOwnerId(form.get("OwnerId"));

        return artifact;
    }

}
