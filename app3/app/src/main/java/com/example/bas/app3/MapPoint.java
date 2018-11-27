package com.example.bas.app3;

public class MapPoint {

    int id;
    String name, description;
    Double latitube, longitude;

    public MapPoint(String name, String description, double latitube, double longitude) {
        this.name = name;
        this.description = description;
        this.latitube = latitube;
        this.longitude = longitude;
    }

    public MapPoint(int id, String name, String description, Double latitube, Double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitube = latitube;
        this.longitude = longitude;
    }

    public MapPoint(int id, String name) {
        this.id = id;
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitube() {
        return latitube;
    }

    public void setLatitube(double latitube) {
        this.latitube = latitube;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
