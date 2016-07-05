package com.alejandro_castilla.cloudfitforwear.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.interfaces.WearableHandler;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

public class WearableService extends Service implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = WearableService.class.getSimpleName();
    private final IBinder wearableServiceBinder = new WearableServiceBinder();
    private WearableHandler handler;

    private GoogleApiClient googleApiClient;

    /* Class used to bind with the client */

    public class WearableServiceBinder extends Binder {
        public WearableService getService() {
            return WearableService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
        return wearableServiceBinder;
    }

    @Override
    public void onDestroy() {
        Wearable.DataApi.removeListener(googleApiClient, this);
        googleApiClient.disconnect();
        Log.d(TAG, "Service stopped");
        super.onDestroy();
    }

    private void sendACKToHandheld () {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest
                .create(StaticVariables.ACK_FROM_WEARABLE);
        putDataMapRequest.getDataMap().putBoolean(StaticVariables.WEARABLE_TRAINING_ACK,
                true);
        putDataMapRequest.getDataMap().putLong("timestamp", System.currentTimeMillis());
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        putDataRequest.setUrgent();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "ACK sent.");
            }
        });
    }

    public void setWearableHandler(WearableHandler handler) {
        this.handler = handler;
    }

    /* Google API methods */

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged");

        for (DataEvent event : dataEventBuffer) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed");
                DataItem item = event.getDataItem();

                if (item.getUri().getPath()
                        .compareTo(StaticVariables.TRAINING_FROM_HANDHELD) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String json = dataMap.getString(StaticVariables.WEARABLE_TRAINING);
                    if (json != null) {
                        Log.d(TAG, "WEARABLE TRAINING JSON: " + json);
                        Gson gson = new Gson();
                        WearableTraining tr = gson.fromJson(json,
                                WearableTraining.class);
                        Log.d(TAG, "TRAINING NAME: " + tr.getTitle());
                        handler.saveWearableTraining(tr);
                        sendACKToHandheld();
                    }
                }

            }

        }

    }
}