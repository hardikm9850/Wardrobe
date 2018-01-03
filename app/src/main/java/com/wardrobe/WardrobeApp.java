package com.wardrobe;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.glidebitmappool.GlideBitmapPool;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import pl.tajchert.nammu.Nammu;

/**
 * Created by hardik on 03/01/18.
 */

public class WardrobeApp extends Application {
    private static Context wardrobeApp;

    @Override
    public void onCreate() {
        super.onCreate();
        wardrobeApp = this;
        Stetho.initializeWithDefaults(wardrobeApp);
        Nammu.init(wardrobeApp);
        FlowManager.init(new FlowConfig.Builder(wardrobeApp).openDatabasesOnInit(true).build());
        GlideBitmapPool.initialize(10 * 1024 * 1024);
    }

    public static Context getInstance() {
        return wardrobeApp;
    }
}
