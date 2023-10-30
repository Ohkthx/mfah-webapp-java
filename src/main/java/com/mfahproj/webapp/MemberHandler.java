package com.mfahproj.webapp;

import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class MemberHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Extrack cookie and see if a valid session exists.
        String sessionCookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (sessionCookie != null && sessionCookie.startsWith("SESSIONID=")) {
            String sessionId = sessionCookie.split("=")[1];

            Member member = App.getMemberSession(sessionId);
            if (member != null) {
                // Valid non-timeout sessions found. Send to member home page.
                String response = Utils.readResourceFile("member.html");
                response = response.replace("{{emailAddress}}", member.getEmailAddress());

                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }
        }

        // No prior session, send to login page.
        exchange.getResponseHeaders().add("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
    }
}
