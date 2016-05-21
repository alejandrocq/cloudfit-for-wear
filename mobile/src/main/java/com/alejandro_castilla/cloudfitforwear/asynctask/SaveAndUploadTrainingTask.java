package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;

/**
 * Created by alejandrocq on 14/05/16.
 */
public class SaveAndUploadTrainingTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private CloudFitService cloudFitService;
    private ActivityInterface activityInterface;

    private Training training;
    private boolean trainingSavedAndUploaded = true;

    public SaveAndUploadTrainingTask(Activity context, CloudFitService cloudFitService,
                                     ActivityInterface activityInterface,
                                     Training training) {
        this.context = context;
        this.cloudFitService = cloudFitService;
        this.activityInterface = activityInterface;
        this.training = training;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Utilities.checkInternetConnection(context)) {
            trainingSavedAndUploaded &= CloudFitCloud.saveTraining(cloudFitService, training);

            if (trainingSavedAndUploaded) {
                zDBFunctions.changeStateTraining(training.getDB(), training.getId(),
                        StaticReferences.TRAINING_DONE);
            }
        } else {
            trainingSavedAndUploaded = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (trainingSavedAndUploaded) {
            Toast.makeText(context, "El entrenamiento ha sido guardado en CloudFit",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al guardar el entrenamiento",
                    Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(aVoid);
    }
}
