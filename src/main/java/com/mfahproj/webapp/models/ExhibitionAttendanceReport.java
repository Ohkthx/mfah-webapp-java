package com.mfahproj.webapp.models;

import java.util.Date; // You might need to import the appropriate Date class.

public class ExhibitionAttendanceReport {
    private int exhibitionId;
    private String exhibitionTitle;
    private Date startDate;
    private Date endDate;
    private String description;
    private int transactionItemId;
    private String itemType;
    private double price;
    private Date purchaseDate;

    public ExhibitionAttendanceReport(int exhibitionId, String exhibitionTitle, Date startDate, Date endDate,
            String description,
            int transactionItemId, String itemType, double price, Date purchaseDate) {
        this.exhibitionId = exhibitionId;
        this.exhibitionTitle = exhibitionTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.transactionItemId = transactionItemId;
        this.itemType = itemType;
        this.price = price;
        this.purchaseDate = purchaseDate;
    }

    // Getters and setters for all fields

    public int getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(int exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public String getExhibitionTitle() {
        return exhibitionTitle;
    }

    public void setExhibitionTitle(String exhibitionTitle) {
        this.exhibitionTitle = exhibitionTitle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTransactionItemId() {
        return transactionItemId;
    }

    public void setTransactionItemId(int transactionItemId) {
        this.transactionItemId = transactionItemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "ExhibitionAttendanceReport{" +
                "exhibitionId=" + exhibitionId +
                ", exhibitionTitle='" + exhibitionTitle + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                ", transactionItemId=" + transactionItemId +
                ", itemType='" + itemType + '\'' +
                ", price=" + price +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
