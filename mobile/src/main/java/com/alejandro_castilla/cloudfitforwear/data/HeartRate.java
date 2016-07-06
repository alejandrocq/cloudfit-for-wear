package com.alejandro_castilla.cloudfitforwear.data;

/**
 * Created by alejandrocq on 29/06/16.
 */

public class HeartRate {

    private long timeMark;
    private int value;

    public HeartRate(long timeMark, int value) {
        this.timeMark = timeMark;
        this.value = value;
    }

    public long getTimeMark() {
        return timeMark;
    }

    public void setTimeMark(long timeMark) {
        this.timeMark = timeMark;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Time: " + timeMark + "\n" + "Heart Rate: " + value + "\n";
    }
}
