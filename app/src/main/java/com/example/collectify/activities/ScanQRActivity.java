package com.example.collectify.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast; // Untuk notifikasi singkat

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.collectify.R;
import com.example.collectify.db.SupabaseClient;
import com.example.collectify.model.ScanQRResultModel;
import com.example.collectify.utils.SessionManager;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeResult; // Import BarcodeResult
import com.journeyapps.barcodescanner.BarcodeCallback; // Import BarcodeCallback
import com.journeyapps.barcodescanner.ViewfinderView;

import org.json.JSONException;

import java.io.IOException;

/**
 * Custom Scannner Activity extending from Activity to display a custom layout form scanner view.
 */
public class ScanQRActivity extends AppCompatActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton btnScanQR;
    private ViewfinderView viewfinderView;

    private boolean isScanning = false;

    // Implementasi BarcodeCallback
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (isScanning) return;

            String qrString = result.getText();
            if (qrString == null || qrString.isEmpty()) return;

            isScanning = true;

            new Thread(() -> {
                try {
                    SessionManager sessionManager = new SessionManager(ScanQRActivity.this);
                    String userId = sessionManager.getUserId();

                    ScanQRResultModel scanQRResult = SupabaseClient.scanQRCode(userId, qrString);
                    String msg = scanQRResult.getMessage();

                    runOnUiThread(() -> {
                        Toast.makeText(ScanQRActivity.this, msg, Toast.LENGTH_SHORT).show();
                        isScanning = false;

                        if (scanQRResult.code == ScanQRResultModel.CODE_STAMP_SCAN_SUCCESS) {
                            Intent intent = new Intent(ScanQRActivity.this, CollectionActivity.class);
                            // FLAG_ACTIVITY_NEW_TASK: Memulai Activity di task baru (jika tidak ada)
                            // FLAG_ACTIVITY_CLEAR_TASK: Menghapus semua Activity yang ada di task target
                            // Kombinasi keduanya akan menghapus back stack hingga CollectionActivity menjadi satu-satunya Activity di task baru
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (IOException | JSONException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ScanQRActivity.this, "Terjadi kesalahan saat memindai", Toast.LENGTH_SHORT).show();
                        isScanning = false;
                    });
                }
            }).start();
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_qr);

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

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        btnScanQR = findViewById(R.id.btn_scan_qr);
        viewfinderView = findViewById(R.id.zxing_viewfinder_view);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        // Penting: Set callback ke barcodeScannerView Anda
//        barcodeScannerView.decodeContinuous(callback); // Ini akan terus-menerus scan dan mengirim hasil ke callback

        // Komentar out atau hapus baris ini, karena kita akan menangani hasil melalui callback
        // capture.decode(); // Ini hanya memicu satu scan dan hasilnya akan dikirim ke CaptureManager internal, bukan langsung ke kita.

        btnScanQR.setOnClickListener(v -> {
            if (isScanning) return;
            barcodeScannerView.decodeSingle(callback);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
        // Penting: Pastikan scanner di-resume saat Activity kembali aktif
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
        // Penting: Pause scanner saat Activity tidak aktif untuk menghemat baterai
        barcodeScannerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Jangan lupakan panggilan super!
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}