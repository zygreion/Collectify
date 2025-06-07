package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectify.R;

public class MainActivity extends AppCompatActivity {

    private int waktu_loading = 3000; // 4000 = 4 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(home);
                finish();
            }
        }, waktu_loading);
    }
}