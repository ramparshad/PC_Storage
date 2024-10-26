package com.example.pcstorage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcstorage.Adapter.VideoAdapter;
import com.example.pcstorage.Modal.VideoModel;
import com.example.pcstorage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Video_fragment extends Fragment implements VideoAdapter.OnVideoActionListener {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoModel> videoList;
    private FirebaseAuth auth;
    private TextView empty_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(videoAdapter);

        auth = FirebaseAuth.getInstance();
        empty_view = view.findViewById(R.id.empty_view);

        fetchVideos();

        return view;
    }

    private void fetchVideos() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Please login to view your videos", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Data/" + userId + "/videos/");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            videoList.clear();  // Clear the list before adding new items
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fileName = item.getName();
                    String downloadUrl = uri.toString();
                    String storagePath = item.getPath(); // Store the path for deletion
                    videoList.add(new VideoModel(fileName, downloadUrl, storagePath));
                    videoAdapter.notifyDataSetChanged();
                    updateUI();
                });
            }

            // Update the UI in case no videos were found
            if (listResult.getItems().isEmpty()) {
                updateUI();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load videos", Toast.LENGTH_SHORT).show();
            updateUI();
        });
    }

    @Override
    public void onDeleteVideo(String videoName, int position) {
        VideoModel video = videoList.get(position);
        String storagePath = video.getStoragePath();
        StorageReference videoRef = FirebaseStorage.getInstance().getReference(storagePath);

        videoRef.delete().addOnSuccessListener(aVoid -> {
            videoList.remove(position);
            videoAdapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Video deleted", Toast.LENGTH_SHORT).show();
            updateUI();  // Update the UI after deletion
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to delete video", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onViewVideo(String videoUrl) {
        // Handle viewing the video (e.g., using an Intent to open the video URL)
    }

    private void updateUI() {
        if (videoList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }
    }
}
