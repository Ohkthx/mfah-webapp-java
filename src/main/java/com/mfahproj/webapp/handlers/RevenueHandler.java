package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.mfahproj.webapp.models.MuseumRevenue;
import com.sun.net.httpserver.HttpHandler;
import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Utils;

import java.io.IOException;

public class RevenueHandler implements HttpHandler {
    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        String sessionId = Session.extractSessionId(exchange);
        Member member = Session.getMemberSession(sessionId);
        Employee employee = Session.getEmployeeSession(sessionId);

        if (member != null || employee != null) {
            String response = Utils.dynamicNavigator(exchange, "report/revenue.html");
            response = response.replace("{{revenue}}", getRevenue());

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

            Utils.sendResponse(exchange, response);
            return;
        } else {
            String response = Utils.dynamicNavigator(exchange, "login.html");

            Utils.sendResponse(exchange, response);
            return;
        }
    }

    public String getRevenue() {
        String revenue = "";
        for (MuseumRevenue museumRevenue : Database.getMuseumRevenue()) {
            revenue += "<tr>";
            revenue += "<td>" + museumRevenue.getMuseumName() + "</td>";
            revenue += "<td>" + museumRevenue.getRevenue() + "</td>";
            revenue += "</tr>";
        }
        return revenue;
    }
}
