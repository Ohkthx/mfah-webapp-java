package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

// Used to intercept requests before processing them.
public class MiddlewareHandler implements HttpHandler {
    // Interface used to create a callback.
    @FunctionalInterface
    public interface HttpRequestCallback {
        // Returns 'true' if a redirect happened.
        boolean handle(HttpExchange exchange) throws IOException;
    }

    // Holds the handler and callback.
    private HttpHandler targetHandler;
    private HttpRequestCallback callback;

    // Creates a new instance.
    public MiddlewareHandler(HttpHandler targetHandler, HttpRequestCallback callback) {
        this.targetHandler = targetHandler;
        this.callback = callback;
    }

    // Intercepts the handle, potentially calling the define callback first.
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (callback != null) {
            // Calls the callback if it exists.
            try {
                if (callback.handle(exchange)) {
                    // Client was redirected, do not continue request.
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Process as normal.
        targetHandler.handle(exchange);
    }
}