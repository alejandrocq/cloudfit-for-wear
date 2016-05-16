package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.interfaces.TaskToActivityInterface;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 11/05/16.
 */
public class GetUserInfoTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private CloudFitService cloudFitService;

    private ArrayList<RequestTrainer> requests;
    private User cloudFitUser;
    private TaskToActivityInterface taskToActivityInterface;

    public GetUserInfoTask(Activity context, CloudFitService cloudFitService,
                           TaskToActivityInterface taskToActivityInterface) {
        this.context = context;
        this.cloudFitService = cloudFitService;
        this.taskToActivityInterface = taskToActivityInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        cloudFitUser = CloudFitCloud.getUserInfo(cloudFitService);
        requests = CloudFitCloud.getRequestTrainer(cloudFitService);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        taskToActivityInterface.saveUserInfo(cloudFitUser, requests);
        super.onPostExecute(aVoid);
    }
}
