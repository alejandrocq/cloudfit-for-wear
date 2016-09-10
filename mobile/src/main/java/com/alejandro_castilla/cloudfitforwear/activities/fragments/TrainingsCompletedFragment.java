package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.TrainingsCompletedFragmentAdapter;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;
import com.alejandro_castilla.cloudfitforwear.utilities.TrainingsDb;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

import java.util.ArrayList;

public class TrainingsCompletedFragment extends ScrollViewFragment
        implements TrainingsCompletedFragmentAdapter.LayoutUpdater {

    private View view;
    private TrainingsCompletedFragmentAdapter trainingsCompletedFragmentAdapter;

    private TrainingsDb db;
    private User cloudFitUser;
    private ArrayList<WearableTraining> trainingsCompleted;
    private CloudFitDataHandler cloudFitDataHandler;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = view;

        trainingsCompletedFragmentAdapter =
                new TrainingsCompletedFragmentAdapter(getActivity(),
                        TrainingsCompletedFragment.this, trainingsCompleted);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trCompletedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(trainingsCompletedFragmentAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        getTrainingsCompleted();
        checkNumberOfTrainingsAndUpdateLayout();
        trainingsCompletedFragmentAdapter.setTrainingsCompleted(trainingsCompleted);

        super.onViewCreated(view, savedInstanceState);
    }

    public void getTrainingsCompleted() {
        trainingsCompleted = db.getAllTrainings(cloudFitUser.getId());

        if (trainingsCompletedFragmentAdapter != null) {
            trainingsCompletedFragmentAdapter.setTrainingsCompleted(trainingsCompleted);
        }

        if (cloudFitDataHandler != null) {
            cloudFitDataHandler.updateTrainingsCompletedNotifications(trainingsCompleted.size());
        }

    }

    public void checkNumberOfTrainingsAndUpdateLayout() {
        if (view != null) {
            ImageView img = (ImageView) view.findViewById(R.id.imgInfo);
            TextView txt = (TextView) view.findViewById(R.id.textNoTrainingsCompleted);

            if (trainingsCompleted.size()>0) {
                img.setVisibility(View.GONE);
                txt.setVisibility(View.GONE);
            } else {
                img.setVisibility(View.VISIBLE);
                txt.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setDb(TrainingsDb db) {
        this.db = db;
    }

    public void setCloudFitUser(User cloudFitUser) {
        this.cloudFitUser = cloudFitUser;
    }

    @Override
    public void updateLayout() {
        getTrainingsCompleted();
        checkNumberOfTrainingsAndUpdateLayout();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cloudFitDataHandler = (CloudFitDataHandler) activity;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_trainings_completed;
    }

    @Override
    public boolean pullToRefreshEnabled() {
        return true;
    }

    @Override
    public int[] getPullToRefreshColorResources() {
        return new int[]{R.color.mdl_color_primary};
    }

    @Override
    public void onRefresh() {
        getTrainingsCompleted();
        checkNumberOfTrainingsAndUpdateLayout();
        setRefreshing(false);
    }
}
