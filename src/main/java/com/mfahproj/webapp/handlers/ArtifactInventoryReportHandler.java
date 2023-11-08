package com.mfahproj.webapp.handlers;


import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.ArtifactInventoryReport;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class ArtifactInventoryReportHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("ArtifactInventoryReportHandler");
        String response = Utils.dynamicNavigator(exchange,"/reports/artifactinventoryreport.html");
        response = response.replace("{{report}}", getArtifactInventoryReport());

        exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
    }

    public static String getArtifactInventoryReport() {
        String report = "";
        for (ArtifactInventoryReport artifactInventoryReport : Database.getArtifactInventoryReport()) {
            report += "<tr>";
            report += "<td>" + artifactInventoryReport.getArtifactTitle()+ "</td>";
            report += "<td>" + artifactInventoryReport.getCollectionTitle() + "</td>";
            report += "<td>" + artifactInventoryReport.getFirstName() + "</td>";
            report += "<td>" + artifactInventoryReport.getLastName() + "</td>";
            report += "</tr>";
        }
        return report;
    }

}
