package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collectify.R;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegister, btnCollection, btnScanQR, btnMerchandise, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnCollection = findViewById(R.id.btn_collection);
        btnScanQR = findViewById(R.id.btn_scan_qr);
        btnMerchandise = findViewById(R.id.btn_merchandise);
        btnProfile = findViewById(R.id.btn_profile);

//        // Fungsi Login
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Membuka halaman profile
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//            }
//        });
//
//        // Fungsi Register
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Membuka halaman profile
//                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
//                finish();
//            }
//        });
//
//        // Fungsi Collection
//        btnCollection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Membuka halaman profile
//                startActivity(new Intent(MainActivity.this, CollectionActivity.class));
//                finish();
//            }
//        });
//
//        // Fungsi Scan QR
//        btnScanQR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Membuka halaman profile
//                startActivity(new Intent(MainActivity.this, ScanQRActivity.class));
//                finish();
//            }
//        });
//
//        // Fungsi Merchandise
//        btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Membuka halaman profile
//                startActivity(new Intent(MainActivity.this, MerchandiseActivity.class));
//                finish();
//            }
//        });

        // Fungsi Profile
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Membuka halaman profile
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }
}
