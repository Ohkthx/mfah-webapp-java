package com.mfahproj.webapp.models;

public class ArtifactOwner {

    private int ownerId = -1;
    private String name;
    private String phoneNumber;

    // Create a blank Artist.
    public ArtifactOwner() {
    };

    // Used to create a new ArtifactOwner.
    // OwnerId is created by AUTOINCREMENT.
    public ArtifactOwner(String name, String phoneNumber) {
        this(-1, name, phoneNumber);
    }

    public ArtifactOwner(int ownerId, String name, String phoneNumber) {
        this.setOwnerId(ownerId);
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
    }

    // Owner ID getter.
    public int getOwnerId() {
        return this.ownerId;
    }

    // Owner ID setter.
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    // Name getter.
    public String getName() {
        return this.name;
    }

    // Name setter.
    public void setName(String name) {
        this.name = name;
    }

    // PhoneNumber getter.
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    // PhoneNumber setter.
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Artifact Owner{"
                + "OwnerId=" + this.ownerId
                + ", Name='" + this.name + '\''
                + ", Phone Number=" + this.phoneNumber
                + '}';
    }
}
