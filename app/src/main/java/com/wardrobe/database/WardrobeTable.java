package com.wardrobe.database;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by hardik on 03/01/18.
 */
@Table(database = WardrobeDatabase.class, allFields = true)
public class WardrobeTable extends BaseModel {
    @PrimaryKey(autoincrement = true)
    int id;
    String imagePath;
    int clothType; //shirt/pant
    int isFavourite;

    public WardrobeTable() {
    }

    public WardrobeTable(String imagePath, int clothType, int isFavourite) {
        this.imagePath = imagePath;
        this.clothType = clothType;
        this.isFavourite = isFavourite;
    }

    public int getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getClothType() {
        return clothType;
    }

    public int getIsFavourite() {
        return isFavourite;
    }


    public static class ClothType {
        public static final int TYPE_SHIRT = 0;
        public static final int TYPE_PANT = 1;
    }

    public static class InFavourite {
        public static final int NO = 0;
        public static final int YES = 1;
    }
}
