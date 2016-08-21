package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;

import java.util.ArrayList;

public class GetRequestsTask extends AsyncTask<Void, String, Void> {

    private CloudFitDataHandler cloudFitDataHandler;
    private CloudFitService cloudFitService;

    private ArrayList<RequestTrainer> requests;

    public GetRequestsTask(Activity context, CloudFitService cloudFitService) {
        this.cloudFitService = cloudFitService;
        cloudFitDataHandler = (CloudFitDataHandler) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        requests = CloudFitCloud.getRequestTrainer(cloudFitService);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cloudFitDataHandler.saveRequests(requests);
        super.onPostExecute(aVoid);
    }
}
