package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.data.HeartRate;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 31/07/16.
 */
public class ExerciseCompletedFragment extends ScrollViewFragment implements OnMapReadyCallback {

    private WearableTraining training;
    private Exercise exercise;

    private MapView mapView;

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
            if (exercise.getRunning().getHeartRateMin() > 0) {
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
            Entry hrEntry = new Entry(hr.getTimeMark() / 1000, hr.getValue());
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

        /*Set results*/

        TextView timeElapsedTextView = (TextView) view.findViewById(R.id.timeElapsedText);
        TextView averageHrTextView = (TextView) view.findViewById(R.id.averageHrText);
        TextView maxHrTextView = (TextView) view.findViewById(R.id.maxHrText);
        TextView minHrTextView = (TextView) view.findViewById(R.id.minHrText);

        if (exercise.getType() == Exercise.TYPE_RUNNING) {
            timeElapsedTextView
                    .setText(Utilities.secondsToStringFormat(exercise.getRunning().getTimeR()));
        } else {
            timeElapsedTextView
                    .setText(Utilities.secondsToStringFormat(exercise.getRest().getRestr()));
        }

        int sumHr = 0;
        for (HeartRate hr : exercise.getHeartRateList()) {
            sumHr += hr.getValue();
        }

        int averageHr = sumHr / exercise.getHeartRateList().size();
        averageHrTextView.setText(averageHr + " bpm (medio)");

        maxHrTextView.setText((int) chart.getYMax() + " bpm (máx.)");
        minHrTextView.setText((int) chart.getYMin() + " bpm (mín.)");

        /* Load map */

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setTraining(WearableTraining training) {
        this.training = training;
    }

    /* Google Maps API methods */

    @Override
    public void onMapReady(GoogleMap map) {
        Location startLocation = exercise.getGPSData().get(0).getLocation();
        LatLng l = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
        map.addMarker(new MarkerOptions()
        .position(l)
        .title("Inicio ruta"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
        mapView.onResume();
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
