package com.example.pcstorage.Modal;

public class ImageModel {

    private String imageUrl;
    private String url;

    public ImageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageModel.class)
    }

    public ImageModel(String imageUrl) {
        this.imageUrl = imageUrl;
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getUrl() {
        return url;
    }
}
