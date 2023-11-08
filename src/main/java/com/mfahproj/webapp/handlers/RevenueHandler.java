package com.mfahproj.webapp.handlers;


import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.MuseumRevenue;
import com.sun.net.httpserver.HttpHandler;
import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;

import java.io.IOException;
import java.io.OutputStream;

public class RevenueHandler implements HttpHandler{
    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {

            String response = Utils.dynamicNavigator(exchange,"revenue.html");
            response = response.replace("{{revenue}}", getRevenue());

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
    }

    public String getRevenue() {
        String revenue = "";
        for (MuseumRevenue museumRevenue : Database.getMuseumRevenue()) {
            revenue += "<tr>";
            revenue += "<td>" + museumRevenue.getMuseumName()+ "</td>";
            revenue += "<td>" + museumRevenue.getRevenue() + "</td>";
            revenue += "</tr>";
        }
        return revenue;
    }
}
