package com.example.collectify.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.R;
import com.example.collectify.adapter.MerchandiseAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.MerchandiseModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MerchandiseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MerchandiseAdapter adapter;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise);

        recyclerView = findViewById(R.id.merchandiseRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setVisibility(View.VISIBLE);

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
    }
}