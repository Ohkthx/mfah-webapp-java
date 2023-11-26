-- Destroy and recreate database from scratch

DROP DATABASE IF EXISTS museum;
CREATE DATABASE museum;

use museum;


-- TABLES

CREATE TABLE Artist (
	ArtistId INT UNSIGNED AUTO_INCREMENT,
	FirstName VARCHAR(32) NOT NULL,
	LastName VARCHAR(32) NOT NULL,

	PRIMARY KEY (ArtistId)
);

CREATE TABLE Museum (
	MuseumId INT UNSIGNED AUTO_INCREMENT,
	Name VARCHAR(32) NOT NULL,
	Address VARCHAR(64) NOT NULL,
	TotalRevenue INT UNSIGNED DEFAULT 0,
	OperationalCost INT UNSIGNED DEFAULT 0,
	
	PRIMARY KEY (MuseumId)
);

CREATE TABLE ArtifactOwner (
	OwnerId INT UNSIGNED AUTO_INCREMENT,
	Name VARCHAR(64) NOT NULL,
	PhoneNumber VARCHAR(16) NOT NULL,

	PRIMARY KEY (OwnerId)
);

CREATE TABLE Exhibition (
	ExhibitionId INT UNSIGNED AUTO_INCREMENT,
	Title VARCHAR(64) NOT NULL,
	StartDate DATE,
	EndDate DATE,
	Description VARCHAR(256) NOT NULL,
	MuseumId INT UNSIGNED,
	
	PRIMARY KEY (ExhibitionId),
	FOREIGN KEY (MuseumId) REFERENCES Museum(MuseumId)
);

CREATE TABLE Collection (
	CollectionId INT UNSIGNED AUTO_INCREMENT,
	Title VARCHAR(64) NOT NULL,
	Date DATE,
	Description VARCHAR(256) NOT NULL,
	MuseumId INT UNSIGNED,
	ExhibitionId INT UNSIGNED,
	
	PRIMARY KEY (CollectionId),
	FOREIGN KEY (MuseumId) REFERENCES Museum(MuseumId),
	FOREIGN KEY (ExhibitionId) REFERENCES Exhibition(ExhibitionId)
);

CREATE TABLE Artifact (
	ArtifactId INT UNSIGNED AUTO_INCREMENT,
	Title VARCHAR(64) NOT NULL,
	ArtistId INT UNSIGNED,
	Date DATE,
	Place VARCHAR(32),
	Medium VARCHAR(32),
	Dimensions VARCHAR(32),
	CollectionId INT UNSIGNED,
	Description VARCHAR(256),
	OwnerId INT UNSIGNED,
	
	PRIMARY KEY (ArtifactId),
	FOREIGN KEY (CollectionId) REFERENCES Collection(CollectionId),
	FOREIGN KEY (ArtistId) REFERENCES Artist(ArtistId),
	FOREIGN KEY (OwnerId) REFERENCES ArtifactOwner(OwnerId)
);

CREATE TABLE Members (
	MemberId INT UNSIGNED AUTO_INCREMENT,
	FirstName VARCHAR(32) NOT NULL,
	LastName VARCHAR(32) NOT NULL,
	MembershipType VARCHAR(128) NOT NULL,
	ExpirationDate DATE NOT NULL,
	BirthDate DATE,
	EmailAddress VARCHAR(128) NOT NULL,
	Password VARCHAR(128) NOT NULL,
	LastLogin DATE,

	PRIMARY KEY (MemberId),
	UNIQUE(EmailAddress)
);

CREATE TABLE Transactions (
	ItemId INT UNSIGNED AUTO_INCREMENT,
	ItemType VARCHAR(32) NOT NULL,
	Price DOUBLE(8,2),
	MemberId INT UNSIGNED,
	PurchaseDate DATE,
	MuseumId INT UNSIGNED,

	PRIMARY KEY (ItemId),
	FOREIGN KEY (MemberId) REFERENCES Members(MemberId),
	FOREIGN KEY (MuseumId) REFERENCES Museum(MuseumId)
);

CREATE TABLE Employee (
	EmployeeId INT UNSIGNED AUTO_INCREMENT,
	FirstName VARCHAR(32) NOT NULL,
	LastName VARCHAR(32) NOT NULL,
	JobTitle VARCHAR(32) NOT NULL,
	PhoneNumber VARCHAR(16),
	EmailAddress VARCHAR(128) NOT NULL,
	Password VARCHAR(128) NOT NULL,
	Salary DOUBLE(9,3),
	MuseumId INT UNSIGNED,
	SupervisorId INT UNSIGNED,
	AccessLevel VARCHAR(16),
	LastLogin DATE,
	
	PRIMARY KEY (EmployeeId),
	FOREIGN KEY (MuseumId) REFERENCES Museum(MuseumId),
	FOREIGN KEY (SupervisorId) REFERENCES Employee(EmployeeId),
	UNIQUE(EmailAddress)
);


CREATE TABLE Program (
	ProgramId INT UNSIGNED AUTO_INCREMENT,
	Name VARCHAR(64) NOT NULL,
	Speaker VARCHAR(64) NOT NULL,
	RoomName VARCHAR(64) NOT NULL,
	StartDate DATE,
	EndDate DATE,
	MuseumId INT UNSIGNED,

	PRIMARY KEY (ProgramId),
	FOREIGN KEY (MuseumId) REFERENCES Museum(MuseumId)
);

CREATE TABLE Notification (
    NotificationId INT UNSIGNED AUTO_INCREMENT,
    MemberId INT UNSIGNED,
    NotificationText TEXT,
    NotificationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IsCheck BOOLEAN DEFAULT 0,

    PRIMARY KEY (NotificationId),
    FOREIGN KEY (MemberId) REFERENCES Members(MemberId)
);


-- TRIGGERS


DELIMITER $$
CREATE TRIGGER senior_member_update 
BEFORE UPDATE ON Members
FOR EACH ROW
BEGIN
    DECLARE age INT;
    SET age = TIMESTAMPDIFF(YEAR, NEW.BirthDate, CURDATE());
    
    -- Update if age is senior citizen age.
    IF age >= 55 AND NEW.MembershipType != 'SENIOR' THEN
            SET NEW.MembershipType = 'SENIOR';

            -- Insert into Notifications table
            INSERT INTO Notification (MemberId, NotificationText, NotificationTime)
            VALUES (NEW.MemberId, 'Membership update! You are now considered a senior member.', NOW());
    END IF;
END$$
DELIMITER ;


DELIMITER $$
CREATE TRIGGER membership_expiration_notification
BEFORE UPDATE ON Members
FOR EACH ROW
BEGIN
    DECLARE member_id INT;
    DECLARE expiration_date DATE;
    DECLARE today_date DATE;
	SET today_date = CURDATE();

    SET member_id = NEW.MemberId;
    SELECT ExpirationDate INTO expiration_date FROM Members WHERE MemberId = member_id;


    IF DATEDIFF(expiration_date, today_date) = 1 THEN
        INSERT INTO Notification (MemberId, NotificationText, NotificationTime)
        VALUES (member_id, 'One day remaining of membership until expiration.', NOW());
    ELSEIF expiration_date = today_date THEN
        INSERT INTO Notification (MemberId, NotificationText, NotificationTime)
        VALUES (member_id, 'Last day of membership until expiration.', NOW());
    ELSEIF DATEDIFF(expiration_date, today_date) <= 7 THEN
        INSERT INTO Notification (MemberId, NotificationText, NotificationTime)
        VALUES (member_id, 'One week of membership until expiration.', NOW());
    END IF;
END$$
DELIMITER ;


-- Print results of creations

SHOW TABLES;
SELECT trigger_name FROM information_schema.triggers;