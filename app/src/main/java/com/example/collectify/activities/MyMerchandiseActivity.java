package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.collectify.R;
import com.example.collectify.adapter.MyMerchandiseAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.MyMerchandiseModel;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import java.io.IOException;
import java.util.List;

public class MyMerchandiseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyMerchandiseAdapter adapter;
    ProgressBar progressBar;
    TextView emptyStateTextView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_merchandise);

        recyclerView = findViewById(R.id.myMerchandiseRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            // Kembali ke MerchandiseActivity, bukan finish() agar state terjaga
            startActivity(new Intent(MyMerchandiseActivity.this, MerchandiseActivity.class));
            finish(); // Tutup activity ini
        });

        setupBottomNavigation();
        fetchData();
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                SessionManager sessionManager = new SessionManager(this);
                String userId = sessionManager.getUserId();
                List<MyMerchandiseModel> list = SupabaseClient.fetchMyMerchandise(userId);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (!isFinishing()) {
                        if (list.isEmpty()) {
                            emptyStateTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyStateTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter = new MyMerchandiseAdapter(this, list);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (!isFinishing()) progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Tetap tandai item merchandise sebagai aktif
        bottomNavigationView.setSelectedItemId(R.id.nav_merchandise);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Jika memilih item yang sama, jangan lakukan apa-apa
            if (id == R.id.nav_merchandise) {
                return true;
            }

            Intent intent = null;
            if (id == R.id.nav_home) {
                intent = new Intent(MyMerchandiseActivity.this, HomeActivity.class);
            } else if (id == R.id.nav_collection) {
                intent = new Intent(MyMerchandiseActivity.this, CollectionActivity.class);
            } else if (id == R.id.nav_scan) {
                intent = new Intent(MyMerchandiseActivity.this, ScanQRActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(MyMerchandiseActivity.this, ProfileActivity.class);
            }

            if(intent != null){
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            return true;
        });
    }

    // Handle back press agar kembali ke MerchandiseActivity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MyMerchandiseActivity.this, MerchandiseActivity.class));
        finish();
    }
}