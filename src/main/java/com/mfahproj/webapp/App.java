package com.mfahproj.webapp;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.mfahproj.webapp.handlers.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class App {

    public static void main(String[] args) throws Exception {
        // Load the configuration file.
        Config config = Config.loadConfig("app.config");

        // Set the database variables.
        Database.setConfiguration(config);

        // Create thread and middleware responsible for managing expired sessions.
        Session.startScheduler();
        MiddlewareHandler.HttpRequestCallback callback = App.createCallback();

        // Create HTTPS server and set routes.
        if (config.webappUseHttps) {
            HttpServer httpsServer = App.createServer(config, true);
            App.setRoutes(httpsServer, callback);
            httpsServer.setExecutor(Executors.newCachedThreadPool());
            httpsServer.start();
            System.out.printf("Started HTTPS server on port %d.\n", config.webappHttpsPort);
        }

        // Create HTTP server and set routes.
        HttpServer httpServer = App.createServer(config, false);
        App.setRoutes(httpServer, callback);
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.start();
        System.out.printf("Started HTTP server on port %d.\n", config.webappHttpPort);
    }

    // Assign routes to the server.
    private static void setRoutes(HttpServer server, MiddlewareHandler.HttpRequestCallback callback) {
        // Homepage
        server.createContext("/", new MiddlewareHandler(new HomeHandler(), callback));
        server.createContext("/failure", new MiddlewareHandler(new FailureHandler(), callback));
        server.createContext("/success", new MiddlewareHandler(new SuccessHandler(), callback));

        // Login / Logout Page
        server.createContext("/login", new MiddlewareHandler(new LoginHandler(), callback));
        server.createContext("/logout", new MiddlewareHandler(new LogoutHandler(), callback));

        // Homepages
        server.createContext("/member", new MiddlewareHandler(new MemberHandler(), callback));
        server.createContext("/employee", new MiddlewareHandler(new EmployeeHandler(), callback));
        server.createContext("/generate", new MiddlewareHandler(new GenerateHandler(), callback));
        server.createContext("/notifications", new MiddlewareHandler(new NotificationsHandler(), callback));

        // Used for registration.
        server.createContext("/member/register", new MiddlewareHandler(new RegisterMemberHandler(), callback));
        server.createContext("/employee/register", new MiddlewareHandler(new RegisterEmployeeHandler(), callback));
        server.createContext("/artifact/register", new MiddlewareHandler(new ArtifactHandler(), callback));

        // Used for editing content.
        server.createContext("/member/edit", new MiddlewareHandler(new EditMemberHandler(), callback));
        server.createContext("/employee/edit", new MiddlewareHandler(new EditEmployeeHandler(), callback));

        // Reports
        server.createContext("/employee/report", new MiddlewareHandler(new ReportHandler(), callback));

        // Artwork
        server.createContext("/artistwork", new MiddlewareHandler(new ArtistWorkHandler(), callback));

        //Museum Revenue
        server.createContext("/revenue", new MiddlewareHandler(new RevenueHandler(), callback));

        //Exhibition and Collection
        server.createContext("/exhibition-collection", new MiddlewareHandler(new ExhibitionCollectionHandler(), callback));

        // Museum Revenue Report
        server.createContext("/museum-revenue-report", new MiddlewareHandler(new MuseumRevenueReportHandler(), callback));

        // Artifact Inventory Report
        server.createContext("/artifact-inventory-report", new MiddlewareHandler(new ArtifactInventoryReportHandler(), callback));

        // Exhibition Attendance Report
        server.createContext("/exhibition-attendance-report", new MiddlewareHandler(new ExhibitionAttendanceReportHandler(), callback));


    }


    // Creates a server based on if it is HTTPS or HTTP.
    private static HttpServer createServer(Config config, boolean useHttps) throws Exception {
        if (!useHttps) {
            return HttpServer.create(new InetSocketAddress(config.webappHttpPort), 0);
        }

        // Load the converted Let's Encrypt certificate.
        char[] password = config.webappPassword.toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream(config.webappCert);
        ks.load(fis, password);

        // Set up the factories.
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);
        tmf.init(ks);

        // Set SSL context.
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        // Create the server.
        HttpsServer server = HttpsServer.create(new InetSocketAddress(config.webappHttpsPort), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                // Initialize SSL context for connections.
                SSLContext c = getSSLContext();
                SSLParameters sslparams = c.getDefaultSSLParameters();
                params.setSSLParameters(sslparams);
            }
        });

        return server;
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
