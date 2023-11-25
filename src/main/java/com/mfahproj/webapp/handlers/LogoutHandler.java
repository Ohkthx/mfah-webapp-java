package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        get(exchange);
    }

    // Handles GET requests from the client.
    private void get(HttpExchange exchange) throws IOException {
        // Kill the session.
        String sessionId = Session.extractSessionId(exchange);
        if (sessionId != null) {
            Session.killSession(sessionId);
        }

        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "logout.html");

        Utils.sendResponse(exchange, response);
    }
}
