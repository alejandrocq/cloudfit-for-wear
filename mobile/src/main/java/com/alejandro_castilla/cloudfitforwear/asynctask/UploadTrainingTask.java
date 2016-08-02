package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;

/**
 * Created by alejandrocq on 14/05/16.
 */
public class UploadTrainingTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private CloudFitService cloudFitService;

    private Training training;
    private boolean trainingSavedAndUploaded;

    private MaterialDialog progressDialog;

    public UploadTrainingTask(Activity context, CloudFitService cloudFitService,
                              Training training) {
        this.context = context;
        this.cloudFitService = cloudFitService;
        this.training = training;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new MaterialDialog.Builder(context)
                .title("Subiendo entrenamiento")
                .content("Espere...")
                .progress(true, 0).build();
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Utilities.checkInternetConnection(context)) {
            trainingSavedAndUploaded = CloudFitCloud.saveTraining(cloudFitService, training);
        } else {
            trainingSavedAndUploaded = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();

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
