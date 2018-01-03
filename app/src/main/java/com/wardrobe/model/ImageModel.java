package com.wardrobe.model;

/**
 * Created by hardik on 03/01/18.
 */

public class ImageModel {
    int imageId;
    String imagePath;

    public ImageModel(int imageId, String imagePath) {
        this.imageId = imageId;
        this.imagePath = imagePath;
    }

    public int getImageId() {
        return imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

}
