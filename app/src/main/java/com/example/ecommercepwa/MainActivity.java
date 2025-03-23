package com.example.ecommercepwa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View noInternetLayout;
    private Button retryButton;
    private String webUrl;
    
    private ValueCallback<Uri[]> filePathCallback;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Check for config updates from remote server
        ConfigManager.checkForConfigUpdate(this);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Hide the toolbar as per requirement
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
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
        
        // Enable DOM storage for session persistence
        webSettings.setDomStorageEnabled(true);
        
        // Enable database storage
        webSettings.setDatabaseEnabled(true);
        
        // Configure cache settings for better performance
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Set cache directory and enable caching
        File cachePath = new File(getApplicationContext().getCacheDir(), "webviewcache");
        if (!cachePath.exists()) {
            cachePath.mkdirs();
        }
        
        // Enable offline caching of resources
        webSettings.setAllowFileAccess(true);
        
        // Enable cookies and session persistence
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        
        // Enable Geolocation
        webSettings.setGeolocationEnabled(true);
        
        // Enable file access for media uploads
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        
        // Enable better caching for static assets
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        
        // Handle file uploads
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                MainActivity.this.filePathCallback = filePathCallback;
                
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
                } catch (Exception e) {
                    filePathCallback.onReceiveValue(null);
                    return false;
                }
                return true;
            }
        });
        
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
                
                // Save cookies for session persistence
                CookieManager.getInstance().flush();
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
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (filePathCallback != null) {
                Uri[] results = null;
                
                // Check if response is positive
                if (resultCode == RESULT_OK && data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    } else if (data.getClipData() != null) {
                        // Handle multiple files selection
                        int count = data.getClipData().getItemCount();
                        results = new Uri[count];
                        for (int i = 0; i < count; i++) {
                            results[i] = data.getClipData().getItemAt(i).getUri();
                        }
                    }
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
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
    
    @Override
    protected void onPause() {
        super.onPause();
        // Ensure cookies and session data are saved when app is paused
        CookieManager.getInstance().flush();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Check if the URL has been updated during app pause
        String updatedUrl = ConfigManager.getWebUrl(this);
        if (!webUrl.equals(updatedUrl)) {
            webUrl = updatedUrl;
            loadWebView();
        }
    }
} 