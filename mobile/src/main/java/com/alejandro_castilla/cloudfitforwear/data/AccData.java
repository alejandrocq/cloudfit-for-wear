package com.alejandro_castilla.cloudfitforwear.data;

public class AccData {

    private double xValue;
    private double yValue;
    private double zValue;
    private long timeStamp;

    public AccData() {
        xValue = 0.0;
        yValue = 0.0;
        zValue = 0.0;
        timeStamp = 0;
    }

    public double getxValue() {
        return xValue;
    }

    public void setxValue(double xValue) {
        this.xValue = xValue;
    }

    public double getyValue() {
        return yValue;
    }

    public void setyValue(double yValue) {
        this.yValue = yValue;
    }

    public double getzValue() {
        return zValue;
    }

    public void setzValue(double zValue) {
        this.zValue = zValue;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
