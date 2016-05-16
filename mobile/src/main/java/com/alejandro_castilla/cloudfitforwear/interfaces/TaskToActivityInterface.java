package com.alejandro_castilla.cloudfitforwear.interfaces;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;

import java.util.ArrayList;

/**
 * Interface created to communicate with
 * activities when using AsyncTasks.
 */
public interface TaskToActivityInterface {

    void saveUserInfo(User cloudFitUser, ArrayList<RequestTrainer> request);

}
