package com.alejandro_castilla.cloudfitforwear.utilities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.wearable.activity.ConfirmationActivity;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;

/**
 * Created by alejandrocq on 6/07/16.
 */
public class Utilities {

    public static String buildExerciseInfo (Exercise ex) {
        String exInfo = "Título: "+ex.getTitle()+"\n";

        switch (ex.getType()) {
            case Exercise.TYPE_RUNNING:
                if (ex.getRunning().getTimeP()>0) {
                    exInfo += "Tiempo min.: "
                            + secondsToStandardFormat(ex.getRunning().getTimeP()) + "\n";
                    if (ex.getRunning().getTimeMaxP()>0) {
                        exInfo += "Tiempo max.: "
                                + secondsToStandardFormat(ex.getRunning().getTimeMaxP()) + "\n";
                    }
                } else if (ex.getRunning().getDistanceP() != -1.0) {
                    exInfo += "Distancia: "
                            + secondsToStandardFormat(ex.getRunning().getDistanceP()) + "\n";
                }

                if (ex.getRunning().getHeartRateMin()>0 && ex.getRunning().getHeartRateMax()>0) {
                    exInfo += "Frec. min.: "
                            + ex.getRunning().getHeartRateMin() + "\n"
                            + "Frec. max.: " + ex.getRunning().getHeartRateMax()
                            + "\n";
                }
                break;
            case Exercise.TYPE_REST:
                exInfo += "Descanso: "
                        + secondsToStandardFormat(ex.getRest().getRestp());
                break;
        }

        return exInfo;
    }

    public static String secondsToStandardFormat (long totalSeconds) {
        String time;

        int hours = (int) totalSeconds / 3600;
        int minutes = (int) (totalSeconds % 3600) / 60;
        int seconds = (int) totalSeconds % 60;

        time = hours + " h " + minutes + " m " + seconds + " s";

        return time;

    }

    public static String secondsToStandardFormat (double totalSeconds) {
        String time;

        int hours = (int) totalSeconds / 3600;
        int minutes = (int) (totalSeconds % 3600) / 60;
        int seconds = (int) totalSeconds % 60;

        time = hours + " h " + minutes + " m " + seconds + " s";

        return time;

    }

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

    public static void showConfirmation (Activity context, String message, int animationType) {
        Intent intent = new Intent (context,
                ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, animationType);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        context.startActivity(intent);
    }

}
