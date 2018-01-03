package com.wardrobe.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;

import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.wardrobe.R;
import com.wardrobe.adapter.ImageAdapter;
import com.wardrobe.callback.Callback;
import com.wardrobe.contract.WardrobeContract;
import com.wardrobe.gallery.CameraTask;
import com.wardrobe.gallery.GalleryTask;
import com.wardrobe.model.ImageModel;
import com.wardrobe.presenter.WardrobePresenterImpl;

import java.util.ArrayList;

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
    AppCompatImageButton imgFavourite;

    private WardrobeContract.WardrobePresenter wardrobePresenter;
    private String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private Uri cameraUri;
    private boolean isAddShirtSelected = false;
    private ImageAdapter shirtAdapter, pantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);
        ButterKnife.bind(this);
        context = this;
        shirtAdapter = new ImageAdapter(context);
        pantAdapter = new ImageAdapter(context);
        viewpagerShirt.setAdapter(shirtAdapter);
        viewpagerPant.setAdapter(pantAdapter);
        wardrobePresenter = new WardrobePresenterImpl(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE: {
                if (data == null) return;
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                for (Image image : images) {
                    Log.d(TAG, "Image " + image.path);
                }
                wardrobePresenter.storeImages(images, isAddShirtSelected);
            }
            break;
            case REQUEST_CAMERA: {
                if (resultCode != RESULT_OK) return;
                ArrayList<Image> images = CameraTask.saveCameraImageToFile(context, cameraUri);
                wardrobePresenter.storeImages(images, isAddShirtSelected);
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
    public void startGalleryChooser(boolean isShirtSelected) {
        isAddShirtSelected = isShirtSelected;
        GalleryTask.launchGallaryChooser(this, new Callback<Uri>() {
            @Override
            public void returnResult(Uri _cameraUri) {
                cameraUri = _cameraUri;
            }
        });
    }

    @Override
    public void setupShirtView(ArrayList<ImageModel> imageModels) {
        shirtAdapter.setData(imageModels);
    }

    @Override
    public void setupPantView(ArrayList<ImageModel> imageModels) {
        pantAdapter.setData(imageModels);
    }

    @Override
    public void changeFavouriteState(@IntegerRes int resource) {
        Drawable drawable = ContextCompat.getDrawable(context, resource);
        imgFavourite.setBackgroundDrawable(drawable);
    }

    @Override
    public void showPlaceholderForShirt(ImageModel placeholderModel) {
        shirtAdapter.setData(placeholderModel);
    }

    @Override
    public void showPlaceholderForPant(ImageModel placeholderModel) {
        pantAdapter.setData(placeholderModel);
    }

    @OnClick({R.id.img_add_shirt, R.id.img_add_pant, R.id.img_favourite})
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
                ImageModel shirtModel = shirtAdapter.getItemAtPosition(currentShirtItem);
                ImageModel pantModel = pantAdapter.getItemAtPosition(currentPantItem);
                wardrobePresenter.addToFavourites(shirtModel, pantModel);
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
        ImageModel pantModel = pantAdapter.getItemAtPosition(currentPantItemPosition);
        ImageModel shirtModel = shirtAdapter.getItemAtPosition(currentShirtItemPosition);
        wardrobePresenter.onPageChanged(shirtModel, pantModel);
    }
}
