package com.alejandro_castilla.cloudfitforwear.data;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 24/04/16.
 */
public class TrainingData {

    /* Data fields */
    private String date;
    private long elapsedTime;
    private ArrayList<HeartRate> heartRateList;

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

    /* Get methods */

    public String getDate() {
        return date;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public ArrayList<HeartRate> getHeartRateList() {
        return heartRateList;
    }

    /* Set methods */

    public void setDate(String date) {
        this.date = date;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setHeartRateList(ArrayList<HeartRate> heartRateList) {
        this.heartRateList = heartRateList;
    }
}