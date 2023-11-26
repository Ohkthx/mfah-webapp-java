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

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin) 
VALUES ("Regular", "Test-Member", "REGULAR", CURDATE() + INTERVAL 5 DAY, CURDATE() - INTERVAL 20 YEAR, "regular@test.com", "regular", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin) 
VALUES ("Member", "Test-Member", "SEASONAL", CURDATE() + INTERVAL 5 DAY, CURDATE() - INTERVAL 58 YEAR, "member@test.com", "member", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Spongebob", "Squarepants", "SEASONAL", CURDATE() + INTERVAL 1 MONTH, CURDATE() - INTERVAL 30 YEAR, "spongebob@krustykrab.com", "ilovesquidward", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Squidward", "Tentacles", "SEASONAL", CURDATE() + INTERVAL 1 DAY, CURDATE() - INTERVAL 35 YEAR, "squidward@krustykrab.com", "ihatespongebob", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Harry", "Potter", "REGULAR", CURDATE() + INTERVAL 7 DAY, CURDATE() - INTERVAL 18 YEAR, "harry@hogwarts.com", "boywholived", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Jose", "Altuve", "REGULAR", CURDATE() + INTERVAL 10 DAY, CURDATE() - INTERVAL 33 YEAR, "jose@astros.com", "worldchamp", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Simba", "daLion", "REGULAR", CURDATE() - INTERVAL 5 MONTH, CURDATE() - INTERVAL 15 YEAR, "simba@lionking.com", "lionking", CURRENT_TIMESTAMP);
  
INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Graham", "Fitzgerald", "SEASONAL", CURDATE() + INTERVAL 6 MONTH, CURDATE() - INTERVAL 40 YEAR, "graham@test.com", "graham", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Penelope", "Crawford", "REGULAR", CURDATE() + INTERVAL 1 YEAR, CURDATE() - INTERVAL 30 YEAR, "penelope@test.com", "penelope", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Dexter", "Wong", "NONE", CURDATE(), CURDATE() - INTERVAL 25 YEAR, "dexter@test.com", "dexter", CURRENT_TIMESTAMP);

INSERT INTO Members (FirstName, LastName, MembershipType, ExpirationDate, BirthDate, EmailAddress, Password, LastLogin)
VALUES ("Victoria", "Preston", "REGULAR", CURDATE() + INTERVAL 1 YEAR, CURDATE() - INTERVAL 28 YEAR, "victoria@test.com", "victoria", CURRENT_TIMESTAMP);

-- Employees

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("ADMIN", "ADMIN", "ADMIN", "000-000-0000", "admin@mfah.org", "password", 0, 1, NULL, "ADMIN", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Gary", "Tinterow", "Director", "713-777-7777", "director@mfah.org", "director", 250000, 1, NULL, "MANAGER", CURRENT_TIMESTAMP());

UPDATE Employee SET SupervisorId = 2 WHERE EmployeeId = 2;

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Iam", "Human", "Supervisor", "713-777-7778", "supervisor@mfah.org", "password1234", 100000, 2, 2, "SUPERVISOR", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("A'nother", "Pearson", "Clerk", "713-777-7779", "normal@mfah.org", "password1234", 50000, 3, 3, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Sum", "Employee", "Janitor", "713-777-7780", "employee@mfah.org", "employee", 50000, 4, 3, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Eleanor", "Gallagher", "Curator", "713-777-7781", "gallagher@mfah.org", "EG!MFAH", 60000, 1, 3, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Lucas", "Hawkins", "Archivist", "713-777-7782", "hawkins@mfah.org", "LH!MFAH", 60000, 5, 3, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Cassandra", "Wong", "Conservator", "713-777-7783", "wong@mfah.org", "CW!MFAH", 60000, 4, 3, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Dexter", "Mendez", "Registrar", "713-777-7784", "mendez@mfah.org", "DM!MFAH", 60000, 3, 2, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Isabella", "Fleming", "Exhibition Designer", "713-777-7785", "fleming@mfah.org", "IF!MFAH", 70000, 2, 2, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Oscar", "Rodriguez", "Museum Educator", "713-777-7786", "rodriguez@mfah.org", "OR!MFAH", 70000, 5, 3, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Gavin", "Barnes", "Archaeologist", "713-777-7787", "barnes@mfah.org", "GB!MFAH", 80000, 1, 2, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Penelope", "Chen", "Exhibition Coordinator", "713-777-7788", "chen@mfah.org", "PC!MFAH", 70000, 2, 2, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Fiona", "Evans", "Visitor Services Coordinator", "713-777-7789", "evans@mfah.org", "FE!MFAH", 60000, 1, 2, "NORMAL", CURRENT_TIMESTAMP());

INSERT INTO Employee (FirstName, LastName, JobTitle, PhoneNumber, EmailAddress, Password, Salary, MuseumId, SupervisorId, AccessLevel, LastLogin)
VALUES ("Howard", "Fisher", "Gallery Attendant", "713-777-7790", "fisher@mfah.org", "HF!MFAH", 60000, 2, 3, "NORMAL", CURRENT_TIMESTAMP());

-- Artists

INSERT INTO Artist (FirstName, LastName)
VALUES ("Leonardo", "da Vinci");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Vincent", "van Gogh");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Pablo", "Picasso");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Georgia", "O'Keeffe");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Claude", "Monet");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Frida", "Kahlo");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Eva", "Harrison");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Cameron", "Fletcher");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Sylvia", "Woods");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Miles", "Mitchell");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Nina", "Reyes");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Vincent", "Wang");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Olivia", "Hudson");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Felix", "Bryant");

INSERT INTO Artist (FirstName, LastName)
VALUES ("Lila", "Harper");

-- Owners

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Richman", "777-777-7778");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Richerman", "777-777-7779");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Liam Patel", "777-777-7780");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Ava Nguyen", "777-777-7781");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Elijah Rodriguez", "777-777-7782");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Noah Johnson", "777-777-7783");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Emma Davis", "777-777-7784");

INSERT INTO ArtifactOwner (Name, PhoneNumber)
VALUES ("Harrison Sterling", "777-777-7785");

-- Exhibitions

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Rembrandt to Van Gogh", CURDATE(), CURDATE() + INTERVAL 1 YEAR, "Outstanding selections from the Armand Hammer Collection include paintings by Rembrandt and Titian, as well as significant works by artists such as Cezanne, Degas, Gauguin, Van Gogh, Manet, and Monet.", 1);

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Robert Frank and Todd Webb: Across America, 1955", CURDATE() - INTERVAL 1 MONTH, CURDATE() + INTERVAL 2 YEAR, "For the first time, the 1955 U.S. survey projects of photographers Robert Frank and Todd Webb come together, capturing a singular vision of \“America.\”", 2);

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Vertigo of Color: Matisse, Derain, and the Origins of Fauvism", CURDATE() + INTERVAL 2 MONTH, CURDATE() + INTERVAL 5 MONTH, "In the summer of 1905, Henri Matisse and André Derain embarked on a creative partnership that would change the course of French painting.", 2);

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Kehinde Wiley: An Archaeology of Silence", CURDATE() + INTERVAL 5 DAY, CURDATE() + INTERVAL 7 MONTH, "Kehinde Wiley: An Archaeology of Silence showcases Kehinde Wiley's new, monumental body of work created against the backdrop of the COVID-19 pandemic, the murder of George Floyd, and the global rise of the Black Lives Matter movement.", 3);

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Eye on Houston: High School Documentary Photography", CURDATE() - INTERVAL 8 MONTH, CURDATE() - INTERVAL 9 DAY, "Eye on Houston: High School Documentary Photography celebrates Houston's diverse neighborhoods through an ongoing collaboration between the Houston Independent School District and the Museum of Fine Arts, Houston.", 4);

INSERT INTO Exhibition (Title, StartDate, EndDate, Description, MuseumId)
VALUES ("Magical and Mystical Oaxaca: Celebrating Oaxacan Art and Culture", CURDATE() - INTERVAL 3 MONTH, CURDATE() - INTERVAL 1 MONTH, "The exhibition Magical and Mystical Oaxaca: Celebrating Oaxacan Art and Culture showcases the vibrant art and culture of Oaxaca, Mexico.", 5);

-- Collections

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("Antiquities", CURDATE(), "Art of the ancient world provides an introduction to the styles and subjects found in the art of the ancient Mediterranean and Middle East. The antiquities collection began with donations from Houston native Annette Finnigan in 1931.", 1, 1);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("American Painting & Sculpture", CURDATE(), "The collection of American art comprises important paintings and sculpture from the 18th century to the early 20th century.", 2, 2);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("Arts of Asia", CURDATE(), "The Museum's collections of Asian art span nearly five millennia and encompass the cultures of China, the Himalayas, India, Japan, Korea, and Southeast Asia.", 1, 1);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("European Art", CURDATE(), "The collection of European art comprises important paintings and sculpture from the 13th to early 20th century.", 3, 3);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("Photography", CURDATE(), "The Museum's photography collection comprises more than 35,000 items spanning the full history of the medium, from invention to present day.", 5, 5);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("American Avant Garde", CURDATE(), "A collection of innovative techniques utlized by American artists from the early 21st century.", 4, 4);

INSERT INTO Collection (Title, Date, Description, MuseumId, ExhibitionId)
VALUES ("Finger Painting", CURDATE(), "A collection of figerprinting murals and art pieces from vistors of the museum.", 5, 6);


-- Programs

INSERT INTO Program (Name, StartDate, EndDate, Speaker, RoomName, MuseumId)
VALUES ("Learn how to look like you know about art like a critic.", CURDATE(), CURDATE(), "Bob Ross", "Lobby", 1);