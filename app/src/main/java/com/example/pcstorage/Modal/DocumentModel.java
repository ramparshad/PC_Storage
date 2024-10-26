package com.example.pcstorage.Modal;

public class DocumentModel {
        private String name;
        private String url;

        public DocumentModel() {
            // Empty constructor for Firebase
        }

        public DocumentModel(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
    
