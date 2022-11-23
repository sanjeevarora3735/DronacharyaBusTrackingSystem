package com.example.dbts;

public class Bus_Location {
    private double Latitude;
    private double Longitude;

    public Bus_Location() {
    }

    public Bus_Location(Bus_Location getBusLocation) {
        Latitude = getBusLocation.getLatitude();
        Longitude = getBusLocation.getLongitude();
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public Bus_Location(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }
}