package com.wardrobe.gallery;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.wardrobe.R;
import com.wardrobe.callback.Callback;

/**
 * Created by hardik on 03/01/18.
 */

public class DisplayUtils {


    public static void showAlert(@NonNull Activity activity, String title, String positiveText, String negativeText, DialogInterface.OnClickListener onYesListener, DialogInterface.OnClickListener onNoListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setPositiveButton(positiveText, onYesListener);
        builder.setNegativeButton(negativeText, onNoListener);
        builder.show();
    }

    public static void showTutorial(@NonNull Activity activity, @NonNull View anchorView, @NonNull String title, @NonNull String description,
                                    @Nullable final Callback<Boolean> callback) {
        TapTargetView.showFor(activity,                 // `this` is an Activity
                description.length() > 0 ? TapTarget.forView(anchorView, title, description) :
                        TapTarget.forView(anchorView, title)
                                // All options below are optional
                                .outerCircleColor(R.color.gray)      // Specify a color for the outer circle
                                .targetCircleColor(android.R.color.white)   // Specify a color for the target circle
                                .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                .titleTextColor(android.R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.blueStone)  // Specify the color of the description text
                                .textColor(R.color.colorAccent)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(30),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels

                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        super.onTargetDismissed(view, userInitiated);
                        if (callback != null) {
                            callback.returnResult(true);
                        }
                    }
                });
    }

    public static void showTutorial(@NonNull Activity activity, @NonNull View anchorView, @NonNull String title,
                                    Callback<Boolean> callback) {
        showTutorial(activity, anchorView, title, "", callback);
    }
}
