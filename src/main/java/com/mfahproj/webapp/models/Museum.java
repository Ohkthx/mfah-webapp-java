package com.mfahproj.webapp.models;

public class Museum {

    private int museumId = -1;
    private String name;
    private String address;
    private int totalRevenue;
    private int operationalCost;

    // Create a blank museum.
    public Museum() {
    };

    // Used to create a new museum.
    // museumId is created by AUTOINCREMENT.
    public Museum(String name, String address, int totalRevenue, int operationalCost) {
        this(-1, name, address, totalRevenue, operationalCost);
    }

    public Museum(int museumId, String name, String address, int totalRevenue, int operationalCost) {
        this.setMuseumId(museumId);
        this.setName(name);
        this.setAddress(address);
        this.setTotalRevenue(totalRevenue);
        this.setOperationalCost(operationalCost);
    }

    // MuseumId getter.
    public int getMuseumId() {
        return this.museumId;
    }

    // MuseumId setter.
    public void setMuseumId(int museumId) {
        this.museumId = museumId;
    }

    // Name getter.
    public String getName() {
        return this.name;
    }

    // Name setter.
    public void setName(String name) {
        this.name = name;
    }

    // Address getter.
    public String getAddress() {
        return this.name;
    }

    // Address setter.
    public void setAddress(String address) {
        this.address = address;
    }

    // TotalRevenue getter.
    public int getTotalRevenue() {
        return this.totalRevenue;
    }

    // TotalRevenue setter.
    public void setTotalRevenue(int totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    // OperationalCost getter.
    public int getOperationalCost() {
        return this.totalRevenue;
    }

    // OperationalCost setter.
    public void setOperationalCost(int operationCost) {
        this.operationalCost = operationCost;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Museum{"
                + ", museumId='" + this.museumId
                + ", name='" + this.name + '\''
                + ", address='" + this.address + '\''
                + ", totalRevenue=" + this.totalRevenue + '\''
                + ", operationalCost=" + this.operationalCost
                + '}';
    }
}