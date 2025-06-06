package com.example.collectify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CollectionModel implements Serializable {
    public int id;
    public String name;
    public String image_url;
    public String qr_string;
    public String jam_operasional;
    public String harga_tiket;
    public String deskripsi;
    public int sudah_dikoleksi;
    public String lokasi;
}
