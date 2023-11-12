package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.ExihibitionsAndCollections;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class ExhibitionCollectionHandler implements HttpHandler {
    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {

        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        Employee employee = Session.getEmployeeSession(sessionId);

        if (member != null || employee !=null) {
            String response = Utils.dynamicNavigator(exchange, "exhibition-collection.html");
            response = response.replace("{{exhibition-collection}}", getExhibitionCollection());
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
            return;
        }else {
            String response = Utils.dynamicNavigator(exchange, "login.html");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

            return;
        }
    }

    public String getExhibitionCollection() {
        String exhibitionCollection = "";
        for (ExihibitionsAndCollections exhibition : Database.getExhibitionAndCollection()) {
            exhibitionCollection += "<tr>";
            exhibitionCollection += "<td>" + exhibition.getExihibitionTitle() + "</td>";
            exhibitionCollection += "<td>" + exhibition.getCollectionTitle()+ "</td>";
            exhibitionCollection += "</tr>";
        }
        return exhibitionCollection;
    }
}
