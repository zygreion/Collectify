package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.R;
import com.example.collectify.adapter.MerchandiseAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.MerchandiseModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MerchandiseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MerchandiseAdapter adapter;
    ProgressBar progressBar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise);

        recyclerView = findViewById(R.id.merchandiseRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);

        setupBottomNavigation();

        new Thread(() -> {
            try {
                List<MerchandiseModel> list = SupabaseClient.fetchMerchandise();
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        adapter = new MerchandiseAdapter(MerchandiseActivity.this, list);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_merchandise);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(MerchandiseActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_collection) {
                    startActivity(new Intent(MerchandiseActivity.this, CollectionActivity.class));
                } else if (id == R.id.nav_scan) {
                    startActivity(new Intent(MerchandiseActivity.this, ScanQRActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(MerchandiseActivity.this, ProfileActivity.class));
                } else if (id == R.id.nav_merchandise) {
                    return true; // sudah di halaman ini
                }

                overridePendingTransition(0, 0);
                return true;
            }
        });
    }
}
