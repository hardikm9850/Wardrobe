package com.wardrobe.gallery.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wardrobe.WardrobeApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by hardik on 31/01/17.
 */

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    public static final String DIRECTORY_NAME = "Wardrobe";

    public static String copy(@NonNull String imagePath) {
        String fileName;
        File destination;
        FileInputStream inStream = null;
        try {
            makeDirectoryIfNotExist();
            fileName = "IMG".concat("-").concat(System.currentTimeMillis() + "").concat(".jpg");
            destination = new File(getDirectoryRoot(), fileName);
            Log.d(TAG, "dir name " + destination.getAbsolutePath());
            if (!destination.createNewFile())
                return null;
            File source = new File(imagePath);
            inStream = new FileInputStream(source);
            FileOutputStream outStream = new FileOutputStream(destination);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            Log.d(TAG, "Destination file " + destination.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
            return null;
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on copying", e);
            } catch (Exception e) {
                Log.e(TAG, "Exception on copying", e);
            }
        }
        return destination.getAbsolutePath();
    }

    private static void makeDirectoryIfNotExist() {
        File directory = new File(getDirectoryRoot());
        if (!directory.exists()) {
            boolean isDirCreated = directory.mkdirs();
            if (!isDirCreated) {
                Log.e(TAG, "Failed to create directory");
            }
        }
    }

    public static String getDirectoryRoot() {
        String path;
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Context context = WardrobeApp.getInstance();
        if (isSDPresent) {
            /*
             * Returns absolute path to application-specific directory on the primary
             * shared/external storage device where the application can place cache
             * files it owns. These files are internal to the application, and not
             * typically visible to the user as media.
             */
            path = context.getExternalCacheDir().getAbsolutePath() + File.separator.concat(DIRECTORY_NAME);
        } else {
            /*
             * No additional permissions are required for the calling app to read or
             * write files under the returned path.
             */
            path = context.getFilesDir() + File.separator.concat(DIRECTORY_NAME);
        }
        path = Environment.getExternalStorageDirectory() + File.separator.concat(DIRECTORY_NAME);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return path;
    }
}
