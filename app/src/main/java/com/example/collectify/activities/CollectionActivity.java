package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectify.R;
import com.example.collectify.adapter.CollectionAdapter;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.CollectionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CollectionAdapter adapter;
    List<CollectionModel> CollectionModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        recyclerView = findViewById(R.id.recyclerViewCollection);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CollectionAdapter(this, CollectionModels);
        recyclerView.setAdapter(adapter);

        getAllCollections();
    }

    private void getAllCollections() {
        new Thread(() -> {
            try {
                JSONArray jsonArray = SupabaseClient.getAllCollections();
                CollectionModels.clear();

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

                    CollectionModels.add(item);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (IOException | JSONException e) {
                Log.e("CollectionActivity", "Error fetching collections", e);
            }

        }).start();
    }

    private String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
            sb.append(line);

        reader.close();
        return sb.toString();
    }
}
