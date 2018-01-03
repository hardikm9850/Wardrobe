package com.wardrobe.callback;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.wardrobe.R;

import java.lang.ref.WeakReference;

import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by hardik on 03/01/18.
 */

public class CameraPermissionCallback implements PermissionCallback {
    public static final int REQUEST_CAMERA = 301;

    private WeakReference<Activity> activityWeakReference;
    private Callback<Uri> callback;

    public CameraPermissionCallback(Activity activity, Callback<Uri> callback) {
        activityWeakReference = new WeakReference<>(activity);
        this.callback = callback;
    }

    @Override
    public void permissionGranted() {
        Activity activity = activityWeakReference.get();
        if (activity == null)
            return;
        Uri cameraUri = startCamera(activity);
        if (callback != null) {
            callback.returnResult(cameraUri);
        }
    }

    @Override
    public void permissionRefused() {
        Activity activity = activityWeakReference.get();
        if (activity == null)
            return;
        Context context = activity.getApplicationContext();
        Toast.makeText(context, R.string.camera_permission, Toast.LENGTH_LONG).show();
    }

    private static Uri startCamera(@NonNull final android.app.Activity activity) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, REQUEST_CAMERA);
        return uri;
    }
}