package com.alejandro_castilla.cloudfitforwear.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup1;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup5;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.OptionalGroup1;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alejandrocq on 14/05/16.
 */
public class Utilities {

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean connected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        return connected;
    }

    public static String dateOnMillisToString(long dateOnMillis) {
        String dateString;
        Date date = new Date(dateOnMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        dateString = simpleDateFormat.format(date);

        return dateString;
    }

    public static String buildExerciseDescriptionFromExerciseGroup (ExerciseGroup exerciseGroup) {
        String exerciseDescription = "";

        switch (exerciseGroup.getGroup()) {
            case StaticReferences.EXERCISE_GROUP1:
                ExerciseGroup1 exerciseGroup1 = (ExerciseGroup1) exerciseGroup;

                if (exerciseGroup1.getTimeP()>0) {
                    exerciseDescription += "Tiempo Mínimo: "
                            + secondsToStandardFormat(exerciseGroup1.getTimeP()) + "\n";
                } else if (exerciseGroup1.getTimeMaxP()>0) {
                    exerciseDescription += "Tiempo Máximo: "
                            + secondsToStandardFormat(exerciseGroup1.getTimeMaxP()) + "\n";
                } else if (exerciseGroup1.getDistanceP() != -1.0) {
                    exerciseDescription += "Distancia: "
                            + secondsToStandardFormat(exerciseGroup1.getDistanceP()) + "\n";
                }

                if (exerciseGroup1.isOptional()) {
                    OptionalGroup1 exerciseOptional = exerciseGroup1.getOptional();
                    exerciseDescription += "Frecuencia cardíaca mínima: "
                            + exerciseOptional.getHrmin() + "\n"
                            + "Frecuencia cardíaca máxima: " + exerciseOptional.getHrmax()
                            + "\n";
                }

                break;
            case StaticReferences.EXERCISE_GROUP5:
                ExerciseGroup5 exerciseGroup5 = (ExerciseGroup5) exerciseGroup;
                exerciseDescription += "Tiempo de descanso: "
                        + secondsToStandardFormat(exerciseGroup5.getRestp());
                break;
        }
        return exerciseDescription;
    }

    public static String secondsToStandardFormat (long totalSeconds) {
        String time;

        int hours = (int) totalSeconds / 3600;
        int minutes = (int) (totalSeconds % 3600) / 60;
        int seconds = (int) totalSeconds % 60;

        time = hours + " hora(s) " + minutes + " minuto(s) " + seconds + " segundo(s)";

        return time;

    }

    public static String secondsToStandardFormat (double totalSeconds) {
        String time;

        int hours = (int) totalSeconds / 3600;
        int minutes = (int) (totalSeconds % 3600) / 60;
        int seconds = (int) totalSeconds % 60;

        time = hours + " hora(s) " + minutes + " minuto(s) " + seconds + " segundo(s)";

        return time;

    }


}
