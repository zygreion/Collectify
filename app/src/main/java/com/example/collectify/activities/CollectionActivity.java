package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.R;
import com.example.collectify.adapter.CollectionAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.CollectionModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CollectionAdapter adapter;
    List<CollectionModel> collectionModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection);

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

        recyclerView = findViewById(R.id.recyclerViewCollection);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CollectionAdapter(this, collectionModels);
        recyclerView.setAdapter(adapter);

        setupBottomNavigation();

        getAllCollections();
    }

    // Metode untuk mengatur navigasi
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_collection);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(CollectionActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_collection) {
                    return true; // Sudah di halaman ini
                } else if (id == R.id.nav_scan) {
                    startActivity(new Intent(CollectionActivity.this, ScanQRActivity.class));
                } else if (id == R.id.nav_merchandise) {
                    startActivity(new Intent(CollectionActivity.this, MerchandiseActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(CollectionActivity.this, ProfileActivity.class));
                }

                overridePendingTransition(0, 0);
                return true;
            }
        });
    }

    private void getAllCollections() {
        new Thread(() -> {
            try {
                JSONArray jsonArray = SupabaseClient.getAllCollections();
                collectionModels.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    CollectionModel item = new CollectionModel();
                    item.id = obj.getInt("id");
                    item.name = obj.getString("name");
                    item.image_url = obj.getString("image_url");
                    item.qr_string = obj.getString("qr_string");
                    item.jam_operasional = obj.getString("jam_operasional");
                    item.harga_tiket = obj.getString("harga_tiket");
                    item.deskripsi = obj.getString("deskripsi");
                    item.sudah_dikoleksi = obj.optInt("sudah_dikoleksi", 0);
                    item.lokasi = obj.getString("lokasi");

                    collectionModels.add(item);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (IOException | JSONException e) {
                Log.e("CollectionActivity", "Error fetching collections", e);
            }
        }).start();
    }
}
