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
    String shirtPath;
    String pantPath;


    public FavouriteTable(int shirtId, int pantId, String shirtPath, String pantPath) {
        this.shirtId = shirtId;
        this.pantId = pantId;
        this.shirtPath = shirtPath;
        this.pantPath = pantPath;
    }

    public FavouriteTable() {

    }

    public FavouriteTable(int shirtId, int pantId) {
        this.shirtId = shirtId;
        this.pantId = pantId;
    }


    public String getShirtPath() {
        return shirtPath;
    }

    public String getPantPath() {
        return pantPath;
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

    public int getShirtId() {
        return shirtId;
    }

    public int getPantId() {
        return pantId;
    }
}
