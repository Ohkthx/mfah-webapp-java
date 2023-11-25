package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AboutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "about.html");
        response = response.replace("{{notifyNum}}", "0");

        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        if (member != null) {
            // Updates the notifications panel item.
            response = MemberHandler.setNotifications(member, response);
        }

        Utils.sendResponse(exchange, response);
    }

}
