package com.mfahproj.webapp.models;

public class ArtifactInventoryReport {
    private String artifactTitle;
    private String collectionTitle;
    private String collectionDate;
    private String collectionDescription;
    private String artifactDate;
    private String artifactPlace;
    private String artifactMedium;
    private String artifactDimensions;
    private String artistFirstName;
    private String artistLastName;

    public ArtifactInventoryReport(String artifactTitle, String collectionTitle, String collectionDate,
            String collectionDescription,
            String artifactDate, String artifactPlace, String artifactMedium, String artifactDimensions,
            String artistFirstName, String artistLastName) {
        this.artifactTitle = artifactTitle;
        this.collectionTitle = collectionTitle;
        this.collectionDate = collectionDate;
        this.collectionDescription = collectionDescription;
        this.artifactDate = artifactDate;
        this.artifactPlace = artifactPlace;
        this.artifactMedium = artifactMedium;
        this.artifactDimensions = artifactDimensions;
        this.artistFirstName = artistFirstName;
        this.artistLastName = artistLastName;
    }

    public ArtifactInventoryReport() {
        this.artifactTitle = "";
        this.collectionTitle = "";
        this.collectionDate = "";
        this.collectionDescription = "";
        this.artifactDate = "";
        this.artifactPlace = "";
        this.artifactMedium = "";
        this.artifactDimensions = "";
        this.artistFirstName = "";
        this.artistLastName = "";
    }

    public String getArtifactTitle() {
        return artifactTitle;
    }

    public void setArtifactTitle(String artifactTitle) {
        this.artifactTitle = artifactTitle;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getCollectionDescription() {
        return collectionDescription;
    }

    public void setCollectionDescription(String collectionDescription) {
        this.collectionDescription = collectionDescription;
    }

    public String getArtifactDate() {
        return artifactDate;
    }

    public void setArtifactDate(String artifactDate) {
        this.artifactDate = artifactDate;
    }

    public String getArtifactPlace() {
        return artifactPlace;
    }

    public void setArtifactPlace(String artifactPlace) {
        this.artifactPlace = artifactPlace;
    }

    public String getArtifactMedium() {
        return artifactMedium;
    }

    public void setArtifactMedium(String artifactMedium) {
        this.artifactMedium = artifactMedium;
    }

    public String getArtifactDimensions() {
        return artifactDimensions;
    }

    public void setArtifactDimensions(String artifactDimensions) {
        this.artifactDimensions = artifactDimensions;
    }

    public String getArtistFirstName() {
        return artistFirstName;
    }

    public void setArtistFirstName(String artistFirstName) {
        this.artistFirstName = artistFirstName;
    }

    public String getArtistLastName() {
        return artistLastName;
    }

    public void setArtistLastName(String artistLastName) {
        this.artistLastName = artistLastName;
    }

    @Override
    public String toString() {
        return "ArtifactInventoryReport{" +
                "artifactTitle='" + artifactTitle + '\'' +
                ", collectionTitle='" + collectionTitle + '\'' +
                ", collectionDate='" + collectionDate + '\'' +
                ", collectionDescription='" + collectionDescription + '\'' +
                ", artifactDate='" + artifactDate + '\'' +
                ", artifactPlace='" + artifactPlace + '\'' +
                ", artifactMedium='" + artifactMedium + '\'' +
                ", artifactDimensions='" + artifactDimensions + '\'' +
                ", artistFirstName='" + artistFirstName + '\'' +
                ", artistLastName='" + artistLastName + '\'' +
                '}';
    }
}
