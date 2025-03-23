package com.example.ecommercepwa;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static final String PREF_NAME = "ECommercePWASettings";
    private static final String KEY_WEB_URL = "webUrl";
    private static final String DEFAULT_URL = "https://example.com";

    // Load the web URL from shared preferences or config file
    public static String getWebUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // If URL exists in preferences, use it
        if (prefs.contains(KEY_WEB_URL)) {
            return prefs.getString(KEY_WEB_URL, DEFAULT_URL);
        }
        
        // Otherwise, load from assets config file
        return loadUrlFromConfig(context);
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
    
    // Load URL from assets config.json
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