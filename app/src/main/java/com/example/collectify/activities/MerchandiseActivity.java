// KODE SETELAH DIPERBAIKI (SOLUSI)
package com.example.collectify.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.R;
import com.example.collectify.adapter.MerchandiseAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.MerchandiseModel;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MerchandiseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MerchandiseAdapter adapter;
    ProgressBar progressBar;
    TextView totalStampTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_merchandise);

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

        // Inisialisasi semua View
        totalStampTextView = findViewById(R.id.totalStampTextView);
        recyclerView = findViewById(R.id.merchandiseRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Menjalankan setup untuk Bottom Navigation
        setupBottomNavigation();

        // Menampilkan progress bar sambil mengambil data
        progressBar.setVisibility(View.VISIBLE);

        // Thread untuk mengambil data merchandise dan total stamp dari Supabase
        new Thread(() -> {
            try {
                List<MerchandiseModel> list = SupabaseClient.fetchMerchandise();
                SessionManager sessionManager = new SessionManager(MerchandiseActivity.this);
                String userId = sessionManager.getUserId();
                int totalStamp = SupabaseClient.fetchTotalStamp(userId);

                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        adapter = new MerchandiseAdapter(MerchandiseActivity.this, list, totalStampTextView, totalStamp);
                        recyclerView.setAdapter(adapter);
                        totalStampTextView.setText("Total Stamp yang Bisa Ditukar: " + totalStamp);
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

        // Inisialisasi tombol kembali
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        // Listener untuk ikon MyMerchandise
        ImageView myMerchandiseIcon = findViewById(R.id.myMerchandiseIcon);
        myMerchandiseIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MerchandiseActivity.this, MyMerchandiseActivity.class);
            startActivity(intent);
        });
    }

    // Metode untuk mengatur navigasi
    private void setupBottomNavigation () {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_merchandise);

        Context context = this;

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(context, HomeActivity.class));
                } else if (id == R.id.nav_collection) {
                    startActivity(new Intent(context, CollectionSectionsActivity.class));
                } else if (id == R.id.nav_scan) {
                    startActivity(new Intent(context, ScanQRActivity.class));
                } else if (id == R.id.nav_merchandise) {
                    return true; // Sudah di halaman ini
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(context, ProfileActivity.class));
                }

                overridePendingTransition(0, 0);
                return true;
            }
        });
    }
}
