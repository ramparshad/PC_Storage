package com.example.pcstorage.Modal;

public class VideoModel {
    private String videoName;
    private String videoUrl;
    private String storagePath; // Store the path for deletion

    public VideoModel(String videoName, String videoUrl, String storagePath) {
        this.videoName = videoName;
        this.videoUrl = videoUrl;
        this.storagePath = storagePath;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getStoragePath() {
        return storagePath;
    }
}
