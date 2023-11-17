package com.mfahproj.webapp.models;

import java.util.concurrent.ThreadLocalRandom;

public class Artifact {

    private int ArtifactId = -1;
    private String Title = "";
    private int ArtistId = 1;
    private java.sql.Date Date = new java.sql.Date(System.currentTimeMillis());
    private String Place = "";
    private String Medium = "";
    private String Dimensions = "";
    private int CollectionId = 1;
    private String Description = "";
    private int OwnerId = 1;

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

    // Creates a random instance of a Artifact.
    public static Artifact generateRandom(int artistId, int collectionId, int ownerId) {
        Artifact artifact = new Artifact();

        String[] words = { "Potato", "Carrot", "Soup", "Eggroll", "Coffee Boba", "Pho", "Bread", "Blue", "Red", "Black",
                "Green", "Panda", "Dragon", "Flower", "River", "Treat", "Tree", "Dog", "Mushroom", "Star" };
        String[] mediums = { "Pencil", "Pen", "Chalk", "Clay", "Charcoal" };
        String[] dimensions = { "1x1x1", "1x2x1", "1x3x1" };

        // Create a random title.
        int titleWords = ThreadLocalRandom.current().nextInt(1, 6);
        String title = "";
        for (int i = 0; i < titleWords; i++) {
            String word = words[ThreadLocalRandom.current().nextInt(words.length)];
            title = String.format("%s %s", title, word);
        }

        // Create a random description.
        int descWords = ThreadLocalRandom.current().nextInt(8, 16);
        String desc = "";
        for (int i = 0; i < descWords; i++) {
            String word = words[ThreadLocalRandom.current().nextInt(words.length)];
            desc = String.format("%s %s", desc, word);
        }

        // Create a random creation date.
        long years100 = 1000L * 60 * 60 * 24 * 365 * 100;
        long current = System.currentTimeMillis();
        long minYear = current - years100;
        long yearMs = ThreadLocalRandom.current().nextLong(minYear, current);
        java.sql.Date date = new java.sql.Date(yearMs);

        // Set the information for the artifact.
        artifact.setTitle(title);
        artifact.setDescription(desc);
        artifact.setDate(date);
        artifact.setPlace(words[ThreadLocalRandom.current().nextInt(words.length)]);
        artifact.setMedium(mediums[ThreadLocalRandom.current().nextInt(mediums.length)]);
        artifact.setDimensions(dimensions[ThreadLocalRandom.current().nextInt(dimensions.length)]);
        artifact.setArtistId(artistId);
        artifact.setCollectionId(collectionId);
        artifact.setOwnerId(ownerId);

        return artifact;
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
