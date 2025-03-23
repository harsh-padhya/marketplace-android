package com.example.ecommercepwa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static final String PREF_NAME = "ECommercePWASettings";
    private static final String KEY_WEB_URL = "webUrl";
    private static final String DEFAULT_URL = "https://example.com";
    private static final String CONFIG_URL = "https://raw.githubusercontent.com/harsh-padhya/config-server/main/config.json";
    private static final String PREF_CONFIG_LAST_UPDATED = "configLastUpdated";
    private static final long CONFIG_UPDATE_INTERVAL = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

    // Load the web URL from shared preferences or config file
    public static String getWebUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // If URL exists in preferences, use it
        if (prefs.contains(KEY_WEB_URL)) {
            return prefs.getString(KEY_WEB_URL, DEFAULT_URL);
        }
        
        // Otherwise, load from assets config file as fallback
        return loadUrlFromConfig(context);
    }
    
    // Check if we should update config from remote
    public static void checkForConfigUpdate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long lastUpdated = prefs.getLong(PREF_CONFIG_LAST_UPDATED, 0);
        long currentTime = System.currentTimeMillis();
        
        // If config has never been fetched or it's older than the update interval
        if (lastUpdated == 0 || (currentTime - lastUpdated) > CONFIG_UPDATE_INTERVAL) {
            fetchRemoteConfig(context);
        }
    }
    
    // Fetch config from remote URL
    private static void fetchRemoteConfig(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(CONFIG_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    
                    final String jsonString = stringBuilder.toString();
                    
                    handler.post(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            String webUrl = jsonObject.optString(KEY_WEB_URL, null);
                            
                            if (webUrl != null && !webUrl.isEmpty()) {
                                // Save the new URL to preferences
                                SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                prefs.edit()
                                    .putString(KEY_WEB_URL, webUrl)
                                    .putLong(PREF_CONFIG_LAST_UPDATED, System.currentTimeMillis())
                                    .apply();
                                
                                Log.d(TAG, "Successfully updated config from remote URL: " + webUrl);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing remote config JSON", e);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching remote config", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
        
        executor.shutdown();
    }
    
    // Save the web URL to shared preferences
    public static void saveWebUrl(Context context, String url) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_WEB_URL, url).apply();
    }
    
    // Reset to default URL from config
    public static void resetToDefaultUrl(Context context) {
        String defaultUrl = loadUrlFromConfig(context);
        saveWebUrl(context, defaultUrl);
    }
    
    // Load URL from assets config.json as fallback
    private static String loadUrlFromConfig(Context context) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open("config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
            
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.optString(KEY_WEB_URL, DEFAULT_URL);
            
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading config file", e);
            return DEFAULT_URL;
        }
    }
} 