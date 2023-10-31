package com.mfahproj.webapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    public static String readResourceFile(String filename) throws IOException {
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

    public static String dynamicNavigator(HttpExchange exchange, String filename) throws IOException {
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

        String resource = Utils.readResourceFile(filename);
        return resource.replace("{{clientLoggedIn}}", path);
    }
}
