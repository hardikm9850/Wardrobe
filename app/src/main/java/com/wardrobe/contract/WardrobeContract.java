package com.wardrobe.contract;

import com.darsh.multipleimageselect.models.Image;
import com.wardrobe.model.ImageModel;

import java.util.ArrayList;

/**
 * Created by hardik on 03/01/18.
 */

public interface WardrobeContract {
    interface WardrobeView {
        void startGalleryChooser(boolean isShirtSelected);

        void setupShirtView(ArrayList<ImageModel> imageModels);

        void setupPantView(ArrayList<ImageModel> imageModels);
    }

    interface WardrobePresenter {
        void onAddNewShirtClicked();

        void onAddNewPantClicked();

        void storeImages(ArrayList<Image> images, boolean isShirtSelected);

        void storeFavouriteItem(ImageModel shirtModel, ImageModel pantModel);
    }
}
