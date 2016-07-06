package com.alejandro_castilla.cloudfitforwear.data.exercises;

import com.alejandro_castilla.cloudfitforwear.data.HeartRate;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 6/07/16.
 */
public class Exercise {

    private String title;
    private ArrayList<HeartRate> heartRateList;

    public Exercise(String title) {
        this.title = title;
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
}
