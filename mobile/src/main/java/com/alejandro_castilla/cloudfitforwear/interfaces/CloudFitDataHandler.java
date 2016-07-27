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
public interface CloudFitDataHandler {

    void saveUserInfo(User cloudFitUser);
    void saveRequests(ArrayList<RequestTrainer> requests);
    void updateTrainingsList(ArrayList<CalendarEvent> calendarEvents);
    void saveAndParseTraining(Training training);
    void downloadTrainingToBeSyncedWithWearable(CalendarEvent calendarEvent);
    void updateTrainingsCompletedNotifications(int trainingsNumber);

    CloudFitService getCloudFitService();

}
