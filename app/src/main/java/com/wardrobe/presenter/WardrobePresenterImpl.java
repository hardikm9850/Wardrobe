package com.wardrobe.presenter;

import com.darsh.multipleimageselect.models.Image;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wardrobe.R;
import com.wardrobe.contract.WardrobeContract;
import com.wardrobe.contract.WardrobeContract.WardrobeView;
import com.wardrobe.database.FavouriteTable;
import com.wardrobe.database.FavouriteTable_Table;
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
    private List<FavouriteTable> favouriteTableList;

    public WardrobePresenterImpl(WardrobeView _wardrobeView) {
        this.wardrobeView = _wardrobeView;
        setDataForViews(true);
        setDataForViews(false);
        fetchFavourites();
    }

    private void fetchFavourites() {
        favouriteTableList = SQLite.select().from(FavouriteTable.class).queryList();
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
    public void addToFavourites(ImageModel shirtModel, ImageModel pantModel) {
        if (shirtModel == null || pantModel == null)
            return;
        int shirtId = shirtModel.getImageId();
        int pantId = pantModel.getImageId();
        FavouriteTable favouriteTable = new FavouriteTable(shirtId, pantId);
        favouriteTable.update();
        favouriteTable.save();
        favouriteTableList.add(favouriteTable);
        wardrobeView.changeFavouriteState(R.drawable.ic_like);
    }

    @Override
    public void onPageChanged(ImageModel shirtModel, ImageModel pantModel) {
        if (shirtModel == null)
            return;
        if (pantModel == null)
            return;
        if (favouriteTableList == null) {
            favouriteTableList = SQLite.select().from(FavouriteTable.class).queryList();
        }
        FavouriteTable favouriteTable = new FavouriteTable(shirtModel.getImageId(), pantModel.getImageId());
        boolean isCombinationLiked = favouriteTableList.contains(favouriteTable);
        if (isCombinationLiked) {
            wardrobeView.changeFavouriteState(R.drawable.ic_like);
        } else {
            wardrobeView.changeFavouriteState(R.drawable.ic_dis_like);
        }
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
            return;
        }
        wardrobeView.setupPantView(imageModels);
        shouldShowLikedForFirstCombination(imageModels.get(0).getImageId());
    }

    private void shouldShowLikedForFirstCombination(int pantId) {
        WardrobeTable wardrobeTable = SQLite.select(WardrobeTable_Table.id).
                from(WardrobeTable.class).
                orderBy(WardrobeTable_Table.id.asc()).
                querySingle();
        if (wardrobeTable == null)
            return;
        int shirtId = wardrobeTable.getId();
        FavouriteTable favouriteTable = SQLite.select().
                from(FavouriteTable.class).
                where(FavouriteTable_Table.shirtId.eq(shirtId)).
                and(FavouriteTable_Table.pantId.eq(pantId)).querySingle();
        if (favouriteTable != null) {
            wardrobeView.changeFavouriteState(R.drawable.ic_like);
        }
    }
}
