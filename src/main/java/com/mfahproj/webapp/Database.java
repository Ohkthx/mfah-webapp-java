package com.mfahproj.webapp;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Date;

import com.mfahproj.webapp.models.*;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Database {
    // Result of a database query / insertion / update.
    public static enum Result {
        SUCCESS,
        DUPLICATE,
        FAILURE
    }

    public static Result employeeEditorFailure() {
        return Result.FAILURE;
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

    @WebServlet("/employee/employeeView")
    public class EmployeeSortServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            String sortField = request.getParameter("sortField");
            String sortOrder = request.getParameter("sortOrder"); // 'ASC' or 'DESC'

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "SELECT * FROM employee ORDER BY " + sortField + " " + sortOrder;
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.executeQuery();
                    // Process and send the results to front-end
                }
            } catch (SQLException e) {
                // Handle SQL exceptions
            }
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

    public static boolean deleteEmployee(String tableName, String field, int entityId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();
            for (Employee n : Database.getAllEmployees()) {
                if (n.getSupervisorId() == entityId) {
                    String sql = String.format("UPDATE %s SET %s = 0 WHERE %s = ?", tableName, field, field, entityId);
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, entityId);
                    pstmt.executeUpdate();
                }
            }
            String sql = String.format("DELETE FROM %s WHERE EmployeeId = ?", tableName, entityId);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, entityId);
            pstmt.executeUpdate();

            // Execute the query
            return pstmt.executeUpdate() != 0 ? true : false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    // Removes an entity from the database.
    public static boolean deleteEntity(String tableName, String field, int entityId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, field);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, entityId);

            // Execute the query
            return pstmt.executeUpdate() != 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
                    + "SET FirstName = ?, LastName = ?, Password = ?, EmailAddress = ?, LastLogin = ? , MembershipType = ?, ExpirationDate = ?, BirthDate = ? "
                    + "WHERE MemberId = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getPassword());
            pstmt.setString(4, member.getEmailAddress());
            pstmt.setDate(5, member.getLastLogin());
            pstmt.setString(6, member.getMembershipType());
            pstmt.setDate(7, member.getExpirationDate());
            pstmt.setDate(8, member.getBirthDate());
            pstmt.setInt(9, member.getMemberId());

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

    // Obtains all employees from the database.
    public static List<Employee> getAllEmployees() {
        List<Employee> employees = new Vector<Employee>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Employee ORDER BY EmployeeId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
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

                employees.add(employee);
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

        return employees;
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

    public static int getMaxEmployeeID() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();
            String sql = "SELECT EmployeeId FROM Employee ORDER BY EmployeeId DESC";
            pstmt = conn.prepareStatement(sql);

            // execute SQL query
            results = pstmt.executeQuery();

            if (!results.next()) {
                return -1;
            }

            return results.getInt("EmployeeId");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;

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
                    + "SET FirstName = ?, LastName = ?, JobTitle = ?, EmailAddress = ?, AccessLevel = ?, SupervisorId = ?, Password = ?, PhoneNumber = ?, LastLogin = ? "
                    + "WHERE EmployeeId = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getJobTitle());
            pstmt.setString(4, employee.getEmailAddress());
            pstmt.setString(5, employee.getAccessLevel());
            pstmt.setString(6, Integer.toString(employee.getSupervisorId()));
            pstmt.setString(7, employee.getPassword());
            pstmt.setString(8, employee.getPhoneNumber());
            pstmt.setDate(9, employee.getLastLogin());
            pstmt.setInt(10, employee.getEmployeeId());

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

    public static List<Employee> getAllSupervisors() {
        List<Employee> employees = new Vector<Employee>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Employee WHERE AccessLevel != 'NORMAL'");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                Employee employee = new Employee();
                employee.setEmployeeId(results.getInt("EmployeeId"));
                employee.setFirstName(results.getString("FirstName"));
                employee.setLastName(results.getString("LastName"));
                employee.setJobTitle(results.getString("JobTitle"));
                employee.setPhoneNumber(results.getString("PhoneNumber"));
                employee.setEmailAddress(results.getString("EmailAddress"));
                employee.setPassword(results.getString("Password"));
                employee.setSalary(results.getDouble("Salary"));
                employee.setMuseumId(results.getInt("MuseumId"));
                employee.setSupervisorId(results.getInt("SupervisorId"));
                employee.setAccessLevel(results.getString("AccessLevel"));
                employee.setLastLogin(results.getDate("LastLogin"));

                employees.add(employee);
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

        return employees;
    }

    // Obtains all artifacts from the database.
    public static List<Artifact> getAllArtifacts() {
        List<Artifact> artifacts = new Vector<Artifact>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Artifact ORDER BY ArtifactId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
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

                artifacts.add(artifact);
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

        return artifacts;
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
            String sql = "UPDATE Artifact "
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
            pstmt.setInt(10, artifact.getArtifactId());

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

    // Obtains all programs from the database.
    public static List<Program> getAllPrograms() {
        List<Program> programs = new Vector<Program>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Program ORDER BY ProgramId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                Program program = new Program();
                program.setProgramId(results.getInt("ProgramId"));
                program.setName(results.getString("Name"));
                program.setSpeaker(results.getString("Speaker"));
                program.setRoomName(results.getString("RoomName"));
                program.setStartDate(results.getDate("StartDate"));
                program.setEndDate(results.getDate("EndDate"));
                program.setMuseumId(results.getInt("MuseumId"));

                programs.add(program);
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

        return programs;
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
            program.setStartDate(results.getDate("StartDate"));
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
                    + "(Name, Speaker, RoomName, StartDate, EndDate, MuseumId) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, program.getName());
            pstmt.setString(2, program.getSpeaker());
            pstmt.setString(3, program.getRoomName());
            pstmt.setDate(4, program.getStartDate());
            pstmt.setDate(5, program.getEndDate());
            pstmt.setInt(6, program.getMuseumId());

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

    // Obtains all collections from the database.
    public static List<Collection> getAllCollections() {
        List<Collection> collections = new Vector<Collection>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Collection ORDER BY CollectionId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                Collection collection = new Collection();
                collection.setCollectionId(results.getInt("CollectionId"));
                collection.setTitle(results.getString("Title"));
                collection.setDate(results.getDate("Date"));
                collection.setDescription(results.getString("Description"));
                collection.setLocationId(results.getInt("MuseumId"));
                collection.setExhibitionId(results.getInt("ExhibitionId"));
                collections.add(collection);
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

        return collections;
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
            collection.setCollectionId(results.getInt("CollectionId"));
            collection.setTitle(results.getString("Title"));
            collection.setDate(results.getDate("Date"));
            collection.setDescription(results.getString("Description"));
            collection.setLocationId(results.getInt("MuseumId"));
            collection.setExhibitionId(results.getInt("ExhibitionId"));

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
            String sql = "UPDATE Program "
                    + "SET Name = ?, Speaker = ?, RoomName = ?, StartDate = ?, EndDate = ? "
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
            String sql = "INSERT INTO Collection "
                    + "(Title, Date, Description, MuseumId, ExhibitionId) "
                    + "VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, collection.getTitle());
            pstmt.setDate(2, collection.getDate());
            pstmt.setString(3, collection.getDescription());
            pstmt.setInt(4, collection.getLocationId());
            pstmt.setInt(5, collection.getExhibitionId());

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
            String sql = "UPDATE Collection "
                    + "SET Title = ?, Date = ?, Description = ?, MuseumId = ?, ExhibitionId = ? "
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
                    + "(FirstName, LastName) "
                    + "VALUES (?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getFirstName());
            pstmt.setString(2, obj.getLastName());

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

    // Obtains all artists from the database.
    public static List<Artist> getAllArtists() {
        List<Artist> artists = new Vector<Artist>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Artist ORDER BY ArtistId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                int id = results.getInt("ArtistId");
                String firstName = results.getString("FirstName");
                String lastName = results.getString("LastName");

                artists.add(new Artist(id, firstName, lastName));
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

        return artists;
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
            obj.setArtistId(results.getInt("ArtistId"));
            obj.setFirstName(results.getString("FirstName"));
            obj.setLastName(results.getString("LastName"));

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
            String sql = "UPDATE Artist "
                    + "SET FirstName = ?, LastName = ? "
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
                    + "(Name, PhoneNumber) "
                    + "VALUES (?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getName());
            pstmt.setString(2, obj.getPhoneNumber());

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

    // Obtains all artists from the database.
    public static List<ArtifactOwner> getAllArtifactOwners() {
        List<ArtifactOwner> owners = new Vector<ArtifactOwner>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM ArtifactOwner ORDER BY OwnerId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                ArtifactOwner owner = new ArtifactOwner();
                owner.setOwnerId(results.getInt("OwnerId"));
                owner.setName(results.getString("Name"));
                owner.setPhoneNumber(results.getString("PhoneNumber"));

                owners.add(owner);
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

        return owners;
    }

    // Obtain a ArtifactOwner from the database using Id.
    public static ArtifactOwner getArtifactOwner(int ArtifactOwnerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database
            conn = Database.connect();

            String sql = "SELECT * FROM ArtifactOwner WHERE OwnerId = ?";
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
            String sql = "UPDATE ArtifactOwner "
                    + "SET Name = ?, PhoneNumber = ? "
                    + "WHERE OwnerId = ?";
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
                    + "(Name, Address, TotalRevenue, OperationalCost) "
                    + "VALUES (?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getName());
            pstmt.setString(2, obj.getAddress());
            pstmt.setDouble(3, obj.getTotalRevenue());
            pstmt.setDouble(4, obj.getOperationalCost());

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

    // Obtains all museums from the database.
    public static List<Museum> getAllMuseums() {
        List<Museum> museums = new Vector<Museum>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Museum ORDER BY MuseumId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                Museum museum = new Museum();
                museum.setMuseumId(results.getInt("MuseumId"));
                museum.setName(results.getString("Name"));
                museum.setAddress(results.getString("Address"));
                museum.setTotalRevenue(results.getDouble("TotalRevenue"));
                museum.setOperationalCost(results.getDouble("OperationalCost"));

                museums.add(museum);
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

        return museums;
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
            obj.setTotalRevenue(results.getDouble("TotalRevenue"));
            obj.setOperationalCost(results.getDouble("OperationalCost"));

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
            String sql = "UPDATE Museum "
                    + "SET Name = ?, Address = ?, TotalRevenue = ?, OperationalCost = ? "
                    + "WHERE MuseumId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getName());
            pstmt.setString(2, obj.getAddress());
            pstmt.setDouble(3, obj.getTotalRevenue());
            pstmt.setDouble(4, obj.getOperationalCost());
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
            String sql = "INSERT INTO Exhibition "
                    + "(Title, StartDate, EndDate, Description, MuseumId) "
                    + "VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, obj.getTitle());
            pstmt.setDate(2, obj.getStartDate());
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

    // Obtains all artists from the database.
    public static List<Exhibition> getAllExhibitions() {
        List<Exhibition> exhibitions = new Vector<Exhibition>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet results = null;

        try {
            // Connect to the database.
            conn = Database.connect();

            // Execute the query.
            pstmt = conn.prepareStatement("SELECT * FROM Exhibition ORDER BY ExhibitionId ASC");
            results = pstmt.executeQuery();

            // Create the list of notifications.
            while (results.next()) {
                Exhibition exhibition = new Exhibition();
                exhibition.setExhibitionId(results.getInt("ExhibitionId"));
                exhibition.setTitle(results.getString("Title"));
                exhibition.setStartDate(results.getDate("StartDate"));
                exhibition.setEndDate(results.getDate("EndDate"));
                exhibition.setDescription(results.getString("Description"));
                exhibition.setMuseumId(results.getInt("MuseumId"));

                exhibitions.add(exhibition);
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

        return exhibitions;
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
            obj.setExhibitionId(results.getInt("ExhibitionId"));
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
            String sql = "UPDATE Exhibition "
                    + "SET Title = ?, StartDate = ?, EndDate = ?, Description = ?, MuseumId = ? "
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


    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static List<MemberDemographics> getMemberDemographics() {
        List<MemberDemographics> list = new ArrayList<>();

        try(Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT" +
                " SUM(CASE WHEN age <= 12 THEN 1 ELSE 0 END) AS Children," +
                " SUM(CASE WHEN age BETWEEN 13 AND 19 THEN 1 ELSE 0 END) AS Teens,"+
                " SUM(CASE WHEN age BETWEEN 20 AND 54 THEN 1 ELSE 0 END) AS Adults,"+
                " SUM(CASE WHEN age >= 55 THEN 1 ELSE 0 END) AS Seniors"+
                " FROM ("+
                    " SELECT"+
                        " DATEDIFF(CURDATE(), birth_date) / 365 AS age"+
                    " FROM"+
                        " Members"+
                ") AS age_calculated;")) {

        ResultSet results = pstmt.executeQuery();
        
        while (results.next()) {
            list.add(new MemberDemographics(results.getInt("Children"), results.getInt("Teens"), 
            results.getInt("Adults"), results.getInt("Seniors")));
        }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static List<MemberDemographicsReport> getMemberDemographicsReport(String query) {
        List<MemberDemographicsReport> list = new ArrayList<>();
        try (
            Connection conn = Database.connect();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int children = rs.getInt("children");
                int teens = rs.getInt("teens");
                int adults = rs.getInt("adults");
                int seniors = rs.getInt("seniors");

                list.add(new MemberDemographicsReport(children, teens, adults, seniors));
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
