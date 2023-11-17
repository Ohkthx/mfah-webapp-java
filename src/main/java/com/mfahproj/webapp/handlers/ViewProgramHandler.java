package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Program;
import com.mfahproj.webapp.models.Employee;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ViewProgramHandler implements HttpHandler {

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
        String response = Utils.dynamicNavigator(exchange, "program/view.html");
        response = response.replace("{{programDetails}}", ViewProgramHandler.getProgramDetails());
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Populates a table with individual program values.
    private static String getProgramDetails() {
        String s = "";
        for (Program a : Database.getAllPrograms()) {
            s += "<tr>"
                    + String.format("\n<td>%s</td>", a.getName())
                    + String.format("\n<td>%s</td>", a.getSpeaker())
                    + String.format("\n<td>%s</td>", a.getRoomName())
                    + String.format("\n<td>%s</td>", a.getStartDate().toString())
                    + String.format("\n<td>%s</td>", a.getEndDate().toString())
                    + String.format("\n<td><a href=\"/program/edit?programId=%s\">Edit</a></td>", a.getProgramId())
                    + String.format("\n<td><a href=\"/program/delete?programId=%s\">Delete</a></td>", a.getProgramId())
                    + "</tr>";
        }
        return s;
    }

}
