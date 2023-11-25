package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MemberTriggerHandler implements HttpHandler {
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
        Member member = Session.getMemberSession(sessionId);
        if (member == null) {
            // Member does not have a session, send to login screen.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Send the trigger webpage to the user.
        String response = Utils.dynamicNavigator(exchange, "member/trigger.html");
        response = MemberHandler.setNotifications(member, response);

        Utils.sendResponse(exchange, response);
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        // NOTE: Do not need to process form, just expecting POST request.
        // InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(),
        // "utf-8");
        // BufferedReader br = new BufferedReader(isr);
        // String formData = br.readLine();

        // Prevent non-logged in users from accessing the page.
        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        if (member == null) {
            // Member does not have a session, send to login screen.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Set the user information to trigger.
        boolean result = MemberTriggerHandler.resetTriggers(member);
        String route = result ? "/success" : "/failure";

        exchange.getResponseHeaders().add("Location", route);
        exchange.sendResponseHeaders(302, -1);
    }

    // Resets the triggers for the member. True indicates success.
    private static boolean resetTriggers(Member member) {
        if (member == null) {
            return false;
        }

        // Update the membership expiration.
        long week = 1000L * 60 * 60 * 24 * 6;
        java.sql.Date expires = new java.sql.Date(System.currentTimeMillis() - week);
        member.setExpirationDate(expires);

        // Update the birthdate.
        long age = 1000L * 60 * 60 * 24 * 365 * 56;
        java.sql.Date birthDate = new java.sql.Date(System.currentTimeMillis() - age);
        member.setBirthDate(birthDate);

        // Set the membership not senior.
        member.setMembershipType(Member.Memberships.REGULAR.name());

        // Edit the members information.
        switch (Database.editMember(member)) {
            case SUCCESS:
                return true;
            default:
                return false;
        }
    }
}
