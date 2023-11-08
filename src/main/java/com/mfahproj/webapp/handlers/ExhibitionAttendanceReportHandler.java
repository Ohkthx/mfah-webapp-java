package com.mfahproj.webapp.handlers;
import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.ExhibitionAttendanceReport;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class ExhibitionAttendanceReportHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("ExhibitionAttendanceReportHandler");
        String response = Utils.dynamicNavigator(exchange,"/reports/exhibitionattendancereport.html");
        response = response.replace("{{report}}", getExhibitionAttendanceReport());

        exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
    }

    public static String getExhibitionAttendanceReport() {
        String report = "";
        for (ExhibitionAttendanceReport exhibitionAttendanceReport : Database.getExhibitionAttendanceReport()) {
            report += "<tr>";
            report += "<td>" + exhibitionAttendanceReport.getAttendanceTitle()+ "</td>";
            report += "<td>" + exhibitionAttendanceReport.getAttendance() + "</td>";
            report += "</tr>";
        }
        return report;
    }
}
