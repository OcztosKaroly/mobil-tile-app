package com.example.tile_shop_application;

public class Billing {
    private String userId;
    private String name;
    private String country;
    private String postnumber;
    private String city;
    private String address;

    public Billing() { }

    public Billing(String userId, String name, String country, String postnumber, String city, String address) {
        this.userId = userId;
        this.name = name;
        this.country = country;
        this.postnumber = postnumber;
        this.city = city;
        this.address = address;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getPostnumber() { return postnumber;  }
    public String getCity() { return city; }
    public String getAddress() { return address; }
}
