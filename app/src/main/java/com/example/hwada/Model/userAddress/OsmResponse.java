package com.example.hwada.Model.userAddress;

import java.util.List;

public class OsmResponse {
    private int place_id;
    private String licence;
    private String osm_type;
    private long osm_id;
    private String lat;
    private String lon;
    private String display_name;
    private Address address;
    private List<String> boundingbox;

    public OsmResponse() {
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getOsm_type() {
        return osm_type;
    }

    public void setOsm_type(String osm_type) {
        this.osm_type = osm_type;
    }

    public long getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(long osm_id) {
        this.osm_id = osm_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getBoundingbox() {
        return boundingbox;
    }

    public void setBoundingbox(List<String> boundingbox) {
        this.boundingbox = boundingbox;
    }

    @Override
    public String toString() {
        return "OsmResponse{" +
                "place_id=" + place_id +
                ", licence='" + licence + '\'' +
                ", osm_type='" + osm_type + '\'' +
                ", osm_id=" + osm_id +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", display_name='" + display_name + '\'' +
                ", address=" + address +
                ", boundingbox=" + boundingbox +
                '}';
    }
}
