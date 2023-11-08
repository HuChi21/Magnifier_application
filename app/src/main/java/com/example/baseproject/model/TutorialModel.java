package com.example.baseproject.model;

public class TutorialModel {
    private int img;
    private int content;
    public TutorialModel(int img, int content) {
        this.img = img;
        this.content = content;
    }
    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }
}
