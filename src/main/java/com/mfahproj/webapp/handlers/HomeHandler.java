package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "homepage.html");
        response = response.replace("{{notifyNum}}", "0");

        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        Employee employee = Session.getEmployeeSession(sessionId);

        if (sessionId == null) {
            response = response.replace("{{dropdownmenu}}", "");
        } else {
            if (member != null || employee != null) {
                String forMember = "<div class='dropdown'>" +
                        "            <button class='dropbtn'>Data Queries</button>" +
                        "            <div class='dropdown-content'>" +
                        "                <a href='/artistwork'>Artistwork</a>" +
                        "                <a href='/revenue'>Revenue</a>" +
                        "                <a href='/exhibition-collection'>Exhibition Collections</a>" +
                        "            </div>" +
                        "        </div>";

                response = response.replace("{{dropdownmenu}}", forMember);
                if (member != null) {
                    // Updates the notifications panel item.
                    response = MemberHandler.setNotifications(member, response);
                }
            } else {
                response = response.replace("{{dropdownmenu}}", "");
            }

        }
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

}
