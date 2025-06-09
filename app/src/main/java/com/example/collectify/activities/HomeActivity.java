package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.collectify.R;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

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

        setupBottomNavigation();
        SessionManager sessionManager = new SessionManager(this);

        // --- Autologin check --------------------------------------------------
        if (!sessionManager.isLoggedIn()) {
            // Langsung ke Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    // Metode untuk mengatur navigasi
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_collection) {
                    navigate(CollectionActivity.class);
                } else if (id == R.id.nav_scan) {
                    navigate(ScanQRActivity.class);
                } else if (id == R.id.nav_merchandise) {
                    navigate(MerchandiseActivity.class);
                } else if (id == R.id.nav_profile) {
                    navigate(ProfileActivity.class);
                }

                overridePendingTransition(0, 0);
                return true;
            }
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
}
