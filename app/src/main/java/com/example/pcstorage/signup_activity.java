package com.example.pcstorage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class signup_activity extends AppCompatActivity {

    TextInputEditText edit_gmail, edit_Password,name_cloud;
    Button register_btn;
    ProgressBar progressBar2;
    private FirebaseAuth auth;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        edit_gmail = findViewById(R.id.edit_gmail);
        edit_Password = findViewById(R.id.edit_Password);
        register_btn = findViewById(R.id.register_btn);
        name_cloud = findViewById(R.id.name_cloud);
        progressBar2 = findViewById(R.id.progressBar2);

        auth = FirebaseAuth.getInstance();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = edit_gmail.getText().toString();
                String password = edit_Password.getText().toString();
                String name = name_cloud.getText().toString();

                if (TextUtils.isEmpty(name)|| TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(signup_activity.this, "Fill all fields properly", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog dialog = showProgressDialog();
                    dialog.show();
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Intent intent = new Intent(signup_activity.this, MainActivity.class);
                            startActivity(intent);
                            dialog.dismiss();

                            finish();
                        } else {
                            Toast.makeText(signup_activity.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private AlertDialog showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }
}