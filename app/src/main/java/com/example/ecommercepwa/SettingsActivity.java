package com.example.ecommercepwa;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Set up action bar with back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }
        
        // Initialize UI components
        urlEditText = findViewById(R.id.urlEditText);
        Button saveButton = findViewById(R.id.saveButton);
        
        // Load current URL from preferences
        String currentUrl = ConfigManager.getWebUrl(this);
        urlEditText.setText(currentUrl);
        
        // Set save button click listener
        saveButton.setOnClickListener(v -> saveSettings());
    }
    
    private void saveSettings() {
        String newUrl = urlEditText.getText().toString().trim();
        
        // Validate URL (basic validation)
        if (newUrl.isEmpty()) {
            urlEditText.setError("URL cannot be empty");
            return;
        }
        
        // Add http:// if protocol is missing
        if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
            newUrl = "https://" + newUrl;
            urlEditText.setText(newUrl);
        }
        
        // Save URL to preferences
        ConfigManager.saveWebUrl(this, newUrl);
        
        Toast.makeText(this, "Settings saved. Restart the app to apply changes.", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 