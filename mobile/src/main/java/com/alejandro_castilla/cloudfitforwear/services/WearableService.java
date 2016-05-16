package com.alejandro_castilla.cloudfitforwear.services;

import android.content.Intent;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.asynctask.CheckWearableConnectedTask;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableService extends WearableListenerService {

    private final String TAG = WearableService.class.getSimpleName();

    private GoogleApiClient googleApiClient;
    private CheckWearableConnectedTask checkWearable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started.");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();

        checkWearable = new CheckWearableConnectedTask(googleApiClient);
        checkWearable.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }
}
