package com.example.collectify.model;

public class StampModel {
    public int id;
    public String name;
    public String image_url;
    public String qr_string;
    public int collection_id;
    public String jam_operasional;
    public String harga_tiket;
    public String deskripsi;
    public int sudah_dikoleksi;
    public String lokasi;

    public StampModel(int id, String name, int collection_id) {
        this.id = id;
        this.name = name;
        this.collection_id = collection_id;
    }

    public StampModel(int id, String name, String image_url, String qr_string, int collection_id, String jam_operasional, String harga_tiket, String deskripsi, int sudah_dikoleksi, String lokasi) {
        this.id = id;
        this.name = name;
        this.image_url = image_url;
        this.qr_string = qr_string;
        this.collection_id = collection_id;
        this.jam_operasional = jam_operasional;
        this.harga_tiket = harga_tiket;
        this.deskripsi = deskripsi;
        this.sudah_dikoleksi = sudah_dikoleksi;
        this.lokasi = lokasi;
    }
}
