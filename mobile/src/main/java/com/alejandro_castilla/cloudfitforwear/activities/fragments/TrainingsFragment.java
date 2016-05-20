package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.TrainingsFragmentAdapter;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.interfaces.FragmentToActivityInterface;
import com.alejandro_castilla.cloudfitforwear.interfaces.TaskToFragmentInterface;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;
import com.blunderer.materialdesignlibrary.views.CardView;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 17/05/16.
 */
public class TrainingsFragment extends ScrollViewFragment implements TaskToFragmentInterface {

    private CardView cardView;
    private RecyclerView recyclerView;
    private TrainingsFragmentAdapter trainingsFragmentAdapter;

    private FragmentToActivityInterface fragmentToActivityInterface;
    private ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        cardView = (CardView) view.findViewById(R.id.cardView);
//
//        cardView.setOnNormalButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Botón de descarga pulsado", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });

        trainingsFragmentAdapter =
                new TrainingsFragmentAdapter(calendarEvents);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(trainingsFragmentAdapter);
        recyclerView.setHasFixedSize(true);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentToActivityInterface = (FragmentToActivityInterface) activity;
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
        Toast.makeText(getActivity(), "Actualizando...", Toast.LENGTH_LONG).show();
        new GetTrainingsTask(getActivity(), this,
                fragmentToActivityInterface.getCloudFitService(), -1,
                StaticVariables.GET_ALL_TRAININGS).execute();
    }

    @Override
    public void stopRefreshing() {
        setRefreshing(false);
    }

    @Override
    public void updateTrainingsList(ArrayList<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
        trainingsFragmentAdapter.setCalendarEvents(calendarEvents);
        trainingsFragmentAdapter.notifyDataSetChanged();
    }
}