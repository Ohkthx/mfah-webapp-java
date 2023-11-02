package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Member;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditMemberHandler implements HttpHandler {
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
        String response = Utils.dynamicNavigator(exchange, "member/edit.html");

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
        Member member = Session.getMemberSession(sessionId);
        if (member == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data to edit the member information.
        Map<String, String> form = Utils.parseForm(formData);
        member = EditMemberHandler.editMember(member, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "member/edit.html");
        switch (Database.editMember(member)) {
            case SUCCESS:
                // Update the members session.
                Session.updateMemberSession(sessionId, member);

                // Create a session for the new member.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", member.getEmailAddress());
                return;
            default:
                // Could not create member.
                System.out.printf("%s failed to create.\n", member.getEmailAddress());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Edits a member from the form data provided.
    private static Member editMember(Member member, Map<String, String> form) {
        if (!StringUtils.isNullOrEmpty(form.get("firstName"))) {
            member.setFirstName(form.get("firstName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("lastName"))) {
            member.setLastName(form.get("lastName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("password"))) {
            member.setPassword(form.get("password"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("emailAddress"))) {
            member.setEmailAddress(form.get("emailAddress"));
        }

        return member;
    }

}
