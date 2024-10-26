package com.example.pcstorage.webviews;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pcstorage.R;

public class linkedin_WebView extends AppCompatActivity {

    WebView web_view_linkedin;
    ProgressBar pg_bar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.webview_linkedin);

        web_view_linkedin=findViewById(R.id.web_view_linkedin);
        pg_bar=findViewById(R.id.pg_bar);

        web_view_linkedin.loadUrl("https://www.linkedin.com/in/ram-parshad-9054a7287/");
        web_view_linkedin.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pg_bar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pg_bar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (web_view_linkedin.canGoBack()){
            web_view_linkedin.goBack();
        }
        else {
            super.onBackPressed();
        }


    }
}