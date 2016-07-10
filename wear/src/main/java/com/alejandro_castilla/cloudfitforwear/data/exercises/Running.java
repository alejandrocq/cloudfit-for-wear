package com.alejandro_castilla.cloudfitforwear.data.exercises;

/**
 * Created by alejandrocq on 6/07/16.
 */
public class Running {
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
