package com.wardrobe.model;

/**
 * Created by hardik on 05/01/18.
 */

public class FavouriteModel {
    String shirtFilePath;
    String pantFilePath;

    public String getShirtFilePath() {
        return shirtFilePath;
    }

    public String getPantFilePath() {
        return pantFilePath;
    }

    public FavouriteModel(String shirtFilePath, String pantFilePath) {
        this.shirtFilePath = shirtFilePath;
        this.pantFilePath = pantFilePath;
    }
}
