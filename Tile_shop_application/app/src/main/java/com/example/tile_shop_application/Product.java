package com.example.tile_shop_application;

import androidx.annotation.NonNull;

public class Product {

    private String id;
    private String name;
    private String description;
    private String color;
    private String size;
    private String where;
    private int price;

    public Product() { }

    public Product(Product product) {
        this.id = product.id;
        this.name = product.name;
        this.description = product.description;
        this.color = product.color;
        this.size = product.size;
        this.where = product.where;
        this.price = product.price;
    }

    public Product(@NonNull String id,
                   @NonNull String name,
                   @NonNull String description,
                   @NonNull String color,
                   @NonNull String size,
                   @NonNull String where,
                   int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.size = size;
        this.where = where;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public String getSize() { return size; }
    public String getWhere() { return where; }
    public int getPrice() { return price; }
}
