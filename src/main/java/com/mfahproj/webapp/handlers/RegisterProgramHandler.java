package com.mfahproj.webapp.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Program;
import com.mfahproj.webapp.models.Employee;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterProgramHandler implements HttpHandler {
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
        if (employee == null) {
            // No prior session, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Show register form for an employee.
        String response = Utils.dynamicNavigator(exchange, "program/register.html");

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

        // Parse the form data to create a new user.
        Map<String, String> form = Utils.parseForm(formData);
        Program program = RegisterProgramHandler.createProgram(form);

        String response;
        switch (Database.createProgram(program)) {
            case SUCCESS:
                // Program created
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("%s created.\n", program.getName());
                return;
            case DUPLICATE:
                // Duplicate program detected, refresh the program register page.
                System.out.printf("%s is a duplicate program.\n", program.getName());
                response = "<body>"
                        + "    <h4>Program already exists, please try again.</h4>"
                        + "    <a href='/program/register'>Register Program</a>"
                        + "</body>";

                break;
            default:
                // Could not create program.
                System.out.printf("%s failed to create.\n", program.getName());
                response = "An unknown error!";
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Creates a new artifact from the form data provided.
    private static Program createProgram(Map<String, String> form) {
        Program program = new Program();

        program.setName(form.get("name"));
        program.setSpeaker(form.get("speaker"));
        program.setRoomName(form.get("roomName"));
        program.setMuseumId(Integer.parseInt(form.get("museumId")));

        Date sdate = RegisterProgramHandler.parseDate(form.get("startDate"));
        Date edate = RegisterProgramHandler.parseDate(form.get("endDate"));
        if (sdate != null) {
            program.setStartDate(new java.sql.Date(sdate.getTime()));
        }
        if (edate != null) {
            program.setStartDate(new java.sql.Date(edate.getTime()));
        }

        return program;
    }

    private static Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
