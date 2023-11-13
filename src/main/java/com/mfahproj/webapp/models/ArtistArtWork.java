package com.mfahproj.webapp.models;

public class ArtistArtWork {

    private String firstName;
    private String lastName;
    private String artworkTitle;

    public ArtistArtWork() {
        this.firstName = "";
        this.lastName = "";
        this.artworkTitle = "";
    }

    public ArtistArtWork(String firstName, String lastName, String artworkTitle) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.artworkTitle = artworkTitle;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", artworkTitle='" + artworkTitle + '\'' +
                '}';
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

    public String getArtworkTitle() {
        return artworkTitle;
    }

    public void setArtworkTitle(String artworkTitle) {
        this.artworkTitle = artworkTitle;
    }
}
