package com.wardrobe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.glidebitmappool.GlideBitmapFactory;
import com.glidebitmappool.GlideBitmapPool;
import com.glidebitmappool.internal.BitmapPool;
import com.wardrobe.R;
import com.wardrobe.database.WardrobeTable;
import com.wardrobe.model.ImageModel;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hardik on 03/01/18.
 */

public class ImageAdapter extends PagerAdapter {

    @BindView(R.id.img_cloth)
    ImageView imgCloth;
    @BindView(R.id.txt_placeholder)
    AppCompatTextView txtPlaceholder;

    private ArrayList<WardrobeTable> imageModels;
    private Context context;

    public ImageAdapter(Context _context) {
        this.context = _context;
        this.imageModels = new ArrayList<>();
    }

    public void setData(ArrayList<WardrobeTable> _imageModels) {
        this.imageModels.clear();
        this.imageModels = _imageModels;
        notifyDataSetChanged();
    }


    public void setData(WardrobeTable _imageModel) {
        this.imageModels.add(_imageModel);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return imageModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    public WardrobeTable getItemAtPosition(int position) {
        if (imageModels.size() == 0)
            return null;
        if (position > imageModels.size())
            return null;
        return imageModels.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_image_container, container, false);
        ButterKnife.bind(this, view);
        String path = imageModels.get(position).getImagePath();
        if (path == null) {
            txtPlaceholder.setVisibility(View.VISIBLE);
            container.addView(view, 0);
            return view;
        }

        File file = new File(path);
        if (!file.exists()) {
            imgCloth.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pant));
            container.addView(view, 0);
            return view;
        }
        Glide.with(view.getContext())
                .load(path)
                .asBitmap()
                .placeholder(R.drawable.shirt)
                .dontAnimate()
                .thumbnail(0.2f)
                .override(150, 150)
                .into(imgCloth);
        container.addView(view, 0);
        return view;
    }

    /**
     * Recycling bitmap
     *
     * @param container container
     * @param position  position
     * @param object    view object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        Glide.clear(view);
    }

    public ArrayList<WardrobeTable> getData() {
        return imageModels;
    }
}
