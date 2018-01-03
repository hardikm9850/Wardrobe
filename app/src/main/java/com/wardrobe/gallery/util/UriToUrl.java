package com.wardrobe.gallery.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;

import com.wardrobe.WardrobeApp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Class mostly deals with URLs
 */
public final class UriToUrl {

    /**
     * converts given image url to image path
     * @param imageUri uri of an image
     * @return corresponding image path
     */
    public static String getImagePathFromUri(@NonNull Uri imageUri) {
        final Context context = WardrobeApp.getInstance();
        String imagePath = null;
        String[] projection = {Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imageUri,
                projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
            imagePath = cursor.getString(column_index);
            cursor.close();
        }
        //If we could not find it in content resolver,
        //Then we use workaround of that.
        if (imagePath == null) {
            File f = new File(imageUri.toString());
            imagePath = f.getPath();
            if (imagePath.substring(0, 1).equals("f")) {
                try {
                    imagePath = URLDecoder.decode(imagePath, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                imagePath = imagePath.substring(6, imagePath.length());
            }
        }
        return imagePath;
    }
}
