package com.example.tile_shop_application;

public class User {
    private String id;
    private String email;
    private String phone;
    private String customerType;
    private String taxNumber;
    private boolean isBillingSameAsShipping;

    public User() { }

    public User(String id, String email, String phone, String customerType, String taxNumber, boolean isBillingSameAsShipping) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.customerType = customerType;
        this.taxNumber = taxNumber;
        this.isBillingSameAsShipping = isBillingSameAsShipping;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCustomerType() { return customerType; }
    public String getTaxNumber() { return taxNumber; }
    public boolean getIsBillingSameAsShipping() { return isBillingSameAsShipping; }
}
