package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "homepage.html");
        String sessionId = Session.extractSessionId(exchange);
        Employee emp = Session.getEmployeeSession(sessionId);
        Member member = Session.getMemberSession(sessionId);

        if(sessionId ==  null){

                System.out.println("Not Logedin");
                response = response.replace("{{dropdownmenu}}",  "");
        }
        else {
            if(emp !=   null){

                String forEmp = "<div class='dropdown'>" +
                                    "<button class='dropbtn'>Reports</button>' " +
                                    "<div class='dropdown-content'> " +
                                        "<a href='/museum-revenue-report'>Museum Revenue Report</a>" +
                                        "<a href='/artifact-inventory-report'>Artifact Inventory Report</a> " +
                                        "<a href='/exhibition-attendance-report'>Exhibition Attendance Report</a>" +
                                    "</div>" +
                                "</div>";
                String forMember =  "<div class='dropdown'> <button class='dropbtn'>Data Queries</button><div class='dropdown-content'><a href='/artistwork'>Artistwork</a><a href='/revenue'>Revenue</a><a href='/exhibition-collection'>Exhibition Collections</a></div></div>";
                response = response.replace("{{dropdownmenu}}", forEmp);
            }
            else if (member != null){

                String forMember =  "<div class='dropdown'>" +
                        "            <button class='dropbtn'>Data Queries</button>" +
                        "            <div class='dropdown-content'>" +
                        "                <a href='/artistwork'>Artistwork</a>" +
                        "                <a href='/revenue'>Revenue</a>" +
                        "                <a href='/exhibition-collection'>Exhibition Collections</a>" +
                        "            </div>" +
                        "        </div>";

                response = response.replace("{{dropdownmenu}}",  forMember);
            }
            else{
                System.out.println("Not Logedin");
                response = response.replace("{{dropdownmenu}}",  "");
            }

        }
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }



}
