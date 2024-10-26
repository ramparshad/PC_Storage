package com.example.pcstorage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pcstorage.fragments.Document_fragment;
import com.example.pcstorage.fragments.Image_fragment;
import com.example.pcstorage.fragments.Video_fragment;
import com.example.pcstorage.webviews.github_WebView;
import com.example.pcstorage.webviews.linkedin_WebView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView nav;
    private TextView internet_text;
    private BroadcastReceiver internetReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize BottomNavigationView and TextView
        nav = findViewById(R.id.nav);
        internet_text = findViewById(R.id.internet_text);
        internet_text.setVisibility(View.GONE);

        // Register BroadcastReceiver to listen for connectivity changes
        internetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!isInternetAvailable()) {
                    internet_text.setVisibility(View.VISIBLE);
                } else {
                    internet_text.setVisibility(View.GONE);
                }
            }
        };
        registerReceiver(internetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Set up BottomNavigationView listener
        nav.setOnNavigationItemSelectedListener(listener);

        // Load the default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Image_fragment()).commit();

        // Show the status bar with the specified color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.sky));
        }
    }

    // Method to check if the internet is available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    // Listener for BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener listener = item -> {
        Fragment selected_Fragment = null;
        int id = item.getItemId();

        if (id == R.id.image_nav) {
            selected_Fragment = new Image_fragment();
        } else if (id == R.id.video_nav) {
            selected_Fragment = new Video_fragment();
        } else if (id == R.id.doc_nav) {
            selected_Fragment = new Document_fragment();
        }

        // Replace the current fragment with the selected one
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selected_Fragment).commit();
        return true;
    };

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.visit_github) {
            Intent intent1 = new Intent(this, github_WebView.class);
            startActivity(intent1);
        } else if (id == R.id.visit_linkedin) {
            Intent intent2 = new Intent(this, linkedin_WebView.class);
            startActivity(intent2);
        } else if (id == R.id.share_app) {
            String shareText = "Check out this amazing app: [PC Storage]!\nDownload it from: https://drive.google.com/file/d/1kGQgVSSOtE6kljxORR38zddacnu8CQEj/view?usp=drive_link";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share app via"));
        } else if (id == R.id.upload_nav) {
            Intent intent4 = new Intent(MainActivity.this, Upload_activity.class);
            startActivity(intent4);
        } else if (id == R.id.logout) {
            Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
            dialog.setContentView(R.layout.dialog_layout);
            Button yes_btn = dialog.findViewById(R.id.yes_btn);
            Button no_btn = dialog.findViewById(R.id.no_btn);

            yes_btn.setOnClickListener(v1 -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Splash_Activity.class));
                finish();
            });
            no_btn.setOnClickListener(v12 -> dialog.dismiss());
            dialog.show();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to avoid memory leaks
        if (internetReceiver != null) {
            unregisterReceiver(internetReceiver);
        }
    }
}
