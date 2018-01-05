package com.wardrobe.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.darsh.multipleimageselect.models.Image;
import com.wardrobe.R;
import com.wardrobe.WardrobeApp;
import com.wardrobe.gallery.util.BitmapUtils;
import com.wardrobe.gallery.util.UriToUrl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by hardik on 04/10/17.
 */

public class CameraTask {
    private static final String TAG = CameraTask.class.getSimpleName();
    public static final String DIRECTORY_NAME = "Wardrobe";

    @NonNull
    public static ArrayList<Image> saveCameraImageToFile(@Nullable Context context, @Nullable Uri cameraUri) {
        if (context == null) {
            context = WardrobeApp.getInstance();
        }
        if(cameraUri == null){
            return new ArrayList<>();
        }
        ArrayList<Image> imageArrayList = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(cameraUri);
        context.sendBroadcast(intent);
        String imagePath = UriToUrl.getImagePathFromUri(cameraUri);
        final Bitmap newBitmap = BitmapUtils.rotateImage(imagePath);
        if (newBitmap == null) {
            //Image is Ok
            Log.d(TAG, "Camera Image as is " + imagePath);
        } else {
            //Save rotated bitmap
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + DIRECTORY_NAME);
            boolean isDirCreated = dir.mkdirs();
            if (!isDirCreated) {
                Toast.makeText(context, R.string.error_occured, Toast.LENGTH_SHORT).show();
                return imageArrayList;
            }
            final String fileName = System.currentTimeMillis() + "_camera" + ".jpg";
            final File file = new File(dir, fileName);
            FileOutputStream fOut;
            try {
                boolean isFileCreated = file.createNewFile();
                if (!isFileCreated)
                    return imageArrayList;
                fOut = new FileOutputStream(file);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
                fOut.flush();
                fOut.close();
                imagePath = file.getAbsolutePath();
                Log.d(TAG, "Camera Image changed" + imagePath);
            } catch (Exception e) {
                Log.e(TAG, "Camera exception occured while taking picture", e);
            }
        }

        imageArrayList.add(new Image(12, imagePath, imagePath, true));
        return imageArrayList;
    }
}
