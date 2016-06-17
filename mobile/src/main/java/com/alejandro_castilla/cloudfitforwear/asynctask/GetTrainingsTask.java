package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 10/05/16.
 */

public class GetTrainingsTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private ActivityInterface activityInterface;
    private CloudFitService cloudFitService;
    private short taskType;

    private Training training;
    private long trainingid;
    private String trainingString;

    private ArrayList<CalendarEvent> calendarEvents;

    public GetTrainingsTask(Activity context,
                            CloudFitService cloudFitService, long trainingid, short taskType) {

        this.context = context;
        this.activityInterface = (ActivityInterface) context;
        this.cloudFitService = cloudFitService;
        this.trainingid = trainingid;
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
        switch (taskType) {
            case StaticVariables.GET_ALL_TRAININGS:
//                    activityInterface.stopRefreshing();
                    activityInterface.updateTrainingsList(calendarEvents);
                break;
            case StaticVariables.GET_SINGLE_TRAINING:
                activityInterface.saveAndParseTraining(training);
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
