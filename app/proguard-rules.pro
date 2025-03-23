# Add project specific ProGuard rules here.

# Keep the WebView JavaScript interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep ConfigManager class as it's used for dynamic configuration
-keep class com.example.ecommercepwa.ConfigManager { *; }

# Keep NetworkUtils class
-keep class com.example.ecommercepwa.NetworkUtils { *; }

# General Android rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# For native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep R classes - they are required for proper resource access
-keep class **.R$* {
    *;
}

# WebView rules
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-keep public class android.webkit.WebView
-keep public class android.webkit.WebResourceRequest

# Keep cookies and session management classes
-keep class android.webkit.CookieManager { *; }
-keep class android.webkit.CookieSyncManager { *; }

# Keep JSON classes which are used for config parsing
-keep class org.json.** { *; } 