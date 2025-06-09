package com.example.collectify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.collectify.R;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.utils.SessionManager;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText emailInput, passwordInput;
    Button loginButton;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        TextView tvRegister = findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });


        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    String response = SupabaseClient.loginUser(email, password);

                    JSONObject resJson = new JSONObject(response);
                    if (resJson.has("access_token") && resJson.has("user")) {

                        String accessToken = resJson.getString("access_token");
                        JSONObject user = resJson.getJSONObject("user");
                        String userId = user.getString("id");

                        // Simpan session
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.saveSession(accessToken, userId);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        });
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Login gagal: " + resJson.toString(), Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}