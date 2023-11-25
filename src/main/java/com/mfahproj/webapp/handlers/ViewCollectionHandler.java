package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.util.HashMap;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Collection;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Exhibition;
import com.mfahproj.webapp.models.Museum;
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

        Utils.sendResponse(exchange, response);
    }

    // Populates a table with individual collection values.
    private static String getCollectionDetails() {
        // Map of exihibitions to display in the table.
        HashMap<Integer, Exhibition> exhibitions = new HashMap<Integer, Exhibition>();
        for (Exhibition exhibition : Database.getAllExhibitions()) {
            exhibitions.put(exhibition.getExhibitionId(), exhibition);
        }

        // Map of museums to display in the table.
        HashMap<Integer, Museum> museums = new HashMap<Integer, Museum>();
        for (Museum museum : Database.getAllMuseums()) {
            museums.put(museum.getMuseumId(), museum);
        }

        String s = "";
        for (Collection a : Database.getAllCollections()) {
            s += "<tr>"
                    + String.format("\t<td>%s</td>", a.getTitle())
                    + String.format("\t<td>%s</td>", a.getDate())
                    + "\n<td>View in edit.</td>"
                    + String.format("\t<td>%s</td>", museums.get(a.getLocationId()).getName())
                    + String.format("\t<td>%s</td>", exhibitions.get(a.getExhibitionId()).getTitle())
                    + String.format("\t<td><a href=\"/collection/edit?collectionId=%s\">Edit</a></td>",
                            a.getCollectionId())
                    + "</tr>";
        }
        return s;
    }

}
