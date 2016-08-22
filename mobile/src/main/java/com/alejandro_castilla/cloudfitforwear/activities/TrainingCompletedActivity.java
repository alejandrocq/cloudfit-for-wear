package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.ExerciseCompletedFragment;
import com.alejandro_castilla.cloudfitforwear.asynctask.UploadTrainingTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.alejandro_castilla.cloudfitforwear.utilities.TrainingsDb;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.activities.ViewPagerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.ViewPagerHandler;
import com.google.gson.Gson;

public class TrainingCompletedActivity extends ViewPagerActivity {
    private final String TAG = TrainingCompletedActivity.class.getSimpleName();

    private WearableTraining training;

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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from CloudFit service");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent cloudFitServiceIntent = new Intent(TrainingCompletedActivity.this,
                CloudFitService.class);
        bindService(cloudFitServiceIntent, cloudFitServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sync:
                new UploadTrainingTask(this, cloudFitService,
                        Utilities.buildTrainingToUpload(training))
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case R.id.action_delete:
                String dialogDescr = "¿Está seguro de que quiere eliminar este entrenamiento?";
                new MaterialDialog.Builder(this)
                        .title("Atención")
                        .content(dialogDescr)
                        .positiveText("Sí")
                        .negativeText("Cancelar")
                        .titleColorRes(R.color.md_grey_800)
                        .contentColorRes(R.color.md_grey_800)
                        .backgroundColorRes(R.color.md_white_1000)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                TrainingsDb db = new TrainingsDb(TrainingCompletedActivity.this);
                                boolean res = db.deleteTraining(training.getTrainingId());

                                if (res) {
                                    Toast.makeText(TrainingCompletedActivity.this,
                                            "Entrenamiento eliminado correctamente",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(TrainingCompletedActivity.this,
                                            "Error al eliminar el entrenamiento",
                                            Toast.LENGTH_LONG).show();
                                }

                                finish();
                            }
                        })
                        .show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.training_completed_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(cloudFitServiceConnection);
    }

    /* Material Design Library methods */

    @Override
    public ViewPagerHandler getViewPagerHandler() {
        ViewPagerHandler handler = new ViewPagerHandler(this);
        Gson gson = new Gson();

        training = gson.fromJson(getIntent().getStringExtra("training_completed"),
                WearableTraining.class);

        for (Exercise ex : training.getExercises()) {
            ExerciseCompletedFragment fragment = new ExerciseCompletedFragment();
            fragment.setExercise(ex);
            handler.addPage(ex.getTitle(), fragment);
        }
        return handler;
    }

    @Override
    public int defaultViewPagerPageSelectedPosition() {
        return 0;
    }

    @Override
    public boolean showViewPagerIndicator() {
        return false;
    }

    @Override
    public boolean replaceActionBarTitleByViewPagerPageTitle() {
        return true;
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
