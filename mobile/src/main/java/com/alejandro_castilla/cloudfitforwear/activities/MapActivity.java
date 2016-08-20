package com.alejandro_castilla.cloudfitforwear.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.data.GPSLocation;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

public class MapActivity extends com.blunderer.materialdesignlibrary.activities.Activity
        implements OnMapReadyCallback {

    private MapView mapView;
    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ruta seguida");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        exercise = new Gson().fromJson(getIntent().getStringExtra("exercise"), Exercise.class);

        mapView = (MapView) findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (exercise.getGPSData().size()>0) {
            GPSLocation startLocation = exercise.getGPSData().get(0);
            LatLng l1 = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(l1)
                    .title("Inicio de la ruta"));

            int endIndex = exercise.getGPSData().size() - 1;
            GPSLocation endLocation = exercise.getGPSData().get(endIndex);
            LatLng l2 = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(l2)
                    .title("Final de la ruta"));

            //Move camera to start location
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(l1, 15));
            mapView.onResume();
        }

        //Draw route
        PolylineOptions polOptions = new PolylineOptions();

        for (GPSLocation l : exercise.getGPSData()) {
            polOptions.add(new LatLng(l.getLatitude(), l.getLongitude()));
        }

        map.addPolyline(polOptions);
        mapView.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_map;
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
