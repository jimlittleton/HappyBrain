package edu.unf.alloway.happybrain.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import edu.unf.alloway.happybrain.MainActivity;
import edu.unf.alloway.happybrain.R;

/**
 * Utility class that handles showing/scheduling notifications
 */
public class NotificationServiceUtils extends BroadcastReceiver {

    private static final String TAG = NotificationServiceUtils.class.getSimpleName();
    private static final String ACTION_SHOW_NOTIFICATION = "edu.unf.alloway.happybrain.NOTIFICATION";
    private static final int ALARM_INTENT_REQUEST_CODE = 500;
    private static final int INTERVAL = 86_400_000;  // Same as 24 hours
    private static final int HOUR = 12;
    private static final int MINUTE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra(TAG, 0);
        if (requestCode == ALARM_INTENT_REQUEST_CODE) {
            showNotification(context);
        }
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Need to make sure the alarm gets reset properly if need
            // when the device reboots
            initRepeatingNotification(context);
        }
    }

    public static void showNotification(Context context) {
        final int NOTIFICATION_ID = 100;
        final int ID_OPEN_ACTIVITY = 200;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                context.getString(R.string.notif_channel_id)
        );
        builder.setSmallIcon(R.drawable.ic_insert_emoticon_black_24dp);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.notif_content_text));
        builder.setAutoCancel(true);

        // Gives a banner-style drop down on phones running Nougat +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        }

        // Don't vibrate if the user's device is on silent
        if (audio != null) {
            switch (audio.getRingerMode()) {
                case AudioManager.RINGER_MODE_NORMAL:
                case AudioManager.RINGER_MODE_VIBRATE:
                    // The vibration pattern is {delay, vibrate, delay, vibrate}
                    builder.setVibrate(new long[]{0, 250, 250, 250});
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    builder.setVibrate(new long[]{0});
                    break;
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        // Opens the MainActivity but only if the activity was in the background.
        // Any further clicks won't do anything
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent openActivity = PendingIntent.getActivity(
                context,
                ID_OPEN_ACTIVITY,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(openActivity);

        if (manager != null) {
            manager.notify(
                    context.getString(R.string.notif_channel_id),
                    NOTIFICATION_ID,
                    builder.build()
            );
        }
    }

    /**
     * Uses the {@link AlarmManager} to set an alarm that fires every 24 hours
     * at 12:00 PM. When the alarm goes off, {@link #showNotification(Context)}
     * will be called.
     */
    public static void initRepeatingNotification(Context context) {
        Intent intent = new Intent(context, NotificationServiceUtils.class);
        intent.putExtra(TAG, ALARM_INTENT_REQUEST_CODE);

        boolean isAlarmSet = PendingIntent.getBroadcast(
                context,
                ALARM_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null;
        // Don't want to set the alarm if the alarm has already ben set
        if (isAlarmSet) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to trigger the notification at 12:00 PM daily
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, HOUR);
        calendar.set(Calendar.MINUTE, MINUTE);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        // Set the pending intent that will trigger the broadcast to
        // show the notification when fired
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_INTENT_REQUEST_CODE,
                intent,
                0);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + calendar.getTimeInMillis(),
                    INTERVAL,
                    alarmPendingIntent);
        }
    }
}
