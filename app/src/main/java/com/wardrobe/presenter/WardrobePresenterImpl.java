package com.wardrobe.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.darsh.multipleimageselect.models.Image;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wardrobe.R;
import com.wardrobe.WardrobeApp;
import com.wardrobe.alarm.AlarmTask;
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

    private final WardrobeView wardrobeView;
    private final String PREFS_TOUR = "is_tour_pending";
    private final SharedPreferences sharedPreferences;
    private List<FavouriteTable> favouriteTableList;
    private final int UNAVAILABLE = -1;

    public WardrobePresenterImpl(WardrobeView _wardrobeView) {
        this.wardrobeView = _wardrobeView;
        String PREFS_NAME = "wardrobe";
        sharedPreferences = WardrobeApp.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setDataForShirts();
        setDataForPants();
        fetchFavourites();
        checkIfAppTourIsPending();
        shouldScheduleAlarm();
    }

    /**
     * Checks if we need to schedule the alarm that suggest user unique combination daily
     */
    private void shouldScheduleAlarm() {
        String PREFS_ALARM = "is_alarm_set";
        boolean isAlarmSet = sharedPreferences.getBoolean(PREFS_ALARM, false);
        if (isAlarmSet)
            return;
        if (!wardrobeView.getShirtAdapterList().isEmpty()) {
            AlarmTask.scheduleRepeatingAlarm(WardrobeApp.getInstance().getApplicationContext());
            sharedPreferences.edit().putBoolean(PREFS_ALARM, true).apply();
        }
    }

    /**
     * Checks if user onboarding/demo is given already
     */
    private void checkIfAppTourIsPending() {
        boolean isTourPending = sharedPreferences.getBoolean(PREFS_TOUR, true);
        if (isTourPending) {
            wardrobeView.provideAppTour();
        }
    }

    /**
     * Fetches list of favourite combination of shirt and pant set by user
     */
    private void fetchFavourites() {
        favouriteTableList = SQLite.select().from(FavouriteTable.class).queryList();
    }

    /**
     * User wants to add a new shirt, show him option to add from gallery or camera
     */
    @Override
    public void onAddNewShirtClicked() {
        wardrobeView.startGalleryChooser(WardrobeContract.ClothType.SHIRT);
    }

    /**
     * User wants to add a new pant,-x-
     */
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
        if (images.isEmpty())
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
    public void addToFavourites(WardrobeTable shirtModel, WardrobeTable pantModel) {
        if (shirtModel == null || pantModel == null)
            return;
        int shirtId = shirtModel.getId();
        int pantId = pantModel.getId();
        if (shirtId == UNAVAILABLE || pantId == UNAVAILABLE)
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
    public void onPageChanged(WardrobeTable shirtModel, WardrobeTable pantModel) {
        if (shirtModel == null)
            return;
        if (pantModel == null)
            return;
        if (favouriteTableList == null) {
            favouriteTableList = SQLite.select().from(FavouriteTable.class).queryList();
        }
        FavouriteTable favouriteTable = new FavouriteTable(shirtModel.getId(), pantModel.getId());
        boolean isCombinationLiked = favouriteTableList.contains(favouriteTable);
        if (isCombinationLiked) {
            wardrobeView.changeFavouriteState(R.drawable.ic_like);
        } else {
            wardrobeView.changeFavouriteState(R.drawable.ic_dis_like);
        }
    }

    /**
     * User has clicked shuffle, we are using Collections.shuffle method to shuffle the adapter<view>list
     * We also check if the current combination is favourite one after the list was shuffled
     */
    @Override
    public void onShuffleClicked() {
        int pantId = UNAVAILABLE, shirtId = UNAVAILABLE;
        List<WardrobeTable> shirtModels = wardrobeView.getShirtAdapterList();
        List<WardrobeTable> pantModels = wardrobeView.getPantAdapterList();
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
            pantId = pantModels.get(0).getId();
        }
        if (shirtModels.size() > 0) {
            shirtId = shirtModels.get(0).getId();
        }
        checkIfFavouriteCombination(shirtId, pantId);
    }

    /**
     * Marks app tour as complete
     */
    @Override
    public void appTourComplete() {
        sharedPreferences.edit().putBoolean(PREFS_TOUR, false).apply();
    }

    @Override
    public void shouldShowUniqueCombination(Intent intent) {
        if (intent != null && intent.getBooleanExtra(WardrobeApp.TAG_ALARM_NOTIFIER, false)) {
            List<WardrobeTable> shirtList = SQLite.
                    select().
                    from(WardrobeTable.class).
                    where(WardrobeTable_Table.clothType.eq(WardrobeTable.ClothType.TYPE_SHIRT)).
                    queryList();

            List<WardrobeTable> pantList = SQLite.
                    select().
                    from(WardrobeTable.class).
                    where(WardrobeTable_Table.clothType.eq(WardrobeTable.ClothType.TYPE_SHIRT)).
                    queryList();
            if (shirtList.size() == 0 || pantList.size() == 0)
                return;

            Collections.shuffle(shirtList);
            Collections.shuffle(pantList);
            wardrobeView.setupShirtView(shirtList);
            wardrobeView.setupPantView(pantList);
        }
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
        if (list.isEmpty()) {
            WardrobeTable placeholderModel = new WardrobeTable(UNAVAILABLE, null);
            if (isShirtSelected) {
                wardrobeView.showPlaceholderForShirt(placeholderModel);
            } else {
                wardrobeView.showPlaceholderForPant(placeholderModel);
            }
            return;
        }
        //Identifying which view(Shirt/Pant) to bind the data
        if (isShirtSelected) {
            wardrobeView.setupShirtView(list);
            return;
        }
        wardrobeView.setupPantView(list);
        //Checking if current combination is marked favourite
        checkIfFavouriteCombination(UNAVAILABLE, list.get(0).getId());
    }

    /**
     * Checks if first visible combination of shirt and pant(if available) are user's favourite
     * We fetch first shirt id and check if favourite table has this <shirtId,pantId> pair
     *
     * @param pantId  pantId
     * @param shirtId shirtId
     */
    private void checkIfFavouriteCombination(int shirtId, int pantId) {
        if (pantId == UNAVAILABLE)
            return;
        if (shirtId == UNAVAILABLE) {
            WardrobeTable shirtEntry = SQLite.select(WardrobeTable_Table.id).
                    from(WardrobeTable.class).
                    where(WardrobeTable_Table.clothType.eq(WardrobeTable.ClothType.TYPE_SHIRT)).
                    orderBy(WardrobeTable_Table.id.asc()).
                    querySingle();
            if (shirtEntry == null)
                return;
            shirtId = shirtEntry.getId();
        }
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
