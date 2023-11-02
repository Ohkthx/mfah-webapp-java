package com.mfahproj.webapp.models;

public class Artifact {

    private int ArtifactId = -1;
    private String Title;
    private int ArtistId;
    private java.sql.Date Date;
    private String Place;
    private String Medium;
    private String Dimensions;
    private int CollectionId;
    private String Description;
    private int OwnerId;

    // Create a blank Artifact.
    public Artifact() {
    };

    // Used to create a new Artifact.
    // ArtifactId is created by AUTOINCREMENT.
    public Artifact(String Title, int ArtistId, java.sql.Date Date, String Place, String Medium,
            String Dimensions, int CollectionId, String Description, int OwnerId) {
        this(-1, Title, ArtistId, Date, Place, Medium, Dimensions, CollectionId, Description, OwnerId);
    }

    public Artifact(int ArtifactId, String Title, int ArtistId, java.sql.Date Date, String Place, String Medium,
    String Dimensions, int CollectionId, String Description, int OwnerId) {
        this.setArtifactId(ArtifactId);
        this.setTitle(Title);
        this.setArtistId(ArtistId);
        this.setDate(Date);
        this.setPlace(Place);
        this.setMedium(Medium);
        this.setDimensions(Dimensions);
        this.setCollectionId(CollectionId);
        this.setDescription(Description);
        this.setOwnerId(OwnerId);
    }

    // Artifact ID getter.
    public int getArtifactId() {
        return this.ArtifactId;
    }

    // Artifact ID setter.
    public void setArtifactId(int ArtifactId) {
        this.ArtifactId = ArtifactId;
    }

    // Title getter.
    public String getTitle() {
        return this.Title;
    }

    // Title setter.
    public void setTitle(String Title) {
        this.Title = Title;
    }

    // Artist ID getter.
    public int getArtistId() {
        return this.ArtistId;
    }

    // Artist ID setter.
    public void setArtistId(int ArtistId) {
        this.ArtistId = ArtistId;
    }

    // Date getter.
    public java.sql.Date getDate() {
        return this.Date;
    }

    // Date setter.
    public void setDate(java.sql.Date Date) {
        this.Date = Date;
    }

    // Place getter.
    public String getPlace() {
        return this.Place;
    }

    // Place setter.
    public void setPlace(String Place) {
        this.Place = Place;
    }

    // Medium getter.
    public String getMedium() {
        return this.Medium;
    }

    // Medium setter.
    public void setMedium(String Medium) {
        this.Medium = Medium;
    }

    // Dimensions getter.
    public String getDimensions() {
        return this.Dimensions;
    }

    // Dimensions setter.
    public void setDimensions(String Dimensions) {
        this.Dimensions = Dimensions;
    }

    // Description getter.
    public String getDescription() {
        return this.Description;
    }

    // Description setter.
    public void setDescription(String Description) {
        this.Description = Description;
    }

    // Collection Id getter.
    public int getCollectionId() {
        return this.CollectionId;
    }

    // Collection Id setter.
    public void setCollectionId(int CollectionId) {
        this.CollectionId = CollectionId;
    }

    // Owner ID getter.
    public int getOwnerId() {
        return this.OwnerId;
    }

    // Owner ID setter.
    public void setOwnerId(int OwnerId) {
        this.OwnerId = OwnerId;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Artifact{"
                + "ArtifactId=" + this.ArtifactId
                + ", Title='" + this.Title + '\''
                + ", Date='" + this.Date + '\''
                + ", Place='" + this.Place + '\''
                + ", Medium=" + this.Medium + '\''
                + ", Dimensions='" + this.Dimensions + '\''
                + ", CollectionId='" + this.CollectionId + '\''
                + ", Description=" + this.Description + '\''
                + ", OwnerId=" + this.OwnerId
                + '}';
    }
}
