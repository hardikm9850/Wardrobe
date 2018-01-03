package com.wardrobe.contract;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;

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

        void changeFavouriteState(@DrawableRes int resourceId);

        void showPlaceholderForShirt(ImageModel placeholderModel);

        void showPlaceholderForPant(ImageModel placeholderModel);
    }

    interface WardrobePresenter {
        void onAddNewShirtClicked();

        void onAddNewPantClicked();

        void storeImages(ArrayList<Image> images, boolean isShirtSelected);

        void addToFavourites(ImageModel shirtModel, ImageModel pantModel);

        void onPageChanged(ImageModel shirtModel,ImageModel pantModel);
    }
}
