package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Collection;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ViewCollectionHandler implements HttpHandler {

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
        String response = Utils.dynamicNavigator(exchange, "collection/view.html");
        response = response.replace("{{collectionDetails}}", ViewCollectionHandler.getCollectionDetails());
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Populates a table with individual collection values.
    private static String getCollectionDetails() {
        String s = "";
        for (Collection a : Database.getAllCollections()) {
            s += "<tr>"
                    + String.format("\t<td>%s</td>", a.getTitle())
                    + String.format("\t<td>%s</td>", a.getDate())
                    + "\n<td>View in edit.</td>"
                    + String.format("\t<td>%s</td>", a.getLocationId())
                    + String.format("\t<td>%s</td>", a.getExhibitionId())
                    + String.format("\t<td><a href=\"/collection/edit?collectionId=%s\">Edit</a></td>",
                            a.getCollectionId())
                    + String.format("\t<td><a href=\"/collection/delete?collectionId=%s\">Delete</a></td>",
                            a.getCollectionId())
                    + "</tr>";
        }
        return s;
    }

}
