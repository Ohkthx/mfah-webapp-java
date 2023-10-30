package com.mfahproj.webapp;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        get(exchange);
    }

    // Handles GET requests from the client.
    private void get(HttpExchange exchange) throws IOException {
        String response = Utils.readResourceFile("logout.html");

        // Kill the session.
        String sessionCookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (sessionCookie != null && sessionCookie.startsWith("SESSIONID=")) {
            String sessionId = sessionCookie.split("=")[1];
            App.killSession(sessionId);
        }

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
