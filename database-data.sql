-- Museums

INSERT INTO Museum (MuseumId, Name, Address) 
VALUES (1, "Audrey Jones Beck Building", "5601 Main Street, Houston, TX 77005");

INSERT INTO Museum (MuseumId, Name, Address) 
VALUES (2, "Bayou Bend Collection", "6003 Memorial Drive, Houston, TX 77007");

INSERT INTO Museum (MuseumId, Name, Address) 
VALUES (3, "Caroline Wiess Law Building", "1001 Bissonnet Street, Houston, TX 77005");

INSERT INTO Museum (MuseumId, Name, Address) 
VALUES (4, "Nancy and Rich Kinder Building", "5500 Main Street, Houston, TX 77004");

INSERT INTO Museum (MuseumId, Name, Address) 
VALUES (5, "Rienzi", "1406 Kirby Drive, Houston, TX 77019");

-- Members

INSERT INTO Members (FirstName, LastName, MembershipType, BirthDate, EmailAddress, Password, LastLogin) 
VALUES ("Regular", "Test-Member", "REGULAR", CURDATE() - INTERVAL 20 YEAR, "regular@test.com", "regular", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, BirthDate, EmailAddress, Password, LastLogin) 
VALUES ("Member", "Test-Member", "SEASONAL", CURDATE() - INTERVAL 25 YEAR, "member@test.com", "member", CURRENT_TIMESTAMP);

-- Employees

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Gary", "Tinterow", "Director", "713-777-7777", "director@mfah.org", "PartyChick5!", 250000, 1, NULL, "MANAGER", CURRENT_TIMESTAMP());

UPDATE Employee SET SupervisorId = 1 WHERE EmployeeId = 1;

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Iam", "Human", "Supervisor", "713-777-7778", "supervisor@mfah.org", "password1234", 100000, 2, 1, "SUPERVISOR", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("A'nother", "Pearson", "Clerk", "713-777-7779", "normal@mfah.org", "password1234", 50000, 3, 2, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Sum", "Employee", "Janitor", "713-777-7780", "employee@mfah.org", "employee", 50000, 4, 2, "NORMAL", CURRENT_TIMESTAMP());