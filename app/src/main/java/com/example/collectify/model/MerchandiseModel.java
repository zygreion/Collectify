package com.example.collectify.model;

public class MerchandiseModel {
    public int id;
    public String name;
    public String imageUrl;
    public int stock;
    public int requiredStamp;

    public MerchandiseModel(int id, String name, String imageUrl, int stock, int requiredStamp) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.requiredStamp = requiredStamp;
    }
}
