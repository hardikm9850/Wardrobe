package com.wardrobe.presenter;

import com.darsh.multipleimageselect.models.Image;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wardrobe.contract.WardrobeContract;
import com.wardrobe.contract.WardrobeContract.WardrobeView;
import com.wardrobe.database.FavouriteTable;
import com.wardrobe.database.WardrobeTable;
import com.wardrobe.database.WardrobeTable_Table;
import com.wardrobe.model.ImageModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hardik on 03/01/18.
 */

public class WardrobePresenterImpl implements WardrobeContract.WardrobePresenter {

    private WardrobeView wardrobeView;


    public WardrobePresenterImpl(WardrobeView _wardrobeView) {
        this.wardrobeView = _wardrobeView;
        setDataForViews(true);
        setDataForViews(false);
    }

    @Override
    public void onAddNewShirtClicked() {
        wardrobeView.startGalleryChooser(true);
    }

    @Override
    public void onAddNewPantClicked() {
        wardrobeView.startGalleryChooser(false);
    }

    @Override
    public void storeImages(ArrayList<Image> images, boolean isShirtSelected) {
        int TYPE_SHIRT = WardrobeTable.ClothType.TYPE_SHIRT;
        int TYPE_PANT = WardrobeTable.ClothType.TYPE_PANT;
        int IS_FAVOURITE = WardrobeTable.InFavourite.NO;

        if (images == null)
            return;
        if (images.size() == 0)
            return;
        for (Image image : images) {
            String imagePath = image.path;
            WardrobeTable wardrobeTable = new WardrobeTable(imagePath, isShirtSelected ? TYPE_SHIRT : TYPE_PANT, IS_FAVOURITE);
            wardrobeTable.update();
            wardrobeTable.save();
        }
        setDataForViews(isShirtSelected);
    }

    @Override
    public void storeFavouriteItem(ImageModel shirtModel, ImageModel pantModel) {
        if (shirtModel == null || pantModel == null)
            return;
        int shirtId = shirtModel.getImageId();
        int pantId = pantModel.getImageId();
        FavouriteTable favouriteTable = new FavouriteTable(shirtId, pantId);
        favouriteTable.update();
        favouriteTable.save();
    }

    private void setDataForViews(boolean isShirtSelected) {
        List<WardrobeTable> list;
        list = SQLite.
                select().
                from(WardrobeTable.class).
                where(WardrobeTable_Table.clothType.eq(isShirtSelected ?
                        WardrobeTable.ClothType.TYPE_SHIRT : WardrobeTable.ClothType.TYPE_PANT)).
                queryList();
        if (list.size() == 0)
            return;
        ArrayList<ImageModel> imageModels = new ArrayList<>();
        for (WardrobeTable wardrobeTable : list) {
            int id = wardrobeTable.getId();
            String path = wardrobeTable.getImagePath();
            ImageModel imageModel = new ImageModel(id, path);
            imageModels.add(imageModel);
        }
        if (isShirtSelected) {
            wardrobeView.setupShirtView(imageModels);
            wardrobeView.setupPantView(imageModels);
        } else {
            wardrobeView.setupPantView(imageModels);
        }
    }
}
