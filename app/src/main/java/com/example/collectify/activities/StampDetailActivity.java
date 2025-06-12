package com.example.collectify.activities;

import android.graphics.Color;
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
import com.example.collectify.model.StampModel;
import com.example.collectify.utils.Utils;

public class StampDetailActivity extends AppCompatActivity {

    ImageView imgStamp;
    TextView tvStampName, tvScanned, tvLokasi, tvJamOperasional, tvHargaTiket, tvDeskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stamp_detail);

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
                    originalBottom + systemBars.bottom
            );
            return insets;
        });

        tvStampName = findViewById(R.id.tv_stamp_name);
        imgStamp = findViewById(R.id.img_stamp);
        tvLokasi = findViewById(R.id.tv_lokasi);
        tvJamOperasional = findViewById(R.id.tv_jam_operasional);
        tvHargaTiket = findViewById(R.id.tv_harga_tiket);
        tvDeskripsi = findViewById(R.id.tv_deskripsi);
        tvScanned = findViewById(R.id.tv_scanned);

        // Ambil data dari intent
        StampModel item = (StampModel) getIntent().getSerializableExtra("stamp");

        if (item != null) {
            tvStampName.setText(item.name);

            if (item.scannedAt != null) {
                tvScanned.setTextColor(Color.parseColor("#086F08"));
                tvScanned.setText("Dikoleksi pada " + Utils.formatDateTime(item.scannedAt));
            } else {
                tvScanned.setTextColor(Color.parseColor("#FF0000"));
                tvScanned.setText("Belum dikoleksi");
            }

            tvLokasi.setText(item.lokasi);
            tvJamOperasional.setText(item.jamOperasional);
            tvHargaTiket.setText(item.hargaTiket);
            tvDeskripsi.setText(item.deskripsi);

            Glide.with(this).load(item.imageUrl).into(imgStamp);
        }
    }
}