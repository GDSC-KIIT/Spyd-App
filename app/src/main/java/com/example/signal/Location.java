package com.example.signal;

public class Location {
    private String latitude_db;
    private String longitude_db;
    private String networkProviderUser_db;

    public Location() {
    }

    public String getLatitude_db() {
        return latitude_db;
    }

    public String getNetworkProviderUser_db() {
        return networkProviderUser_db;
    }

    public void setNetworkProviderUser_db(String networkProviderUser_db) {
        this.networkProviderUser_db = networkProviderUser_db;
    }

    public void setLatitude_db(String latitude_db) {
        this.latitude_db = latitude_db;
    }

    public String getLongitude_db() {
        return longitude_db;
    }

    public void setLongitude_db(String longitude_db) {
        this.longitude_db = longitude_db;
    }
}
