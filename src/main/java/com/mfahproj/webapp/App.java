package com.mfahproj.webapp;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.sun.net.httpserver.HttpServer;

public class App {
    // Time until sessions expires with no refreshing.
    // 1000ms * 60 (seconds) * 15 = 15 minutes.
    private static final long TIMEOUT = 1000 * 60 * 15;

    // Holds all of the current employee and member sessions.
    private static Map<String, Member> member_sessions = new HashMap<>();
    // private static Map<String, Employee> employee_sessions = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // This is processed before each request is made. Returns true if redirect
        // happened.
        MiddlewareHandler.HttpRequestCallback callback = exchange -> {
            // Check if the member has an expired cookie.
            String sessionCookie = exchange.getRequestHeaders().getFirst("Cookie");
            if (sessionCookie == null || !sessionCookie.startsWith("SESSIONID=")) {
                return false;
            }

            // Get the members session.
            String sessionId = sessionCookie.split("=")[1];
            Member member = App.member_sessions.get(sessionId);
            if (member == null) {
                // No session found.
                return false;
            }

            if (member.getLastLogin().getTime() < System.currentTimeMillis() - App.TIMEOUT) {
                // Timeout exceeded, remove the session and redirect to timeout page.
                App.member_sessions.remove(sessionId);

                // Redirect to timeout page.
                String response = Utils.readResourceFile("timeout.html");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return true;
            }

            // Refresh the user session.
            member.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
            App.member_sessions.put(sessionId, member);
            return false;
        };

        // Load the configuration file.
        Properties config = Utils.loadConfig("app.config");
        int port = 8080;
        try {
            port = Integer.parseInt(config.getProperty("webapp.port"));
        } catch (Exception e) {
            System.out.println("Unable to parse the webapp port number, check configuration file.");
            System.exit(1);
        }

        // Set the database variables.
        Database.setConfiguration(config);

        // Create the HTTP server and URIs to handle.
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Homepage
        server.createContext("/", new MiddlewareHandler(new HomePageHandler(), callback));

        // Login Page
        server.createContext("/login", new MiddlewareHandler(new LoginHandler(), callback));

        // Member Homepage
        server.createContext("/home", new MiddlewareHandler(new HomeHandler(), callback));

        // Used for member registeration.
        server.createContext("/register", new MiddlewareHandler(new RegisterHandler(), callback));
        server.setExecutor(null);

        // Start the server for listening.
        server.start();
    }

    // Obtains a member session to track logins. Removes sessions the exceed a
    // timeout to prevent old sessions being refreshed.
    public static Member getSession(String uuid) {
        long oldest = System.currentTimeMillis() - App.TIMEOUT;

        // Remove all sessions that exceed the current timeout.
        for (Map.Entry<String, Member> session : App.member_sessions.entrySet()) {
            Member member = session.getValue();
            if (member.getLastLogin().getTime() < oldest) {
                System.out.printf("Expired session: %s\n", member.getEmailAddress());
                App.member_sessions.remove(session.getKey());
            }
        }

        Member member = App.member_sessions.get(uuid);
        if (member == null) {
            // No session found.
            return null;
        }

        // Update the last login time for the member to refresh session time.
        member.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
        App.member_sessions.put(uuid, member);

        return member;
    }

    public static String newSession(Member member) {
        // Create new session.
        String uuid = UUID.randomUUID().toString();

        App.member_sessions.put(uuid, member);
        return uuid;
    }
}
