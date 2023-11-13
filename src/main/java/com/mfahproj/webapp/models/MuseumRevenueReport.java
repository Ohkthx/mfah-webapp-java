package com.mfahproj.webapp.models;

public class MuseumRevenueReport {
    private int museumId;
    private String museumName;
    private String address;
    private Double currentTotalRevenue;
    private Double totalRevenue;

    public MuseumRevenueReport(int museumId, String museumName, String address, Double currentTotalRevenue, Double totalRevenue) {
        this.museumId = museumId;
        this.museumName = museumName;
        this.address = address;
        this.currentTotalRevenue = currentTotalRevenue;
        this.totalRevenue = totalRevenue;
    }

    public MuseumRevenueReport() {
        this.museumId = 0;
        this.museumName = "";
        this.address = "";
        this.currentTotalRevenue = 0.0;
        this.totalRevenue = 0.0;
    }

    public int getMuseumId() {
        return museumId;
    }

    public void setMuseumId(int museumId) {
        this.museumId = museumId;
    }

    public String getMuseumName() {
        return museumName;
    }

    public void setMuseumName(String museumName) {
        this.museumName = museumName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getCurrentTotalRevenue() {
        return currentTotalRevenue;
    }

    public void setCurrentTotalRevenue(Double currentTotalRevenue) {
        this.currentTotalRevenue = currentTotalRevenue;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Override
    public String toString() {
        return "MuseumRevenueReport{" +
                "museumId=" + museumId +
                ", museumName='" + museumName + '\'' +
                ", address='" + address + '\'' +
                ", currentTotalRevenue=" + currentTotalRevenue +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}
