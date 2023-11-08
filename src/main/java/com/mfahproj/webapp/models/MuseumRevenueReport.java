package com.mfahproj.webapp.models;

public class MuseumRevenueReport {
    private String museumName;
    private Double totalRevenue;

    public MuseumRevenueReport(String museumName, Double totalRevenue) {
        this.museumName = museumName;
        this.totalRevenue = totalRevenue;
    }

    public MuseumRevenueReport() {
        this.museumName = "";
        this.totalRevenue = 0.0;
    }
    public String getMuseumName() {
        return museumName;
    }

    public void setMuseumName(String museumName) {
        this.museumName = museumName;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    //String representation of the model=
    @Override
    public String toString() {
        return "MuseumRevenueReport{" +
                "museumName='" + museumName + '\'' +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}
