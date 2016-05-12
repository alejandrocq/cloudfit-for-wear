package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.calendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 10/05/16.
 */
public class GetAllTrainingsTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private ProgressDialog progressDialog;
    private CloudFitService cloudFitService;

    private ArrayList<calendarEvent> calendarEvents;

    public GetAllTrainingsTask(Activity context, CloudFitService cloudFitService) {
        this.context = context;
        this.cloudFitService = cloudFitService;

        progressDialog = new ProgressDialog(context);

    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Espere...");
        progressDialog.setMessage("Descargando entrenamientos...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        calendarEvents = CloudFitCloud.getTrainings(cloudFitService);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        Toast.makeText(context, "Nombre del entrenamiento: " + calendarEvents.get(0).getText(),
                Toast.LENGTH_LONG).show();
        super.onPostExecute(aVoid);
    }
}
