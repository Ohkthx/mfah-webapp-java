package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.ArtifactInventoryReport;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.ExhibitionAttendanceReport;
import com.mfahproj.webapp.models.MuseumRevenueReport;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            get(exchange);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            post(exchange);
        }
    }

    // Handles GET requests from the client.
    private void get(HttpExchange exchange) throws IOException {
        // Check if a session exists.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee != null) {
            // Valid non-timeout sessions found. Send to employee home page.
            String response = Utils.dynamicNavigator(exchange, "employee/report.html");

            response = response.replace("{{report}}", "");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }

        // No prior session, send to login page.
        exchange.getResponseHeaders().add("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");

        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        String response = "";
        // Parse the form data to print the information.
        Map<String, String> form = Utils.parseForm(formData);
        String type = form.get("type");

        if (type.equals("AIR")) {

            String artifactDate = form.get("artifactDate");
            String collectionDate = form.get("collectionDate");
            String artifactPlace = form.get("filter_artifact_place");
            String artifactMedium = form.get("filter_artifact_medium");

            // Start building the SQL query with placeholders for filters
            StringBuilder query = new StringBuilder(
                    "SELECT Collection.Title AS CollectionTitle, Collection.Date AS CollectionDate, Collection.Description AS CollectionDescription, ");
            query.append(
                    "Artifact.Title AS ArtifactTitle, Artifact.Date AS ArtifactDate, Artifact.Place AS ArtifactPlace, ");
            query.append("Artifact.Medium AS ArtifactMedium, Artifact.Dimensions AS ArtifactDimensions, ");
            query.append("Artist.FirstName AS ArtistFirstName, Artist.LastName AS ArtistLastName ");
            query.append("FROM Collection ");
            query.append("LEFT JOIN Artifact ON Collection.CollectionId = Artifact.CollectionId ");
            query.append("LEFT JOIN Artist ON Artifact.ArtistId = Artist.ArtistId");

            // Create a list to store the filter conditions
            List<String> filterConditions = new ArrayList<>();

            // Check if filters are provided and add them to the list
            if (collectionDate != null && !collectionDate.isEmpty()) {
                filterConditions.add("Collection.Date = '" + collectionDate + "'");
            }

            if (artifactDate != null && !artifactDate.isEmpty()) {
                filterConditions.add("Artifact.Date = '" + artifactDate + "'");
            }

            if (artifactPlace != null && !artifactPlace.isEmpty()) {
                filterConditions.add("Artifact.Place = '" + artifactPlace + "'");
            }

            if (artifactMedium != null && !artifactMedium.isEmpty()) {
                filterConditions.add("Artifact.Medium = '" + artifactMedium + "'");
            }

            // If filter conditions are present, add the WHERE clause
            if (!filterConditions.isEmpty()) {
                query.append(" WHERE ");
                query.append(String.join(" AND ", filterConditions));
            }
            response = Utils.dynamicNavigator(exchange, "employee/report.html");
            response = response.replace("{{report}}", getArtifactInventoryReport(query.toString()));

        }

        // Museum Revenue Report
        else if (type.equals("MRR")) {

            StringBuilder query = new StringBuilder();
            query.append(
                    "SELECT subquery.MuseumId, subquery.Name, subquery.Address,subquery.CurrentTotalRevenue,subquery.TotalRevenue FROM(");
            query.append("SELECT Museum.MuseumId, Museum.Name, Museum.Address, ");
            query.append("Museum.TotalRevenue AS CurrentTotalRevenue, SUM(Transactions.Price) AS TotalRevenue ");
            query.append("FROM Museum ");
            query.append("LEFT JOIN Transactions ON Museum.MuseumId = Transactions.MuseumId ");
            query.append("GROUP BY Museum.MuseumId, Museum.Name, Museum.Address, Museum.TotalRevenue) AS subquery ");

            String museumName = form.get("filter_museum_name");
            String museumAddress = form.get("filter_museum_address");
            String start = form.get("start");
            String end = form.get("end");

            if (museumName != null || museumAddress != null || start != null || end != null) {

                query.append("WHERE ");
                List<String> conditions = new ArrayList<>();
                if (museumName != null && !museumName.isEmpty()) {
                    conditions.add("subquery.Name = '" + museumName + "'");
                }

                if (museumAddress != null && !museumAddress.isEmpty()) {
                    conditions.add("subquery.Address = '" + museumAddress + "'");
                }

                if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
                    conditions.add("subquery.TotalRevenue BETWEEN " + start + " AND " + end);
                }

                if (!conditions.isEmpty()) {
                    query.append(" " + String.join(" AND ", conditions));
                }
            }

            response = Utils.dynamicNavigator(exchange, "employee/report.html");
            response = response.replace("{{report}}", getRevenueReport(query.toString()));
        }

        // Exhibition Attendance Report
        else if (type.equals("EAR")) {
            String startDate = form.get("startDate");
            String endDate = form.get("endDate");
            StringBuilder query = new StringBuilder(
                    "SELECT Exhibition.ExhibitionId, Exhibition.Title AS ExhibitionTitle, Exhibition.StartDate, Exhibition.EndDate, Exhibition.Description, ");
            query.append(
                    "Transactions.ItemId AS TransactionItemId, Transactions.ItemType, Transactions.Price, Transactions.PurchaseDate ");
            query.append("FROM Exhibition ");
            query.append("LEFT JOIN Collection ON Exhibition.ExhibitionId = Collection.ExhibitionId ");
            query.append("LEFT JOIN Transactions ON Collection.CollectionId = Transactions.ItemId");

            // Check if startDate and endDate filters are provided
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                query.append(" WHERE Exhibition.StartDate BETWEEN '").append(startDate).append("' AND '")
                        .append(endDate).append("'");
            }

            response = Utils.dynamicNavigator(exchange, "employee/report.html");
            response = response.replace("{{report}}", getExhibitionScheduleReport(query.toString()));

        }



        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }


    // Report handler
    public static String getArtifactInventoryReport(String query) {
        StringBuilder report = new StringBuilder();
        report.append("<h1>Artifact Inventory Report: </h1>");
        report.append("<table>");
        report.append("<tr>");
        report.append("<th>Artifact Title</th>");
        report.append("<th>Collection Title</th>");
        report.append("<th>Collection Date</th>");
        report.append("<th>Collection Description</th>");
        report.append("<th>Artifact Date</th>");
        report.append("<th>Artifact Place</th>");
        report.append("<th>Artifact Medium</th>");
        report.append("<th>Artifact Dimensions</th>");
        report.append("<th>Artist First Name</th>");
        report.append("<th>Artist Last Name</th>");
        report.append("</tr>");
        for (ArtifactInventoryReport artifactInventoryReport : Database.getArtifactInventoryReport(query)) {
            report.append("<tr>");
            report.append("<td>").append(artifactInventoryReport.getArtifactTitle()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getCollectionTitle()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getCollectionDate()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getCollectionDescription()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getArtifactDate()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getArtifactPlace()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getArtifactMedium()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getArtifactDimensions()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getArtistFirstName()).append("</td>");
            report.append("<td>").append(artifactInventoryReport.getArtistLastName()).append("</td>");
            report.append("</tr>");
        }
        report.append("</table>");
        return report.toString();
    }

    // Exhibition attendance report
    public static String getExhibitionScheduleReport(String query) {
        StringBuilder report = new StringBuilder();
        report.append("<h1> Exhibition Schedule Report </h1>");
        report.append("<table>");
        report.append("<tr>");
        report.append("<th>Exhibition ID</th>");
        report.append("<th>Exhibition Title</th>");
        report.append("<th>Start Date</th>");
        report.append("<th>End Date</th>");
        report.append("<th>Description</th>");
        report.append("<th>Transaction Item ID</th>");
        report.append("<th>Transaction Item Type</th>");
        report.append("<th>Transaction Price</th>");
        report.append("<th>Transaction Purchase Date</th>");
        report.append("</tr>");
        for (ExhibitionAttendanceReport exhibitionAttendanceReport : Database.getExhibitionAttendanceReport(query)) {
            report.append("<tr>");
            report.append("<td>").append(exhibitionAttendanceReport.getExhibitionId()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getExhibitionTitle()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getStartDate()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getEndDate()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getDescription()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getTransactionItemId()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getItemType()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getPrice()).append("</td>");
            report.append("<td>").append(exhibitionAttendanceReport.getPurchaseDate()).append("</td>");
            report.append("</tr>");
        }
        report.append("</table>");
        return report.toString();
    }

    // Get museum revenue report
    public static String getRevenueReport(String query) {
        StringBuilder report = new StringBuilder();
        List<MuseumRevenueReport> museumReports = Database.getMuseumRevenueReport(query);
        report.append("<h1>Museum Revenue Report: </h1>");
        report.append("<table>");
        report.append("<tr>");
        report.append("<th>Museum ID</th>");
        report.append("<th>Name</th>");
        report.append("<th>Address</th>");
        report.append("<th>Current Total Revenue</th>");
        report.append("<th>Total Revenue</th>");
        report.append("</tr>");
        for (MuseumRevenueReport museumRevenueReport : museumReports) {
            report.append("<tr>");
            report.append("<td>").append(museumRevenueReport.getMuseumId()).append("</td>");
            report.append("<td>").append(museumRevenueReport.getMuseumName()).append("</td>");
            report.append("<td>").append(museumRevenueReport.getAddress()).append("</td>");
            report.append("<td>").append(museumRevenueReport.getCurrentTotalRevenue()).append("</td>");
            report.append("<td>").append(museumRevenueReport.getTotalRevenue()).append("</td>");
            report.append("</tr>");
        }
        report.append("</table>");
        return report.toString();
    }

}
