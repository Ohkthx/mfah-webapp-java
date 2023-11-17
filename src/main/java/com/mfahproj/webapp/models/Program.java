package com.mfahproj.webapp.models;

public class Program {

    private String name = "";
    private int programId = -1;
    private String speaker = "";
    private String roomName = "";
    private java.sql.Date startDate = new java.sql.Date(System.currentTimeMillis());
    private java.sql.Date endDate = new java.sql.Date(System.currentTimeMillis());
    private int museumId = 1;

    // Create a blank program.
    public Program() {
    };

    // Used to create a new program.
    // programId is created by AUTOINCREMENT.
    public Program(String name, String speaker, String roomName, java.sql.Date startDate,
            java.sql.Date endDate, int museumId) {
        this(name, -1, speaker, roomName, startDate, endDate, museumId);
    }

    public Program(String name, int programId, String speaker, String roomName, java.sql.Date startDate,
            java.sql.Date endDate, int museumId) {
        this.setName(name);
        this.setProgramId(programId);
        this.setSpeaker(speaker);
        this.setRoomName(roomName);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setMuseumId(museumId);
    }

    // Name getter.
    public String getName() {
        return this.name;
    }

    // Name setter.
    public void setName(String name) {
        this.name = name;
    }

    // Program Id getter.
    public int getProgramId() {
        return this.programId;
    }

    // Program Id setter.
    public void setProgramId(int programId) {
        this.programId = programId;
    }

    // Speaker getter.
    public String getSpeaker() {
        return this.speaker;
    }

    // Speaker setter.
    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    // RoomName getter.
    public String getRoomName() {
        return this.roomName;
    }

    // RoomName setter.
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    // Start Date getter.
    public java.sql.Date getStartDate() {
        return this.startDate;
    }

    // Start Date setter.
    public void setStartDate(java.sql.Date startDate) {
        this.startDate = startDate;
    }

    // End Date getter.
    public java.sql.Date getEndDate() {
        return this.endDate;
    }

    // End Date setter.
    public void setEndDate(java.sql.Date endDate) {
        this.endDate = endDate;
    }

    // Museum Id getter.
    public int getMuseumId() {
        return this.museumId;
    }

    // Museum Id setter.
    public void setMuseumId(int museumId) {
        this.museumId = museumId;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Program{"
                + "name=" + this.name
                + ", programId='" + this.programId + '\''
                + ", speaker='" + this.speaker + '\''
                + ", roomName='" + this.roomName + '\''
                + ", startDate=" + this.startDate + '\''
                + ", endDate=" + this.endDate + '\''
                + '}';
    }
}