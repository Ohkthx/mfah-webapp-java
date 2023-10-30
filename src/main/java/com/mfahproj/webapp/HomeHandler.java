package com.mfahproj.webapp;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
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

        String response = Utils.readResourceFile("homepage.html");

        // Modify the 'Profile/Login' navigation menu to change if client is logged in
        String path = "";
        if (sessionId == null) {
            path = String.format("<a href=\"/%s\">%s</a>", "login", "Login");
        } else {
            String text = isMember ? "member" : "employee";
            path = String.format("<a href=\"/%s\">%s</a>", text, "Profile");
        }
        response = response.replace("{{clientLoggedIn}}", path);

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
