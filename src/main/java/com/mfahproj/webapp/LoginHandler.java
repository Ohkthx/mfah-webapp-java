package com.mfahproj.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

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
        // Check if a valid session currently exists.
        boolean isMember = true;
        String sessionId = null;
        String sessionCookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (sessionCookie != null && sessionCookie.startsWith("SESSIONID=")) {
            sessionId = sessionCookie.split("=")[1];
            if (App.getMemberSession(sessionId) == null) {
                if (App.getEmployeeSession(sessionId) == null) {
                    // No active sessions found.
                    sessionId = null;
                } else {
                    // Is not a member but has an active session.
                    isMember = false;
                }
            }
        }

        // Modify the 'Profile/Login' navigation menu to change if client is logged in
        String path = "";
        if (sessionId == null) {
            path = String.format("<a href=\"/%s\">%s</a>", "login", "Login");
        } else {
            String text = isMember ? "member" : "employee";
            path = String.format("<a href=\"/%s\">%s</a>", text, "Profile");
        }

        // Edit the placeholders with dynamic text.
        String response = Utils.readResourceFile("login.html");
        response = response.replace("{{credentials}}", "");
        response = response.replace("{{clientLoggedIn}}", path);

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
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
                // TODO: Need to reflect this to database.

                // Create a session for the user
                sessionId = App.newMemberSession(member);
            }
        } else {
            Employee employee = Database.getEmployee(email, password);
            if (employee != null) {
                System.out.printf("%s (employee) logged in.\n", email);

                // Update the last login.
                employee.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
                // TODO: Need to reflect this to database.

                // Create a session for the user
                sessionId = App.newEmployeeSession(employee);
            }
        }

        String location = "/" + loginType.toLowerCase();
        if (!StringUtils.isNullOrEmpty(sessionId)) {
            exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId);
            exchange.getResponseHeaders().add("Location", location);
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Member not found.
        System.out.printf("%s failed to logged in.\n", email);

        String response = Utils.readResourceFile("login.html");
        response = response.replace("{{credentials}}", "<p>Invalid credentials, please try again.</p>");
        exchange.sendResponseHeaders(200, response.length());
        try (
                OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
