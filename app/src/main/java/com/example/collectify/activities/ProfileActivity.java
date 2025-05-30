package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collectify.R;

public class ProfileActivity extends AppCompatActivity {

    TextView avatar, tvUsername, tvJoinDate, tvFullName, tvEmail;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        avatar = findViewById(R.id.avatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvJoinDate = findViewById(R.id.tvJoinDate);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);

        // Dummy data (ganti dengan data dari Firebase / SharedPreferences)
        String username = "bndri123";
        String joinDate = "27 Mei 2025";
        String fullName = "Bndri Pratama";
        String email = "andri@example.com";

        tvUsername.setText(username);
        tvJoinDate.setText("Bergabung sejak: " + joinDate);
        tvFullName.setText(fullName);
        tvEmail.setText(email);

        // Avatar berisi huruf pertama dari username
        if (username.length() > 0) {
            avatar.setText(String.valueOf(username.charAt(0)).toUpperCase());
        }

        // Fungsi Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tambahkan logika logout sesuai kebutuhan
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish(); // Menutup halaman profile
            }
        });
    }
}
