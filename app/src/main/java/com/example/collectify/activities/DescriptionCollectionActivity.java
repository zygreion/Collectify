package com.example.collectify.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.collectify.R;
import com.example.collectify.model.CollectionModel;

public class DescriptionCollectionActivity extends AppCompatActivity {

    ImageView imageView;
    TextView lokasi, jamOperasional, hargaTiket, deskripsi, namaTempat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_collection);

        imageView = findViewById(R.id.imageView);
        lokasi = findViewById(R.id.tvLokasi);
        jamOperasional = findViewById(R.id.tvJamOperasional);
        hargaTiket = findViewById(R.id.tvHargaTiket);
        deskripsi = findViewById(R.id.tvDeskripsi);
        namaTempat = findViewById(R.id.tvNamaTempat);

        // Ambil data dari intent
        CollectionModel item = (CollectionModel) getIntent().getSerializableExtra("collection");

        if (item != null) {
            namaTempat.setText(item.name);
            lokasi.setText("Lokasi: " + item.lokasi);
            jamOperasional.setText("Jam operasional: " + item.jam_operasional);
            hargaTiket.setText("Harga tiket: " + item.harga_tiket);
            deskripsi.setText(item.deskripsi);

            Glide.with(this).load(item.image_url).into(imageView);
        }
    }
}