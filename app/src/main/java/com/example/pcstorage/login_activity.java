package com.example.pcstorage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import android.view.LayoutInflater;
import android.view.View;

public class login_activity extends AppCompatActivity {

    TextInputEditText edit_gmail_login, edit_Password_login;
    Button login_button;
    TextView signUp_text;
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(login_activity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        edit_Password_login = findViewById(R.id.edit_Password_login);
        edit_gmail_login = findViewById(R.id.edit_gmail_login);
        login_button = findViewById(R.id.login_button);
        signUp_text = findViewById(R.id.signUp_text);

        auth = FirebaseAuth.getInstance();

        signUp_text.setOnClickListener(v -> startActivity(new Intent(login_activity.this, signup_activity.class)));

        login_button.setOnClickListener(v -> {
            String gmail = edit_gmail_login.getText().toString();
            String Password = edit_Password_login.getText().toString();

            if (TextUtils.isEmpty(gmail) || TextUtils.isEmpty(Password)) {
                Toast.makeText(login_activity.this, "Fill properly", Toast.LENGTH_SHORT).show();
                return;
            } else if (Password.length() < 4) {
                Toast.makeText(login_activity.this, "Password must be greater than 4 characters", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Show progress dialog when starting the login process
                AlertDialog progressDialog = showProgressDialog();

                auth.signInWithEmailAndPassword(gmail, Password)
                        .addOnCompleteListener(this, task -> {
                            progressDialog.dismiss();                        // Dismiss progress dialog once task is complete
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                Toast.makeText(this, "User registered: ", Toast.LENGTH_SHORT).show();               // + user.getUid()     for print user id from he/she can register in our app


                                startActivity(new Intent(login_activity.this, MainActivity.class));
                                finish();
                            } else {
                                handleSignInError(task.getException());


                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });

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

    // Handle sign-in errors (wrong email/password, no internet, etc.)
    private void handleSignInError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "No user found with this email", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
