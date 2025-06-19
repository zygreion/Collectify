package com.example.collectify.db;

import android.util.Log;

import com.example.collectify.model.CollectionModel;
import com.example.collectify.model.CollectionStampsModel;
import com.example.collectify.model.MerchandiseModel;
import com.example.collectify.model.MyMerchandiseModel;
import com.example.collectify.model.ScanQRResultModel;
import com.example.collectify.model.StampModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://ezokhygzfibymtuorwes.supabase.co"; // ganti
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV6b2toeWd6ZmlieW10dW9yd2VzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg2NzI0NDUsImV4cCI6MjA2NDI0ODQ0NX0.yke0cGdj2ZEzTRmCK1q5ojIY4h6fVsUHzVJvvI3LI7U"; // ganti

    public static String registerUser(String email, String password) throws IOException, JSONException {
        URL url = new URL(SUPABASE_URL + "/auth/v1/signup");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        String response = readResponse(conn);
        JSONObject json = new JSONObject(response);
        if (!json.has("user")) {
            throw new JSONException("No 'user' object in response: " + json.toString());
        }
        JSONObject userObj = json.getJSONObject("user");
        if (!userObj.has("id")) {
            throw new JSONException("No 'id' in user object: " + userObj.toString());
        }
        return userObj.getString("id");
    }

    public static void insertUserToTable(String id, String username, String fullName, String email) throws IOException {
        URL url = new URL(SUPABASE_URL + "/rest/v1/user");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Prefer", "return=minimal");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        try {
            body.put("id", id);
            body.put("username", username);
            body.put("full_name", fullName);
            body.put("email", email);
        } catch (JSONException e) {
            throw new IOException("Failed to build JSON payload", e);
        }

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code >= 400) {
            InputStream errorStream = conn.getErrorStream();
            String error = new BufferedReader(new InputStreamReader(errorStream)).lines().collect(Collectors.joining("\n"));
            throw new IOException("Failed to insert user: " + error);
        }
    }

    public static String loginUser(String email, String password) throws IOException {
        URL url = new URL(SUPABASE_URL + "/auth/v1/token?grant_type=password");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream is = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) ? conn.getInputStream() : conn.getErrorStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        }
    }

    public static JSONObject getUserData(String accessToken, String userId) throws IOException, JSONException {
        URL url = new URL(SUPABASE_URL + "/rest/v1/user?id=eq." + userId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
//        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        String response = readResponse(conn);
        JSONArray jsonArray = new JSONArray(response);
        if (jsonArray.length() == 0) {
            throw new JSONException("User not found");
        }
        return jsonArray.getJSONObject(0);
    }

    public static List<MerchandiseModel> fetchMerchandise() throws IOException, JSONException {
        List<MerchandiseModel> merchandiseList = new ArrayList<>();
        URL url = new URL(SUPABASE_URL + "/rest/v1/merchandise?select=*");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");

        String response = readResponse(conn);
        JSONArray jsonArray = new JSONArray(response);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String imageUrl = obj.getString("image_url");
            int stock = obj.getInt("stock");
            int requiredStamp = obj.getInt("required_stamp");
            merchandiseList.add(new MerchandiseModel(id, name, imageUrl, stock, requiredStamp));
        }
        return merchandiseList;
    }

    public static List<CollectionStampsModel> getCollectionsStampsFromUser(String userId) throws IOException, JSONException {
        URL url = new URL(SUPABASE_URL + "/rest/v1/rpc/get_collections_stamps_from_user");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("p_user_id", userId);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        Log.d("SupabaseClient", "Response code: " + responseCode);

        String response = readResponse(conn);
        Log.d("SupabaseClient", "Response body: " + response);

        List<CollectionStampsModel> collectionStampsList = new ArrayList<>();

        if (responseCode == 200) {
            JSONArray collectionsArray = new JSONArray(response);

            for (int i = 0; i < collectionsArray.length(); i++) {
                JSONObject collectionObject = collectionsArray.getJSONObject(i);

                CollectionModel collection = new CollectionModel(
                        collectionObject.getLong("id"),
                        collectionObject.getString("name"),
                        collectionObject.getString("image_url"),
                        collectionObject.getInt("total_stamps_collected"),
                        collectionObject.getInt("total_stamps")
                );

                List<StampModel> stampsInCollection = new ArrayList<>();
                JSONArray stampsArray = collectionObject.getJSONArray("stamps");
                for (int j = 0; j < stampsArray.length(); j++) {
                    JSONObject stampObject = stampsArray.getJSONObject(j);

                    // --- Pengecekan null dan parsing untuk scanned_at ---
                    OffsetDateTime scannedAt = null;
                    if (stampObject.has("scanned_at") && !stampObject.isNull("scanned_at")) {
                        try {
                            // Hanya parse jika bukan null di JSON
                            scannedAt = OffsetDateTime.parse(stampObject.getString("scanned_at"));
                        } catch (DateTimeParseException e) {
                            Log.e("SupabaseClient", "Error parsing scanned_at timestamp for stamp ID " + stampObject.optLong("id") + ": " + stampObject.optString("scanned_at"), e);
                            // Pertimbangkan bagaimana Anda ingin menangani error parsing (misalnya, biarkan null, atau default ke Instant.EPOCH)
                        }
                    }
                    // --- Akhir pengecekan null dan parsing ---

                    stampsInCollection.add(new StampModel(
                            stampObject.getLong("id"),
                            stampObject.getString("name"),
                            stampObject.getString("image_url"),
                            stampObject.getString("jam_operasional"),
                            stampObject.getString("harga_tiket"),
                            stampObject.getString("deskripsi"),
                            stampObject.getString("lokasi"),
                            stampObject.getBoolean("is_scanned"),
                            scannedAt
                    ));
                }

                collectionStampsList.add(new CollectionStampsModel(
                        collection,
                        stampsInCollection
                ));
            }
        }

        if (responseCode >= 400) {
            throw new IOException("Failed to fetch data: " + response);
        }

        return collectionStampsList;
    }

    public static List<StampModel> getAllStamps() throws IOException, JSONException {
        URL url = new URL(SUPABASE_URL + "/rest/v1/stamp?select=*");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        Log.d("SupabaseClient", "Response code: " + responseCode);

        String response = readResponse(conn);
        Log.d("SupabaseClient", "Response body: " + response);

        List<StampModel> stampList = new ArrayList<>();

        if (responseCode == 200) {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject stampObject = jsonArray.getJSONObject(i);

                stampList.add(new StampModel(
                        stampObject.getLong("id"),
                        stampObject.getString("name"),
                        stampObject.getString("image_url"),
                        stampObject.getString("qr_string"),
                        stampObject.getLong("collection_id"),
                        stampObject.getString("jam_operasional"),
                        stampObject.getString("harga_tiket"),
                        stampObject.getString("deskripsi"),
                        stampObject.getString("lokasi")
                ));
            }
        }

        if (responseCode >= 400) {
            throw new IOException("Failed to fetch data: " + response);
        }

        return stampList;
    }

    public static int fetchTotalStamp(String userId) throws IOException, JSONException {
//        String urlString = SUPABASE_URL + "/rest/v1/user_stamp?user_id=eq." + userId + "&select=id";
        String urlString = SUPABASE_URL + "/rest/v1/user_stamp?select=id&user_id=eq." + userId + "&is_exchanged=eq.false";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        String response = readResponse(conn);
        JSONArray jsonArray = new JSONArray(response);
        return jsonArray.length();
    }

    public static String exchangeMerchandise(String userId, int merchandiseId) throws IOException, JSONException {
//        URL url = new URL(SUPABASE_URL + "/rest/v1/rpc/exchange_merchandise");
        URL url = new URL(SUPABASE_URL + "/rest/v1/rpc/exchange_merchandise_5");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("p_user_id", userId);
        body.put("p_merchandise_id", merchandiseId);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 204) {
            return "success";
        } else {
            String errorResponse = readResponse(conn);
            return errorResponse;
        }
    }

    public static String exchangeMerchandise2(String userId, int merchandiseId) throws IOException, JSONException {
        URL url = new URL(SUPABASE_URL + "/rest/v1/rpc/exchange_merchandise_2");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("p_user_id", userId);
        body.put("p_merchandise_id", merchandiseId);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 204) {
            return "success";
        } else {
            String errorResponse = readResponse(conn);
            return errorResponse;
        }
    }

    public static List<MyMerchandiseModel> fetchMyMerchandise(String userId) throws IOException, JSONException {
        String requestUrl = SUPABASE_URL + "/rest/v1/user_merchandise?select=exchanged_at,merchandise(name,image_url)&user_id=eq." + userId + "&order=exchanged_at.desc";
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            String response = readResponse(conn);
            List<MyMerchandiseModel> myMerchandiseList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String exchangedAt = jsonObject.getString("exchanged_at");

                JSONObject merchandiseObject = jsonObject.getJSONObject("merchandise");
                String name = merchandiseObject.getString("name");
                String imageUrl = merchandiseObject.getString("image_url");

                myMerchandiseList.add(new MyMerchandiseModel(name, imageUrl, exchangedAt));
            }
            return myMerchandiseList;
        }
        return new ArrayList<>();
    }

    public static StampModel getStampByQrString(String qrString) throws IOException, JSONException {
        StampModel stamp = null;
        URL url = new URL(SUPABASE_URL + "/rest/v1/stamp?select=*&qr_string=eq." + qrString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        String response = readResponse(conn);
        JSONArray jsonArray = new JSONArray(response);

        // Stamp with given QR String is found
        if (jsonArray.length() == 1) {
            JSONObject obj = jsonArray.getJSONObject(0);

            stamp = new StampModel(
                    obj.getLong("id"),
                    obj.getString("name"),
                    obj.getString("image_url"),
                    obj.getString("jam_operasional"),
                    obj.getString("harga_tiket"),
                    obj.getString("deskripsi"),
                    obj.getString("lokasi")
            );

        } else {
            Log.e("SupabaseClient", "Stempel tidak valid!");
        }

        // If not, return model with id = -999 and name = "Unknown
        return stamp;
    }

    public static ScanQRResultModel scanQRCode(String userId, String qrString) throws IOException, JSONException {
        StampModel stamp = getStampByQrString(qrString);
        ScanQRResultModel scanQRResult = new ScanQRResultModel(0, stamp);
        // Jika Stamp tidak ditemukan, tidak bisa mencatat pemindaian
        if (stamp == null) {
            Log.d("SupabaseClient", "Stamp dengan QR String" + qrString + " tidak ditemukan!");
            scanQRResult.code = ScanQRResultModel.CODE_STAMP_NOT_FOUND;
            return scanQRResult;
        }

        scanQRResult.code = ScanQRResultModel.CODE_STAMP_FOUND;

        URL url = new URL(SUPABASE_URL + "/rest/v1/user_stamp");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        try {
            body.put("user_id", userId);
            body.put("stamp_id", stamp.id);
        } catch (JSONException e) {
            throw new IOException("Failed to build JSON payload", e);
        }

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code >= 200 && code < 400) {
            Log.i("SupabaseClient", "Berhasil scan QR String" + qrString + " oleh userId " + userId);
            scanQRResult.code = ScanQRResultModel.CODE_STAMP_SCAN_SUCCESS;
            stamp.isScanned = true;
            stamp.scannedAt = OffsetDateTime.now(ZoneOffset.ofHours(7));
        } else if (code >= 400) {
            InputStream errorStream = conn.getErrorStream();
            String error = new BufferedReader(new InputStreamReader(errorStream)).lines().collect(Collectors.joining("\n"));

            // Coba parse JSON dari error response
            try {
                JSONObject errorJson = new JSONObject(error);
                String errCode = errorJson.optString("code");
                String errMessage = errorJson.optString("message");
                String errDetails = errorJson.optString("details");

                Log.e("SupabaseClient", "Kode Error: " + errCode);
                Log.e("SupabaseClient", "Pesan Error: " + errMessage);
                Log.e("SupabaseClient", "Detail: " + errDetails);

                // Bisa juga disimpan di model kalau mau ditampilkan ke user
                scanQRResult.code = ScanQRResultModel.CODE_STAMP_ALREADY_SCANNED;
            } catch (JSONException e) {
                Log.e("SupabaseClient", "Gagal parse JSON error: " + error);
            }
        }

        return scanQRResult;
    }
}