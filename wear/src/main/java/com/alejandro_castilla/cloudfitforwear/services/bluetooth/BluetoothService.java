package com.alejandro_castilla.cloudfitforwear.services.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.messaging.MessageType;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 16/03/16.
 */

public class BluetoothService extends Service {

    public enum BluetoothStatus { IDLE, NOT_SUPPORTED }
    public static BluetoothStatus bluetoothStatus;
    private static final String TAG = BluetoothService.class.getSimpleName();

    private final IBinder bluetoothServiceBinder = new BluetoothServiceBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<BluetoothDevice> devices;
    private BluetoothDevice targetDevice;
    private boolean deviceFound;

    /* Messengers fields */
    private Messenger messenger = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    public void onDestroy() {
        stopDiscoveryOfDevices();
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "BluetoothService has been started.");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
        messenger = intent.getParcelableExtra("messenger");
        return bluetoothServiceBinder;
    }

    public void findBluetoothDevice(String macAddress) {
        targetDevice = null;
        deviceFound = false;
        startDiscoveryOfDevices();
        new FindBluetoothDeviceTask().execute(macAddress);
    }

    private void startDiscoveryOfDevices() {
        devices = new ArrayList<>();
        bluetoothAdapter.startDiscovery();

        //BroadcastReceiver to receive data
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice deviceFound = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceData = deviceFound.getName() + " " + deviceFound.getAddress();
                    Log.d("Device found", deviceData);
                    devices.add(deviceFound);
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
        Log.i(TAG, "Receiver registered.");

    }

    private void stopDiscoveryOfDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            this.unregisterReceiver(broadcastReceiver);
            bluetoothStatus = BluetoothStatus.IDLE;
            bluetoothAdapter.cancelDiscovery();
            Log.i(TAG, "Receiver unregistered and discovery stopped.");
        }
    }

    /* Class used to bind with the client */

    public class BluetoothServiceBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    /* AsyncTask to find devices on devices list */

    private class FindBluetoothDeviceTask extends AsyncTask<String, Integer, BluetoothDevice> {
        @Override
        protected BluetoothDevice doInBackground(String... params) {
            try {
                // Wait for devices list to be updated.
                Thread.sleep(10000);
                stopDiscoveryOfDevices();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (devices != null) {
                for (BluetoothDevice device : devices) {
                    if (device.getAddress().contains(params[0])) {
                        // We found it
                        deviceFound = true;
                        targetDevice = device;
                        Log.d(TAG, "The device requested has been found.");
                    }
                }
            }
            return targetDevice;
        }

        @Override
        protected void onPostExecute(BluetoothDevice bluetoothDevice) {
            super.onPostExecute(bluetoothDevice);
            Message msg;
            if (!deviceFound) {
                Log.d(TAG, "Device not found");
                msg = Message.obtain(null, MessageType.DEVICE_NOT_FOUND);
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                //Send the device to MainActivity
                Log.d(TAG, "Sending targetDevice to MainActivity");
                msg = Message.obtain(null, MessageType.DEVICE_FOUND);
                msg.obj = targetDevice;
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }


        }
    }

}
