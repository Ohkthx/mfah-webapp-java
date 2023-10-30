package com.mfahproj.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterMemberHandler implements HttpHandler {
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
        // Check if a valid session currently exists.
        boolean isMember = true;
        String sessionId = null;
        String sessionCookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (sessionCookie != null && sessionCookie.startsWith("SESSIONID=")) {
            sessionId = sessionCookie.split("=")[1];
            if (App.getMemberSession(sessionId) == null) {
                if (App.getEmployeeSession(sessionId) == null) {
                    // No active sessions found.
                    sessionId = null;
                } else {
                    // Is not a member but has an active session.
                    isMember = false;
                }
            }
        }

        // Modify the 'Profile/Login' navigation menu to change if client is logged in
        String path = "";
        if (sessionId == null) {
            path = String.format("<a href=\"/%s\">%s</a>", "login", "Login");
        } else {
            String text = isMember ? "member" : "employee";
            path = String.format("<a href=\"/%s\">%s</a>", text, "Profile");
        }

        // Show register form for a new member.
        String response = Utils.readResourceFile("register.html");

        // Edit the placeholders with dynamic text.
        response = response.replace("{{clientLoggedIn}}", path);

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
        Member member = RegisterMemberHandler.createMember(form);

        String response;
        switch (Database.createMember(member)) {
            case SUCCESS:
                // Create a session for the new member.
                String sessionId = App.newMemberSession(member);
                exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId);
                exchange.getResponseHeaders().add("Location", "/member");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", member.getEmailAddress());
                return;
            case DUPLICATE:
                // Duplicate member detected, point them to login page.
                System.out.printf("%s is a duplicate member.\n", member.getEmailAddress());
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

    // Creates a new member from the form data provided.
    private static Member createMember(Map<String, String> form) {
        Member member = new Member();

        member.setEmailAddress(form.get("emailAddress"));
        member.setPassword(form.get("password"));
        member.setFirstName(form.get("firstName"));
        member.setLastName(form.get("lastName"));
        member.setMembershipType(form.get("membershipType").toUpperCase());

        // Convert and set dates.
        member.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
        Date birthDate = RegisterMemberHandler.parseDate(form.get("birthDate"));
        if (birthDate != null) {
            member.setBirthDate(new java.sql.Date(birthDate.getTime()));
        }

        return member;
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
