package com.fuad.ramadancalendar;

public class Ramadan {
    private String day;
    private String date;
    private String sahr;
    private String itmam;

    public Ramadan() {
    }

    public Ramadan(String day, String date, String sahr, String itmam) {
        this.day = day;
        this.date = date;
        this.sahr = sahr;
        this.itmam = itmam;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSahr() {
        return sahr;
    }

    public void setSahr(String sahr) {
        this.sahr = sahr;
    }

    public String getItmam() {
        return itmam;
    }

    public void setItmam(String itmam) {
        this.itmam = itmam;
    }
}
