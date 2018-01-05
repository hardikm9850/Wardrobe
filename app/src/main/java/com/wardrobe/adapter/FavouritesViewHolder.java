package com.wardrobe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.wardrobe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hardik on 05/01/18.
 */

public class FavouritesViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.img_fav_shirt)
    ImageView imgFavShirt;
    @BindView(R.id.img_fav_pant)
    ImageView imgFavPant;

    public FavouritesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
