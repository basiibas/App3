package com.example.bas.app3;

public class Booking {

    int id;
    String date;
    String start;
    String end;

    public Booking(String date, String start, String end) {
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public Booking(int id, String date, String start, String end) {
        this.id = id;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
