package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.interfaces.WearableStatusHandler;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class CheckWearableConnectedTask extends AsyncTask<Void, Void, Void> {

    private final String TAG = CheckWearableConnectedTask.class.getSimpleName();

    private GoogleApiClient googleApiClient;
    private WearableStatusHandler wearableStatusHandler;

    public CheckWearableConnectedTask(WearableStatusHandler wearableStatusHandler,
                                      GoogleApiClient googleApiClient) {
        this.wearableStatusHandler = wearableStatusHandler;
        this.googleApiClient = googleApiClient;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        boolean isWearableConnected;

        while (!this.isCancelled()) {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
                    .getConnectedNodes(googleApiClient).await();

            if (nodes != null && nodes.getNodes().size() > 0) {
                isWearableConnected = true;
                Log.d(TAG, "Watch connected: " + nodes.getNodes().get(0).getDisplayName());
            } else {
                isWearableConnected = false;
            }

            wearableStatusHandler.updateWearableState(isWearableConnected);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.d(TAG, "CheckWearableConnectedTask interrupted");
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

}
