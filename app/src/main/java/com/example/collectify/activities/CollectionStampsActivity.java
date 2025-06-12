package com.example.collectify.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collectify.adapter.CollectionSectionAdapter;
import com.example.collectify.adapter.StampAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.CollectionModel;
import com.example.collectify.model.CollectionStampsModel;
import com.example.collectify.model.StampModel;
import com.example.collectify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.collectify.R; // Pastikan ini mengarah ke R Anda
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CollectionStampsActivity extends AppCompatActivity {

    private TextView tvCollectionName, tvScanned;
    private ImageView imgCollection;
    private RecyclerView rvStamp;
    private StampAdapter adapter;
    private CollectionStampsModel collectionStamps;
    private CollectionModel collection;
    private List<StampModel> stampList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_stamps);

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
                    originalBottom + systemBars.bottom
            );
            return insets;
        });

        imgCollection = findViewById(R.id.img_collection);
        tvCollectionName = findViewById(R.id.tv_collection_name);
        tvScanned = findViewById(R.id.tv_scanned);
        rvStamp = findViewById(R.id.rv_stamp);

        collectionStamps = (CollectionStampsModel) getIntent().getSerializableExtra("collectionStamps");

        if (collectionStamps != null) {
            collection = collectionStamps.collection;
            stampList = collectionStamps.stampList;

            Glide.with(this)
                    .load(collection.imageUrl)
                    .placeholder(R.drawable.loading)
                    .into(imgCollection);
            tvCollectionName.setText(collectionStamps.collection.name);
            tvScanned.setText("Collected (" + collectionStamps.collection.totalStampsCollected + "/" + collectionStamps.collection.totalStamps + ")");

            // --- Setup RecyclerView Utama ---
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        // LayoutManager untuk RecyclerView utama (vertikal)
        rvStamp.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new StampAdapter(this, stampList);
        rvStamp.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}