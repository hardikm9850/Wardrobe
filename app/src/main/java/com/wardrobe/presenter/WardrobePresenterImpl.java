package com.wardrobe.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.darsh.multipleimageselect.models.Image;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wardrobe.R;
import com.wardrobe.WardrobeApp;
import com.wardrobe.contract.WardrobeContract;
import com.wardrobe.contract.WardrobeContract.WardrobeView;
import com.wardrobe.database.FavouriteTable;
import com.wardrobe.database.FavouriteTable_Table;
import com.wardrobe.database.WardrobeTable;
import com.wardrobe.database.WardrobeTable_Table;
import com.wardrobe.model.ImageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Presenter acting as a communicator between view(Activity) and repo (SQLite)
 * Created by hardik on 03/01/18.
 */

public class WardrobePresenterImpl implements WardrobeContract.WardrobePresenter {

    private WardrobeView wardrobeView;
    private List<FavouriteTable> favouriteTableList;
    private final String PREFS_NAME = "wardrobe";
    private final String PREFS_TOUR = "is_tour_pending";
    private SharedPreferences sharedPreferences;

    public WardrobePresenterImpl(WardrobeView _wardrobeView) {
        this.wardrobeView = _wardrobeView;
        sharedPreferences = WardrobeApp.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setDataForShirts();
        setDataForPants();
        fetchFavourites();
        checkIfAppTourIsPending();
    }

    private void checkIfAppTourIsPending() {
        boolean isTourPending = sharedPreferences.getBoolean(PREFS_TOUR, true);
        if (isTourPending) {
            wardrobeView.provideAppTour();
        }
    }

    private void fetchFavourites() {
        favouriteTableList = SQLite.select().from(FavouriteTable.class).queryList();
    }

    @Override
    public void onAddNewShirtClicked() {
        wardrobeView.startGalleryChooser(WardrobeContract.ClothType.SHIRT);
    }

    @Override
    public void onAddNewPantClicked() {
        wardrobeView.startGalleryChooser(WardrobeContract.ClothType.PANT);
    }


    /**
     * Once the user has selected images from Gallery / Camera, we put them in SQlite along with the type of cloth {Shirt/Pant}
     *
     * @param images    list of images
     * @param clothType if cloth type if shirt or pant
     */
    @Override
    public void storeImages(ArrayList<Image> images, WardrobeContract.ClothType clothType) {
        int TYPE_SHIRT = WardrobeTable.ClothType.TYPE_SHIRT;
        int TYPE_PANT = WardrobeTable.ClothType.TYPE_PANT;
        int IS_FAVOURITE = WardrobeTable.InFavourite.NO;
        boolean isShirtSelected = clothType.equals(WardrobeContract.ClothType.SHIRT);

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

    /**
     * User has added current combination to his liked/favourite section
     * We put this entry in favourite table
     *
     * @param shirtModel model object representing shirt obj
     * @param pantModel  model object representing pant obj
     */
    @Override
    public void addToFavourites(ImageModel shirtModel, ImageModel pantModel) {
        if (shirtModel == null || pantModel == null)
            return;
        int shirtId = shirtModel.getImageId();
        int pantId = pantModel.getImageId();
        if (shirtId == -1 || pantId == -1)
            return;
        FavouriteTable storedEntity = SQLite.
                select().
                from(FavouriteTable.class).
                where(FavouriteTable_Table.shirtId.eq(shirtId)).and(FavouriteTable_Table.pantId.eq(pantId)).
                querySingle();
        boolean isCombinationMarkedFavourite = storedEntity != null && storedEntity.getShirtId() == shirtId;
        wardrobeView.changeFavouriteState(isCombinationMarkedFavourite ? R.drawable.ic_dis_like : R.drawable.ic_like);
        if (!isCombinationMarkedFavourite) {
            String shirtPath = shirtModel.getImagePath();
            String pantPath = pantModel.getImagePath();
            FavouriteTable favouriteTable = new FavouriteTable(shirtId, pantId, shirtPath, pantPath);
            favouriteTable.update();
            favouriteTable.save();
            favouriteTableList.add(favouriteTable);
        } else {
            SQLite.delete().
                    from(FavouriteTable.class).
                    where(FavouriteTable_Table.shirtId.eq(shirtId)).and(FavouriteTable_Table.pantId.eq(pantId)).
                    execute();
        }
    }

    /**
     * On every viewpager change event we check if current combination is added in favourites
     * If yes then we show the favourite icon, dislike otherwise
     *
     * @param shirtModel model object representing shirt obj
     * @param pantModel  model object representing pant obj
     */
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

    @Override
    public void onShuffleClicked() {
        int pantId = -1;
        ArrayList<ImageModel> shirtModels = wardrobeView.getShirtAdapterList();
        ArrayList<ImageModel> pantModels = wardrobeView.getPantAdapterList();
        if (shirtModels != null) {
            Collections.shuffle(shirtModels);
        }
        if (pantModels != null) {
            Collections.shuffle(pantModels);
        }
        assert shirtModels != null;
        wardrobeView.setupShirtView(new ArrayList<>(shirtModels));
        assert pantModels != null;
        wardrobeView.setupPantView(new ArrayList<>(pantModels));

        if (pantModels.size() > 0) {
            pantId = pantModels.get(0).getImageId();
        }
        checkIfFavouriteCombination(pantId);
    }

    @Override
    public void appTourComplete() {
        sharedPreferences.edit().putBoolean(PREFS_TOUR, false).apply();
    }

    private void setDataForShirts() {
        setDataForViews(true);
    }

    private void setDataForPants() {
        setDataForViews(false);
    }

    /**
     * Fetches the stored image path of shirt/pant from SQlite and pass this list to View
     *
     * @param isShirtSelected boolean indication type of clothes images we want to provide to views
     */
    private void setDataForViews(boolean isShirtSelected) {
        List<WardrobeTable> list;
        list = SQLite.
                select().
                from(WardrobeTable.class).
                where(WardrobeTable_Table.clothType.eq(isShirtSelected ?
                        WardrobeTable.ClothType.TYPE_SHIRT : WardrobeTable.ClothType.TYPE_PANT)).
                queryList();
        //If list is empty, we show placeholder text
        if (list.size() == 0) {
            ImageModel placeholderModel = new ImageModel(-1, null);
            if (isShirtSelected) {
                wardrobeView.showPlaceholderForShirt(placeholderModel);
                return;
            }
            wardrobeView.showPlaceholderForPant(placeholderModel);
            return;
        }
        //We convert table entries to adapter view model
        ArrayList<ImageModel> imageModels = new ArrayList<>();
        for (WardrobeTable wardrobeTable : list) {
            //Converting table entry to adapter view model
            imageModels.add(wardrobeTable.getImageModel());
        }
        //Identifying which view(Shirt/Pant) to bind the data
        if (isShirtSelected) {
            wardrobeView.setupShirtView(imageModels);
            return;
        }
        wardrobeView.setupPantView(imageModels);
        //Checking if current combination is marked favourite
        checkIfFavouriteCombination(imageModels.get(0).getImageId());
    }

    /**
     * Checks if first visible combination of shirt and pant(if available) are user's favourite
     * We fetch first shirt id and check if favourite table has this <shirtId,pantId> pair
     *
     * @param pantId pantId
     */
    private void checkIfFavouriteCombination(int pantId) {
        if (pantId == -1)
            return;
        WardrobeTable shirtEntry = SQLite.select(WardrobeTable_Table.id).
                from(WardrobeTable.class).
                where(WardrobeTable_Table.clothType.eq(WardrobeTable.ClothType.TYPE_SHIRT)).
                orderBy(WardrobeTable_Table.id.asc()).
                querySingle();
        if (shirtEntry == null)
            return;

        int shirtId = shirtEntry.getId();
        FavouriteTable favouriteTable = SQLite.select().
                from(FavouriteTable.class).
                where(FavouriteTable_Table.shirtId.eq(shirtId)).
                and(FavouriteTable_Table.pantId.eq(pantId)).querySingle();
        if (favouriteTable != null) {
            wardrobeView.changeFavouriteState(R.drawable.ic_like);
        } else {
            wardrobeView.changeFavouriteState(R.drawable.ic_dis_like);
        }
    }
}
