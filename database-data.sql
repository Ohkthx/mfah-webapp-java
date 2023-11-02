-- Museums

INSERT INTO Museum (Name, Address) 
VALUES ("Audrey Jones Beck Building", "5601 Main Street, Houston, TX 77005");

INSERT INTO Museum (Name, Address) 
VALUES ("Bayou Bend Collection", "6003 Memorial Drive, Houston, TX 77007");

INSERT INTO Museum (Name, Address) 
VALUES ("Caroline Wiess Law Building", "1001 Bissonnet Street, Houston, TX 77005");

INSERT INTO Museum (Name, Address) 
VALUES ("Nancy and Rich Kinder Building", "5500 Main Street, Houston, TX 77004");

INSERT INTO Museum (Name, Address) 
VALUES ("Rienzi", "1406 Kirby Drive, Houston, TX 77019");

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

-- Artists

INSERT INTO Artist (FirstName, LastName)
VALUES ("First", "Artist");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Second", "Artistt");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Third", "Artisttt");

-- Owners

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Richman", "777-777-7778");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Richerman", "777-777-7779");

-- Exhibitions

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Rembrandt to Van Gogh", CURDATE(), CURDATE() + INTERVAL 1 YEAR, "Outstanding selections from the Armand Hammer Collection include paintings by Rembrandt and Titian, as well as significant works by artists such as Cezanne, Degas, Gauguin, Van Gogh, Manet, and Monet.", 1);

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Robert Frank and Todd Webb: Across America, 1955", CURDATE() - INTERVAL 1 MONTH, CURDATE() + INTERVAL 2 YEAR, "For the first time, the 1955 U.S. survey projects of photographers Robert Frank and Todd Webb come together, capturing a singular vision of \“America.\”", 2);


-- Collections

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("Antiquities", CURDATE(), "Art of the ancient world provides an introduction to the styles and subjects found in the art of the ancient Mediterranean and Middle East. The antiquities collection began with donations from Houston native Annette Finnigan in 1931.", 1, 1);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("American Painting & Sculpture", CURDATE(), "The collection of American art comprises important paintings and sculpture from the 18th century to the early 20th century.", 2, 2);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("Arts of Asia", CURDATE(), "The Museum's collections of Asian art span nearly five millennia and encompass the cultures of China, the Himalayas, India, Japan, Korea, and Southeast Asia.", 1, 1);
