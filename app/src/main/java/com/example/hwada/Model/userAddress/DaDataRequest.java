package com.example.hwada.Model.userAddress;

import com.example.hwada.Model.LocationCustom;

public class DaDataRequest {
    private double lat;
    private double lon;

    public DaDataRequest(LocationCustom locationCustom) {
        this.lat = locationCustom.getLatitude();
        this.lon = locationCustom.getLongitude();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
