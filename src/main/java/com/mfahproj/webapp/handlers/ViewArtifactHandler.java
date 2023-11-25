package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Artifact;
import com.mfahproj.webapp.models.Artist;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ViewArtifactHandler implements HttpHandler {

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
        String response = Utils.dynamicNavigator(exchange, "artifact/view.html");
        response = response.replace("{{artifactDetails}}", ViewArtifactHandler.getArtifactDetails());
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Populates a table with individual artifact values.
    private static String getArtifactDetails() {
        HashMap<Integer, Artist> artists = new HashMap<Integer, Artist>();
        for (Artist artist : Database.getAllArtists()) {
            artists.put(artist.getArtistId(), artist);
        }

        String s = "";
        for (Artifact a : Database.getAllArtifacts()) {
            Artist artist = artists.get(a.getArtistId());
            s += "<tr>"
                    + String.format("\n<td>%s</td>", a.getTitle())
                    + String.format("\n<td>%s %s</td>", artist.getFirstName(), artist.getLastName())
                    + String.format("\n<td>%s</td>", a.getDate().toString())
                    + String.format("\n<td>%s</td>", a.getPlace())
                    + String.format("\n<td>%s</td>", a.getMedium())
                    + String.format("\n<td><a href=\"/artifact/edit?artifactId=%s\">Edit</a></td>", a.getArtifactId())
                    + String.format("\n<td><a href=\"/artifact/delete?artifactId=%s\">Delete</a></td>",
                            a.getArtifactId())
                    + "</tr>";
        }
        return s;
    }

}
