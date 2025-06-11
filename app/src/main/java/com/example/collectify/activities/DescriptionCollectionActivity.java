package com.example.collectify.activities;

import android.os.Bundle;
import android.view.View;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_description_collection);

        View mainView = findViewById(R.id.main);

        // Simpan padding asli dari layout XML
        int originalLeft = mainView.getPaddingLeft();
        int originalTop = mainView.getPaddingTop();
        int originalRight = mainView.getPaddingRight();
        int originalBottom = mainView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    originalLeft + systemBars.left,
                    originalTop + systemBars.top,
                    originalRight + systemBars.right,
                    originalBottom
            );
            return insets;
        });

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