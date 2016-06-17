package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.TrainingsFragmentAdapter;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 17/05/16.
 */
public class TrainingsFragment extends ScrollViewFragment {

    private RecyclerView recyclerView;
    private TrainingsFragmentAdapter trainingsFragmentAdapter;

    private ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();

    private ActivityInterface activityInterface;

    public void setCalendarEvents(ArrayList<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
        trainingsFragmentAdapter.setCalendarEvents(calendarEvents);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        trainingsFragmentAdapter =
                new TrainingsFragmentAdapter(getActivity(), calendarEvents);
        recyclerView = (RecyclerView) view.findViewById(R.id.trRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(trainingsFragmentAdapter);
        recyclerView.setHasFixedSize(true);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityInterface = (ActivityInterface) activity;
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
                activityInterface.getCloudFitService(), -1,
                StaticVariables.GET_ALL_TRAININGS).execute();
    }
}