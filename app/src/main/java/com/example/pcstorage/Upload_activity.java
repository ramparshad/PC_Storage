package com.example.pcstorage;

import android.annotation.SuppressLint;
import android.app.StatusBarManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Upload_activity extends AppCompatActivity {

    private AlertDialog progressDialog;

    private CardView cardViewImg, cardViewDoc, card_video;
    private static final int PICK_IMAGE_REQUEST = 1000;
    private static final int PICK_DOCUMENT_REQUEST = 1001;
    private static final int PICK_VIDEO_REQUEST = 1002;

    private ImageView img_card, img_doc, img_video;
    private Uri imageUri, documentUri, videoUri;
    private Button upload_image_btn, upload_doc_btn, upload_Video_btn;
    private StorageReference storageRef;
    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload);

        if(getSupportActionBar() != null){
          getSupportActionBar().setTitle("Upload Data");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        img_card = findViewById(R.id.img_card);
        img_doc = findViewById(R.id.img_doc);
        img_video = findViewById(R.id.img_video);

        cardViewImg = findViewById(R.id.cardViewImg);
        cardViewDoc = findViewById(R.id.cardViewDoc);
        card_video = findViewById(R.id.card_video);

        upload_image_btn = findViewById(R.id.upload_image_btn);
        upload_doc_btn = findViewById(R.id.upload_doc_btn);
        upload_Video_btn = findViewById(R.id.upload_Video_btn);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            storageRef = FirebaseStorage.getInstance().getReference("Data").child(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        cardViewImg.setOnClickListener(v -> pickImage());
        cardViewDoc.setOnClickListener(v -> pickDocument());
        card_video.setOnClickListener(v -> pickVideo());

        upload_image_btn.setOnClickListener(v -> uploadingImage());
        upload_doc_btn.setOnClickListener(v -> uploadingDocument());
        upload_Video_btn.setOnClickListener(v -> uploadingVideo());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.sky));
        }

    }

    // for back button in Action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Progress dialog method
    @SuppressLint("InflateParams")
    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.progress_dialog, null);
            builder.setView(dialogView);
            builder.setCancelable(false); // Disable dismissing by back button
            progressDialog = builder.create();
        }
        progressDialog.show();
    }

    private void uploadingVideo() {
        if (videoUri != null) {
            showProgressDialog("Uploading Video...");
            uploadFileToFirebase("videos/", videoUri);
        } else {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadingDocument() {
        if (documentUri != null) {
            showProgressDialog("Uploading Document...");
            uploadFileToFirebase("documents/", documentUri);
        } else {
            Toast.makeText(this, "No document selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadingImage() {
        if (imageUri != null) {
            showProgressDialog("Uploading Image...");
            uploadFileToFirebase("images/", imageUri);
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Pick image from gallery
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Pick document from file manager
    private void pickDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
    }

    // Pick video from gallery
    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();

            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = fileUri;
                upload_image_btn.setVisibility(View.VISIBLE);

            } else if (requestCode == PICK_DOCUMENT_REQUEST) {
                documentUri = fileUri;
                upload_doc_btn.setVisibility(View.VISIBLE);

            } else if (requestCode == PICK_VIDEO_REQUEST) {
                videoUri = fileUri;
                upload_Video_btn.setVisibility(View.VISIBLE);
            }
        }
    }

    // Upload file to Firebase Storage with user's UID
    private void uploadFileToFirebase(String folderPath, Uri fileUri) {
        StorageReference fileRef = storageRef.child(folderPath + System.currentTimeMillis());

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Toast.makeText(Upload_activity.this, "File uploaded", Toast.LENGTH_SHORT).show();

                    // Hide the upload buttons after success
                    upload_image_btn.setVisibility(View.GONE);
                    upload_doc_btn.setVisibility(View.GONE);
                    upload_Video_btn.setVisibility(View.GONE);

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(Upload_activity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                });
    }
}
