package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.util.HashMap;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Exhibition;
import com.mfahproj.webapp.models.Museum;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ViewExhibitionHandler implements HttpHandler {

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
        String response = Utils.dynamicNavigator(exchange, "exhibition/view.html");
        response = response.replace("{{exhibitionDetails}}", ViewExhibitionHandler.getExhibitionDetails());

        Utils.sendResponse(exchange, response);
    }

    // Populates a table with individual exhibition values.
    private static String getExhibitionDetails() {
        // Map of museums to display in the table.
        HashMap<Integer, Museum> museums = new HashMap<Integer, Museum>();
        for (Museum museum : Database.getAllMuseums()) {
            museums.put(museum.getMuseumId(), museum);
        }

        String s = "";
        for (Exhibition a : Database.getAllExhibitions()) {
            s += "<tr>"
                    + String.format("\n<td>%s</td>", a.getTitle())
                    + String.format("\n<td>%s</td>", a.getStartDate().toString())
                    + String.format("\n<td>%s</td>", a.getEndDate().toString())
                    + "\n<td>View in edit.</td>"
                    // + String.format("\n<td>Description too long, edit to view.</td>",
                    // a.getDescription())
                    + String.format("\n<td>%s</td>", museums.get(a.getMuseumId()).getName())
                    + String.format("\n<td><a href=\"/exhibition/edit?exhibitionId=%s\">Edit</a></td>",
                            a.getExhibitionId())
                    + "</tr>";
        }
        return s;
    }

}
