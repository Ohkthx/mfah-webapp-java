#!/usr/bin/env bash

# Compiles the project into a single archie.
mvn clean compile assembly:single 

# Run the application.
java -Djava.net.preferIPv4Stack=true -jar target/webapp-1.0-SNAPSHOT-jar-with-dependencies.jar