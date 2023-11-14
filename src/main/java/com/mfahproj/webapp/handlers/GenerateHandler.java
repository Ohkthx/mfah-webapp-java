package com.mfahproj.webapp.handlers;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Artifact;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.mfahproj.webapp.models.Transaction;
import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateHandler implements HttpHandler {
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
        Member member = Session.getMemberSession(sessionId);
        if (employee != null || member != null) {
            String type = employee != null ? "employee" : "member";
            String response = Utils.dynamicNavigator(exchange, String.format("/%s/generate.html", type));
            if (member != null) {
                // Updates the notifications panel item.
                response = MemberHandler.setNotifications(member, response);
            }

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
        // Parse the form data to print the information.
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        Map<String, String> form = Utils.parseForm(formData);

        // Check user type requesting.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        Member member = Session.getMemberSession(sessionId);

        // Non-logged in clients should not access this post request.
        if (employee == null && member == null) {
            String response = Utils.dynamicNavigator(exchange, "/login");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

            return;
        }

        // Get basic information about member or employee.
        boolean isEmployee = employee != null ? true : false;
        int id = isEmployee ? employee.getEmployeeId() : member.getMemberId();

        // Could not parse successfully.
        // TODO: Handle this more elegantly.
        if (!GenerateHandler.generate(form, isEmployee, id)) {
            System.out.println("Failed to create random items.");
            exchange.getResponseHeaders().add("Location", "/failure");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        exchange.getResponseHeaders().add("Location", "/success");
        exchange.sendResponseHeaders(302, -1);
    }

    // Parses the form data and generates results based on what was selected.
    private static boolean generate(Map<String, String> formData, boolean isEmployee, int id) {
        String action = formData.get("action");
        if (StringUtils.isNullOrEmpty(action)) {
            return false;
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(formData.get("amount"));
        } catch (Exception e) {
            System.err.println("Unable to parse amount.");
            return false;
        }

        if (action.equalsIgnoreCase("tickets")) {
            GenerateHandler.createTickets(amount, isEmployee, id);
        } else if (action.equalsIgnoreCase("transactions")) {
            GenerateHandler.createTransactions(amount, isEmployee, id);
        } else if (action.equalsIgnoreCase("artifacts")) {
            GenerateHandler.createArtifacts(amount);
        } else {
            System.err.println("No matches for generation.");
            return false;
        }

        return true;
    }

    // Create `amount` of random ticket sales.
    private static void createTickets(int amount, boolean isEmployee, int id) {
        List<Transaction> transactions = new Vector<Transaction>();
        // Create n amount of transactions.
        for (int i = 0; i < amount; i++) {
            if (isEmployee) {
                // Random member.
                id = ThreadLocalRandom.current().nextInt(1, 12);
            }

            // Random museum.
            int museumId = ThreadLocalRandom.current().nextInt(1, 6);
            transactions.add(Transaction.generateRandom(id, museumId, 5.0, 20.0, Transaction.Type.TICKET));

        }

        // Insert into database.
        switch (Database.createTransactionsBatch(transactions)) {
            case SUCCESS:
                break;
            case DUPLICATE:
                System.err.println("Duplicate transaction.");
                break;
            default:
                System.err.println("Unknown error");
        }
    }

    // Create `amount` of random transactions.
    private static void createTransactions(int amount, boolean isEmployee, int id) {
        List<Transaction> transactions = new Vector<Transaction>();

        // Create n amount of transactions.
        for (int i = 0; i < amount; i++) {
            if (isEmployee) {
                // Random member.
                id = ThreadLocalRandom.current().nextInt(1, 12);
            }

            // Random museum.
            int museumId = ThreadLocalRandom.current().nextInt(1, 6);
            transactions.add(Transaction.generateRandom(id, museumId, 5.0, 50.0, null));
        }

        // Insert into database.
        switch (Database.createTransactionsBatch(transactions)) {
            case SUCCESS:
                break;
            case DUPLICATE:
                System.err.println("Duplicate transaction.");
                break;
            default:
                System.err.println("Unknown error");
        }
    }

    // Create `amount` of random artifacts.
    private static void createArtifacts(int amount) {
        List<Artifact> artifacts = new Vector<Artifact>();

        // Create n amount of artifacts.
        for (int i = 0; i < amount; i++) {
            int artistId = ThreadLocalRandom.current().nextInt(1, 16);
            int ownerId = ThreadLocalRandom.current().nextInt(1, 9);
            int collectionId = ThreadLocalRandom.current().nextInt(1, 6);

            artifacts.add(Artifact.generateRandom(artistId, collectionId, ownerId));
        }

        // Insert into database.
        switch (Database.createArtifactsBatch(artifacts)) {
            case SUCCESS:
                break;
            case DUPLICATE:
                System.err.println("Duplicate transaction.");
                break;
            default:
                System.err.println("Unknown error");
        }
    }
}
