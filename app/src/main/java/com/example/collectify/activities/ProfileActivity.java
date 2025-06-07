package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectify.R;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    TextView avatar, tvUsername, tvJoinDate, tvFullName, tvEmail;
    Button btnLogout;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_collection) {
                startActivity(new Intent(this, CollectionActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_scan) {
                startActivity(new Intent(this, ScanQRActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_merchandise) {
                startActivity(new Intent(this, MerchandiseActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
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
}
