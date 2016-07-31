package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.data.HeartRate;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 31/07/16.
 */
public class ExerciseCompletedFragment extends ScrollViewFragment {

    private Exercise exercise;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Draw heart rate chart */

        LineChart chart = (LineChart) view.findViewById(R.id.heartRateChart);
        chart.setDescription("Tiempo (s)");

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);

        if (exercise.getType() == Exercise.TYPE_RUNNING) {
            if (exercise.getRunning().getHeartRateMin()>0) {
                LimitLine hrMinLine = new LimitLine(exercise.getRunning().getHeartRateMin(),
                        "Frec. mín.");
                hrMinLine.setLineColor(Color.RED);
                hrMinLine.setLineWidth(3f);
                hrMinLine.setTextColor(Color.BLACK);
                hrMinLine.setTextSize(12f);
                chart.getAxisLeft().addLimitLine(hrMinLine);

                LimitLine hrMaxLine = new LimitLine(exercise.getRunning().getHeartRateMax(),
                        "Frec. máx.");
                hrMaxLine.setLineColor(Color.RED);
                hrMaxLine.setLineWidth(3f);
                hrMaxLine.setTextColor(Color.BLACK);
                hrMaxLine.setTextSize(12f);
                chart.getAxisLeft().addLimitLine(hrMaxLine);
            }
        }

        ArrayList<Entry> hrValues = new ArrayList<>();

        for (HeartRate hr : exercise.getHeartRateList()) {
            Entry hrEntry = new Entry(hr.getTimeMark()/1000, hr.getValue());
            hrValues.add(hrEntry);
        }

        LineDataSet hrDataSet = new LineDataSet(hrValues, "Ritmo cardíaco");
        hrDataSet.setColor(getResources().getColor(R.color.md_grey_700));
        hrDataSet.setCircleColor(getResources().getColor(R.color.md_red_800));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(hrDataSet);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
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
        setRefreshing(false);
    }
}
