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
}
