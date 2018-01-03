package com.wardrobe.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;

import com.wardrobe.callback.Callback;
import com.wardrobe.callback.CameraPermissionCallback;
import com.wardrobe.callback.WritePermissionCallback;

import java.util.ArrayList;
import java.util.List;

import pl.tajchert.nammu.Nammu;

/**
 * Created by hardik on 06/03/17.
 */

public class GalleryTask {

    private static int SELECTION_LIMIT = 5;

    /**
     * @param activity              activity instance
     * @param cameraUriNameCallback callback that allows stores Uri of camera image
     */
    public static void launchGallaryChooser(final Activity activity, final Callback<Uri> cameraUriNameCallback) {
        String selectOption = "Select from Camera/Gallery";
        String camera = "Camera";
        String album = "Gallery";
        DisplayUtils.showAlert(activity, selectOption, camera, album, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Ask for camera permission
                boolean hasCameraPermission = Nammu.checkPermission(Manifest.permission.CAMERA);
                boolean hasWritePermission = Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                List<String> permissionRequest = new ArrayList<>();
                if (hasCameraPermission && hasWritePermission) {
                    new CameraPermissionCallback(activity, cameraUriNameCallback).permissionGranted();
                    return;
                }
                if (!hasWritePermission) {
                    permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!hasCameraPermission) {
                    permissionRequest.add(Manifest.permission.CAMERA);
                }
                String[] permissionArray = permissionRequest.toArray(new String[permissionRequest.size()]);
                Nammu.askForPermission(activity, permissionArray, new CameraPermissionCallback(activity, cameraUriNameCallback));
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Ask for Write Media permission
                if (Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new WritePermissionCallback(activity, SELECTION_LIMIT).permissionGranted();
                    return;
                }
                Nammu.askForPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, new WritePermissionCallback(activity, SELECTION_LIMIT));
            }
        });
    }
}
