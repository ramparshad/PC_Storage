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

public class github_WebView extends AppCompatActivity {

    WebView web_view_github;
    ProgressBar pg_bar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.webview_github);

        web_view_github=findViewById(R.id.web_view_github);
        pg_bar=findViewById(R.id.pg_bar);

        web_view_github.loadUrl("https://github.com/ramparshad");
        web_view_github.setWebViewClient(new WebViewClient() {
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
        if (web_view_github.canGoBack()){
            web_view_github.goBack();
        }
        else {
            super.onBackPressed();
        }


    }
}