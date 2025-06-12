package com.example.collectify.model;

import java.io.Serializable;

public class CollectionModel implements Serializable {
    public long id;
    public String name;
    public String imageUrl;
    public int totalStampsCollected;
    public int totalStamps;

    public CollectionModel(long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public CollectionModel(long id, String name, String imageUrl, int totalStampsCollected, int totalStamps) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.totalStamps = totalStamps;
        this.totalStampsCollected = totalStampsCollected;
    }
}
