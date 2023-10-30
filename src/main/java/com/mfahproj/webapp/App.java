package com.mfahproj.webapp;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpServer;

public class App {
    // Time until sessions expires with no refreshing.
    // 1000ms * 60 (seconds) * 15 = 15 minutes.
    private static final long TIMEOUT = 1000 * 60 * 15;

    // Holds all of the current employee and member sessions and timestamp.
    private static long lastWipe = System.currentTimeMillis();
    private static Map<String, Member> member_sessions = new HashMap<>();
    private static Map<String, Employee> employee_sessions = new HashMap<>();

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

            boolean hadExpiration = false;
            if (App.checkMemberExpiration(sessionId)) {
                hadExpiration = true;
            } else if (App.checkEmployeeExpiration(sessionId)) {
                hadExpiration = true;
            }

            if (hadExpiration) {
                // Redirect to timeout page.
                String response = Utils.readResourceFile("timeout.html");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

                return true;
            }

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

        // Homepages
        server.createContext("/member", new MiddlewareHandler(new MemberHandler(), callback));
        server.createContext("/employee", new MiddlewareHandler(new EmployeeHandler(), callback));

        // Used for registeration.
        server.createContext("/register", new MiddlewareHandler(new RegisterHandler(), callback));
        server.createContext("/register-employee", new MiddlewareHandler(new RegisterEmployeeHandler(), callback));
        server.setExecutor(null);

        // Start the server for listening.
        server.start();
    }

    // Checks and removes any expired sessions.
    private static void checkExpiredSessions() {
        long maxSeconds = 1000 * 15;
        if (System.currentTimeMillis() - App.lastWipe < maxSeconds) {
            // Don't bother processing yet.
            return;
        }

        // Maximum age for a session.
        long oldest = System.currentTimeMillis() - App.TIMEOUT;

        // Remove all member sessions that exceed the current timeout.
        for (Map.Entry<String, Member> session : App.member_sessions.entrySet()) {
            Member member = session.getValue();
            if (member.getLastLogin().getTime() < oldest) {
                System.out.printf("Expired session: %s\n", member.getEmailAddress());
                App.member_sessions.remove(session.getKey());
            }
        }

        // Remove all employee sessions that exceed the current timeout.
        for (Map.Entry<String, Employee> session : App.employee_sessions.entrySet()) {
            Employee employee = session.getValue();
            if (employee.getLastLogin().getTime() < oldest) {
                System.out.printf("Expired session: %s\n", employee.getEmailAddress());
                App.employee_sessions.remove(session.getKey());
            }
        }

        // Update last wipe timestamp.
        App.lastWipe = System.currentTimeMillis();
    }

    // Checks if the member expired, returns 'true' if so.
    private static boolean checkMemberExpiration(String uuid) {
        Member member = App.member_sessions.get(uuid);
        if (member == null) {
            // No session found.
            return false;
        }

        if (member.getLastLogin().getTime() < System.currentTimeMillis() - App.TIMEOUT) {
            // Timeout exceeded, remove the session and redirect to timeout page.
            App.member_sessions.remove(uuid);
            return true;
        }

        // Refresh the user session.
        member.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
        App.member_sessions.put(uuid, member);
        return false;
    }

    // Checks if the employee expired, returns 'true' if so.
    private static boolean checkEmployeeExpiration(String uuid) {
        Employee employee = App.employee_sessions.get(uuid);
        if (employee == null) {
            // No session found.
            return false;
        }

        if (employee.getLastLogin().getTime() < System.currentTimeMillis() - App.TIMEOUT) {
            // Timeout exceeded, remove the session and redirect to timeout page.
            App.employee_sessions.remove(uuid);
            return true;
        }

        // Refresh the user session.
        employee.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
        App.employee_sessions.put(uuid, employee);
        return false;
    }

    // Obtains a member session to track logins.
    public static Member getMemberSession(String uuid) {
        // Clear any expired sessions.
        App.checkExpiredSessions();

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

    public static String newMemberSession(Member member) {
        // Create new session.
        String uuid = UUID.randomUUID().toString();

        App.member_sessions.put(uuid, member);
        return uuid;
    }

    // Obtains a employee session to track logins.
    public static Employee getEmployeeSession(String uuid) {
        // Clear any expired sessions.
        App.checkExpiredSessions();

        Employee employee = App.employee_sessions.get(uuid);
        if (employee == null) {
            // No session found.
            return null;
        }

        // Update the last login time for the employee to refresh session time.
        employee.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
        App.employee_sessions.put(uuid, employee);

        return employee;
    }

    public static String newEmployeeSession(Employee employee) {
        // Create new session.
        String uuid = UUID.randomUUID().toString();

        App.employee_sessions.put(uuid, employee);
        return uuid;
    }
}
