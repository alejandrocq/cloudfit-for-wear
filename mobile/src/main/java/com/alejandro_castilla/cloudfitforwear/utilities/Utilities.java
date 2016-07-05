package com.alejandro_castilla.cloudfitforwear.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup1;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup5;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.OptionalGroup1;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Element;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static ArrayList<ExerciseGroup> createExercisesListFromElement(ArrayList<Element>
                                                                                  elements) {
        ArrayList<ExerciseGroup> exercises = new ArrayList<>();

        for (Element element : elements) {
            if (element instanceof ExerciseGroup) {
                ExerciseGroup exerciseGroup = (ExerciseGroup) element;
                exercises.add(exerciseGroup);
            }
        }

        return exercises;
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

    public static WearableTraining trainingToWearableTraining (Training training,
                                                               User cloudFitUser) {

        WearableTraining wearableTraining = new WearableTraining(training.getTitle(),
                training.getId(), cloudFitUser.getId());
        ArrayList<ExerciseGroup> exercises;

        exercises = createExercisesListFromElement(training.getElements());

        for (ExerciseGroup exerciseGroup : exercises) {
            switch (exerciseGroup.getGroup()) {
                case StaticReferences.EXERCISE_GROUP1:
                    ExerciseGroup1 exerciseGroup1 = (ExerciseGroup1) exerciseGroup;
                    WearableTraining.RunningExercise runningExercise =
                            wearableTraining.getRunningExercise();

                    if (exerciseGroup1.getTimeP()>0) {
                        runningExercise.setTimeP(exerciseGroup1.getTimeP());
                        if (exerciseGroup1.getTimeMaxP()>0) {
                            runningExercise.setTimeMaxP(exerciseGroup1.getTimeMaxP());
                        }
                    } else if (exerciseGroup1.getDistanceP() != -1.0) {
                        runningExercise.setDistanceP(exerciseGroup1.getDistanceP());
                    }

                    if (exerciseGroup1.isOptional()) {
                        OptionalGroup1 optionalGroup1 = exerciseGroup1.getOptional();

                        runningExercise.setHeartRateMin(optionalGroup1.getHrmin());
                        runningExercise.setHeartRateMax(optionalGroup1.getHrmax());

                    }

                    wearableTraining.setRunningExercise(runningExercise);

                    break;
                case StaticReferences.EXERCISE_GROUP5:
                    ExerciseGroup5 exerciseGroup5 = (ExerciseGroup5) exerciseGroup;
                    WearableTraining.RestExercise restExercise =
                            wearableTraining.getRestExercise();

                    restExercise.setRestp(exerciseGroup5.getRestp());
                    wearableTraining.setRestExercise(restExercise);
                    break;
            }
        }
        return wearableTraining;
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

    public static void checkAppDirectory() {
        File dir = new File (StaticVariables.APP_PATH);

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static void writeAssetFile(Context c, String file, String path) {
        try {
            if (new File(path).exists()) {
                Log.d("UTILS", "File path: " + path);
                return;
            }

            InputStream input = c.getAssets().open(file);
            String outputFile = path;

            OutputStream output = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
