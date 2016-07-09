package com.alejandro_castilla.cloudfitforwear.data.exercises;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
