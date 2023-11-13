package com.mfahproj.webapp.models;

public class ExihibitionsAndCollections {
    private String exihibitionTitle;
    private String collectionTitle;

    public ExihibitionsAndCollections() {
        this.exihibitionTitle = "";
        this.collectionTitle = "";
    }

    public ExihibitionsAndCollections(String exihibitionTitle, String collectionTitle) {
        this.exihibitionTitle = exihibitionTitle;
        this.collectionTitle = collectionTitle;
    }

    // Getter and Setter

    public String getExihibitionTitle() {
        return exihibitionTitle;
    }

    public void setExihibitionTitle(String exihibitionTitle) {
        this.exihibitionTitle = exihibitionTitle;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    // String Representation of the model

    @Override
    public String toString() {
        return "ExihibitionsAndCollections{" +
                "exihibitionTitle='" + exihibitionTitle + '\'' +
                ", collectionTitle='" + collectionTitle + '\'' +
                '}';
    }
}
