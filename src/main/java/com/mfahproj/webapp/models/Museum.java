package com.mfahproj.webapp.models;

public class Museum {

    private int museumId = -1;
    private String name = "";
    private String address = "";
    private double totalRevenue = 0.0;
    private double operationalCost = 0.0;

    // Create a blank museum.
    public Museum() {
    };

    // Used to create a new museum.
    // museumId is created by AUTOINCREMENT.
    public Museum(String name, String address, double totalRevenue, double operationalCost) {
        this(-1, name, address, totalRevenue, operationalCost);
    }

    public Museum(int museumId, String name, String address, double totalRevenue, double operationalCost) {
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
        return this.address;
    }

    // Address setter.
    public void setAddress(String address) {
        this.address = address;
    }

    // TotalRevenue getter.
    public double getTotalRevenue() {
        return this.totalRevenue;
    }

    // TotalRevenue setter.
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    // OperationalCost getter.
    public double getOperationalCost() {
        return this.totalRevenue;
    }

    // OperationalCost setter.
    public void setOperationalCost(double operationCost) {
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