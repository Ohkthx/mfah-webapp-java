package com.mfahproj.webapp.models;

public class Exhibition {

    private int exhibitionId = -1;
    private String title;
    private java.sql.Date startDate;
    private java.sql.Date endDate;
    private String description;
    private int museumId;

    // Create a blank exhibition.
    public Exhibition() {
    };

    // Used to create a new exhibition.
    // exhibitionId is created by AUTOINCREMENT.
    public Exhibition(String title, java.sql.Date startDate, java.sql.Date endDate, 
        String description, int museumId) {
        this(-1, title, startDate, endDate, description, museumId);
    }

    public Exhibition(int exhibitionId, String title, java.sql.Date starDate, java.sql.Date endDate, String description, int museumId) {
        this.setExhibitionId(exhibitionId);
        this.setTitle(title);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setDescription(description);
        this.setMuseumId(museumId);
    }

    // ExhibitionId getter.
    public int getExhibitionId() {
        return this.exhibitionId;
    }

    // ExhibitionId setter.
    public void setExhibitionId(int exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    // Title getter.
    public String getTitle() {
        return this.title;
    }

    // Title setter.
    public void setTitle(String title) {
        this.title = title;
    }

    // StartDate getter.
    public java.sql.Date getStartDate() {
        return this.startDate;
    }

    // StartDate setter.
    public void setStartDate(java.sql.Date startDate) {
        this.startDate = startDate;
    }

    // EndDate getter.
    public java.sql.Date getEndDate() {
        return this.startDate;
    }

    // EndDate setter.
    public void setEndDate(java.sql.Date endDate) {
        this.endDate = endDate;
    }

    // Description getter.
    public String getDescription() {
        return this.description;
    }

    // Description setter.
    public void setDescription(String description) {
        this.description = description;
    }

    // MuseumId getter.
    public int getMuseumId() {
        return this.museumId;
    }

    // MuseumId setter.
    public void setMuseumId(int museumId) {
        this.museumId = museumId;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Exhibition{"
                + ", exhibitionId='" + this.exhibitionId
                + ", title='" + this.title + '\''
                + ", startDate='" + this.startDate + '\''
                + ", endDate='" + this.endDate + '\''
                + ", desc=" + this.description + '\''
                + ", museumid=" + this.museumId 
                + '}';
    }
}