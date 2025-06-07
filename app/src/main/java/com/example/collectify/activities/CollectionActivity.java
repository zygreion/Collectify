package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        recyclerView = findViewById(R.id.recyclerViewCollection);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CollectionAdapter(this, collectionModels);
        recyclerView.setAdapter(adapter);

        setupBottomNavigation();

        getAllCollections();
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set selected menu item sesuai halaman ini
        bottomNavigationView.setSelectedItemId(R.id.nav_collection);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_collection) {
                    // Sudah di CollectionActivity, tidak perlu aksi
                    return true;
                } else if (id == R.id.nav_scan) {
                    // Buka ScanQRActivity
                    startActivity(new Intent(CollectionActivity.this, ScanQRActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(CollectionActivity.this, HomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_merchandise) {
                    startActivity(new Intent(CollectionActivity.this, MerchandiseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(CollectionActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
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
