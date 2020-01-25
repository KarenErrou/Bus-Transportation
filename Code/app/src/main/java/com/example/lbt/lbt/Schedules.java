package com.example.lbt.lbt;

public class Schedules {
    // private String id;
    private String price;
    private String fromTime ;
    private String toTime;
    private String timeCalc;
    private String fromStation;
    private String toStation;
    private String busNbr;


    public Schedules(String price, String fromTime, String toTime, String timeCalc, String fromStation, String toStation, String busNbr) {
        //this.id = id;
        this.price = price;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.timeCalc = timeCalc;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.busNbr = busNbr;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getTimeCalc() {
        return timeCalc;
    }

    public void setTimeCalc(String timeCalc) {
        this.timeCalc = timeCalc;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public String getBusNbr() {
        return busNbr;
    }

    public void setBusNbr(String busNbr) {
        this.busNbr = busNbr;
    }
}
