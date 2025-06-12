package com.example.collectify.model;

import java.io.Serializable;
import java.util.List;

public class CollectionStampsModel implements Serializable {
    public CollectionModel collection;
    public List<StampModel> stampList;

    public CollectionStampsModel(CollectionModel collection, List<StampModel> stampList) {
        this.collection = collection;
        this.stampList = stampList;
    }
}
