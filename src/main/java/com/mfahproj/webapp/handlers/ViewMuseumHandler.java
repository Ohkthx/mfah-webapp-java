package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Museum;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ViewMuseumHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            get(exchange);
        }
    }

    private void get(HttpExchange exchange) throws IOException {
        // Validate a employee is accessing the page, otherwise send to login.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Send the employee the viewing page.
        String response = Utils.dynamicNavigator(exchange, "museum/view.html");
        response = response.replace("{{museumDetails}}", ViewMuseumHandler.getMuseumDetails());

        Utils.sendResponse(exchange, response);
    }

    // Populates a table with individual museum values.
    private static String getMuseumDetails() {
        String s = "";
        for (Museum a : Database.getAllMuseums()) {
            s += "<tr>"
                    + String.format("\n<td>%s</td>", a.getName())
                    + String.format("\n<td>%s</td>", a.getAddress())
                    + String.format("\n<td><a href=\"/museum/edit?museumId=%s\">Edit</a></td>", a.getMuseumId())
                    + "</tr>";
        }
        return s;
    }

}
