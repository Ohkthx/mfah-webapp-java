package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.MuseumRevenueReport;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class MuseumRevenueReportHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("MuseumRevenueReportHandler");
        String response = Utils.dynamicNavigator(exchange,"museumrevenuereport.html");
            response = response.replace("{{report}}", getRevenueReport());

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
    }

    public static String getRevenueReport() {
        String report = "";
        for (MuseumRevenueReport museumRevenueReport : Database.getMuseumRevenueReport()) {
            report += "<tr>";
            report += "<td>" + museumRevenueReport.getMuseumName()+ "</td>";
            report += "<td>" + museumRevenueReport.getTotalRevenue() + "</td>";
            report += "</tr>";
        }
        return report;
    }
}
