package com.example.pcstorage.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pcstorage.Modal.ImageModel;
import com.example.pcstorage.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    protected Context context;
    private List<ImageModel> imageList;

    public ImageAdapter(Context context, List<ImageModel> imageList) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel imageModel = imageList.get(position);

        // Load the image using the URL stored in ImageModel
        Glide.with(holder.itemView.getContext())
                .load(imageModel.getImageUrl()) // Use the URL from ImageModel
                .into(holder.imageView);

        // Delete button action
        holder.deleteBtn.setOnClickListener(v -> {
                    deleteImage(imageModel, position);

                });
            holder.downloadBtn.setOnClickListener(v -> {
                // Check if WRITE_EXTERNAL_STORAGE permission is granted
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the WRITE_EXTERNAL_STORAGE permission
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1001
                    );
                } else {
                    // Permission is granted, proceed with the download
                    downloadImage(imageModel.getImageUrl(), imageModel.getUrl());
                }
            });
        }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteBtn, downloadBtn;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
        }
    }

    // Method to delete the image
    private void deleteImage(ImageModel imageModel, int position) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageModel.getImageUrl());

        imageRef.delete().addOnSuccessListener(aVoid -> {
            imageList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show();
        });
    }

    private void downloadImage(String imageUrl, String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            imageName = "downloaded_image_" + System.currentTimeMillis() + ".jpg";
        }

        Log.d("Download", "URL: " + imageUrl);
        Log.d("Download", "File Name: " + imageName);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setTitle("Downloading " + imageName);
        request.setDescription("Downloading image...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Check Android version and set destination accordingly
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Scoped storage for Android 10+
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, imageName);
        } else {
            // For Android 9 and below
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, imageName);
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
        }
    }


}




/*
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<ImageModel> imageList;

    public ImageAdapter(List<ImageModel> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageList.get(position).getImageUrl();
        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }*/
