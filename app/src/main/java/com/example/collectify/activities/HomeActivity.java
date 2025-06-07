package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collectify.R;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        // --- Autologin check --------------------------------------------------
        if (!sessionManager.isLoggedIn()) {
            // Langsung ke Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // --- BottomNavigationView --------------------------------------------
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);         // tandai menu aktif

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;                                // sudah di Home
            }
            if (id == R.id.nav_collection) {
                navigate(CollectionActivity.class);
                return true;
            }
            if (id == R.id.nav_scan) {
                launchScanner();
                return true;
            }
            if (id == R.id.nav_merchandise) {
                navigate(MerchandiseActivity.class);
                return true;
            }
            if (id == R.id.nav_profile) {
                navigate(ProfileActivity.class);
                return true;
            }
            return false;
        });
    }

    // ------------------------------ Helpers ----------------------------------

    /** Pindah Activity tanpa menumpuk BackStack. */
    private void navigate(@NonNull Class<?> target) {
        Intent intent = new Intent(this, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0); // tanpa animasi
        finish();
    }

    /** Jalankan Scan QR. */
    private void launchScanner() {
        ScanOptions options = new ScanOptions()
                .setOrientationLocked(false)
                .setCaptureActivity(ScanQRActivity.class)
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        barcodeLauncher.launch(options);
    }

    // Handle hasil scan
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) {
                    if (result.getOriginalIntent() != null &&
                            result.getOriginalIntent().hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Toast.makeText(this, "Kamera tidak diizinkan", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    Log.d("HomeActivity", "Scanned: " + result.getContents());
                    // TODO: proses QR di sini
                }
            });
}
