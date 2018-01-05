package com.wardrobe.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wardrobe.R;
import com.wardrobe.adapter.FavouritesAdapter;
import com.wardrobe.database.FavouriteTable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hardik on 05/01/18.
 */

public class FavouritesActivity extends AppCompatActivity {

    @BindView(R.id.recycler_favourites)
    RecyclerView recyclerFavourites;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        ButterKnife.bind(this);
        setupViews();
    }

    private void setupViews() {
        toolbar.setTitle(getString(R.string.favourites));
        setSupportActionBar(toolbar);
        List<FavouriteTable> list = SQLite.select().from(FavouriteTable.class).queryList();
        if(list.size()==0){
            recyclerFavourites.setVisibility(View.GONE);
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerFavourites.setLayoutManager(linearLayoutManager);
        FavouritesAdapter favouritesAdapter = new FavouritesAdapter(this, list);
        recyclerFavourites.setAdapter(favouritesAdapter);
    }
}
