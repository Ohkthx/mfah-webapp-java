package com.mfahproj.webapp;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HomePageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionCookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (sessionCookie != null && sessionCookie.startsWith("SESSIONID=")) {
            String sessionId = sessionCookie.split("=")[1];

            Member member = App.getSession(sessionId);
            if (member != null) {
                // Active member session found, redirect to the member home page.
                exchange.getResponseHeaders().add("Location", "/home");
                exchange.sendResponseHeaders(302, -1);
                return;
            }
        }

        // Show default non-logged in homepage.
        String response = Utils.readResourceFile("homepage.html");
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
