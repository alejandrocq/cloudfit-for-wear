package com.alejandro_castilla.cloudfitforwear.activities;

import android.os.Bundle;

import com.alejandro_castilla.cloudfitforwear.activities.fragments.ExerciseCompletedFragment;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.blunderer.materialdesignlibrary.activities.ViewPagerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.ViewPagerHandler;
import com.google.gson.Gson;

public class TrainingCompletedActivity extends ViewPagerActivity {

    private WearableTraining training;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* Material Design Library methods */

    @Override
    public ViewPagerHandler getViewPagerHandler() {
        ViewPagerHandler handler = new ViewPagerHandler(this);
        Gson gson = new Gson();

        training = gson.fromJson(getIntent().getStringExtra("training_completed"),
                WearableTraining.class);

        for (Exercise ex : training.getExercises()) {
            ExerciseCompletedFragment fragment = new ExerciseCompletedFragment();
            fragment.setExercise(ex);
            handler.addPage(ex.getTitle(), fragment);
        }
        return handler;
    }

    @Override
    public int defaultViewPagerPageSelectedPosition() {
        return 0;
    }

    @Override
    public boolean showViewPagerIndicator() {
        return true;
    }

    @Override
    public boolean replaceActionBarTitleByViewPagerPageTitle() {
        return true;
    }

    @Override
    protected boolean enableActionBarShadow() {
        return false;
    }

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return new ActionBarDefaultHandler(this);
    }
}
