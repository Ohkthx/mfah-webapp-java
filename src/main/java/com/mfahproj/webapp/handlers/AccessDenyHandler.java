package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AccessDenyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "accessDeny.html");

        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        if (member != null) {
            // Set the notification count.
            response = MemberHandler.setNotifications(member, response);
        }

        Utils.sendResponse(exchange, response);
    }
}
