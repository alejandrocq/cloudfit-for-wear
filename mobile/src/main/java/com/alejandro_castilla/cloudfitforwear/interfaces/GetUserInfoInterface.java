package com.alejandro_castilla.cloudfitforwear.interfaces;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;

import java.util.ArrayList;

/**
 * Interface created to save user info obtained from GetUserInfoTask on MainActivity.
 */
public interface GetUserInfoInterface {

    void saveUserInfo(User cloudFitUser, ArrayList<RequestTrainer> request);

}
