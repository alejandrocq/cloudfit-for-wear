package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;

import java.util.ArrayList;

public class GetTrainingsTask extends AsyncTask<Void, String, Void> {

    private CloudFitDataHandler cloudFitDataHandler;
    private CloudFitService cloudFitService;
    private short taskType;

    private Training training;
    private long trainingId;
    private String trainingString;

    private ArrayList<CalendarEvent> calendarEvents;

    public GetTrainingsTask(Activity context,
                            CloudFitService cloudFitService,
                            long trainingId,
                            short taskType) {

        this.cloudFitDataHandler = (CloudFitDataHandler) context;
        this.cloudFitService = cloudFitService;
        this.trainingId = trainingId;
        this.taskType = taskType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        switch (taskType) {
            case StaticVariables.GET_ALL_TRAININGS:
                calendarEvents = CloudFitCloud.getTrainings(cloudFitService);
                break;
            case StaticVariables.GET_SINGLE_TRAINING:
                training = CloudFitCloud.downloadAndParseTraining(cloudFitService, trainingId);
                break;
            case StaticVariables.GET_TRAINING_NOT_DONE:
                trainingId = zDBFunctions.getTrainingNotDone(cloudFitService.getDB(),
                        Long.parseLong(cloudFitService.getFit().getSetting().getUserID()));
                training = CloudFitCloud.downloadAndParseTraining(cloudFitService, trainingId);
                break;
            case StaticVariables.GET_TRAINING_DONE:
                trainingString = CloudFitCloud.getTrainingDone(cloudFitService, trainingId);
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        switch (taskType) {
            case StaticVariables.GET_ALL_TRAININGS:
                cloudFitDataHandler.updateTrainingsList(calendarEvents);
                break;
            case StaticVariables.GET_SINGLE_TRAINING:
                cloudFitDataHandler.processTrainingDownloaded(training);
                break;
            case StaticVariables.GET_TRAINING_NOT_DONE:
                // TODO do something here if needed
                break;
            case StaticVariables.GET_TRAINING_DONE:
                //TODO do something here if needed
                break;
        }

        super.onPostExecute(aVoid);
    }
}
