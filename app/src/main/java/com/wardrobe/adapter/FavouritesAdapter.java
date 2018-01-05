package com.wardrobe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.wardrobe.R;
import com.wardrobe.database.FavouriteTable;
import java.util.List;

/**
 * Created by hardik on 05/01/18.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesViewHolder> {

    private Context context;
    private List<FavouriteTable> dataList;

    public FavouritesAdapter(Context _context, List<FavouriteTable> _dataList) {
        this.context = _context;
        dataList = _dataList;
    }

    @Override
    public FavouritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_favourite, parent, false);
        return new FavouritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouritesViewHolder holder, int position) {
        FavouriteTable favouriteTable = dataList.get(position);
        String pantPath = favouriteTable.getPantPath();
        String shirtPath = favouriteTable.getShirtPath();
        Glide.with(context).load(shirtPath).asBitmap().into(holder.imgFavShirt);
        Glide.with(context).load(pantPath).asBitmap().into(holder.imgFavPant);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
