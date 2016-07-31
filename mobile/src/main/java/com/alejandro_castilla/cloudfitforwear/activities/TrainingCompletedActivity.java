package com.alejandro_castilla.cloudfitforwear.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.ExerciseCompletedFragment;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.alejandro_castilla.cloudfitforwear.utilities.TrainingsDb;
import com.blunderer.materialdesignlibrary.activities.ViewPagerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.ViewPagerHandler;
import com.google.gson.Gson;

public class TrainingCompletedActivity extends ViewPagerActivity {

    private WearableTraining training;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sync:

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
            fragment.setTraining(training);
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
        return true;
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
