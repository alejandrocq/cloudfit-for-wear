package com.alejandro_castilla.cloudfitforwear.data;

import com.alejandro_castilla.cloudfitforwear.data.exercises.Rest;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Running;

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

    private Running running;
    private Rest rest;

    public WearableTraining(String title, long cloudFitId, long userId) {
        this.title = title;
        this.cloudFitId = cloudFitId;
        this.userId = userId;
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
