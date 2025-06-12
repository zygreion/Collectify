package com.example.collectify.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class StampModel implements Serializable {
    public long id;
    public String name;
    public String imageUrl;
    public String qrString;
    public long collectionId;
    public String jamOperasional;
    public String hargaTiket;
    public String deskripsi;
    public String lokasi;
    public boolean isScanned;
    public OffsetDateTime scannedAt;

    public StampModel(long id, String name, String imageUrl, String jamOperasional, String hargaTiket, String deskripsi, String lokasi) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.jamOperasional = jamOperasional;
        this.hargaTiket = hargaTiket;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
    }

    public StampModel(long id, String name, long collectionId) {
        this.id = id;
        this.name = name;
        this.collectionId = collectionId;
    }

    public StampModel(long id, String name, String imageUrl, String jamOperasional, String hargaTiket, String deskripsi, String lokasi, boolean isScanned, OffsetDateTime scannedAt) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.jamOperasional = jamOperasional;
        this.hargaTiket = hargaTiket;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.isScanned = isScanned;
        this.scannedAt = scannedAt;
    }

    public StampModel(long id, String name, String imageUrl, String qrString, long collectionId, String jamOperasional, String hargaTiket, String deskripsi, String lokasi) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.qrString = qrString;
        this.collectionId = collectionId;
        this.jamOperasional = jamOperasional;
        this.hargaTiket = hargaTiket;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
    }


}
