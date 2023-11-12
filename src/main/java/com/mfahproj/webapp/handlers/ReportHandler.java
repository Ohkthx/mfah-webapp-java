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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        String response="";
        // Parse the form data to print the information.
        Map<String, String> form = Utils.parseForm(formData);
        String type  = form.get("type");
        // keys: action, type, amount
        if (type.equals("AIR")){
            System.out.println("This is AI report Request");
            response = Utils.dynamicNavigator(exchange, "employee/report.html");
            response = response.replace("{{report}}", getArtifactInventoryReport());

        } else if (type.equals("MRR")) {
            System.out.println("This is MR report Request");
             response = Utils.dynamicNavigator(exchange, "employee/report.html");
            response = response.replace("{{report}}", getRevenueReport());
        }
        else if (type.equals("EAR")){
            System.out.println("This is EA report Request");
            response = Utils.dynamicNavigator(exchange, "employee/report.html");
            response = response.replace("{{report}}", getExhibitionAttendanceReport());
        }

        // Load register form.
//        String response = Utils.dynamicNavigator(exchange, "employee/report.html");
//        response = response.replace("{{report}}", getArtifactInventoryReport());
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }


    // Report handler
    public static String getArtifactInventoryReport() {
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
        for (ArtifactInventoryReport artifactInventoryReport : Database.getArtifactInventoryReport()) {
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
    public static String getExhibitionAttendanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("<h1> Exhibition Attendance Report </h1>");
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
        for (ExhibitionAttendanceReport exhibitionAttendanceReport : Database.getExhibitionAttendanceReport()) {
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
    public static String getRevenueReport() {
        StringBuilder report = new StringBuilder();
        List<MuseumRevenueReport> museumReports = Database.getMuseumRevenueReport();
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





