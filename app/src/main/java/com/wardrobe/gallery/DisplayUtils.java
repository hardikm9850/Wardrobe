package com.wardrobe.gallery;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.wardrobe.R;

/**
 * Created by hardik on 03/01/18.
 */

class DisplayUtils {


    public static void showAlert(@NonNull Activity activity, String title, String positiveText, String negativeText, DialogInterface.OnClickListener onYesListener, DialogInterface.OnClickListener onNoListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setPositiveButton(positiveText, onYesListener);
        builder.setNegativeButton(negativeText, onNoListener);
        builder.show();
    }
}
