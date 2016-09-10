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
import com.alejandro_castilla.cloudfitforwear.activities.adapters.TrainingsFragmentAdapter;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 17/05/16.
 */
public class TrainingsFragment extends ScrollViewFragment {

    private View view;
    private RecyclerView recyclerView;
    private TrainingsFragmentAdapter trainingsFragmentAdapter;

    private ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();

    private CloudFitDataHandler cloudFitDataHandler;

    public void setCalendarEvents(ArrayList<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
        trainingsFragmentAdapter.setCalendarEvents(calendarEvents);
        checkNumberOfTrainingsAndUpdateLayout();
    }

    public void checkNumberOfTrainingsAndUpdateLayout() {
        if (view != null) {
            ImageView img = (ImageView) view.findViewById(R.id.imgInfo);
            TextView txt = (TextView) view.findViewById(R.id.textNoTrainings);

            if (calendarEvents.size()>0) {
                img.setVisibility(View.GONE);
                txt.setVisibility(View.GONE);
            } else {
                img.setVisibility(View.VISIBLE);
                txt.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        trainingsFragmentAdapter =
                new TrainingsFragmentAdapter(getActivity(), calendarEvents);
        recyclerView = (RecyclerView) view.findViewById(R.id.trRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(trainingsFragmentAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        checkNumberOfTrainingsAndUpdateLayout();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cloudFitDataHandler = (CloudFitDataHandler) activity;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_trainings;
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
        new GetTrainingsTask(getActivity(),
                cloudFitDataHandler.getCloudFitService(), -1,
                StaticVariables.GET_ALL_TRAININGS).execute();
    }
}