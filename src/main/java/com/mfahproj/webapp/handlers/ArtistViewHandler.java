package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Artist;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ArtistViewHandler implements HttpHandler {

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
        String response = Utils.dynamicNavigator(exchange, "artist/view.html");
        response = response.replace("{{artistDetails}}", ArtistViewHandler.getArtistDetails(sessionId));
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Populates a table with individual artist values.
    private static String getArtistDetails(String session) {
        String s = "";
        for (Artist a : Database.getAllArtists()) {
            s += "<tr>"
                    + String.format("\t<td>%s</td>", a.getFirstName())
                    + String.format("\t<td>%s</td>", a.getLastName())
                    + String.format("\t<td><a href=\"/artist/edit?artistId=%s\">Edit</a></td>", a.getArtistId())
                    + "</tr>";
        }
        return s;
    }

}
