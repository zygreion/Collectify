package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.collectify.R;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    TextView avatar, tvUsername, tvJoinDate, tvFullName, tvEmail;
    Button btnLogout;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

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

        sessionManager = new SessionManager(this);

        avatar = findViewById(R.id.avatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvJoinDate = findViewById(R.id.tvJoinDate);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserData();

        btnLogout.setOnClickListener(view -> {
            sessionManager.logout();
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finishAffinity();
        });

        setupBottomNavigation();
    }

    private void loadUserData() {
        new Thread(() -> {
            try {
                String accessToken = sessionManager.getAccessToken();
                String userId = sessionManager.getUserId();

                if (accessToken == null || userId == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Session tidak ditemukan", Toast.LENGTH_SHORT).show());
                    return;
                }

                JSONObject userData = SupabaseClient.getUserData(accessToken, userId);

                String username = userData.getString("username");
                String joinDate = userData.getString("created_at").split("T")[0];
                String fullName = userData.getString("full_name");
                String email = userData.getString("email");

                runOnUiThread(() -> {
                    tvUsername.setText(username);
                    tvJoinDate.setText(joinDate);
                    tvFullName.setText(fullName);
                    tvEmail.setText(email);
                    avatar.setText(String.valueOf(username.charAt(0)).toUpperCase());
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Gagal memuat profil: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // Metode untuk mengatur navigasi
    private void setupBottomNavigation () {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_collection) {
                    startActivity(new Intent(ProfileActivity.this, CollectionActivity.class));
                } else if (id == R.id.nav_scan) {
                    startActivity(new Intent(ProfileActivity.this, ScanQRActivity.class));
                } else if (id == R.id.nav_merchandise) {
                    startActivity(new Intent(ProfileActivity.this, MerchandiseActivity.class));
                } else if (id == R.id.nav_profile) {
                    return true; // Sudah di halaman ini
                }

                overridePendingTransition(0, 0);
                return true;
            }
        });
    }
}
