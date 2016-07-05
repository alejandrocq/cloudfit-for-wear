package com.alejandro_castilla.cloudfitforwear.data;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 25/05/16.
 */
public class WearableTraining {

    public static final int NOT_UPLOADED = 0;
    public static final int UPLOADED = 1;

    private String title;
    private long trainingId;
    private long userId;
    private long startDate;
    private long endDate;
    private long cloudFitId;
    private int state;

    private RunningExercise runningExercise;
    private RestExercise restExercise;
    private ArrayList<HeartRate> heartRateList;

    public WearableTraining(String title, long cloudFitId, long userId) {
        this.title = title;
        this.cloudFitId = cloudFitId;
        this.userId = userId;
        runningExercise = new RunningExercise();
        restExercise = new RestExercise();
        heartRateList = new ArrayList<>();
    }

    /* Exercises classes */

    public class RunningExercise {
        /* Parameters set by the trainer */
        private double distanceP = -1.0;
        private double timeP = -1.0;
        private double timeMaxP = -1.0;

        private int heartRateMin = -1;
        private int heartRateMax = -1;

        /* Goals made by the athlete */
        private double distanceR;
        private double timeR;

        public double getDistanceP() {
            return distanceP;
        }

        public void setDistanceP(double distanceP) {
            this.distanceP = distanceP;
        }

        public double getTimeP() {
            return timeP;
        }

        public void setTimeP(double timeP) {
            this.timeP = timeP;
        }

        public double getTimeMaxP() {
            return timeMaxP;
        }

        public void setTimeMaxP(double timeMaxP) {
            this.timeMaxP = timeMaxP;
        }

        public int getHeartRateMin() {
            return heartRateMin;
        }

        public void setHeartRateMin(int heartRateMin) {
            this.heartRateMin = heartRateMin;
        }

        public int getHeartRateMax() {
            return heartRateMax;
        }

        public void setHeartRateMax(int heartRateMax) {
            this.heartRateMax = heartRateMax;
        }

        public double getDistanceR() {
            return distanceR;
        }

        public void setDistanceR(double distanceR) {
            this.distanceR = distanceR;
        }

        public double getTimeR() {
            return timeR;
        }

        public void setTimeR(double timeR) {
            this.timeR = timeR;
        }
    }

    public class RestExercise {
        private int restp = -1; //Set by trainer
        private int restr; //Goal of athlete

        public int getRestp() {
            return restp;
        }

        public void setRestp(int restp) {
            this.restp = restp;
        }

        public int getRestr() {
            return restr;
        }

        public void setRestr(int restr) {
            this.restr = restr;
        }
    }

    /* Get and set methods */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public RunningExercise getRunningExercise() {
        return runningExercise;
    }

    public void setRunningExercise(RunningExercise runningExercise) {
        this.runningExercise = runningExercise;
    }

    public RestExercise getRestExercise() {
        return restExercise;
    }

    public void setRestExercise(RestExercise restExercise) {
        this.restExercise = restExercise;
    }

    public ArrayList<HeartRate> getHeartRateList() {
        return heartRateList;
    }

    public void setHeartRateList(ArrayList<HeartRate> heartRateList) {
        this.heartRateList = heartRateList;
    }

    public long getCloudFitId() {
        return cloudFitId;
    }

    public void setCloudFitId(long cloudFitId) {
        this.cloudFitId = cloudFitId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(long trainingId) {
        this.trainingId = trainingId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
