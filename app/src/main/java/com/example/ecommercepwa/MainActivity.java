package com.example.ecommercepwa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View noInternetLayout;
    private Button retryButton;
    private String webUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Get web URL from configuration
        webUrl = ConfigManager.getWebUrl(this);
        
        // Initialize UI components
        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        noInternetLayout = findViewById(R.id.no_internet_layout);
        retryButton = findViewById(R.id.retryButton);
        
        // Set up retry button
        retryButton.setOnClickListener(v -> loadWebView());
        
        // Set up pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadWebView();
            // Hide the refresh indicator after a short delay
            swipeRefreshLayout.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });
        
        // Set up WebView
        setupWebView();
        
        // Load the web page
        loadWebView();
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        
        // Enable DOM storage
        webSettings.setDomStorageEnabled(true);
        
        // Enable database storage
        webSettings.setDatabaseEnabled(true);
        
        // Set cache mode - use cache when available, else load from network
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Enable Geolocation
        webSettings.setGeolocationEnabled(true);
        
        // Enable PWA features by setting ServiceWorkerClient
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ServiceWorkerController swController = ServiceWorkerController.getInstance();
            swController.setServiceWorkerClient(new ServiceWorkerClient() {
                @Override
                public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
                    return null; // Allow all resource requests by the Service Worker
                }
            });
        }
        
        // Handle navigation within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Keep all navigation within the WebView
                view.loadUrl(url);
                return true;
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide refresh indicator when page finishes loading
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    
    private void loadWebView() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            // Show WebView, hide no internet layout
            webView.setVisibility(View.VISIBLE);
            noInternetLayout.setVisibility(View.GONE);
            
            // Load the URL
            webView.loadUrl(webUrl);
        } else {
            // Show no internet layout, hide WebView
            webView.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
            
            // Hide refresh indicator
            swipeRefreshLayout.setRefreshing(false);
            
            // Show toast message
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            // Navigate to settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        // Handle back button for WebView navigation
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
} 