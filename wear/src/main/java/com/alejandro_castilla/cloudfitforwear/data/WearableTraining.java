package com.alejandro_castilla.cloudfitforwear.data;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 25/05/16.
 */
public class WearableTraining {

    private String trainingTitle;
    private long trainingID;
    private long userID;
    private long startTime;
    private long endTime;

    private RunningExercise runningExercise;
    private RestExercise restExercise;
    private ArrayList<HeartRate> heartRateList;

    public WearableTraining(String trainingTitle, long trainingID, long userID) {
        this.trainingTitle = trainingTitle;
        this.trainingID = trainingID;
        this.userID = userID;
        runningExercise = new RunningExercise();
        restExercise = new RestExercise();
    }

    /* Exercises classes */

    public class RunningExercise {
        /* Parameters set by the trainer */
        private double distanceP;
        private double timeP;
        private double timeMaxP;

        private int heartRateMin;
        private int heartRateMax;

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
        private int restp; //Set by trainer
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

    /* Data classes */

    public class HeartRate {
        private long timeMark;
        private int heartRateValue;

        public long getTimeMark() {
            return timeMark;
        }

        public void setTimeMark(long timeMark) {
            this.timeMark = timeMark;
        }

        public int getHeartRateValue() {
            return heartRateValue;
        }

        public void setHeartRateValue(int heartRateValue) {
            this.heartRateValue = heartRateValue;
        }

        @Override
        public String toString() {
            return "Time: " + timeMark + "\n" + "Heart Rate: " + heartRateValue + "\n";
        }
    }

    /* Get and set methods */

    public String getTrainingTitle() {
        return trainingTitle;
    }

    public void setTrainingTitle(String trainingTitle) {
        this.trainingTitle = trainingTitle;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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
}
