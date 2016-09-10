package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.ExercisesListAdapter;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.activities.ScrollViewActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;

import java.util.ArrayList;

public class TrainingDetailsActivity extends ScrollViewActivity implements CloudFitDataHandler {

    private final String TAG = TrainingDetailsActivity.class.getSimpleName();

    private long trainingID;
    private ArrayList<ExerciseGroup> exercises;

    private ExercisesListAdapter exercisesListAdapter;
    private MaterialDialog downloadingTrainingDialog;

    /**
     * ServiceConnection to connect to CloudFit service.
     */
    private ServiceConnection cloudFitServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Connected to CloudFit service");
            CloudFitService.MyBinder cloudFitServiceBinder = (CloudFitService.MyBinder) service;
            CloudFitService cloudFitService = cloudFitServiceBinder.getService();
            new GetTrainingsTask(TrainingDetailsActivity.this,
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
        setTitle(getIntent().getStringExtra("trainingname"));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        trainingID = getIntent().getLongExtra("trainingid", -1);
        if (trainingID == -1) {
            Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.d(TAG, "Trying to get training with ID: "+trainingID);
            Intent startCloudFitServiceIntent = new Intent (this, CloudFitService.class);
            bindService(startCloudFitServiceIntent, cloudFitServiceConnection, BIND_AUTO_CREATE);
            showDownloadingTrainingDialog();
        }

        exercises = new ArrayList<>();
        exercisesListAdapter = new ExercisesListAdapter(this, exercises);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.trDetailsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(exercisesListAdapter);
        recyclerView.setHasFixedSize(true);

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
    protected void onDestroy() {
        super.onDestroy();
        unbindService(cloudFitServiceConnection);
    }

    public void checkNumberOfExercisesAndUpdateLayout() {
        ImageView img = (ImageView) findViewById(R.id.imgInfo);
        TextView txt = (TextView) findViewById(R.id.textNoTrainingsCompleted);

        if (exercises.size()>0) {
            img.setVisibility(View.GONE);
            txt.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void showDownloadingTrainingDialog() {
        downloadingTrainingDialog = new MaterialDialog.Builder(this)
                .title("Descargando datos del entrenamiento")
                .content("Espere...")
                .progress(true, 0)
                .cancelable(false)
                .titleColorRes(R.color.md_grey_800)
                .contentColorRes(R.color.md_grey_800)
                .backgroundColorRes(R.color.md_white_1000)
                .build();

        downloadingTrainingDialog.show();
    }

    /////////////////////////////
    /* CloudFitHandler methods */
    /////////////////////////////

    @Override
    public void processTrainingDownloaded(Training training) {
        Log.d(TAG, "Training title: "+ training.getTitle());
        exercises = Utilities.createExercisesListFromElement(training.getElements());
        checkNumberOfExercisesAndUpdateLayout();
        exercisesListAdapter.setExercises(exercises);
        exercisesListAdapter.notifyDataSetChanged();
        downloadingTrainingDialog.dismiss();
    }

    @Override
    public void processUserData(User cloudFitUser) {
        //Not needed.
    }

    @Override
    public void saveRequests(ArrayList<RequestTrainer> requests) {
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

    @Override
    public void downloadTrainingToBeSyncedWithWearable(CalendarEvent calendarEvent) {
        //Not needed.
    }

    @Override
    public void updateTrainingsCompletedNotifications(int trainingsNumber) {
        //Not needed.
    }

    /* Material design library methods */

    @Override
    public int getContentView() {
        return R.layout.activity_training_details;
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

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return new ActionBarDefaultHandler(this);
    }

    @Override
    protected boolean enableActionBarShadow() {
        return false;
    }
}
