package com.example.baseproject.model;

public class MyPhotoModel {
    String photoPath;


    public MyPhotoModel(String photoPath, boolean isSelected) {
        this.photoPath = photoPath;

    }


    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }


}
