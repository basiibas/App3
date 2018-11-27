package com.example.bas.app3;

public class Booking {

    int id;
    String name;
    String date;
    String start;
    String end;


    public Booking(int id, String name, String date, String start, String end) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public Booking(String name, String date, String start, String end) {
        this.name = name;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public Booking(String date, String start, String end) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
