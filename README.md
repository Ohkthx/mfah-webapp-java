# MFAH Database and Web App project.

Java, HTML, and MySQL group project to demonstrate learning requirements for Database Systems 3380. Requirements for the assignment include using no frameworks. This is entirely proof-of-concept and should not be used in a production server. Passwords are not hashed.

Objective of this task was to create a database system for the Houston Museum of Fine Arts that manages Members, Employees, Artifacts, Artists, Artist Owners, Transactions, Programs, Collections, Exhibitions, and the Museum itself. Employees have access to modify the Museum's entities directly with different employee access levels give differing access. Members can access their own profiles and edit it. Members are used to demonstrate our database triggers in the form of notifications based on their account status at the time of log-in.

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

## Running the application

__**Requirements**__
- Maven installed, builds the application.
- Java installed, runs the HTTP server.

__**Obtaining the repository**__
- Clone the repository and change into its directory.

```
git clone https://github.com/Ohkthx/mfah-webapp-java
cd mfah-webapp-java
```

__**Configuration**__

Environment variables used to start the HTTP server and give access to database located in `app.config`. Copy `app.config-sample` to `app.config` and make your edits. This needs to be updated to fit your environment. Be sure to load the database with the following two sources. Be sure to report any changes regarding the schema.

- [database_template.sql](https://github.com/Ohkthx/mfah-webapp-java/blob/main/database_template.sql), updated schema for the database.
- [database-data.sql](https://github.com/Ohkthx/mfah-webapp-java/blob/main/database-data.sql), basic data to make login portal functional. View in my depth to see login credentials.

__**Compiling and Running**__
- Windows: Execute `runme.bat`
- Linux / MacOS: Execute `runme.sh`

## Types of date that can be added, modified, and edited

|Entity|Create|View|Edit|Delete|Notes|
|-|-|-|-|-|-|
|Member|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Login as the member and modify profile|
|Employee|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Login as the employee and modify profile|
|Employee|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|Login as Manager (director) to manage|
|Aritfact|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|Requires employee (employee)|
|Aritfact Owner|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Requires Employee (employee)|
|Aritist|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Requires Employee (employee)|
|Program|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Requires Employee (employee)|
|Collection|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Requires Employee (employee)|
|Exhibition|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Requires Employee (employee)|
|Museum|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:||Requires Employee (employee)|

## User Roles

The following are credentials for logging in for testing purposes, there are additional users.

|Email|Password|Type|Access/Membership|Notes|
|-|-|-|-|-|
|director@mfah.org|director|EMPLOYEE|MANAGER|Can do everything employee can, but also edit employees.|
|employee@mfah.org|employee|EMPLOYEE|NORMAL|Normal exmployee view, can edit aspects of the museum.|
|member@test.com|member|MEMBER|SEASONAL|Will demonstrate 2 triggers (Notifcations) after logging in.|

## Semantic Constraints / Triggers
- Members who have 1 week, 1 day, or day-of until expiration are flagged and a notification is created.
    - Member receives a notification each time they log-in explaining one of the three expiration periods.
- Members are upgraded to 'SENIOR' membership when their age exceeds 55 years and are not currently a SENIOR. A notification is generated when this occurs.
    - Member receives a notification when they log-in only a single time explaining their membership change.

## Queries / Reports

__**Queries**__
- Member / Employee authentication (username / password checking)
- Member / Employee profile view of personal information
- Member: Checking notifications
- Employee: Viewing entities to edit
- From homepage (logged in)
    - Artist Artwork, Artifacts and their Artists
    - Revenue, museum revenue
    - Exhibition Collections, exhibitions and their collections assigned
    - Demographics, amount of members that fit in the following categories 'child', 'teen', 'adult', 'senior'

__**Reports**__
- Exhibition Schedule Report (Requires Employee)
    - Utilizes Exhibition, Collection, and Transaction tables.
    - Shows the exhibitions and the transactions associated with them.
- Artifact Inventory Report (Requires Employee)
    - Utilizes Artifact and Collection tables.
    - Shows Artifacts and the Collections they currently belong to.
- Museum Revenue Report (Requires Employee)
    - Utilizes Museum and Transaction tables.
    - Shows the Total Revenue the Musueum Generated from transactions.

## Checkpoint Requirements

- [X] User authentication for different user roles.
    - [X] Member login.
    - [X] Employee login.
        - [X] Access levels with different views.
- [X] Access Levels
    - (Employee: Director ("MANAGER" Role)) Modify other employees.
    - Members of the museum cannot access employee items.
- [X] Data entry forms - to add new data, modify existing data, and 'delete' data.
    - [X] Registeration of new entities (Members, Employees, Museum Date: Employee req.)
    - [X] Login portal 
    - [X] Profile edits (Members and Employees while logged in)
    - [X] Updating last login times upon accessing new pages.
    - [X] Reports.
- [X] Triggers - At least 2.
    - [X] (Member) Notifications for membership expiration.
    - [X] (Member) Notifications for senior citizen membership upgrade.
- [X] Data queries - At least 3.
    - [X] Obtaining members / employees credentials for logging in.
    - [X] Obtaining notifications for members.
    - [X] Creating pseudo data for Tickets, Transactions, and Artifacts.
    - [X] Viewing, editing, deleting all entities.
    - [X] From logged in, homepage:
        - [X] Artist Work
        - [X] Revenue
        - [X] Exhibition Collections
        - [X] Demographics
- [X] Data reports - At least 3. Requires employee to be logged in.
    - [X] Generated when requested.
    - [X] Required SQL queries in the background.
        - [X] Time Interval required.
        - [X] Needs to come from more than one table.

## Building

Using Maven. Personally used Maven plugins in GitHub to compile and run the server.