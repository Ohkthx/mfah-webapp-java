package com.mfahproj.webapp.models;

public class Notification {
    private int id;
    private String text;
    private boolean hasChecked;
    private java.sql.Timestamp time;

    // Create a blank notification.
    public Notification() {
    }

    public Notification(int id, String text, boolean hasChecked, java.sql.Timestamp time) {
        this.setId(id);
        this.setText(text);
        this.setChecked(hasChecked);
        this.setTime(time);
    };

    // Getters
    public int getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public boolean getChecked() {
        return this.hasChecked;
    }

    public java.sql.Timestamp getTime() {
        return this.time;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setChecked(boolean hasChecked) {
        this.hasChecked = hasChecked;
    }

    public void setTime(java.sql.Timestamp time) {
        this.time = time;
    }

    public String asSection() {
        return "<section>"
                + String.format("<h2>%s</h2>", this.getText())
                + String.format("<p>%s</p>", this.getTime())
                + "</section>";
    }
}