package com.example.fairyraceapphw1;

import android.location.Location;

public class Player {
    private String name;
    private double latitude;
    private double longitude;
    private int score;
    private Location location;


    public Player(String name,Location location,int score){
        this.name=name;
        this.latitude=location.getLatitude();
        this.longitude=location.getLongitude();
        this.score=score;
        this.location=location;

    }
    public Player(String name,double lat,double lng,int score){
        this.name=name;
        this.latitude=lat;
        this.longitude=lng;
        this.score=score;
    }

    public int getScore() {
        return score;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return String.format("Name: %-8s Lat: %.2f Lng: %.2f Score: %-4d\n",this.name,this.latitude,this.longitude,this.score);
    }
}

