package com.example.pcstorage.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcstorage.Adapter.ImageAdapter;
import com.example.pcstorage.Modal.ImageModel;
import com.example.pcstorage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Image_fragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private ImageAdapter imageAdapter;
    private List<ImageModel> imageList;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), imageList);
        recyclerView.setAdapter(imageAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            fetchImages();
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void fetchImages() {
        String userId = currentUser.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Data/" + userId + "/images/");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            imageList.clear(); // Clear the list before adding new items
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageList.add(new ImageModel(uri.toString()));
                    updateUI();
                });
            }
            // Call updateUI in case no items were added in the loop
            updateUI();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load images", Toast.LENGTH_SHORT).show();
            updateUI();
        });
    }

    private void updateUI() {
        if (imageList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted. Click download again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot download.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
