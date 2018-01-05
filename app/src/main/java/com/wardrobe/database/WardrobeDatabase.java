package com.wardrobe.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by hardik on 03/01/18.
 */
@Database(name = WardrobeDatabase.NAME, version = WardrobeDatabase.VERSION)
public class WardrobeDatabase {
    static final String NAME = "WardrobeDB";

    static final int VERSION = 2;
}
