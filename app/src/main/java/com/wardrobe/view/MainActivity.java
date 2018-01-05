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
import com.wardrobe.model.ImageModel;
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

public class MainActivity extends AppCompatActivity implements WardrobeContract.WardrobeView {


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
    private String TAG = MainActivity.class.getSimpleName();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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

    @Override
    public void setupShirtView(List<WardrobeTable> wardobeModels) {
        shirtAdapter.setData(new ArrayList<>(wardobeModels));
    }

    @Override
    public void setupPantView(List<WardrobeTable> wardobeModels) {
        pantAdapter.setData(new ArrayList<>(wardobeModels));
    }

    @Override
    public void changeFavouriteState(@IntegerRes int resource) {
        Drawable drawable = ContextCompat.getDrawable(context, resource);
        imgFavourite.setBackground(drawable);
    }

    @Override
    public void showPlaceholderForShirt(WardrobeTable placeholderModel) {
        shirtAdapter.setData(placeholderModel);
    }

    @Override
    public void showPlaceholderForPant(WardrobeTable placeholderModel) {
        pantAdapter.setData(placeholderModel);
    }

    @Override
    public ArrayList<WardrobeTable> getPantAdapterList() {
        return new ArrayList<>(pantAdapter.getData());
    }

    @Override
    public ArrayList<WardrobeTable> getShirtAdapterList() {
        return new ArrayList<>(shirtAdapter.getData());
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_favorite: {
                Intent intent = new Intent(context, FavouritesActivity.class);
                startActivity(intent);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
