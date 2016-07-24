package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.MainActivityGridPagerAdapter;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.interfaces.WearableHandler;
import com.alejandro_castilla.cloudfitforwear.services.WearableService;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends WearableActivity implements WearableHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    private Intent wearableServiceIntent;
    private WearableService wearableService;

    /* Layout Views */
    private TextView
            startInfoTextView,
            trainingNotCompletedTextView,
            trainingCompletedTextView,
            uploadActionTextView,
            deleteActionTextView;
    private ImageView
            startActionImgView,
            settingsImgView,
            trainingNotCompletedImgView,
            uploadActionImgView,
            deleteActionImgView;
    private GridViewPager gridViewPager;
    private AVLoadingIndicatorView uploadProgressView;

    private ServiceConnection wearableServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Connected to Wearable Service");
            WearableService.WearableServiceBinder wearableServiceBinder =
                    (WearableService.WearableServiceBinder) service;
            wearableService = wearableServiceBinder.getService();
            wearableService.setWearableHandler(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from Wearable Service");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                startInfoTextView = (TextView) findViewById(R.id.startInfoText);
                settingsImgView = (ImageView) stub.findViewById(R.id.settingsImg);
                settingsImgView.setOnClickListener(new ActionButtonsClickListener());

                startActionImgView = (ImageView) stub.findViewById(R.id.startActionImg);
                startActionImgView.setOnClickListener(new ActionButtonsClickListener());

                trainingNotCompletedImgView = (ImageView)
                        findViewById(R.id.trainingNotCompletedImg);
                trainingNotCompletedTextView = (TextView)
                        findViewById(R.id.trainingNotCompletedText);
                trainingCompletedTextView = (TextView)
                        findViewById(R.id.trainingCompletedText);
                uploadActionImgView = (ImageView) findViewById(R.id.uploadActionImg);
                uploadActionTextView = (TextView) findViewById(R.id.uploadActionText);
                uploadActionImgView.setOnClickListener(new ActionButtonsClickListener());
                deleteActionImgView = (ImageView) findViewById(R.id.deleteActionImg);
                deleteActionTextView = (TextView) findViewById(R.id.deleteActionText);
                deleteActionImgView.setOnClickListener(new ActionButtonsClickListener());
                uploadProgressView = (AVLoadingIndicatorView) findViewById(R.id.uploadProgressView);

                gridViewPager = (GridViewPager) stub.findViewById(R.id.pager);
                gridViewPager.setAdapter(new MainActivityGridPagerAdapter(MainActivity.this));
                gridViewPager.setOffscreenPageCount(2);
                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator)
                        findViewById(R.id.page_indicator);
                dotsPageIndicator.setPager(gridViewPager);

                checkTrainingsAndUpdateLayout();
            }
        });

        setAmbientEnabled();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        wearableServiceIntent = new Intent(MainActivity.this, WearableService.class);
        bindService(wearableServiceIntent, wearableServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        unbindService(wearableServiceConnection);
        super.onDestroy();
    }

    @Override
    public void saveWearableTraining(WearableTraining tr) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(StaticVariables.KEY_TRAINING_TO_BE_DONE, gson.toJson(tr));
        editor.apply();

        Utilities.showConfirmation(this, "Entrenamiento recibido",
                ConfirmationActivity.SUCCESS_ANIMATION);
        checkTrainingsAndUpdateLayout();
    }

    @Override
    public void showTrainingSentConfirmationAndUpdateData(boolean result) {
        if (result) {
            uploadProgressView.setVisibility(View.GONE);
            Utilities.showConfirmation(this, "Entrenamiento enviado correctamente",
                    ConfirmationActivity.SUCCESS_ANIMATION);
            deleteTrainingDone();
            checkTrainingsAndUpdateLayout();
        } else {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
        }
    }

    private void checkTrainingsAndUpdateLayout() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        String training = prefs.getString(StaticVariables.KEY_TRAINING_TO_BE_DONE, "");
        String trainingDone = prefs.getString(StaticVariables.KEY_TRAINING_DONE, "");

        if (!training.equals("")) {
            if (!trainingDone.equals("")) {
                //Training available but there is a training ready to be synced.
                startInfoTextView.setText(R.string.text_training_ready_to_sync);
                setTrainingNotCompletedLayoutVisibility(View.GONE);
                setTrainingCompletedLayoutVisibility(View.VISIBLE);
            } else {
                startInfoTextView.setText(R.string.text_training_available);
                setTrainingNotCompletedLayoutVisibility(View.VISIBLE);
                setTrainingCompletedLayoutVisibility(View.GONE);
            }
        } else if (!trainingDone.equals("")) {
            startInfoTextView.setText(R.string.text_training_ready_to_sync);
            setTrainingNotCompletedLayoutVisibility(View.GONE);
            setTrainingCompletedLayoutVisibility(View.VISIBLE);
        } else {
            startInfoTextView.setText(R.string.text_training_not_available);
            setTrainingNotCompletedLayoutVisibility(View.VISIBLE);
            setTrainingCompletedLayoutVisibility(View.GONE);
        }
    }

    private void setTrainingNotCompletedLayoutVisibility(int visibility) {
        trainingNotCompletedTextView.setVisibility(visibility);
        trainingNotCompletedImgView.setVisibility(visibility);
    }

    private void setTrainingCompletedLayoutVisibility(int visibility) {
        trainingCompletedTextView.setVisibility(visibility);
        uploadActionImgView.setVisibility(visibility);
        uploadActionTextView.setVisibility(visibility);
        deleteActionImgView.setVisibility(visibility);
        deleteActionTextView.setVisibility(visibility);
    }

    private void deleteTrainingDone() {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString(StaticVariables.KEY_TRAINING_DONE, "");
        editor.apply();
    }

    private class ActionButtonsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int resId = v.getId();
            switch (resId) {
                case R.id.startActionImg:
                    Intent training = new Intent(MainActivity.this,
                            TrainingActivity.class);
                    startActivity(training);
                    finish();
                    break;
                case R.id.settingsImg:
                    Intent settings = new Intent (MainActivity.this,
                            SettingsActivity.class);
                    startActivity(settings);
                    break;
                case R.id.uploadActionImg:
                    uploadProgressView.setVisibility(View.VISIBLE);
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(MainActivity.this);
                    String trainingDone = prefs.getString(StaticVariables.KEY_TRAINING_DONE, "");
                    wearableService.sendTrainingDoneToHandheld(trainingDone);
                    break;
                case R.id.deleteActionImg:
                    deleteTrainingDone();
                    Utilities.showConfirmation(MainActivity.this,
                            "Entrenamiento eliminado correctamente",
                            ConfirmationActivity.SUCCESS_ANIMATION);
                    checkTrainingsAndUpdateLayout();
                    break;
            }
        }
    }

}
