package com.mfahproj.webapp;

import java.util.Properties;

// Holds configuration details.
public class Config {
    // Default database information.
    String dbUrl = "jdbc:mysql://localhost:3306/museum";
    String dbUser = "placeholder";
    String dbPassword = "placeholder";

    // Default WebApp information.
    int webappHttpPort = 8080;
    int webappHttpsPort = 4433;
    boolean webappUseHttps = false;
    String webappCert = "placeholder";
    String webappPassword = "placeholder";

    private Config() {
    }

    // Loads the configuration file.
    public static Config loadConfig(String filename) {
        Config config = new Config();
        // Load the configuration file as a map.
        Properties props = Utils.loadConfig("app.config");
        try {
            config.webappHttpPort = Integer.parseInt(props.getProperty("webapp.port", "8080"));
        } catch (Exception e) {
            System.out.println("Unable to parse the webapp port number, check configuration file.");
        }

        try {
            config.webappHttpsPort = Integer.parseInt(props.getProperty("webapp.httpsPort", "4433"));
        } catch (Exception e) {
            System.out.println("Unable to parse the webapp https port number, check configuration file.");
            System.exit(1);
        }

        // Check if using HTTPS
        try {
            config.webappUseHttps = Boolean.parseBoolean(props.getProperty("webapp.https"));
        } catch (Exception e) {
            System.out.println("Unable to parse the webapp https value, using default.");
        }

        // Load the HTTPS data.
        config.webappCert = props.getProperty("webapp.cert", config.webappCert);
        config.webappPassword = props.getProperty("webapp.password", config.webappPassword);

        // Extract database connection data.
        config.dbUrl = props.getProperty("db.url", config.dbUrl);
        config.dbUser = props.getProperty("db.user", config.dbUser);
        config.dbPassword = props.getProperty("db.password", config.dbPassword);
        return config;
    }
}