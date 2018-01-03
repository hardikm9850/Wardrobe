package com.wardrobe.callback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.wardrobe.R;

import java.lang.ref.WeakReference;

import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by hardik on 03/01/18.
 */

public class WritePermissionCallback implements PermissionCallback {

    private WeakReference<Activity> activityWeakReference;
    private boolean multipleSupported;
    private int selectionLimit;

    public WritePermissionCallback(Activity activity, boolean isMultipleSupported, int selectionLimit) {
        activityWeakReference = new WeakReference<>(activity);
        this.multipleSupported = isMultipleSupported;
        this.selectionLimit = selectionLimit;
    }

    @Override
    public void permissionGranted() {
        Activity activity = activityWeakReference.get();
        if (activity == null)
            return;
        Intent intent = new Intent(activity, AlbumSelectActivity.class);
        selectionLimit = selectionLimit > 1 ? selectionLimit : 1;
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, selectionLimit);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    public void permissionRefused() {
        Activity activity = activityWeakReference.get();
        if (activity == null)
            return;
        Context context = activity.getApplicationContext();
        Toast.makeText(context, R.string.media_permission, Toast.LENGTH_LONG).show();
    }
}