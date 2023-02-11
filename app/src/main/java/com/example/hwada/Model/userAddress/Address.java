package com.example.hwada.Model.userAddress;


import androidx.annotation.NonNull;

public class Address {
    private String man_made;
    private String road;
    private String neighbourhood;
    private String city;
    private String state;
    private String ISO3166_2_lvl4;
    private String postcode;
    private String country;
    private String country_code;

    public Address() {
    }

    public String getMan_made() {
        return man_made;
    }

    public void setMan_made(String man_made) {
        this.man_made = man_made;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getISO3166_2_lvl4() {
        return ISO3166_2_lvl4;
    }

    public void setISO3166_2_lvl4(String ISO3166_2_lvl4) {
        this.ISO3166_2_lvl4 = ISO3166_2_lvl4;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    @Override
    public String toString() {
        return "Address{" +
                "man_made='" + man_made + '\'' +
                ", road='" + road + '\'' +
                ", neighbourhood='" + neighbourhood + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", ISO3166_2_lvl4='" + ISO3166_2_lvl4 + '\'' +
                ", postcode='" + postcode + '\'' +
                ", country='" + country + '\'' +
                ", country_code='" + country_code + '\'' +
                '}';
    }
}