package com.mfahproj.webapp;

import java.sql.*;
import java.util.Properties;

import com.mysql.cj.util.StringUtils;

public class Database {
    // Result of a database query / insertion / update.
    public static enum Result {
        SUCCESS,
        DUPLICATE,
        FAILURE
    }

    // Connection requirements.
    private static String URL = "jdbc:mysql://localhost:3306/museum";
    private static String USER = "placeholder";
    private static String PASSWORD = "placeholder";

    // Load the MySQL JDBC driver, this is required.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Parses the configuration for the values for the database connection.
    public static void setConfiguration(Properties config) {
        Database.URL = config.getProperty("db.url");
        Database.USER = config.getProperty("db.user");
        Database.PASSWORD = config.getProperty("db.password");

        // Make sure the database variables are semi-valid.
        boolean failed = true;
        if (StringUtils.isNullOrEmpty(Database.URL)) {
            System.out.println("No database url specified, check default config.");
        } else if (StringUtils.isNullOrEmpty(Database.USER)) {
            System.out.println("No database user specified, check default config.");
        } else if (StringUtils.isNullOrEmpty(Database.PASSWORD)) {
            System.out.println("No database password specified, check default config.");
        } else {
            failed = false;
        }

        if (failed) {
            // Cannot continue without proper database set up.
            System.exit(1);
        }
    }

    // Creates a connection the database.
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Checks if a user has valid credentials, this wraps getMember().
    public static boolean checkCredentials(String email, String password) {
        Member member = Database.getMember(email, password);
        return member == null ? false : true;
    }

    // Obtain a user from the database using credentials.
    public static Member getMember(String email, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Members WHERE EmailAddress = ? AND Password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Member member = new Member();
            member.setMemberId(results.getInt("MemberId"));
            member.setFirstName(results.getString("FirstName"));
            member.setLastName(results.getString("LastName"));
            member.setMembershipType(results.getString("MembershipType"));
            member.setBirthDate(results.getDate("BirthDate"));
            member.setEmailAddress(results.getString("EmailAddress"));
            member.setPassword(results.getString("Password"));
            member.setLastLogin(results.getDate("LastLogin"));

            return member;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cleanup all of the connections and resources.
            try {
                if (results != null)
                    results.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Create a new member in the database. Fails on duplicates.
    public static Result createMember(Member member) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO Members "
                    + "(FirstName, LastName, MembershipType, BirthDate, EmailAddress, Password, LastLogin) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            System.out.println("CREATING NEW USER");
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getMembershipType());
            pstmt.setDate(4, member.getBirthDate());
            pstmt.setString(5, member.getEmailAddress());
            pstmt.setString(6, member.getPassword());
            pstmt.setDate(7, member.getLastLogin());

            // Execute the query
            pstmt.executeUpdate();
            return Result.SUCCESS;
        } catch (SQLIntegrityConstraintViolationException e) {
            return Result.DUPLICATE;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAILURE;
        } finally {
            // Cleanup all of the connections and resources.
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
