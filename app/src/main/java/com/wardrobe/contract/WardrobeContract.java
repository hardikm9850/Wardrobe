package com.wardrobe.contract;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;

import com.darsh.multipleimageselect.models.Image;
import com.wardrobe.database.WardrobeTable;
import com.wardrobe.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

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

        void setupShirtView(List<WardrobeTable> wardrobeModels);

        void setupPantView(List<WardrobeTable> wardrobeModels);

        void changeFavouriteState(@DrawableRes int resourceId);

        void showPlaceholderForShirt(WardrobeTable placeholderModel);

        void showPlaceholderForPant(WardrobeTable placeholderModel);

        List<WardrobeTable> getPantAdapterList();

        List<WardrobeTable> getShirtAdapterList();

    }

    interface WardrobePresenter {
        void onAddNewShirtClicked();

        void onAddNewPantClicked();

        void storeImages(ArrayList<Image> images, ClothType clothType);

        void addToFavourites(WardrobeTable shirtModel, WardrobeTable pantModel);

        void onPageChanged(WardrobeTable shirtModel, WardrobeTable pantModel);

        void onShuffleClicked();

        void appTourComplete();

        void shouldShowUniqueCombination(Intent intent);
    }
}
