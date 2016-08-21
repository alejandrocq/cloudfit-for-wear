package com.alejandro_castilla.cloudfitforwear.data;

import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;

import java.util.ArrayList;

public class WearableTraining {

    private String title;
    private long trainingId;
    private long userId;
    private long startDate;
    private long endDate;
    private long cloudFitId;

    private ArrayList<Exercise> exercises;

    public WearableTraining(String title, long cloudFitId, long userId) {
        this.title = title;
        this.cloudFitId = cloudFitId;
        this.userId = userId;
    }

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

    public long getCloudFitId() {
        return cloudFitId;
    }

    public void setCloudFitId(long cloudFitId) {
        this.cloudFitId = cloudFitId;
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

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }
}
