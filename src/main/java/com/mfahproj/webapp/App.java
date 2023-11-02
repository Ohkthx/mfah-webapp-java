package com.mfahproj.webapp;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import com.sun.net.httpserver.HttpServer;

public class App {
    public static void main(String[] args) throws Exception {
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

        // Create a thread responsible for just purging expired sessions.
        Session.startScheduler();

        // Create the HTTP server and URIs to handle.
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        MiddlewareHandler.HttpRequestCallback callback = App.createCallback();

        // Homepage
        server.createContext("/", new MiddlewareHandler(new HomeHandler(), callback));

        // Login / Logout Page
        server.createContext("/login", new MiddlewareHandler(new LoginHandler(), callback));
        server.createContext("/logout", new MiddlewareHandler(new LogoutHandler(), callback));

        // Homepages
        server.createContext("/member", new MiddlewareHandler(new MemberHandler(), callback));
        server.createContext("/employee", new MiddlewareHandler(new EmployeeHandler(), callback));

        // Used for registeration.
        server.createContext("/register", new MiddlewareHandler(new RegisterMemberHandler(), callback));
        server.createContext("/register-employee", new MiddlewareHandler(new RegisterEmployeeHandler(), callback));

        // Used for artifact registeration.
        server.createContext("/register-artifact", new MiddlewareHandler(new ArtifactHandler(), callback));

        server.setExecutor(null);

        // Start the server for listening.
        server.start();
    }

    // Creates a callback that is used to serve as middleware between requests to
    // the server by clients. This handles sending them to the timeout page and
    // removing their session locally.
    public static MiddlewareHandler.HttpRequestCallback createCallback() {
        return exchange -> {
            String sessionId = Session.extractSessionId(exchange);
            if (sessionId == null || !Session.exists(sessionId)) {
                return false;
            }

            if (Session.isExpired(sessionId)) {
                // Kill session and redirect.
                Session.killSession(sessionId);

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
    }
}
