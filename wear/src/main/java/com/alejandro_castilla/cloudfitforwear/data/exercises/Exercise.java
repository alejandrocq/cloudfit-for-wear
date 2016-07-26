package com.alejandro_castilla.cloudfitforwear.data.exercises;

import com.alejandro_castilla.cloudfitforwear.data.GPSLocation;
import com.alejandro_castilla.cloudfitforwear.data.HeartRate;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 6/07/16.
 */
public class Exercise {

    public static final int TYPE_RUNNING = 1;
    public static final int TYPE_REST = 5;

    private String title;
    private int type;
    private String cloudFitType;
    private long cloudFitId;

    private long startTime;
    private long endTime;
    private ArrayList<HeartRate> heartRateList;
    private ArrayList<GPSLocation> GPSData;

    private Running running;
    private Rest rest;

    public Exercise(String title, String cloudFitType, long cloudFitId) {
        this.title = title;
        this.cloudFitType = cloudFitType;
        this.cloudFitId = cloudFitId;
        heartRateList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<HeartRate> getHeartRateList() {
        return heartRateList;
    }

    public void setHeartRateList(ArrayList<HeartRate> heartRateList) {
        this.heartRateList = heartRateList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Running getRunning() {
        return running;
    }

    public void setRunning(Running running) {
        this.running = running;
    }

    public Rest getRest() {
        return rest;
    }

    public void setRest(Rest rest) {
        this.rest = rest;
    }

    public String getCloudFitType() {
        return cloudFitType;
    }

    public void setCloudFitType(String cloudFitType) {
        this.cloudFitType = cloudFitType;
    }

    public long getCloudFitId() {
        return cloudFitId;
    }

    public void setCloudFitId(long cloudFitId) {
        this.cloudFitId = cloudFitId;
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

    public ArrayList<GPSLocation> getGPSData() {
        return GPSData;
    }

    public void setGPSData(ArrayList<GPSLocation> GPSData) {
        this.GPSData = GPSData;
    }
}
