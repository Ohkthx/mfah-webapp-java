package com.mfahproj.webapp.handlers;

import java.io.IOException;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.ArtifactOwner;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ViewArtifactOwnerHandler implements HttpHandler {

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
        String response = Utils.dynamicNavigator(exchange, "artifactOwner/view.html");
        response = response.replace("{{artifactOwnerDetails}}",
                ViewArtifactOwnerHandler.getArtifactOwnerDetails());

        Utils.sendResponse(exchange, response);
    }

    // Populates a table with individual artifactOwner values.
    private static String getArtifactOwnerDetails() {
        String s = "";
        for (ArtifactOwner a : Database.getAllArtifactOwners()) {
            s += "<tr>"
                    + String.format("\t<td>%s</td>", a.getName())
                    + String.format("\t<td>%s</td>", a.getPhoneNumber())
                    + String.format("\t<td><a href=\"/artifactOwner/edit?artifactOwnerId=%s\">Edit</a></td>",
                            a.getOwnerId())
                    + "</tr>";
        }
        return s;
    }

}
