package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

/**
 * Created by alejandrocq on 31/07/16.
 */
public class ExerciseCompletedFragment extends ScrollViewFragment {

    private Exercise exercise;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    /* Scroll view fragment methods */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_exercise_completed;
    }

    @Override
    public boolean pullToRefreshEnabled() {
        return false;
    }

    @Override
    public int[] getPullToRefreshColorResources() {
        return new int[0];
    }

    @Override
    public void onRefresh() {

    }
}
