package com.mfahproj.webapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import com.mysql.cj.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;

public class Utils {
    // Loads configuration file.
    public static Properties loadConfig(String filename) {
        Properties config = new Properties();

        // Attempt to open the file.
        try (FileInputStream fis = new FileInputStream(filename)) {
            config.load(fis);
        } catch (IOException ex) {
            System.out.println("Unable to load configuration file " + filename);
            System.exit(1);
        }

        boolean failed = true;
        if (StringUtils.isNullOrEmpty(config.getProperty("db.url"))) {
            System.out.println("No database url specified, check default config.");
        } else if (StringUtils.isNullOrEmpty(config.getProperty("db.user"))) {
            System.out.println("No database user specified, check default config.");
        } else if (StringUtils.isNullOrEmpty(config.getProperty("db.password"))) {
            System.out.println("No database password specified, check default config.");
        } else if (StringUtils.isNullOrEmpty(config.getProperty("webapp.port"))) {
            System.out.println("No webapp port specified, check default config.");
        } else {
            failed = false;
        }

        if (failed) {
            // Cannot continue without configuration file set up.
            System.exit(1);
        }

        return config;
    }

    // Reads a resource file / HTML template to send to client.
    private static String readResourceFile(String filename) throws IOException {
        Path filePath = Paths.get("resources", filename);
        return new String(Files.readAllBytes(filePath));
    }

    // Extracts form data into a hash map.
    public static Map<String, String> parseForm(String formData) throws UnsupportedEncodingException {
        Map<String, String> parsedData = new HashMap<>();

        // Split on the '&' to separate the fields.
        String[] keyValuePairs = formData.split("&");

        for (String pair : keyValuePairs) {
            // Split the key and value from key=value.
            String[] splitPair = pair.split("=");
            if (splitPair.length != 2) {
                continue;
            }

            // Extract the information and store in the hash map.
            String key = URLDecoder.decode(splitPair[0], "UTF-8");
            String value = URLDecoder.decode(splitPair[1], "UTF-8");
            parsedData.put(key, value);
        }

        return parsedData;
    }

    // Creates a href item for the navigation bar.
    private static String makeHref(String link, String text) {
        return String.format("<a href=\"%s\">%s</a>", link, text);
    }

    // Creates the top navigation panel at the top of the page.
    public static String dynamicNavigator(HttpExchange exchange, String filename) throws IOException {
        List<String> elements = new Vector<String>();
        elements.add(makeHref("/", "Home"));
        elements.add(makeHref("/about", "About"));

        // Check if a valid session currently exists.
        boolean isMember = true;
        String sessionId = Session.extractSessionId(exchange);
        if (Session.getMemberSession(sessionId) == null) {
            if (Session.getEmployeeSession(sessionId) == null) {
                // No active sessions found.
                sessionId = null;
            } else {
                // Is not a member but has an active session.
                isMember = false;
            }
        }

        // Modify the 'Profile/Login' navigation menu to change if client is logged in
        if (sessionId == null) {
            elements.add(makeHref("/login", "Login"));
        } else {
            String link = isMember ? "/member" : "/employee";
            elements.add(makeHref(link, "Profile"));
            if (isMember) {
                // Add in the notifications panel.
                elements.add(makeHref("/notifications", "Notifications [{{notifyNum}}]"));
            }

            elements.add(makeHref("/logout", "Logout"));
        }

        String navigation = String.join("\n", elements);
        String resource = Utils.readResourceFile(filename);
        return resource.replace("{{navigationPanel}}", navigation);
    }

    // Wraps sending a response to account for 'too many bytes to write' error.
    public static void sendResponse(HttpExchange exchange, String response) {
        try (OutputStream os = exchange.getResponseBody()) {
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(200, responseBytes.length);
            os.write(responseBytes);
        } catch (Exception e) {
            exchange.getResponseHeaders().add("Location", "/failure");
            try {
                exchange.sendResponseHeaders(302, -1);
            } catch (Exception e2) {
            }
        }
    }
}
