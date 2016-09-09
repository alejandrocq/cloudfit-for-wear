package com.alejandro_castilla.cloudfitforwear.utilities;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup1;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup5;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.OptionalGroup1;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.ACCModel;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.GPSModel;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.HRModel;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Element;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;
import com.alejandro_castilla.cloudfitforwear.data.AccData;
import com.alejandro_castilla.cloudfitforwear.data.GPSLocation;
import com.alejandro_castilla.cloudfitforwear.data.HeartRate;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Rest;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Running;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String dateOnMillisToString(long dateOnMillis) {
        String dateString;
        Date date = new Date(dateOnMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy",
                new Locale("es", "ES"));
        dateString = simpleDateFormat.format(date);

        return dateString;
    }

    public static ArrayList<ExerciseGroup> createExercisesListFromElement(ArrayList<Element>
                                                                                  elements) {
        ArrayList<ExerciseGroup> exercises = new ArrayList<>();

        for (Element element : elements) {
            if (element instanceof ExerciseGroup) {
                ExerciseGroup ex = (ExerciseGroup) element;
                // Check if exercise is compatible
                if (ex.getGroup().equals(StaticReferences.EXERCISE_GROUP1) ||
                        ex.getGroup().equals(StaticReferences.EXERCISE_GROUP5)) {
                    exercises.add(ex);
                }
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
                    if (exerciseGroup1.getTimeMaxP()>0) {
                        exerciseDescription += "Tiempo Máximo: "
                                + secondsToStandardFormat(exerciseGroup1.getTimeMaxP()) + "\n";
                    }
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
        ArrayList<ExerciseGroup> groups;

        groups = createExercisesListFromElement(training.getElements());

        ArrayList<Exercise> exercises = new ArrayList<>();

        Exercise ex;

        for (ExerciseGroup exerciseGroup : groups) {
            switch (exerciseGroup.getGroup()) {
                case StaticReferences.EXERCISE_GROUP1:
                    ExerciseGroup1 exerciseGroup1 = (ExerciseGroup1) exerciseGroup;
                    ex = new Exercise(exerciseGroup1.getTitle(), exerciseGroup1.getType(),
                            exerciseGroup1.getId());
                    ex.setType(Exercise.TYPE_RUNNING);
                    Running running = new Running();

                    if (exerciseGroup1.getTimeP()>0) {
                        running.setTimeP(exerciseGroup1.getTimeP());
                        if (exerciseGroup1.getTimeMaxP()>0) {
                            running.setTimeMaxP(exerciseGroup1.getTimeMaxP());
                        }
                    } else if (exerciseGroup1.getDistanceP() != -1.0) {
                        running.setDistanceP(exerciseGroup1.getDistanceP());
                    }

                    if (exerciseGroup1.isOptional()) {
                        OptionalGroup1 optionalGroup1 = exerciseGroup1.getOptional();

                        running.setHeartRateMin(optionalGroup1.getHrmin());
                        running.setHeartRateMax(optionalGroup1.getHrmax());

                    }

                    ex.setRunning(running);
                    exercises.add(ex);
                    break;
                case StaticReferences.EXERCISE_GROUP5:
                    ExerciseGroup5 exerciseGroup5 = (ExerciseGroup5) exerciseGroup;
                    ex = new Exercise(exerciseGroup5.getTitle(), exerciseGroup5.getType(),
                            exerciseGroup5.getId());
                    ex.setType(Exercise.TYPE_REST);

                    Rest rest = new Rest();
                    rest.setRestp(exerciseGroup5.getRestp());

                    ex.setRest(rest);
                    exercises.add(ex);
                    break;
            }
        }

        wearableTraining.setExercises(exercises);
        return wearableTraining;
    }

    public static Training buildTrainingToUpload (WearableTraining tr) {
        Training training = new Training();

        training.setId(tr.getCloudFitId());
        training.setTitle(tr.getTitle());
        training.setStartdate(tr.getStartDate());
        training.setEnddate(tr.getEndDate());
        training.setState(StaticReferences.TRAINING_TOUPLOAD);

        //Build exercises groups and set the data

        ArrayList<Element> elements = new ArrayList<>();

        for (Exercise ex : tr.getExercises()) {
            switch (ex.getType()) {
                case Exercise.TYPE_RUNNING:
                    ExerciseGroup1 running = new ExerciseGroup1();

                    running.setTitle(ex.getTitle());
                    running.setGroup(StaticReferences.EXERCISE_GROUP1);
                    running.setType(ex.getCloudFitType());
                    running.setId(ex.getCloudFitId());
                    running.setDistanceP(ex.getRunning().getDistanceP());
                    running.setDistanceR(ex.getRunning().getDistanceR());
                    running.setTimeP((long) ex.getRunning().getTimeP());
                    running.setTimeMaxP((int) ex.getRunning().getTimeMaxP());
                    running.setTimeR((long) ex.getRunning().getTimeR());
                    running.setStarttime(ex.getStartTime());
                    running.setEndtime(ex.getEndTime());
                    running.setHeartRateData(heartRateToHRModel(ex.getHeartRateList()));
                    running.setGPSData(GPSDataToGPSModel(ex.getGPSLocationsList()));
                    running.setAccDataList(ACCDataToACCModel(ex.getAccDataList()));

                    if (ex.getRunning().getHeartRateMin() != -1
                            && ex.getRunning().getHeartRateMax() != -1) {
                        OptionalGroup1 optional = new OptionalGroup1();
                        optional.setHrmin(ex.getRunning().getHeartRateMin());
                        optional.setHrmax(ex.getRunning().getHeartRateMax());
                        optional.setSaveall(true); //TODO Fix this

                        running.setOptional(optional);
                    }

                    elements.add(running);
                    break;
                case Exercise.TYPE_REST:
                    ExerciseGroup5 rest = new ExerciseGroup5();

                    rest.setTitle(ex.getTitle());
                    rest.setGroup(StaticReferences.EXERCISE_GROUP5);
                    rest.setType(ex.getCloudFitType());
                    rest.setId(ex.getCloudFitId());
                    rest.setRestr(ex.getRest().getRestr());
                    rest.setStarttime(ex.getStartTime());
                    rest.setEndtime(ex.getEndTime());
                    elements.add(rest);
                    break;
            }
        }

        training.setElements(elements);

        return training;
    }

    public static ArrayList<HRModel> heartRateToHRModel (ArrayList<HeartRate> hrList) {
        ArrayList<HRModel> hrModels = new ArrayList<>();

        for (HeartRate hr : hrList) {
            HRModel hrModel = new HRModel();
            hrModel.setHr(hr.getValue());
            hrModel.setTimestamp(hr.getTimeStamp());
            hrModel.setRr(0);
            hrModel.setNamesensor("Wearable");
            hrModel.setMacaddress("");
            hrModels.add(hrModel);
        }

        return hrModels;
    }

    public static ArrayList<GPSModel> GPSDataToGPSModel (ArrayList<GPSLocation> GPSData) {
        ArrayList<GPSModel> GPSModels = new ArrayList<>();

        for (GPSLocation l : GPSData) {
            GPSModel model = new GPSModel();
            model.setTimestamp(l.getTimeStamp());
            model.setTime(l.getTime());
            model.setAltitude(l.getAltitude());
            model.setLatitude(l.getLatitude());
            model.setLongitude(l.getLongitude());
            model.setSpeed(l.getSpeed());
            GPSModels.add(model);
        }

        return GPSModels;
    }

    public static ArrayList<ACCModel> ACCDataToACCModel (ArrayList<AccData> accDataList) {
        ArrayList<ACCModel> ACCModels = new ArrayList<>();

        for (AccData accData : accDataList) {
            ACCModel model = new ACCModel();
            model.setTimestamp(accData.getTimeStamp());
            model.setX_AXIS(accData.getxValue());
            model.setY_AXIS(accData.getyValue());
            model.setZ_AXIS(accData.getzValue());
            ACCModels.add(model);
        }

        return ACCModels;
    }

    public static float calculateTotalDistance (ArrayList<GPSLocation> locations) {
        float totalDistance = 0;

        for (int i = 0; i < locations.size(); i++) {
            if (i+1 == locations.size()) break;

            Location loc1 = new Location("Location 1");
            loc1.setLatitude(locations.get(i).getLatitude());
            loc1.setLongitude(locations.get(i).getLongitude());

            Location loc2 = new Location("Location 2");
            loc2.setLatitude(locations.get(i+1).getLatitude());
            loc2.setLongitude(locations.get(i+1).getLongitude());

            totalDistance += loc1.distanceTo(loc2) / 1000; //In kilometers
        }

        return totalDistance;
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

    public static String secondsToStringFormat (double totalSeconds) {
        int hours = (int) totalSeconds / 3600;
        int minutes = (int) (totalSeconds % 3600) / 60;
        int seconds = (int) totalSeconds % 60;

        String timeElapsed = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return timeElapsed;
    }

    public static void checkAppDirectory() {
        File dir = new File (StaticVariables.APP_PATH);

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static void writeAssetFile(Context c, String file, String outputFilePath) {
        try {
            if (new File(outputFilePath).exists()) {
                Log.d(TAG, "Asset file " + file + " already exists on " + outputFilePath);
                return;
            }

            InputStream input = c.getAssets().open(file);

            OutputStream output = new FileOutputStream(outputFilePath);

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
