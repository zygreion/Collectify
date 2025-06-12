package com.example.collectify.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.adapter.CollectionSectionAdapter;
import com.example.collectify.adapter.MerchandiseAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.CollectionStampsModel;
import com.example.collectify.model.MerchandiseModel;
import com.example.collectify.model.StampModel;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.collectify.R; // Pastikan ini mengarah ke R Anda
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CollectionSectionsActivity extends AppCompatActivity {

    private RecyclerView rvCollectionSection;
    private CollectionSectionAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_sections);

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

        rvCollectionSection = findViewById(R.id.rv_collection_section);
        progressBar = findViewById(R.id.progress_bar);

        // Menampilkan progress bar sambil mengambil data
        progressBar.setVisibility(View.VISIBLE);

        // --- Setup RecyclerView Utama ---
        setupRecyclerView();

        // --- Setup Bottom Navigation ---
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        // LayoutManager untuk RecyclerView utama (vertikal)
        rvCollectionSection.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Thread untuk mengambil data merchandise dan total stamp dari Supabase
        new Thread(() -> {
            try {
                SessionManager sessionManager = new SessionManager(CollectionSectionsActivity.this);
                String userId = sessionManager.getUserId();
                List<CollectionStampsModel> collectionStampList = SupabaseClient.getCollectionsStampsFromUser(userId);

                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        adapter = new CollectionSectionAdapter(CollectionSectionsActivity.this, collectionStampList);
                        rvCollectionSection.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
    }

    // Metode untuk mengatur navigasi
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_collection);

        final Context context = this;

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(context, HomeActivity.class));
                } else if (id == R.id.nav_collection) {
                    return true; // Sudah di halaman ini
                } else if (id == R.id.nav_scan) {
                    startActivity(new Intent(context, ScanQRActivity.class));
                } else if (id == R.id.nav_merchandise) {
                    startActivity(new Intent(context, MerchandiseActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(context, ProfileActivity.class));
                }

                overridePendingTransition(0, 0);
                return true;
            }
        });
    }
}