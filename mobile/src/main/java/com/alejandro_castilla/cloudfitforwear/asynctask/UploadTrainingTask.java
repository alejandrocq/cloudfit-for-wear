package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;

/**
 * Created by alejandrocq on 14/05/16.
 */
public class UploadTrainingTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private CloudFitService cloudFitService;
    private CloudFitDataHandler cloudFitDataHandler;

    private Training training;
    private boolean trainingSavedAndUploaded;

    public UploadTrainingTask(Activity context, CloudFitDataHandler cloudFitDataHandler,
                              Training training) {
        this.context = context;
        this.cloudFitDataHandler = cloudFitDataHandler;
        this.training = training;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Utilities.checkInternetConnection(context)) {
            trainingSavedAndUploaded = CloudFitCloud
                    .saveTraining(cloudFitDataHandler.getCloudFitService(), training);
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
            //TODO Update training state on db
        } else {
            Toast.makeText(context, "Error al guardar el entrenamiento",
                    Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(aVoid);
    }
}
