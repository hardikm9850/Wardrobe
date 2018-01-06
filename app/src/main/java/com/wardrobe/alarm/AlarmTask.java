package com.wardrobe.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wardrobe.receiver.AlarmReceiver;

import java.util.Calendar;

/**
 * Created by hardik on 05/01/18.
 */

public class AlarmTask {

    private static final int HOUR_OF_DAY = 6;
    private static final int MINUTE_OF_DAY = 0;

    public static void scheduleRepeatingAlarmOnce(Context context) {
        //Schedule alarm through alarm receiver, we got to instantiate this alarm again device is booted up
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        //Creating alarm manager instance
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY); //Morning 6 AM
        calendar.set(Calendar.MINUTE, MINUTE_OF_DAY);
        calendar.set(Calendar.SECOND, 0);
        //We got to repeat the alarm daily
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
    }
}
