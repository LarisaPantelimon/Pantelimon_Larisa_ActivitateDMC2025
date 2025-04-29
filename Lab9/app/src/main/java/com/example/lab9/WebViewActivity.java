package com.example.lab9;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());

        // Activare JavaScript și alte setări
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // opțional, dar util

        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }
}
