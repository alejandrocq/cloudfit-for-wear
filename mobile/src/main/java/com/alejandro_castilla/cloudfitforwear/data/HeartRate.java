package com.alejandro_castilla.cloudfitforwear.data;

public class HeartRate {

    private long timeStamp;
    private int value;

    public HeartRate(long timeStamp, int value) {
        this.timeStamp = timeStamp;
        this.value = value;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Time: " + timeStamp + "\n" + "Heart Rate: " + value + "\n";
    }
}
