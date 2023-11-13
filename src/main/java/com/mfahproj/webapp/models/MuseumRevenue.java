package com.mfahproj.webapp.models;

public class MuseumRevenue {
    private String museumName;
    private Double revenue;

    public MuseumRevenue() {
        this.museumName = "";
        this.revenue = 0.0;
    }

    public MuseumRevenue(String museumName, Double revenue) {
        this.museumName = museumName;
        this.revenue = revenue;
    }

    // Getter and Setters

    public String getMuseumName() {
        return museumName;
    }

    public void setMuseumName(String museumName) {
        this.museumName = museumName;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    // String Representation
    @Override
    public String toString() {
        return "MuseumRevenue{" +
                "museumName='" + museumName + '\'' +
                ", revenue='" + revenue + '\'' +
                '}';
    }
}
