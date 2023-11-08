package com.mfahproj.webapp.models;

public class ArtifactInventoryReport {
    private String artifactTitle;
    private String collectionTitle;
    private String firstName;
    private String lastName;

    public ArtifactInventoryReport(String artifactTitle, String collectionTitle, String firstName, String lastName) {
        this.artifactTitle = artifactTitle;
        this.collectionTitle = collectionTitle;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public ArtifactInventoryReport() {
        this.artifactTitle = "";
        this.collectionTitle = "";
        this.firstName = "";
        this.lastName = "";
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "ArtifactInventoryReport{" +
                "artifactTitle='" + artifactTitle + '\'' +
                ", collectionTitle='" + collectionTitle + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
