package com.wardrobe.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.wardrobe.R;
import com.wardrobe.WardrobeApp;
import com.wardrobe.view.WardrobeActivity;

/**
 * We use pending intent to launch the app to suggest unique combination daily passing proper data.
 * we then use data to show random unique combination
 * Created by hardik on 05/01/18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent _intent) {

        Intent launchIntent = new Intent(context, WardrobeActivity.class);
        launchIntent.putExtra(WardrobeApp.TAG_ALARM_NOTIFIER, true);
        PendingIntent pi = PendingIntent.getActivity(context, 0, launchIntent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Wardrobe")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Wardrobe")
                .setContentText("Good morning, how about this combination we have for you today?")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
