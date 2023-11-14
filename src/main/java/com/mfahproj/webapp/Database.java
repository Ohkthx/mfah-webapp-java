package com.mfahproj.webapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Date;

import com.mfahproj.webapp.models.*;
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
    public static void setConfiguration(Config config) {
        Database.URL = config.dbUrl;
        Database.USER = config.dbUser;
        Database.PASSWORD = config.dbPassword;

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
    public static boolean checkCredentials(String email, String password, boolean isMember) {
        if (isMember) {
            Member member = Database.getMember(email, password);
            return member == null ? false : true;
        }

        Employee employee = Database.getEmployee(email, password);
        return employee == null ? false : true;
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

            // No next results means it failed.
            if (!results.next()) {
                return null;
            }

            Member member = new Member();
            member.setMemberId(results.getInt("MemberId"));
            member.setFirstName(results.getString("FirstName"));
            member.setLastName(results.getString("LastName"));
            member.setMembershipType(results.getString("MembershipType"));
            member.setExpirationDate(results.getDate("ExpirationDate"));
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

            // Prepare a SQL query to create member.
            String sql = "INSERT INTO Members "
                    + "(FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getMembershipType());
            pstmt.setDate(4, member.getExpirationDate());
            pstmt.setDate(5, member.getBirthDate());
            pstmt.setString(6, member.getEmailAddress());
            pstmt.setString(7, member.getPassword());
            pstmt.setDate(8, member.getLastLogin());

            // Execute the query
            pstmt.executeUpdate();
            return Result.SUCCESS;
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
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

    // Edit an existing member in the database.
    public static Result editMember(Member member) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to update.
            String sql = "UPDATE Members "
                    + "SET FirstName = ?, LastName = ?, Password = ?, EmailAddress = ?, LastLogin = ?"
                    + "WHERE MemberId = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getPassword());
            pstmt.setString(4, member.getEmailAddress());
            pstmt.setDate(5, member.getLastLogin());
            pstmt.setInt(6, member.getMemberId());

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

    // Obtain a user from the database using Id.
    public static Employee getEmployee(int employeeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Employee WHERE EmployeeId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Employee employee = new Employee();
            employee.setEmployeeId(results.getInt("EmployeeId"));
            employee.setMuseumId(results.getInt("MuseumId"));
            employee.setFirstName(results.getString("FirstName"));
            employee.setLastName(results.getString("LastName"));
            employee.setJobTitle(results.getString("JobTitle"));
            employee.setPhoneNumber(results.getString("PhoneNumber"));
            employee.setEmailAddress(results.getString("EmailAddress"));
            employee.setPassword(results.getString("Password"));
            employee.setSalary(results.getDouble("Salary"));
            employee.setSupervisorId(results.getInt("SupervisorId"));
            employee.setAccessLevel(results.getString("AccessLevel"));
            employee.setLastLogin(results.getDate("LastLogin"));

            return employee;
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

    // Obtain a user from the database using credentials.
    public static Employee getEmployee(String email, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Employee WHERE EmailAddress = ? AND Password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Employee employee = new Employee();
            employee.setEmployeeId(results.getInt("EmployeeId"));
            employee.setMuseumId(results.getInt("MuseumId"));
            employee.setFirstName(results.getString("FirstName"));
            employee.setLastName(results.getString("LastName"));
            employee.setJobTitle(results.getString("JobTitle"));
            employee.setPhoneNumber(results.getString("PhoneNumber"));
            employee.setEmailAddress(results.getString("EmailAddress"));
            employee.setPassword(results.getString("Password"));
            employee.setSalary(results.getDouble("Salary"));
            employee.setSupervisorId(results.getInt("SupervisorId"));
            employee.setAccessLevel(results.getString("AccessLevel"));
            employee.setLastLogin(results.getDate("LastLogin"));

            return employee;
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

    // Create a new employee in the database. Fails on duplicates.
    public static Result createEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO Employee "
                    + "(FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getJobTitle());
            pstmt.setString(4, employee.getPhoneNumber());
            pstmt.setString(5, employee.getEmailAddress());
            pstmt.setString(6, employee.getPassword());
            pstmt.setDouble(7, employee.getSalary());
            pstmt.setInt(8, employee.getMuseumId());
            pstmt.setInt(9, employee.getSupervisorId());
            pstmt.setString(10, employee.getAccessLevel());
            pstmt.setDate(11, employee.getLastLogin());

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

    // Edit an existing employee in the database.
    public static Result editEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "UPDATE Employee "
                    + "SET FirstName = ?, LastName = ?, Password = ?, PhoneNumber = ?, LastLogin = ? "
                    + "WHERE EmployeeId = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getPassword());
            pstmt.setString(4, employee.getPhoneNumber());
            pstmt.setDate(5, employee.getLastLogin());
            pstmt.setInt(6, employee.getEmployeeId());

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

    // Obtain an artifact from the database using the Artifact ID.
    public static Artifact getArtifact(int artifactID) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Artifact WHERE ArtifactId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, artifactID);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Artifact artifact = new Artifact();
            artifact.setArtifactId(results.getInt("ArtifactId"));
            artifact.setTitle(results.getString("Title"));
            artifact.setArtistId(results.getInt("ArtistId"));
            artifact.setDate(results.getDate("Date"));
            artifact.setPlace(results.getString("Place"));
            artifact.setMedium(results.getString("Medium"));
            artifact.setDimensions(results.getString("Dimensions"));
            artifact.setCollectionId(results.getInt("CollectionId"));
            artifact.setDescription(results.getString("Description"));
            artifact.setOwnerId(results.getInt("OwnerId"));

            return artifact;
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

    // Create a new artifact in the database. Fails on duplicates.
    public static Result createArtifact(Artifact artifact) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO Artifact "
                    + "(Title, ArtistId, Date, Place, Medium, Dimensions, CollectionId, Description, OwnerId) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, artifact.getTitle());
            pstmt.setInt(2, artifact.getArtistId());
            pstmt.setDate(3, artifact.getDate());
            pstmt.setString(4, artifact.getPlace());
            pstmt.setString(5, artifact.getMedium());
            pstmt.setString(6, artifact.getDimensions());
            pstmt.setInt(7, artifact.getCollectionId());
            pstmt.setString(8, artifact.getDescription());
            pstmt.setInt(9, artifact.getOwnerId());

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

    // Edit an existing artist in the database.
    public static Result editArtifact(Artifact artifact) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql =  "UPDATE Artifact "
                    + "SET Title = ?, ArtistId = ?, Date = ?, Place = ?, Medium = ?, Dimensions = ?, "
                    + "CollectionId = ?, Description = ?, OwnerId = ? "
                    + "WHERE ArtifactId = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, artifact.getTitle());
            pstmt.setInt(2, artifact.getArtistId());
            pstmt.setDate(3, artifact.getDate());
            pstmt.setString(4, artifact.getPlace());
            pstmt.setString(5, artifact.getMedium());
            pstmt.setString(6, artifact.getDimensions());
            pstmt.setInt(7, artifact.getCollectionId());
            pstmt.setString(8, artifact.getDescription());
            pstmt.setInt(9, artifact.getOwnerId());

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

   // Obtain a Program from the database using Id.
    public static Program getProgram(int programId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Program WHERE ProgramId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, programId);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Program program = new Program();
            program.setProgramId(results.getInt("ProgramId"));
            program.setName(results.getString("Name"));
            program.setSpeaker(results.getString("Speaker"));
            program.setRoomName(results.getString("RoomName"));
            program.setStartDate(results.getDate("StarDate"));
            program.setEndDate(results.getDate("EndDate"));
            program.setMuseumId(results.getInt("MuseumId"));

            return program;
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

    // Create a new Program in the database. Fails on duplicates.
    public static Result createProgram(Program program) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO Program "
                    + "(ProgramId, Name, Speaker, RoomName, StartDate, EndDate, MuseumId) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, program.getProgramId());
            pstmt.setString(2, program.getName());
            pstmt.setString(3, program.getSpeaker());
            pstmt.setString(4, program.getRoomName());
            pstmt.setDate(5, program.getStartDate());
            pstmt.setDate(6, program.getEndDate());
            pstmt.setInt(7, program.getMuseumId());

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

    // Obtain a Collection from the database using Id.
    public static Collection getCollection(int collectionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Collection WHERE CollectionId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, collectionId);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Collection collection = new Collection();
            collection.setCollectionId(results.getInt("collectionId"));
            collection.setTitle(results.getString("title"));
            collection.setDate(results.getDate("date"));
            collection.setDescription(results.getString("description"));
            collection.setLocationId(results.getInt("locationId"));
            collection.setExhibitionId(results.getInt("exhibitionId"));

            return collection;
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

    // Edit an existing program in the database.
    public static Result editProgram(Program program) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql =  "UPDATE Program "
                    + "(SET Name = ?, Speaker = ?, RoomName = ?, StartDate = ?, EndDate = ?) "
                    + "WHERE ProgramId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, program.getName());
            pstmt.setString(2, program.getSpeaker());
            pstmt.setString(3, program.getRoomName());
            pstmt.setDate(4, program.getStartDate());
            pstmt.setDate(5, program.getEndDate());
            pstmt.setInt(6, program.getProgramId());

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

    // Create a new Program in the database. Fails on duplicates.
    public static Result createCollection(Collection collection) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO Employee "
                    + "(collectionId, title, date, description, locationId, exhibitionId) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, collection.getCollectionId());
            pstmt.setString(2, collection.getTitle());
            pstmt.setDate(3, collection.getDate());
            pstmt.setString(4, collection.getDescription());
            pstmt.setInt(5, collection.getLocationId());
            pstmt.setInt(6, collection.getExhibitionId());

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

    public static Result editCollection(Collection collection) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql =  "EDIT Collection "
                    + "(Title = ?, Date = ?, Description = ?, LocationId = ?, ExhibitionId = ?) "
                    + "WHERE CollectionId = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, collection.getTitle());
            pstmt.setDate(2, collection.getDate());
            pstmt.setString(3, collection.getDescription());
            pstmt.setInt(4, collection.getLocationId());
            pstmt.setInt(5, collection.getExhibitionId());

            pstmt.setInt(6, collection.getCollectionId());

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

    // Create a new Artist in the database. Fails on duplicates.
    public static Result createArtist(Artist obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO Artist "
                    + "(ArtistId, FirstName, LastName) "
                    + "VALUES (?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, obj.getArtistId());
            pstmt.setString(2, obj.getFirstName());
            pstmt.setString(3, obj.getLastName());

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

    // Obtain a Artist from the database using Id.
    public static Artist getArtist(int ArtistId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Artist WHERE ArtistId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ArtistId);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Artist obj = new Artist();
            obj.setArtistId(results.getInt("artistId"));
            obj.setFirstName(results.getString("firstName"));
            obj.setLastName(results.getString("lastName"));

            return obj;
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

    // Edit an existing artist in the database.
    public static Result editArtist(Artist obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql =  "UPDATE Artist "
                    + "(SET FirstName = ?, LastName = ?) "
                    + "WHERE ArtistId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getFirstName());
            pstmt.setString(2, obj.getLastName());
            pstmt.setInt(3, obj.getArtistId());

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

    // Create a new ArtifactOwner in the database. Fails on duplicates.
    public static Result createArtifactOwner(ArtifactOwner obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO ArtifactOwner "
                    + "(OwnerId, Name, PhoneNumber) "
                    + "VALUES (?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, obj.getOwnerId());
            pstmt.setString(2, obj.getName());
            pstmt.setString(3, obj.getPhoneNumber());

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

    // Obtain a ArtifactOwner from the database using Id.
    public static ArtifactOwner getArtifactOwner(int ArtifactOwnerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM ArtifactOwner WHERE ArtifactOwnerId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ArtifactOwnerId);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            ArtifactOwner obj = new ArtifactOwner();
            obj.setOwnerId(results.getInt("OwnerId"));
            obj.setName(results.getString("Name"));
            obj.setPhoneNumber(results.getString("PhoneNumber"));

            return obj;
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

    // Edit an existing artifactOwner in the database.
    public static Result editArtifactOwner(ArtifactOwner obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql =  "UPDATE Artist "
                    + "(SET Name = ?, PhoneNumber = ?) "
                    + "WHERE ArtifactOwnerId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getName());
            pstmt.setString(2, obj.getPhoneNumber());
            pstmt.setInt(3, obj.getOwnerId());

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

    // Create a new Museum in the database. Fails on duplicates.
    public static Result createMuseum(Museum obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO Museum "
                    + "(MuseumId, Name, Address, TotalRevenue, OperationalCost) "
                    + "VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, obj.getMuseumId());
            pstmt.setString(2, obj.getName());
            pstmt.setString(3, obj.getAddress());
            pstmt.setInt(4, obj.getTotalRevenue());
            pstmt.setInt(5, obj.getOperationalCost());

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

    // Obtain a Museum from the database using Id.
    public static Museum getMuseum(int MuseumId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Museum WHERE MuseumId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, MuseumId);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Museum obj = new Museum();
            obj.setMuseumId(results.getInt("MuseumId"));
            obj.setName(results.getString("Name"));
            obj.setAddress(results.getString("Address"));
            obj.setTotalRevenue(results.getInt("TotalRevenue"));
            obj.setOperationalCost(results.getInt("OperationalCost"));

            return obj;
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

    // Edit an existing Museum in the database.
    public static Result editMuseum(Museum obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql =  "UPDATE Artist "
                    + "(SET Name = ?, Address = ?, TotalRevenue = ?, OperationalCost = ?) "
                    + "WHERE MuseumId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getName());
            pstmt.setString(2, obj.getAddress());
            pstmt.setInt(3, obj.getTotalRevenue());
            pstmt.setInt(4, obj.getOperationalCost());
            pstmt.setInt(5, obj.getMuseumId());

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

    // Create a new Museum in the database. Fails on duplicates.
    public static Result createExhibition(Exhibition obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql = "INSERT INTO ArtifactOwner "
                    + "(ExhibitionId, Title, StartDate, EndDate, Description, MuseumId) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, obj.getExhibitionId());
            pstmt.setString(2, obj.getTitle());
            pstmt.setDate(3, obj.getStartDate());
            pstmt.setDate(3, obj.getEndDate());
            pstmt.setString(4, obj.getDescription());
            pstmt.setInt(5, obj.getMuseumId());

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

    // Obtain an exhibition from the database using Id.
    public static Exhibition getExhibition(int exhibitionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM Exhibition WHERE ExhibitionId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, exhibitionId);

            // Execute the query
            results = pstmt.executeQuery();

            // If a record exists, then the credentials are correct
            if (!results.next()) {
                return null;
            }

            Exhibition obj = new Exhibition();
            obj.setMuseumId(results.getInt("ExhibitionId"));
            obj.setTitle(results.getString("Title"));
            obj.setStartDate(results.getDate("StartDate"));
            obj.setEndDate(results.getDate("EndDate"));
            obj.setDescription(results.getString("Description"));
            obj.setMuseumId(results.getInt("MuseumId"));

            return obj;
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

    // Edit an existing Exhibition in the database.
    public static Result editExhibition(Exhibition obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to check the credentials
            String sql =  "UPDATE Exhibition "
                    + "(SET Title = ?, StartDate = ?, EndDate = ?, Description = ?, MuseumId = ?) "
                    + "WHERE ExhibitionId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getTitle());
            pstmt.setDate(2, obj.getStartDate());
            pstmt.setDate(3, obj.getEndDate());
            pstmt.setString(4, obj.getDescription());
            pstmt.setInt(5, obj.getMuseumId());
            pstmt.setInt(6, obj.getExhibitionId());

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

    // Create a new transaction in the database. Fails on duplicates.
    public static Result createTransaction(Transaction transaction) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Prepare a SQL query to create transaction.
            String sql = "INSERT INTO Transactions "
                    + "(ItemType, Price, MemberId, PurchaseDate, MuseumId) "
                    + "VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, transaction.getItemType());
            pstmt.setDouble(2, transaction.getPrice());
            pstmt.setInt(3, transaction.getMemberId());
            pstmt.setDate(4, transaction.getPurchaseDate());
            pstmt.setInt(5, transaction.getMuseumId());

            // Execute the query
            pstmt.executeUpdate();
            return Result.SUCCESS;
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
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

    // Batch insert method for multiple transactions
    public static Result createTransactionsBatch(List<Transaction> transactions) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Template for all transactions being inserted.
            String sql = "INSERT INTO Transactions "
                    + "(ItemType, Price, MemberId, PurchaseDate, MuseumId) "
                    + "VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // Disable auto-commit for batch execution.
            conn.setAutoCommit(false);

            for (Transaction transaction : transactions) {
                pstmt.setString(1, transaction.getItemType());
                pstmt.setDouble(2, transaction.getPrice());
                pstmt.setInt(3, transaction.getMemberId());
                pstmt.setDate(4, transaction.getPurchaseDate());
                pstmt.setInt(5, transaction.getMuseumId());

                // Add to batch statement.
                pstmt.addBatch();
            }

            // Execute batch insert.
            pstmt.executeBatch();
            conn.commit();

            return Result.SUCCESS;
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
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

    // Obtains all notifications for a member
    public static List<Notification> getNotifications(int MemberId) {
        List<Notification> notifications = new Vector<Notification>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT NotificationId, NotificationText, NotificationTime, IsCheck FROM Notification WHERE MemberId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, MemberId);

            // Execute the query
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                int id = results.getInt("NotificationId");
                String text = results.getString("NotificationText");
                Timestamp time = results.getTimestamp("NotificationTime");
                boolean isChecked = results.getBoolean("IsCheck");

                notifications.add(new Notification(id, text, isChecked, time));
            }
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

        return notifications;
    }

    // Batch update method for multiple notifications
    public static Result editNotificationsBatch(List<Notification> notifications, int memberId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Template for all notifications being inserted.
            String sql = "UPDATE Notification "
                    + "SET IsCheck = ? "
                    + "WHERE MemberId = ? AND NotificationId = ?";
            pstmt = conn.prepareStatement(sql);

            // Disable auto-commit for batch execution.
            conn.setAutoCommit(false);

            for (Notification notification : notifications) {
                pstmt.setBoolean(1, notification.getChecked());
                pstmt.setInt(2, memberId);
                pstmt.setInt(3, notification.getId());

                // Add to batch statement.
                pstmt.addBatch();
            }

            // Execute batch insert.
            pstmt.executeBatch();
            conn.commit();

            return Result.SUCCESS;
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
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

    // Batch insert method for multiple artifacts
    public static Result createArtifactsBatch(List<Artifact> artifacts) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            // Template for all artifacts being inserted.
            String sql = "INSERT INTO Artifact "
                    + "(Title, ArtistId, Date, Place, Medium, Dimensions, CollectionId, Description, OwnerId) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);

            // Disable auto-commit for batch execution.
            conn.setAutoCommit(false);

            for (Artifact artifact : artifacts) {
                pstmt.setString(1, artifact.getTitle());
                pstmt.setInt(2, artifact.getArtistId());
                pstmt.setDate(3, artifact.getDate());
                pstmt.setString(4, artifact.getPlace());
                pstmt.setString(5, artifact.getMedium());
                pstmt.setString(6, artifact.getDimensions());
                pstmt.setInt(7, artifact.getCollectionId());
                pstmt.setString(8, artifact.getDescription());
                pstmt.setInt(9, artifact.getOwnerId());

                // Add to batch statement.
                pstmt.addBatch();
            }

            // Execute batch insert.
            pstmt.executeBatch();
            conn.commit();

            return Result.SUCCESS;
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
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

    // Getting ArtistArtWork
    public static List<ArtistArtWork> getArtistArtWork() {
        List<ArtistArtWork> work = new ArrayList<>();
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT Artist.FirstName, Artist.LastName, Artifact.Title " +
                                "FROM Artist " +
                                "LEFT JOIN Artifact ON Artist.ArtistId = Artifact.ArtistId")) {

            ResultSet results = pstmt.executeQuery();

            while (results.next()) {
                ArtistArtWork artistArtwork = new ArtistArtWork();
                artistArtwork.setFirstName(results.getString("FirstName"));
                artistArtwork.setLastName(results.getString("LastName"));
                artistArtwork.setArtworkTitle(results.getString("Title"));

                work.add(artistArtwork);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return work;
    }

    // Get Museum Revenue
    public static List<MuseumRevenue> getMuseumRevenue() {
        List<MuseumRevenue> list = new ArrayList<>();

        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT Museum.Name, SUM(Transactions.Price) AS TotalRevenue " +
                                "FROM Museum " +
                                "LEFT JOIN Transactions ON Museum.MuseumId = Transactions.MuseumId " +
                                "GROUP BY Museum.MuseumId, Museum.Name;")) {

            ResultSet results = pstmt.executeQuery();

            while (results.next()) {
                list.add(new MuseumRevenue(results.getString("Name"), results.getDouble("TotalRevenue")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get Exihibition and colelction
    public static List<ExihibitionsAndCollections> getExhibitionAndCollection() {
        List<ExihibitionsAndCollections> list = new ArrayList<>();
        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT Exhibition.Title AS ExhibitionTitle, Collection.Title AS CollectionTitle " +
                                "FROM Exhibition " +
                                "LEFT JOIN Collection ON Exhibition.ExhibitionId = Collection.ExhibitionId;")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String exhibitionTitle = rs.getString("ExhibitionTitle");
                String collectionTitle = rs.getString("CollectionTitle");
                list.add(new ExihibitionsAndCollections(exhibitionTitle, collectionTitle));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Museun Revenue Report
    public static List<MuseumRevenueReport> getMuseumRevenueReport(String query) {
        List<MuseumRevenueReport> list = new ArrayList<>();
        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int museumId = rs.getInt("MuseumId");
                String museumName = rs.getString("Name");
                String address = rs.getString("Address");
                Double currentTotalRevenue = rs.getDouble("CurrentTotalRevenue");
                Double totalRevenue = rs.getDouble("TotalRevenue");

                list.add(new MuseumRevenueReport(museumId, museumName, address, currentTotalRevenue, totalRevenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ArtifactInventory Report
    public static List<ArtifactInventoryReport> getArtifactInventoryReport(String query) {
        List<ArtifactInventoryReport> list = new ArrayList<>();
        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String collectionTitle = rs.getString("CollectionTitle");
                String artifactTitle = rs.getString("ArtifactTitle");
                String collectionDate = rs.getString("CollectionDate");
                String collectionDescription = rs.getString("CollectionDescription");
                String artifactDate = rs.getString("ArtifactDate");
                String artifactPlace = rs.getString("ArtifactPlace");
                String artifactMedium = rs.getString("ArtifactMedium");
                String artifactDimensions = rs.getString("ArtifactDimensions");
                String artistFirstName = rs.getString("ArtistFirstName");
                String artistLastName = rs.getString("ArtistLastName");
                list.add(new ArtifactInventoryReport(
                        artifactTitle, collectionTitle, collectionDate, collectionDescription,
                        artifactDate, artifactPlace, artifactMedium, artifactDimensions,
                        artistFirstName, artistLastName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Exhibition Attendance Report
    public static List<ExhibitionAttendanceReport> getExhibitionAttendanceReport(String query) {
        List<ExhibitionAttendanceReport> list = new ArrayList<>();
        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int exhibitionId = rs.getInt("ExhibitionId");
                String exhibitionTitle = rs.getString("ExhibitionTitle");
                Date startDate = rs.getDate("StartDate");
                Date endDate = rs.getDate("EndDate");
                String description = rs.getString("Description");
                int transactionItemId = rs.getInt("TransactionItemId");
                String itemType = rs.getString("ItemType");
                double price = rs.getDouble("Price");
                Date purchaseDate = rs.getDate("PurchaseDate");

                list.add(new ExhibitionAttendanceReport(
                        exhibitionId, exhibitionTitle, startDate, endDate, description,
                        transactionItemId, itemType, price, purchaseDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
