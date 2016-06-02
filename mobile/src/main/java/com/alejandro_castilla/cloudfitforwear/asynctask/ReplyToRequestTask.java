package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;

/**
 * Created by alejandrocq on 11/05/16.
 */
public class ReplyToRequestTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private CloudFitService cloudFitService;
    private long userid, trainerid;
    private int accept;
    private boolean result;

    public ReplyToRequestTask(Activity context, CloudFitService cloudFitService, long userid,
                              long trainerid, int accept) {
        this.context = context;
        this.cloudFitService = cloudFitService;
        this.userid = userid;
        this.trainerid = trainerid;
        this.accept = accept;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("ReplyTask", "User ID: " + userid);
        result = CloudFitCloud.responseRequest(cloudFitService, trainerid, userid, accept);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context, "Resultado de la petición: " + result, Toast.LENGTH_SHORT).show();
        new GetRequestsTask(context, cloudFitService).execute();
        super.onPostExecute(aVoid);
    }
}
