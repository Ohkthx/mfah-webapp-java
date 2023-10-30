package com.mfahproj.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterHandler implements HttpHandler {
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
        // Show register form for a new member.
        String response = Utils.readResourceFile("register.html");
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
        Member member = new Member();
        Map<String, String> inputs = Utils.parseForm(formData);
        member.setEmailAddress(inputs.get("emailAddress"));
        member.setPassword(inputs.get("password"));
        member.setFirstName(inputs.get("firstName"));
        member.setLastName("lastName");
        member.setMembershipType(inputs.get("membershipType").toUpperCase());

        Date birthDate = RegisterHandler.parseDate(inputs.get("birthDate"));
        if (birthDate != null) {
            member.setBirthDate(new java.sql.Date(birthDate.getTime()));
            member.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
        }

        String response;
        switch (Database.createMember(member)) {
            case SUCCESS:
                if (birthDate == null) {
                    System.out.printf("%s failed to create (birth date).\n", member.getEmailAddress());
                    response = "Invalid birth date provided.";
                    break;
                }

                // Successfully created and parsed a new member.
                System.out.printf("New member: %s\n\n", member);

                // Create a session for the new member.
                String sessionId = App.newSession(member);
                exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId);
                exchange.getResponseHeaders().add("Location", "/home");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", member.getEmailAddress());
                return;
            case DUPLICATE:
                // Duplicate member detected, point them to login page.
                System.out.printf("%s is a duplicate member..\n", member.getEmailAddress());
                response = "<body>"
                        + "    <h4>Member already exists, please try to login.</h4>"
                        + "    <a href='/login'>Login</a>"
                        + "</body>";

                break;
            default:
                // Could not create member.
                System.out.printf("%s failed to create.\n", member.getEmailAddress());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Parses a date from string into a usable format.
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
