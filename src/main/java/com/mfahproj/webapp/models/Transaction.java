package com.mfahproj.webapp.models;

import java.util.concurrent.ThreadLocalRandom;

public class Transaction {
    // Various objects that can be purchased.
    public enum Type {
        TICKET,
        POSTER,
        CALENDAR,
        BOOK,
        CARD,
        FOSSIL
    }

    private int transactionId = -1;
    private String itemType;
    private double price;
    private java.sql.Date purchaseDate;
    private int memberId = 1;
    private int museumId = 1;

    // Create a blank transaction.
    public Transaction() {
    };

    // Used to create a new transaction.
    // transactionId is created by AUTOINCREMENT.
    public Transaction(Transaction.Type type, double price, java.sql.Date purchaseDate, int memberId, int museumId) {
        this(-1, type, price, purchaseDate, memberId, museumId);
    }

    public Transaction(int transactionId, Transaction.Type type, double price, java.sql.Date purchaseDate, int memberId,
            int museumId) {
        this.setTransactionId(transactionId);
        this.setItemType(type.name());
        this.setMuseumId(museumId);
        this.setPrice(price);
        this.setMemberId(memberId);
        this.setPurchaseDate(purchaseDate);
    }

    // Creates a random instance of a Transaction.
    public static Transaction generateRandom(int memberId, int museumId, double priceMin, double priceMax,
            Transaction.Type type) {
        Transaction transaction = new Transaction();

        // Random item type.
        String itemType = "";
        if (type != null) {
            itemType = type.name();
        } else {
            Transaction.Type[] types = Transaction.Type.values();
            itemType = types[ThreadLocalRandom.current().nextInt(types.length)].name();
        }

        // Create price range between min and max.
        double price = ThreadLocalRandom.current().nextDouble(priceMin, priceMax);

        // Create a random timestamp in the past 2 years.
        long years2 = 1000L * 60 * 60 * 24 * 365 * 2;
        long current = System.currentTimeMillis();
        long minYear = current - years2;
        long yearMs = ThreadLocalRandom.current().nextLong(minYear, current);
        java.sql.Date purchaseDate = new java.sql.Date(yearMs);

        // Set the provided museumId and memberId
        transaction.setItemType(itemType);
        transaction.setPrice(price);
        transaction.setPurchaseDate(purchaseDate);
        transaction.setMemberId(memberId);
        transaction.setMuseumId(museumId);

        return transaction;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public double getPrice() {
        return this.price;
    }

    public int getMuseumId() {
        return this.museumId;
    }

    public int getMemberId() {
        return this.memberId;
    }

    public String getItemType() {
        return this.itemType;
    }

    public java.sql.Date getPurchaseDate() {
        return this.purchaseDate;
    }

    // Setters
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setMuseumId(int museumId) {
        this.museumId = museumId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setPurchaseDate(java.sql.Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}