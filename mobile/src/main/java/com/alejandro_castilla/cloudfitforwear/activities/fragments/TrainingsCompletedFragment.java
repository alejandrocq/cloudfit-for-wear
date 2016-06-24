package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.TrainingsCompletedFragmentAdapter;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 24/06/16.
 */
public class TrainingsCompletedFragment extends ScrollViewFragment {

    private View view;
    private TrainingsCompletedFragmentAdapter trainingsCompletedFragmentAdapter;

    private ArrayList<WearableTraining> trainingsCompleted;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = view;

        trainingsCompleted = checkTrainingsCompleted();

        trainingsCompletedFragmentAdapter =
                new TrainingsCompletedFragmentAdapter(getActivity(), trainingsCompleted);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trCompletedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(trainingsCompletedFragmentAdapter);
        recyclerView.setHasFixedSize(true);

        checkNumberOfTrainingsAndUpdateLayout();

        super.onViewCreated(view, savedInstanceState);
    }

    public ArrayList<WearableTraining> checkTrainingsCompleted() {
        //TODO Check trainings on database

        return new ArrayList<>();
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
        //TODO Call database and get data
        setRefreshing(false);
    }
}
