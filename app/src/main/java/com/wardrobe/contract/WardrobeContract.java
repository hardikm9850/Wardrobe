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
    enum ClothType {
        SHIRT(0), PANT(1);

        private final int cloth;

        ClothType(int _cloth) {
            this.cloth = _cloth;
        }
    }

    interface WardrobeView {
        void provideAppTour();

        void startGalleryChooser(ClothType clothType);

        void setupShirtView(ArrayList<ImageModel> imageModels);

        void setupPantView(ArrayList<ImageModel> imageModels);

        void changeFavouriteState(@DrawableRes int resourceId);

        void showPlaceholderForShirt(ImageModel placeholderModel);

        void showPlaceholderForPant(ImageModel placeholderModel);

        ArrayList<ImageModel> getPantAdapterList();

        ArrayList<ImageModel> getShirtAdapterList();

    }

    interface WardrobePresenter {
        void onAddNewShirtClicked();

        void onAddNewPantClicked();

        void storeImages(ArrayList<Image> images, ClothType clothType);

        void addToFavourites(ImageModel shirtModel, ImageModel pantModel);

        void onPageChanged(ImageModel shirtModel, ImageModel pantModel);

        void onShuffleClicked();

        void appTourComplete();
    }
}
