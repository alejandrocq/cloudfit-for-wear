package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;

public class ReplyToRequestTask extends AsyncTask<Void, String, Void> {

    private Activity context;
    private CloudFitService cloudFitService;
    private long userId, trainerId;
    private int accept;
    private boolean result;

    public ReplyToRequestTask(Activity context,
                              CloudFitService cloudFitService,
                              long userId,
                              long trainerId,
                              int accept) {

        this.context = context;
        this.cloudFitService = cloudFitService;
        this.userId = userId;
        this.trainerId = trainerId;
        this.accept = accept;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        result = CloudFitCloud.responseRequest(cloudFitService, trainerId, userId, accept);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (result && accept == StaticReferences.REQUEST_ACCEPT) {
            Toast.makeText(context, "Petición aceptada correctamente", Toast.LENGTH_SHORT).show();
        } else if (result && accept == StaticReferences.REQUEST_CANCEL) {
            Toast.makeText(context, "Petición rechazada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ha ocurrido un error. Inténtelo de nuevo.",
                    Toast.LENGTH_SHORT).show();
        }

        //Update requests
        new GetRequestsTask(context, cloudFitService)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        super.onPostExecute(aVoid);
    }
}
