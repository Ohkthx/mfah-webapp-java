package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class LoginHandler implements HttpHandler {
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
        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "login.html");
        response = response.replace("{{credentials}}", "");

        Utils.sendResponse(exchange, response);
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Parse the form data.
        Map<String, String> inputs = Utils.parseForm(formData);
        String email = inputs.get("email");
        String password = inputs.get("password");
        String loginType = inputs.get("loginType");

        String sessionId = null;
        if (loginType.toUpperCase().equals("MEMBER")) {
            Member member = Database.getMember(email, password);
            if (member != null) {
                System.out.printf("%s (member) logged in.\n", email);

                // Update the last login.
                member.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
                Database.editMember(member);

                // Create a session for the user
                sessionId = Session.newMemberSession(member);
            }
        } else {
            Employee employee = Database.getEmployee(email, password);
            if (employee != null) {
                System.out.printf("%s (employee) logged in.\n", email);

                // Update the last login.
                employee.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
                Database.editEmployee(employee);

                // Create a session for the user
                sessionId = Session.newEmployeeSession(employee);
            }
        }

        String location = String.format("/%s", loginType.toLowerCase());
        if (!StringUtils.isNullOrEmpty(sessionId)) {
            String cookie = String.format("SESSIONID=%s; Max-Age=%d", sessionId, 900);
            exchange.getResponseHeaders().add("Set-Cookie", cookie);
            exchange.getResponseHeaders().add("Location", location);
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Member not found.
        System.out.printf("%s failed to logged in.\n", email);

        // Load the HTML file to display.
        String response = Utils.dynamicNavigator(exchange, "login.html");
        response = response.replace("{{credentials}}", "<b style='color:red;'>Invalid credentials.</b>");

        Utils.sendResponse(exchange, response);
    }
}
