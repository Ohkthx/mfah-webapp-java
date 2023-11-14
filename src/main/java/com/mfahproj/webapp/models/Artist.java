package com.mfahproj.webapp.models;

public class Artist {

    private int artistId = -1;
    private String firstName;
    private String lastName;

    // Create a blank Artist.
    public Artist() {
    };

    // Used to create a new Artist.
    // ArtistId is created by AUTOINCREMENT.
    public Artist(String firstName, String lastName) {
        this(-1, firstName, lastName);
    }

    public Artist(int artistId, String firstName, String lastName) {
        this.setArtistId(artistId);
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }

    // Artist ID getter.
    public int getArtistId() {
        return this.artistId;
    }

    // Artist ID setter.
    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    // First Name getter.
    public String getFirstName() {
        return this.firstName;
    }

    // First Name setter.
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Last Name getter.
    public String getLastName() {
        return this.lastName;
    }

    // Last Name setter.
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Artist{"
                + "ArtistId=" + this.artistId
                + ", First Name='" + this.firstName + '\''
                + ", Last Name=" + this.lastName
                + '}';
    }
}
