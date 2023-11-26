@echo off

REM Compiles the project into a single archive.
mvn clean compile assembly:single 

REM Run the application.
java -Djava.net.preferIPv4Stack=true -jar target\webapp-1.0-SNAPSHOT-jar-with-dependencies.jar

pause