package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NotImplementedHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "not-implemented.html");

        Utils.sendResponse(exchange, response);
    }
}
