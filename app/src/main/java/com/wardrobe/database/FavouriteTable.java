package com.wardrobe.database;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by hardik on 03/01/18.
 */
@Table(database = WardrobeDatabase.class, allFields = true)
public class FavouriteTable extends BaseModel {
    @PrimaryKey(autoincrement = true)
    int id;
    int shirtId;
    int pantId;

    public FavouriteTable() {

    }

    public FavouriteTable(int shirtId, int pantId) {
        this.shirtId = shirtId;
        this.pantId = pantId;
    }
}
