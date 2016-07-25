package com.alejandro_castilla.cloudfitforwear.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.asynctask.CheckWearableConnectedTask;
import com.alejandro_castilla.cloudfitforwear.interfaces.WearableStatusHandler;
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

public class WearableService extends Service implements WearableStatusHandler, DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = WearableService.class.getSimpleName();

    private WearableStatusHandler wearableStatusHandler;

    private GoogleApiClient googleApiClient;
    private CheckWearableConnectedTask checkWearable;
    private boolean isWearableConnected = false;

    private Messenger mainActivityMessenger;

    private final Handler messageHandler = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StaticVariables.MSG_REQUEST_WEARABLE_STATE:
                    startCheckWearableTask();
                    break;
                case StaticVariables.MSG_SEND_TRAINING_TO_WEARABLE:
                    if (isWearableConnected) {
                        String wearableTrainingJSON = (String) msg.obj;
                        sendTrainingToWearable(wearableTrainingJSON);
                    }
                    break;
                case StaticVariables.MSG_TRAINING_RECEIVED_FROM_WEARABLE_ACK:
                    sendACKToWearable();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Messenger wearableServiceMessenger = new Messenger(messageHandler);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started.");
        mainActivityMessenger = intent.getParcelableExtra("messenger");

        wearableStatusHandler = this;

        try {
            Message msg = Message.obtain(null, StaticVariables.MSG_WEARABLESERVICE_MESSENGER);
            msg.obj = wearableServiceMessenger;
            mainActivityMessenger.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        startCheckWearableTask();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Wearable service stopped.");
        Wearable.DataApi.removeListener(googleApiClient, this);
        googleApiClient.disconnect();
        checkWearable.cancel(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void updateWearableState(boolean isWearableConnected) {
        this.isWearableConnected = isWearableConnected;

        try {
            Message msg = Message.obtain(null, StaticVariables.MSG_WEARABLE_STATE);
            Bundle bundle = new Bundle();
            bundle.putBoolean(StaticVariables.BUNDLE_WEARABLE_STATE, isWearableConnected);
            msg.obj = bundle;
            mainActivityMessenger.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startCheckWearableTask() {
        checkWearable = new CheckWearableConnectedTask(wearableStatusHandler, googleApiClient);
        checkWearable.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendTrainingToWearable (String wearableTrainingJSON) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest
                .create(StaticVariables.TRAINING_FROM_HANDHELD);
        putDataMapRequest.getDataMap().putString(StaticVariables.WEARABLE_TRAINING,
                wearableTrainingJSON);
        putDataMapRequest.getDataMap().putLong("timestamp", System.currentTimeMillis());
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        putDataRequest.setUrgent();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "Training sent");
            }
        });
        Log.d(TAG, "Sending training to wearable.");
    }

    private void sendACKToWearable() {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest
                .create(StaticVariables.ACK_FROM_HANDHELD);
        putDataMapRequest.getDataMap().putBoolean(StaticVariables.WEARABLE_TRAINING_DONE_ACK,
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

    /* Google API methods */

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged");

        for (DataEvent event : dataEventBuffer) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed");
                DataItem item = event.getDataItem();

                if (item.getUri().getPath()
                        .compareTo(StaticVariables.ACK_FROM_WEARABLE) == 0) {

                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    boolean wearableACK = dataMap.getBoolean(StaticVariables.WEARABLE_TRAINING_ACK);

                    if (wearableACK) {
                        try {
                            Message ACKMessage = Message.obtain(null,
                                    StaticVariables.MSG_SEND_TRAINING_TO_WEARABLE_ACK);
                            mainActivityMessenger.send(ACKMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (item.getUri().getPath()
                        .compareTo(StaticVariables.TRAINING_DONE_FROM_WEARABLE) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String trainingDone = dataMap.getString(StaticVariables.WEARABLE_TRAINING_DONE);

                    Bundle b = new Bundle();
                    b.putString(StaticVariables.BUNDLE_WEARABLE_TRAINING_DONE, trainingDone);

                    try {
                        Message msg = Message.obtain(null,
                                StaticVariables.MSG_TRAINING_RECEIVED_FROM_WEARABLE);
                        msg.obj = b;
                        mainActivityMessenger.send(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

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
}
