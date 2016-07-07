package com.alejandro_castilla.cloudfitforwear.utilities;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.alejandro_castilla.cloudfitforwear.R;

/**
 * Created by alejandrocq on 6/07/16.
 */
public class Utilities {

    public static void buildNotification (Context ctx, String title, String content) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_cloudfit)
                        .setContentTitle(title)
                        .setContentText(content);
        //Vibration doesn't work, but it allows us to show the notification on top of the activity.
        builder.setVibrate(new long[] { 0, 0, 0 });
        NotificationManager manager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        //Don't need the ID for the moment, so we set it to 0
        manager.notify(0, builder.build());
        vibrate(ctx, 300);
    }

    public static void vibrate (Context ctx, int millis) {
        Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(millis);
    }

}
