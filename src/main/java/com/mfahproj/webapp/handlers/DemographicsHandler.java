package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.mfahproj.webapp.models.MemberDemographics;
import com.sun.net.httpserver.HttpHandler;
import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Utils;

import java.io.IOException;
import java.io.OutputStream;

public class DemographicsHandler implements HttpHandler {
    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        Employee employee = Session.getEmployeeSession(sessionId);

        if (member != null || employee != null) {
            String response = Utils.dynamicNavigator(exchange, "report/demographics.html");
            response = response.replace("{{demographics}}", getDemographics());

            String forMember = "<div class='dropdown'>" +
                    "            <button class='dropbtn'>Data Queries</button>" +
                    "            <div class='dropdown-content'>" +
                    "                <a href='/artistwork'>Artistwork</a>" +
                    "                <a href='/revenue'>Revenue</a>" +
                    "                <a href='/exhibition-collection'>Exhibition Collections</a>" +
                    "                <a href='/demographics'>Demographics</a>" +
                    "            </div>" +
                    "        </div>";

            response = response.replace("{{dropdownmenu}}", forMember);

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        } else {
            String response = Utils.dynamicNavigator(exchange, "login.html");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

            return;
        }
    }

    public String getDemographics() {
        String demographics = "";
        for (MemberDemographics md : Database.getMemberDemographics()) {
            demographics += "<tr>";
            demographics += "<td>" + md.getChildren() + "</td>";
            demographics += "<td>" + md.getTeens() + "</td>";
            demographics += "<td>" + md.getAdults() + "</td>";
            demographics += "<td>" + md.getSeniors() + "</td>";
            demographics += "</tr>";
        }
        return demographics;
    }
}
