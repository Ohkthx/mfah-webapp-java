package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Member;
import com.mfahproj.webapp.models.Notification;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MemberHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Check if a session exists.
        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        if (member != null) {
            // Valid non-timeout sessions found. Send to member home page.
            String response = Utils.dynamicNavigator(exchange, "member/member.html");
            response = response.replace("{{emailAddress}}", member.getEmailAddress());
            response = response.replace("{{memberDetails}}", MemberHandler.getDetails(member));
            response = MemberHandler.setNotifications(member, response);

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }

        // No prior session, send to login page.
        exchange.getResponseHeaders().add("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
    }

    // Sets the notificaitons
    public static String setNotifications(Member member, String response) {
        List<Notification> notifications = Database.getNotifications(member.getMemberId());
        int totalNotify = 0;
        for (Notification n : notifications) {
            totalNotify += n.getChecked() ? 0 : 1;
        }

        response = response.replace("{{notifyNum}}", String.valueOf(totalNotify));
        return response;
    }

    // Generates the 'About Me' section.
    private static String getDetails(Member member) {
        return "<ul>"
                + String.format("\t<li>First name: %s</li>", member.getFirstName())
                + String.format("\t<li>Last name: %s</li>", member.getLastName())
                + String.format("\t<li>Birth date: %s</li>", member.getBirthDate())
                + String.format("\t<li>Email address: %s</li>", member.getEmailAddress())
                + String.format("\t<li>Membership: %s</li>", member.getMembershipType())
                + String.format("\t<li>Membership Expiration: %s</li>", member.getExpirationDate())
                + String.format("\t<li>Last login: %s</li>", member.getLastLogin())
                + "</ul>";
    }
}
