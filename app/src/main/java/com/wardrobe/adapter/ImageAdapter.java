package com.wardrobe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.glidebitmappool.GlideBitmapFactory;
import com.glidebitmappool.GlideBitmapPool;
import com.wardrobe.R;
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

    private ArrayList<ImageModel> imageModels;
    private Context context;

    public ImageAdapter(Context _context) {
        this.context = _context;
        this.imageModels = new ArrayList<>();
    }

    public void setData(ArrayList<ImageModel> _imageModels) {
        this.imageModels.clear();
        this.imageModels = _imageModels;
        notifyDataSetChanged();
    }


    public void setData(ImageModel _imageModel) {
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

    public ImageModel getItemAtPosition(int position) {
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
        }
        //Bitmap bitmap = GlideBitmapFactory.decodeFile(file.getPath(), 100, 100);
        //if (bitmap == null)
        //return view;
        //imgCloth.setImageBitmap(bitmap);
        Glide.with(view.getContext()).
                load(file.getPath()).
                asBitmap().
                dontAnimate().
                thumbnail(0.4f).
                //placeholder(R.drawable.pant).
                override(200, 200).
                skipMemoryCache(true).
                into(imgCloth);
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
        Log.d("####", "Page is being removed " + position);
        View view = (View) object;
        container.removeView(view);

        ImageView imgView = view.findViewById(R.id.img_cloth);
        BitmapDrawable bmpDrawable = (BitmapDrawable) imgView.getDrawable();
        if (bmpDrawable != null && bmpDrawable.getBitmap() != null) {
            bmpDrawable.getBitmap().recycle();
        }
    }

    public ArrayList<ImageModel> getData() {
        return imageModels;
    }
}
