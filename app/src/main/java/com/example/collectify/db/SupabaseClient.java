package com.example.collectify.db;

import android.os.Build;

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
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }
        return response.toString();
    }

    public static JSONObject getUserData(String accessToken, String userId) throws IOException, JSONException {
        URL url = new URL(SUPABASE_URL + "/rest/v1/user?id=eq." + userId);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        String response = readResponse(conn);
        JSONArray jsonArray = new JSONArray(response);

        if (jsonArray.length() == 0) {
            throw new JSONException("User not found");
        }

        return jsonArray.getJSONObject(0);
    }

}
