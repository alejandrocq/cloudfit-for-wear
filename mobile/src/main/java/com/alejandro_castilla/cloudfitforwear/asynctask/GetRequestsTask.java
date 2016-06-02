package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 28/05/16.
 */
public class GetRequestsTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private ActivityInterface activityInterface;
    private CloudFitService cloudFitService;

    private ArrayList<RequestTrainer> requests;

    public GetRequestsTask(Activity context, CloudFitService cloudFitService) {
        this.context = context;
        this.cloudFitService = cloudFitService;

        activityInterface = (ActivityInterface) context;
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
        activityInterface.saveRequests(requests);
        super.onPostExecute(aVoid);
    }
}
