package com.mfahproj.webapp.models;

public class MemberDemographics {
    private int children;
    private int teens;
    private int adults;
    private int seniors;

    public MemberDemographics() {
        children =0;
        teens =0;
        adults =0;
        seniors =0;
    }

    public MemberDemographics(int ch, int te, int ad, int se) {
        children = ch;
        teens = te;
        adults = ad;
        seniors = se;
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
