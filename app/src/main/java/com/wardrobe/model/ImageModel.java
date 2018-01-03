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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageModel that = (ImageModel) o;

        if (imageId != that.imageId) return false;
        return imagePath.equals(that.imagePath);
    }

    @Override
    public int hashCode() {
        int result = imageId;
        result = 31 * result + imagePath.hashCode();
        return result;
    }
}
