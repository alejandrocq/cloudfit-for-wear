package com.alejandro_castilla.cloudfitforwear.services.zephyrsensor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;

import zephyr.android.BioHarnessBT.BTClient;

/**
 * Created by alejandrocq on 4/04/16.
 */
public class ZephyrService extends Service {

    private static final String TAG = ZephyrService.class.getSimpleName();

    private final IBinder zephyrServiceBinder = new ZephyrServiceBinder();

    private BluetoothAdapter bluetoothAdapter;
    BTClient _bt;
    NewConnectedListener _NConnListener;
    private boolean connected = false;
//    ZephyrProtocol _protocol;

    private Messenger messenger;


    private final int HEART_RATE = 0x100;
    private final int RESPIRATION_RATE = 0x101;
    private final int SKIN_TEMPERATURE = 0x102;
    private final int POSTURE = 0x103;
    private final int PEAK_ACCLERATION = 0x104;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeConnection();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        messenger = intent.getParcelableExtra("messenger");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return zephyrServiceBinder;
    }

    public boolean connectToZephyr(BluetoothDevice zephyrDevice) {
        Log.d(TAG, "Starting connection to Zephyr device.");
        _bt = new BTClient(bluetoothAdapter, zephyrDevice.getAddress());
        _NConnListener = new NewConnectedListener(messageHandler, messageHandler);
        _bt.addConnectedEventListener(_NConnListener);
        if (_bt.IsConnected()) {
            Log.d(TAG, "Connected to Zephyr device.");
            _bt.start();
            connected = true;
        }

        return connected;

    }

    public void closeConnection() {
        if (connected) {
            _bt.removeConnectedEventListener(_NConnListener);
            _bt.Close();
        }
        Log.d(TAG, "Connection to Zephyr device terminated.");
    }


    private final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HEART_RATE:
                    String heartRatetext = msg.getData().getString("HeartRate");
                    System.out.println("Heart Rate Info is " + heartRatetext);
                    Message heartRateMsg = Message.obtain(null, StaticVariables.ZEPHYR_HEART_RATE);
                    Bundle heartRateBundle = new Bundle();
                    heartRateBundle.putString("heartratestring", heartRatetext);
                    heartRateMsg.setData(heartRateBundle);
                    try {
                        messenger.send(heartRateMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /* Class used to bind with the client (MainActivity.java) */

    public class ZephyrServiceBinder extends Binder {
        public ZephyrService getService() {
            return ZephyrService.this;
        }
    }


//    private class BTBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d("BTIntent", intent.getAction());
//            Bundle b = intent.getExtras();
//            Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
//            Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
//            try {
//                BluetoothDevice device = adapter.
//                        getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
//                Method m = BluetoothDevice.class.
//                        getMethod("convertPinToBytes", new Class[] {String.class} );
//                byte[] pin = (byte[])m.invoke(device, "1234");
//                m = device.getClass().getMethod("setPin", new Class [] {pin.getClass()});
//                Object result = m.invoke(device, pin);
//                Log.d("BTTest", result.toString());
//            } catch (SecurityException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            } catch (NoSuchMethodException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
}