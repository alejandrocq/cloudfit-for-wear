package com.alejandro_castilla.cloudfitforwear.services;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.asynctask.CheckWearableConnectedTask;
import com.alejandro_castilla.cloudfitforwear.interfaces.ServiceInterface;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableService extends WearableListenerService implements ServiceInterface {

    private final String TAG = WearableService.class.getSimpleName();

    private ServiceInterface serviceInterface;

    private GoogleApiClient googleApiClient;
    private CheckWearableConnectedTask checkWearable;
    private boolean isWearableConnected = false;

    private Messenger mainActivityMessenger;

    private final Handler messangeHandler = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StaticVariables.MSG_REQUEST_WEARABLE_STATE:

                    break;
                case StaticVariables.MSG_SEND_TRAINING_TO_WEARABLE:

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Messenger wearableServiceMessenger = new Messenger(messangeHandler);

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

    public void checkWearableState() {
        checkWearable = new CheckWearableConnectedTask(serviceInterface, googleApiClient);
        checkWearable.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started.");
        serviceInterface = this;
        mainActivityMessenger = intent.getParcelableExtra("messenger");

        try {
            Message msg = Message.obtain(null, StaticVariables.MSG_WEARABLESERVICE_MESSENGER);
            msg.obj = wearableServiceMessenger;
            mainActivityMessenger.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();

        checkWearableState();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }
}
