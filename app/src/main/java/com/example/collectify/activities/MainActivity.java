package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collectify.R;
import com.example.collectify.utils.SessionManager;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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

            btnScanQR.setOnClickListener(view -> {
                ScanOptions options = new ScanOptions()
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanQRActivity.class)
                        .setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                barcodeLauncher.launch(options);
            });

//            btnMerchandise.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MerchandiseActivity.class)));

            btnProfile.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        }
    }

    // Untuk mendapatkan hasil Scan QR code
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(MainActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });
}
