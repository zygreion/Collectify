package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collectify.R;
import com.example.collectify.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegister, btnCollection, btnScanQR, btnMerchandise, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(this);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnCollection = findViewById(R.id.btn_collection);
        btnScanQR = findViewById(R.id.btn_scan_qr);
        btnMerchandise = findViewById(R.id.btn_merchandise);
        btnProfile = findViewById(R.id.btn_profile);

        if (!sessionManager.isLoggedIn()) {
            // Redirect langsung ke LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            // User sudah login
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);

            btnCollection.setVisibility(View.VISIBLE);
            btnScanQR.setVisibility(View.VISIBLE);
            btnMerchandise.setVisibility(View.VISIBLE);
            btnProfile.setVisibility(View.VISIBLE);

            // Aksi tombol-tombol setelah login
//            btnCollection.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CollectionActivity.class)));

//            btnScanQR.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ScanQRActivity.class)));

//            btnMerchandise.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MerchandiseActivity.class)));

            btnProfile.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        }
    }
}
