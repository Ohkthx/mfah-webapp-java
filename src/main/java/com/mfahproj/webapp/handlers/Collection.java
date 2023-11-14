package com.mfahproj.webapp.models;

public class Collection {

    private int collectionId = -1;
    private String title;
    private java.sql.Date date;
    private String description;
    private int locationId;
    private int exhibitionId;

    // Create a blank collection.
    public Collection() {
    };

    // Used to create a new collection.
    // collectionId is created by AUTOINCREMENT.
    public Collection(String title, java.sql.Date date, String description, 
        int locationId, int exhibitionId) {
        this(-1, title, date, description, locationId, exhibitionId);
    }

    public Collection(int collectionId, String title, java.sql.Date date, String description, 
        int locationId, int exhibitionId) {
        this.setCollectionId(collectionId);
        this.setTitle(title);
        this.setDate(date);
        this.setDescription(description);
        this.setLocationId(locationId);
        this.setExhibitionId(exhibitionId);
    }

    // CollectionId getter.
    public int getCollectionId() {
        return this.collectionId;
    }

    // CollectionId setter.
    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    // Title getter.
    public String getTitle() {
        return this.title;
    }

    // Title setter.
    public void setTitle(String title) {
        this.title = title;
    }

    // Date getter.
    public java.sql.Date getDate() {
        return this.date;
    }

    // Date setter.
    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    // Description getter.
    public String getDescription() {
        return this.description;
    }

    // Description setter.
    public void setDescription(String description) {
        this.description = description;
    }

    // LocationId getter.
    public int getLocationId() {
        return this.locationId;
    }

    // LocationId setter.
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    // ExhibitionId getter.
    public int getExhibitionId() {
        return this.exhibitionId;
    }

    // ExhibitionId setter.
    public void setExhibitionId(int exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Collection{"
                + ", collectionId='" + this.collectionId
                + ", title='" + this.title + '\''
                + ", date='" + this.date + '\''
                + ", desc=" + this.description + '\''
                + ", locationid=" + this.locationId + '\''
                + ", exhibitionid=" + this.exhibitionId + '\''
                + '}';
    }
}