# MFAH Database and Web App project.

Java, HTML, and MySQL group project to demonstrate learning requirements for Database Systems 3380. Requirements for the assignment include using no frameworks. This is entirely proof-of-concept and should not be used in a production server. Passwords are not hashed.

Current features:
- Login and Registration Portals
- Members / Employees can edit select profile details.
- Home screens for Members and Employees.
- Access controls / unique views depending on Member or Employee.
- Session timeouts.
- Generating Pseudo Data for Database population: Tickets, Transactions, and Artifacts.
- Notifications enabled for Members.
- Artifact creation, needs to be added to employee role.
- Generate reports from employee homepage using directors account.

## Testing Logins

The following are credentials for logging in for testing purposes.

|Email|Password|Type|Access/Membership|
|-|-|-|-|
|director@mfah.org|director|EMPLOYEE|MANAGER|
|supervisor@mfah.org|password1234|EMPLOYEE|SUPERVISOR|
|normal@mfah.org|password1234|EMPLOYEE|NORMAL|
|employee@mfah.org|employee|EMPLOYEE|NORMAL|
|regular@test.com|regular|MEMBER|REGULAR|
|member@test.com|member|MEMBER|SEASONAL|

## Checkpoint Requirements

- [X] User authentication for different user roles.
    - [X] Member login.
    - [X] Employee login.
        - [X] Access levels with different views.
- [X] Access Levels
    - Director can only modify other employees.
    - Members of the museum cannot access employee items.
- [X] Data entry forms - to add new data, modify existing data, and 'delete' data.
    - [X] Register
        - [X] Members
        - [X] Employees
    - [X] Login portal 
        - [X] Members
        - [X] Employees
    - [X] Profile edits
        - [X] Members
        - [X] Employees 
    - [X] Updating last login times.
        - [X] Members
        - [X] Employees 
    - [X] Reports.
- [X] Triggers - At least 2.
    - [X] Notifications for membership expiration.
    - [X] Notifications for senior citizen membership upgrade.
- [X] Data queries - At least 3.
    - [X] Obtaining members / employees credentials for logging in.
    - [X] Obtaining notifications for members.
    - [X] Creating pseudo data for Tickets, Transactions, and Artifacts.
    - [X] Viewing, editing, deleting all entities.
- [X] Data reports - At least 3.
    - [X] Generated when requested.
    - [X] Required SQL queries in the background.
        - [X] Time Interval required.
        - [X] Needs to come from more than one table.

## Configuration

Environment variables used to start the HTTP server and give access to database located in `app.config`. Copy `app.config-sample` to `app.config` and make your edits. This needs to be updated to fit your environment. Be sure to load the database with the following two sources. Be sure to report any changes regarding the schema.

- [database_template.sql](https://github.com/Ohkthx/mfah-webapp-java/blob/main/database_template.sql), updated schema for the database.
- [database-data.sql](https://github.com/Ohkthx/mfah-webapp-java/blob/main/database-data.sql), basic data to make login portal functional. View in my depth to see login credentials.

## Building

Using Maven. Personally used Maven plugins in GitHub to compile and run the server.