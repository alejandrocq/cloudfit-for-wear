package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;

public class GetUserInfoTask extends AsyncTask<Void, String, Void> {

    private CloudFitService cloudFitService;

    private User cloudFitUser;
    private CloudFitDataHandler cloudFitDataHandler;

    public GetUserInfoTask(Activity context, CloudFitService cloudFitService) {
        this.cloudFitService = cloudFitService;
        this.cloudFitDataHandler = (CloudFitDataHandler) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        cloudFitUser = CloudFitCloud.getUserInfo(cloudFitService);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cloudFitDataHandler.processUserData(cloudFitUser);
        super.onPostExecute(aVoid);
    }
}
