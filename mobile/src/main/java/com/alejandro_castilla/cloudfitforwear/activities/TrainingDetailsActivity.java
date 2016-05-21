package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.blunderer.materialdesignlibrary.activities.ViewPagerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.ViewPagerHandler;

import java.util.ArrayList;

public class TrainingDetailsActivity extends ViewPagerActivity implements ActivityInterface {

    private final String TAG = TrainingDetailsActivity.class.getSimpleName();

    private long trainingID;
    private Training training;

    private CloudFitService cloudFitService;

    /**
     * ServiceConnection to connect to CloudFit service.
     */
    private ServiceConnection cloudFitServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Connected to CloudFit service");
            CloudFitService.MyBinder cloudFitServiceBinder = (CloudFitService.MyBinder) service;
            cloudFitService = cloudFitServiceBinder.getService();
            new GetTrainingsTask(TrainingDetailsActivity.this, TrainingDetailsActivity.this,
                    cloudFitService, trainingID, StaticVariables.GET_SINGLE_TRAINING).execute();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from CloudFit service");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
//        setContentView(R.layout.activity_training_details);
        trainingID = getIntent().getLongExtra("trainingid", -1);
        if (trainingID == -1) {
            Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.d(TAG, "Trying to get training with ID: "+trainingID);
            Intent startCloudFitServiceIntent = new Intent (this, CloudFitService.class);
            bindService(startCloudFitServiceIntent, cloudFitServiceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(cloudFitServiceConnection);
    }

    /* Activity interface methods */

    @Override
    public void saveAndParseTraining(Training training) {
        Log.d(TAG, "Training title: "+ training.getTitle());
        setTitle(training.getTitle());
    }

    @Override
    public void saveUserInfo(User cloudFitUser, ArrayList<RequestTrainer> request) {
        //Not needed.
    }

    @Override
    public void stopRefreshing() {
        //Not needed.
    }

    @Override
    public void updateTrainingsList(ArrayList<CalendarEvent> calendarEvents) {
        //Not needed.
    }

    @Override
    public CloudFitService getCloudFitService() {
        return null; //Not needed.
    }

    /* Material design library methods */

    @Override
    public ViewPagerHandler getViewPagerHandler() {
        return null;
    }

    @Override
    public boolean showViewPagerIndicator() {
        return false;
    }

    @Override
    public boolean replaceActionBarTitleByViewPagerPageTitle() {
        return false;
    }

    @Override
    protected boolean enableActionBarShadow() {
        return false;
    }

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return new ActionBarDefaultHandler(this);
    }

    @Override
    public int defaultViewPagerPageSelectedPosition() {
        return 0;
    }
}
