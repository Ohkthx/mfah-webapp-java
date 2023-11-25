package com.mfahproj.webapp.models;

public class MemberDemographicsReport {
    private int children;
    private int teens;
    private int adults;
    private int seniors;

    public MemberDemographicsReport(int children, int teens, int adults, int seniors) {
        this.children = children;
        this.teens = teens;
        this.adults = adults;
        this.seniors = seniors;

    }

    public MemberDemographicsReport() {
        this.children =0;
        this.teens =0;
        this.adults =0;
        this.seniors =0;
    }

    public int getChildren() {
        return children;
    }
    public void setChildren(int children) {
        this.children = children;
    }

    public int getTeens() {
        return teens;
    }
    public void setTeens(int teens) {
        this.teens = teens;
    }

    public int getAdults() {
        return adults;
    }
    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getSeniors() {
        return seniors;
    }
    public void setSeniors(int seniors) {
        this.seniors = seniors;
    }

    @Override
    public String toString() {
        return "MemberDemographicsReport{" +
        "children=" + children +
        ", teens=" + teens +
        ", adults=" + adults +
        ", seniors=" + seniors + "}";
    }

}
