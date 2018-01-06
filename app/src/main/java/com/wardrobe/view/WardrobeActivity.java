package com.wardrobe.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.wardrobe.R;
import com.wardrobe.adapter.ImageAdapter;
import com.wardrobe.callback.Callback;
import com.wardrobe.contract.WardrobeContract;
import com.wardrobe.database.WardrobeTable;
import com.wardrobe.gallery.CameraTask;
import com.wardrobe.gallery.DisplayUtils;
import com.wardrobe.gallery.GalleryTask;
import com.wardrobe.presenter.WardrobePresenterImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import pl.tajchert.nammu.Nammu;

import static butterknife.OnPageChange.Callback.PAGE_SELECTED;
import static com.wardrobe.callback.CameraPermissionCallback.REQUEST_CAMERA;


public class WardrobeActivity extends AppCompatActivity implements WardrobeContract.WardrobeView {


    @BindView(R.id.viewpager_shirt)
    ViewPager viewpagerShirt;
    @BindView(R.id.viewpager_pant)
    ViewPager viewpagerPant;
    @BindView(R.id.img_favourite)
    ImageView imgFavourite;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_add_shirt)
    FloatingActionButton imgAddShirt;
    @BindView(R.id.img_add_pant)
    FloatingActionButton imgAddPant;
    @BindView(R.id.img_shuffle)
    FloatingActionButton imgShuffle;

    private WardrobeContract.WardrobePresenter wardrobePresenter;
    private Context context;
    private Uri cameraUri;
    private ImageAdapter shirtAdapter, pantAdapter;
    private WardrobeContract.ClothType clothType;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        context = activity = this;
        shirtAdapter = new ImageAdapter(context);
        pantAdapter = new ImageAdapter(context);
        viewpagerShirt.setAdapter(shirtAdapter);
        viewpagerPant.setAdapter(pantAdapter);
        wardrobePresenter = new WardrobePresenterImpl(this);
        wardrobePresenter.shouldShowUniqueCombination(getIntent());
    }

    /**
     * Provides image path list from either camera or gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE: {
                if (data == null) return;
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                wardrobePresenter.storeImages(images, clothType);
            }
            break;
            case REQUEST_CAMERA: {
                if (resultCode != RESULT_OK) return;
                ArrayList<Image> images = CameraTask.saveCameraImageToFile(context, cameraUri);
                wardrobePresenter.storeImages(images, clothType);
            }
            break;
        }
    }

    /**
     * Camera/Write permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * provides basic app tour i.e showing user how shirt and pant can be added
     */
    @Override
    public void provideAppTour() {
        DisplayUtils.showTutorial(activity, imgAddShirt, "Add your T-Shirts", "Choose and add your cool T-Shirts by clicking here", shirtCallback);
    }

    Callback<Boolean> shirtCallback = new Callback<Boolean>() {
        @Override
        public void returnResult(Boolean result) {
            if (result) {
                DisplayUtils.showTutorial(activity, imgAddPant, "Add your Pants", "Choose and add your funky Pants by clicking here", pantCallback);
            }
        }
    };

    Callback<Boolean> pantCallback = new Callback<Boolean>() {
        @Override
        public void returnResult(Boolean result) {
            if (result) {
                wardrobePresenter.appTourComplete();
            }
        }
    };

    /**
     * Launches a dialog to allow user select image/s
     * @param _clothType clothtype {T-shirt,pant}
     */
    @Override
    public void startGalleryChooser(WardrobeContract.ClothType _clothType) {
        clothType = _clothType;
        GalleryTask.launchGalleryChooser(this, new Callback<Uri>() {
            @Override
            public void returnResult(Uri _cameraUri) {
                cameraUri = _cameraUri;
            }
        });
    }

    /**
     * Setting adapter models
     * @param wardrobeModels adapter models
     */
    @Override
    public void setupShirtView(List<WardrobeTable> wardrobeModels) {
        shirtAdapter.addData(new ArrayList<>(wardrobeModels));
    }

    /**
     * Setting adapter models
     * @param wardrobeModels adapter models
     */
    @Override
    public void setupPantView(List<WardrobeTable> wardrobeModels) {
        pantAdapter.addData(new ArrayList<>(wardrobeModels));
    }

    /**
     * Changes favourite state of current visible combination
     * @param resource resourceId to set to favourite icon
     */
    @Override
    public void changeFavouriteState(@IntegerRes int resource) {
        Drawable drawable = ContextCompat.getDrawable(context, resource);
        imgFavourite.setBackground(drawable);
    }

    /**
     * User has not added any cloth, hence we show simple placeholder
     * @param placeholderModel placeholder model for adapter
     */
    @Override
    public void showPlaceholderForShirt(WardrobeTable placeholderModel) {
        shirtAdapter.addData(placeholderModel);
    }

    @Override
    public void showPlaceholderForPant(WardrobeTable placeholderModel) {
        pantAdapter.addData(placeholderModel);
    }

    /**
     * provides adapter model list to presenter for shuffling for instance
     * @return adapter model list
     */
    @Override
    public ArrayList<WardrobeTable> getPantAdapterList() {
        return new ArrayList<>(pantAdapter.getData());
    }

    /**
     * provides adapter model list to presenter
     * @return adapter model list
     */
    @Override
    public ArrayList<WardrobeTable> getShirtAdapterList() {
        return new ArrayList<>(shirtAdapter.getData());
    }


    /**
     * Click listener for actionable views
     * @param view view
     */
    @OnClick({R.id.img_add_shirt, R.id.img_add_pant, R.id.img_favourite, R.id.img_shuffle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_add_pant: {
                wardrobePresenter.onAddNewPantClicked();
            }
            break;
            case R.id.img_add_shirt: {
                wardrobePresenter.onAddNewShirtClicked();
            }
            break;
            case R.id.img_favourite: {
                int currentShirtItem = viewpagerShirt.getCurrentItem();
                int currentPantItem = viewpagerPant.getCurrentItem();
                WardrobeTable shirtModel = shirtAdapter.getItemAtPosition(currentShirtItem);
                WardrobeTable pantModel = pantAdapter.getItemAtPosition(currentPantItem);
                wardrobePresenter.addToFavourites(shirtModel, pantModel);
            }
            break;
            case R.id.img_shuffle: {
                wardrobePresenter.onShuffleClicked();
            }
            break;
        }
    }

    /**
     * viewpager page listener that provides current item index of viewpager
     * @param currentShirtItemPosition current item index
     */
    @OnPageChange(value = R.id.viewpager_shirt, callback = PAGE_SELECTED)
    public void onShirtChangeListener(int currentShirtItemPosition) {
        int currentPantItemPosition = viewpagerPant.getCurrentItem();
        checkIfCombinationIsFavourite(currentShirtItemPosition, currentPantItemPosition);
    }

    @OnPageChange(value = R.id.viewpager_pant, callback = PAGE_SELECTED)
    public void onPantChangeListener(int currentPantItemPosition) {
        int currentShirtItemPosition = viewpagerShirt.getCurrentItem();
        checkIfCombinationIsFavourite(currentShirtItemPosition, currentPantItemPosition);
    }

    /**
     * checks if current combination of clothes is marked as favourite
     * @param currentShirtItemPosition shirt index
     * @param currentPantItemPosition pant index
     */
    private void checkIfCombinationIsFavourite(int currentShirtItemPosition, int currentPantItemPosition) {
        WardrobeTable pantModel = pantAdapter.getItemAtPosition(currentPantItemPosition);
        WardrobeTable shirtModel = shirtAdapter.getItemAtPosition(currentShirtItemPosition);
        wardrobePresenter.onPageChanged(shirtModel, pantModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Launches favourite combination added by user
     * @param item menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_favorite: {
                Intent intent = new Intent(context, FavouritesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
