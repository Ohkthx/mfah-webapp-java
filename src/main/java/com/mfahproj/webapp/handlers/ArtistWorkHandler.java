package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.ArtistArtWork;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpHandler;

public class ArtistWorkHandler implements HttpHandler {
    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {

        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        Employee employee = Session.getEmployeeSession(sessionId);

        if (member != null || employee != null) {

            String response = Utils.dynamicNavigator(exchange, "report/artwork.html");
            response = response.replace("{{artistwork}}", getArtist());

            String forMember = "<div class='dropdown'>" +
                    "            <button class='dropbtn'>Data Queries</button>" +
                    "            <div class='dropdown-content'>" +
                    "                <a href='/artistwork'>Artistwork</a>" +
                    "                <a href='/revenue'>Revenue</a>" +
                    "                <a href='/exhibition-collection'>Exhibition Collections</a>" +
                    "            </div>" +
                    "        </div>";

            response = response.replace("{{dropdownmenu}}", forMember);

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            String response = Utils.dynamicNavigator(exchange, "login.html");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        return;
    }

    public String getArtist() {
        String artistwork = "";
        for (ArtistArtWork artistArtWork : Database.getArtistArtWork()) {
            artistwork += "<tr>";
            artistwork += "<td>" + artistArtWork.getFirstName() + "</td>";
            artistwork += "<td>" + artistArtWork.getLastName() + "</td>";
            artistwork += "<td>" + artistArtWork.getArtworkTitle() + "</td>";
            artistwork += "</tr>";
        }

        return artistwork;
    }

}
