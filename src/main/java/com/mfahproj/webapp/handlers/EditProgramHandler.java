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
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EditProgramHandler implements HttpHandler {
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
        // Validate the session before sending page.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Extract the ProgramId from the query.
        boolean invalidProgram = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int programId = -1;
        try {
            programId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidProgram = true;
        }

        // Obtain the entity from the database.
        Program program = Database.getProgram(programId);
        if (program == null) {
            invalidProgram = true;
        }

        // Update the credentials to something meaningful if there was an error.
        String response = Utils.dynamicNavigator(exchange, "program/edit.html");
        if (invalidProgram) {
            response = response.replace("{{credentials}}", "<b style='color:red;'>Invalid Id.</b>");
        } else {
            response = response.replace("{{credentials}}", "");
        }

        // Update the default form data by swapping out the placeholders.
        response = EditProgramHandler.setDefaults(program, response);

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Handles POST requests from the client.
    private void post(HttpExchange exchange) throws IOException {
        // Validate the session before sending page.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        if (employee == null) {
            // They are not logged in, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        // Extract the ProgramId from the query.
        boolean invalidProgram = false;
        String query = exchange.getRequestURI().getQuery();
        query = query.replaceAll("[^0-9]", "");

        int programId = -1;
        try {
            programId = Integer.parseInt(query);
        } catch (Exception e) {
            invalidProgram = true;
        }

        // Obtain the entity from the database.
        Program program = Database.getProgram(programId);
        if (program == null) {
            invalidProgram = true;
        }

        if (invalidProgram) {
            // Send them to the failure page.
            exchange.getResponseHeaders().add("Location", "/failure");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Parse the form data to edit the program information.
        Map<String, String> form = Utils.parseForm(formData);
        program = EditProgramHandler.editProgram(program, form);

        // Load edit form.
        String response = Utils.dynamicNavigator(exchange, "program/edit.html");
        // Update the default form data by swapping out the placeholders.
        response = EditProgramHandler.setDefaults(program, response);

        switch (Database.editProgram(program)) {
            case SUCCESS:
                // Update the employees session.
                Session.updateEmployeeSession(sessionId, employee);

                // Create a session for the new employee.
                exchange.getResponseHeaders().add("Location", "/success");
                exchange.sendResponseHeaders(302, -1);

                System.out.printf("Program: %s edited.\n", program.getName());
                return;
            default:
                // Could not create program.
                System.out.printf("Program: %s failed to edit.\n", program.getName());
                response = response.replace("{{credentials}}", "<b style='color:red;'>An unknown error occurred.</b>");
        }

        // Send the response based on the error.
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Sets the defaults values for a form.
    private static String setDefaults(Program program, String webpage) {
        if (program == null) {
            // Create a default program with blank values. Credentials will show an error.
            program = new Program();
        }

        // Replace the placeholder data.
        webpage = webpage.replace("{{programId}}", Integer.toString(program.getProgramId()));
        webpage = webpage.replace("{{name}}", program.getName());
        webpage = webpage.replace("{{speaker}}", program.getSpeaker());
        webpage = webpage.replace("{{roomName}}", program.getRoomName());
        webpage = webpage.replace("{{startDate}}", program.getStartDate().toString());
        return webpage.replace("{{endDate}}", program.getEndDate().toString());
    }

    // Edits an antifact from the form data provided.
    private static Program editProgram(Program program, Map<String, String> form) {

        if (!StringUtils.isNullOrEmpty(form.get("programId"))) {
            program.setProgramId(Integer.parseInt(form.get("programId")));
        }

        if (!StringUtils.isNullOrEmpty(form.get("name"))) {
            program.setName(form.get("name"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("speaker"))) {
            program.setSpeaker(form.get("speaker"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("roomName"))) {
            program.setRoomName(form.get("roomName"));
        }

        if (!StringUtils.isNullOrEmpty(form.get("startDate"))) {
            Date date = EditProgramHandler.parseDate(form.get("startDate"));
            if (date != null) {
                program.setStartDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("endDate"))) {
            Date date = EditProgramHandler.parseDate(form.get("endDate"));
            if (date != null) {
                program.setEndDate(new java.sql.Date(date.getTime()));
            }
        }

        if (!StringUtils.isNullOrEmpty(form.get("museumId"))) {
            program.setMuseumId(Integer.parseInt(form.get("museumId")));
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
