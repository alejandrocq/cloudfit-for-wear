package com.alejandro_castilla.cloudfitforwear.interfaces;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;

import java.util.ArrayList;

/**
 * Interface created to communicate with
 * activities when using AsyncTasks.
 */
public interface ActivityInterface {

    void saveUserInfo(User cloudFitUser);
    void saveRequests(ArrayList<RequestTrainer> requests);
    void stopRefreshing();
    void updateTrainingsList(ArrayList<CalendarEvent> calendarEvents);
    void saveAndParseTraining(Training training);
    void downloadTrainingToBeSyncedWithWearable(CalendarEvent calendarEvent);

    CloudFitService getCloudFitService();

}
