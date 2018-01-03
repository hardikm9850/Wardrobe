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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FavouriteTable that = (FavouriteTable) o;
        if (shirtId != that.shirtId) return false;
        return pantId == that.pantId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + shirtId;
        result = 31 * result + pantId;
        return result;
    }
}
