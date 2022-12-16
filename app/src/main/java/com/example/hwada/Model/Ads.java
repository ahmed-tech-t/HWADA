package com.example.hwada.Model;

public class Ads {
    private String jop;
    private String fullName;
    private String date ;
    private String distance ;

    public String getJop() {
        return jop;
    }

    public void setJop(String jop) {
        this.jop = jop;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Ads(String jop, String fullName, String date, String distance) {
        this.jop = jop;
        this.fullName = fullName;
        this.date = date;
        this.distance = distance;
    }


}
