package com.example.pcstorage.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcstorage.Modal.VideoModel;
import com.example.pcstorage.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoModel> videoList;
    private OnVideoActionListener videoActionListener;

    // Constructor that takes a list and a listener
    public VideoAdapter(List<VideoModel> videoList, OnVideoActionListener videoActionListener) {
        this.videoList = videoList;
        this.videoActionListener = videoActionListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.videoName.setText(video.getVideoName());

        // Set click listener for delete button
        holder.deleteBtn.setOnClickListener(v -> {
            videoActionListener.onDeleteVideo(video.getVideoName(), position);
        });

        // Set click listener for view button
        holder.ViewBtn.setOnClickListener(v -> {
            videoActionListener.onViewVideo(video.getVideoUrl());
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView videoName;
        ImageButton deleteBtn, ViewBtn;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.videoName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            ViewBtn = itemView.findViewById(R.id.ViewBtn);
        }
    }

    // Define an interface for actions
    public interface OnVideoActionListener {
        void onDeleteVideo(String videoName, int position);
        void onViewVideo(String videoUrl);
    }

    // Method to remove a video from the list
    public void removeVideo(int position) {
        videoList.remove(position);
        notifyItemRemoved(position);
    }
}
