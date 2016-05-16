package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.calendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;
import com.alejandro_castilla.cloudfitforwear.interfaces.TaskToActivityInterface;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 10/05/16.
 */

public class GetTrainingsTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private ProgressDialog progressDialog;
    private CloudFitService cloudFitService;
    private TaskToActivityInterface taskToActivityInterface;
    private short taskType;

    private Training training;
    private long trainingid;
    private String trainingString;

    private ArrayList<calendarEvent> calendarEvents;

    public GetTrainingsTask(Activity context, CloudFitService cloudFitService,
                            TaskToActivityInterface taskToActivityInterface, long trainingid,
                            short taskType) {

        this.context = context;
        this.cloudFitService = cloudFitService;
        this.taskToActivityInterface = taskToActivityInterface;
        this.trainingid = trainingid;
        this.taskType = taskType;

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

        switch (taskType) {
            case StaticVariables.GET_ALL_TRAININGS:
                calendarEvents = CloudFitCloud.getTrainings(cloudFitService);
                break;
            case StaticVariables.GET_SINGLE_TRAINING:
                training = CloudFitCloud.downloadAndParseTraining(cloudFitService, trainingid);
//                CloudFitCloud.updateTrainingPerforming(cloudFitService, trainingid, 1);
                // TODO update training state
                break;
            case StaticVariables.GET_TRAINING_NOT_DONE:
                trainingid = zDBFunctions.getTrainingNotDone(cloudFitService.getDB(),
                        Long.parseLong(cloudFitService.getFit().getSetting().getUserID()));
                training = CloudFitCloud.downloadAndParseTraining(cloudFitService, trainingid);
                break;
            case StaticVariables.GET_TRAINING_DONE:
                trainingString = CloudFitCloud.getTrainingDone(cloudFitService, trainingid);
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        switch (taskType) {
            case StaticVariables.GET_ALL_TRAININGS:
                if (calendarEvents.size()>0) {
                    Toast.makeText(context, "Nombre del entrenamiento: "
                            + calendarEvents.get(0).getText(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "ID del entrenamiento: " +
                            calendarEvents.get(0).getId(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case StaticVariables.GET_SINGLE_TRAINING:
                Toast.makeText(context, "Nombre del entrenamiento: " + training.getTitle(),
                        Toast.LENGTH_SHORT).show();
                break;
            case StaticVariables.GET_TRAINING_NOT_DONE:
                // TODO do something here
                break;
            case StaticVariables.GET_TRAINING_DONE:
                //TODO do something here
                break;
        }

        super.onPostExecute(aVoid);
    }
}
