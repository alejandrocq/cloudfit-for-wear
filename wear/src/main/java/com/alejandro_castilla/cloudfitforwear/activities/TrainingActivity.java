package com.alejandro_castilla.cloudfitforwear.activities;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.PracticeActivityGridPagerAdapter;
import com.alejandro_castilla.cloudfitforwear.data.HeartRate;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Exercise;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Rest;
import com.alejandro_castilla.cloudfitforwear.data.exercises.Running;
import com.alejandro_castilla.cloudfitforwear.services.bluetooth.BluetoothService;
import com.alejandro_castilla.cloudfitforwear.services.zephyrsensor.ZephyrService;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TrainingActivity extends WearableActivity implements View.OnClickListener,
        SensorEventListener {

    private final String TAG = TrainingActivity.class.getSimpleName();

    /*Fields for the views used on the layout*/

    private Chronometer chronometer;
    private ImageView resumeActionImgView, pauseActionImgView, exitActionImgView;
    private TextView heartRateTextView, distanceTextView, infoTextView,
            resumeActionTextView, pauseActionTextView;
    private GridViewPager gridViewPager;

    /* Preferences fields */

    private boolean zephyrEnabled;

    /* Status fields */

    private boolean sessionPaused = false;

    /* Chronometer fields */

    private long timeWhenPaused;
    private boolean chronoAllowedToStart = true;

    /* Internal sensors fields */

    private SensorManager sensorManager;
    private Sensor heartRateInternalSensor;

    /* Data fields */

    private WearableTraining training;
    private Exercise currentExercise;
    private int currentExerciseIndex;
    private ArrayList<Exercise> exercisesCompleted;
    private ArrayList<HeartRate> heartRateList;

    /* Fields to connect to services */

    private BluetoothService bluetoothService;
    private boolean bluetoothServiceBinded = false;
    private ZephyrService zephyrService;
    private boolean zephyrServiceBinded = false;

    private ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.BluetoothServiceBinder bluetoothServiceBinder =
                    (BluetoothService.BluetoothServiceBinder) service;
            bluetoothService = bluetoothServiceBinder.getService();
            bluetoothService.findBluetoothDevice("C8:3E:99:0D:DD:43");
            //TODO This mac address should be synced from the phone(and stored on SharedPreferences)
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ServiceConnection zephyrServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ZephyrService.ZephyrServiceBinder zephyrServiceBinder =
                    (ZephyrService.ZephyrServiceBinder) service;
            zephyrService = zephyrServiceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /*Messenger fields*/

    private final Handler messageHandler = new Handler() {

        long timeMark;
        int heartRateInt;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StaticVariables.DEVICE_FOUND:
                    Log.d(TAG, "Device received on " + TAG);
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    zephyrService.connectToZephyr(device);
                    break;
                case StaticVariables.ZEPHYR_HEART_RATE:
                    startChronometer(chronoAllowedToStart, SystemClock.elapsedRealtime());
                    chronoAllowedToStart = false; //Starts chronometer only one time
                    pauseActionImgView.setOnClickListener(TrainingActivity.this);

                    if (!sessionPaused) {
                        String heartRateString = msg.getData().getString("heartratestring");
                        timeMark = SystemClock.elapsedRealtime() - chronometer.getBase();
                        heartRateInt = Integer.parseInt(heartRateString);
                        saveHeartRate(timeMark, heartRateInt);
                        heartRateTextView.setText(heartRateString);
                    }
                    break;
                case StaticVariables.DEVICE_NOT_FOUND:
                    Intent intent = new Intent (TrainingActivity.this,
                            ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.FAILURE_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                            "No se ha podido conectar con el sensor Zephyr");
                    startActivity(intent);
                    finish();
            }
            super.handleMessage(msg);
        }
    };

    private Messenger practiceActivityMessenger = new Messenger(messageHandler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                chronometer = (Chronometer) findViewById(R.id.practiceChronometer);
                heartRateTextView = (TextView) findViewById(R.id.heartRateText);
                distanceTextView = (TextView) findViewById(R.id.distanceText);
                infoTextView = (TextView) findViewById(R.id.infoText);

                resumeActionImgView = (ImageView) findViewById(R.id.practiceResumeActionImg);
                pauseActionImgView = (ImageView) findViewById(R.id.practicePauseActionImg);
                pauseActionTextView = (TextView) findViewById(R.id.practicePauseActionText);
                resumeActionTextView = (TextView) findViewById(R.id.practiceResumeActionText);
                exitActionImgView = (ImageView) findViewById(R.id.practiceExitActionImg);

                //Pause action listener is set later
                resumeActionImgView.setOnClickListener(TrainingActivity.this);
                exitActionImgView.setOnClickListener(TrainingActivity.this);

                gridViewPager = (GridViewPager) stub.findViewById(R.id.practicePager);
                gridViewPager.setAdapter(new PracticeActivityGridPagerAdapter());
                gridViewPager.setOffscreenPageCount(2);
                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator)
                        findViewById(R.id.practicePageIndicator);
                dotsPageIndicator.setPager(gridViewPager);

                //TODO Read WearableTraining and set parameters
                checkSharedPreferencesAndParseTraining();
                heartRateList = new ArrayList<>();

                if (zephyrEnabled) {
                    // Start bluetooth and Zephyr sensor services
                    infoTextView.setText("Conectando con el sensor...");

                    Intent bluetoothServiceIntent = new Intent(TrainingActivity.this,
                            BluetoothService.class);
                    bluetoothServiceIntent.putExtra("messenger", practiceActivityMessenger);
                    bindService(bluetoothServiceIntent, bluetoothServiceConnection,
                            Context.BIND_AUTO_CREATE);
                    bluetoothServiceBinded = true;

                    Intent zephyrServiceIntent = new Intent(TrainingActivity.this,
                            ZephyrService.class);
                    zephyrServiceIntent.putExtra("messenger", practiceActivityMessenger);
                    bindService(zephyrServiceIntent, zephyrServiceConnection,
                            Context.BIND_AUTO_CREATE);
                    zephyrServiceBinded = true;
                } else {
                    //Initialize internal heart rate sensor (if it's available)
                    infoTextView.setText("Iniciando pulsómetro...");
                    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    heartRateInternalSensor = sensorManager
                            .getDefaultSensor(Sensor.TYPE_HEART_RATE);
                    if (heartRateInternalSensor == null) {
                        Intent intent = new Intent (TrainingActivity.this,
                                ConfirmationActivity.class);
                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                ConfirmationActivity.FAILURE_ANIMATION);
                        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                                "Este dispositivo no tiene pulsómetro");
                        startActivity(intent);
                        finish();
                    }
                    sensorManager.registerListener(TrainingActivity.this, heartRateInternalSensor,
                            SensorManager.SENSOR_DELAY_UI);
                }

            }
        });

        setAmbientEnabled();
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        long timeElapsed;
        switch (resId) {
            case R.id.practicePauseActionImg:
                timeWhenPaused = SystemClock.elapsedRealtime() - chronometer.getBase();
                chronometer.stop();
                sessionPaused = true;
                pauseActionImgView.setVisibility(View.GONE);
                resumeActionImgView.setVisibility(View.VISIBLE);
                pauseActionTextView.setVisibility(View.GONE);
                resumeActionTextView.setVisibility(View.VISIBLE);
                infoTextView.setText("Entrenamiento pausado");
                break;
            case R.id.practiceResumeActionImg:
                startChronometer(true, SystemClock.elapsedRealtime() - timeWhenPaused);
                sessionPaused = false;
                pauseActionImgView.setVisibility(View.VISIBLE);
                resumeActionImgView.setVisibility(View.GONE);
                pauseActionTextView.setVisibility(View.VISIBLE);
                resumeActionTextView.setVisibility(View.GONE);
                break;
            case R.id.practiceExitActionImg:
                stopServices();

                if (sessionPaused) {
                    timeElapsed = timeWhenPaused;
                } else {
                    timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
                }

                //TODO Save training data
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        stopServices();

        if (!zephyrEnabled) {
            sensorManager.unregisterListener(this); //Listener for internal heart rate sensor
        }

        super.onDestroy();
    }

    private void stopServices() {
        if (bluetoothServiceBinded) {
            bluetoothService.stopFindTask(); //If this task is still running, we have to stop it to
                                            //avoid problems
            unbindService(bluetoothServiceConnection);
            bluetoothServiceBinded = false;
        }

        if (zephyrServiceBinded) {
            unbindService(zephyrServiceConnection);
            zephyrServiceBinded = false;
        }
    }

    private void prepareCurrentExercise() {
        if (currentExercise.getType() == Exercise.TYPE_RUNNING) {
            Log.d(TAG, "RUNNING EXERCISE");
            final Running running = (Running) currentExercise;

            if (running.getDistanceP() != -1.0 && running.getDistanceP() != 0.0) {
                Log.d(TAG, "Distance set");
                //TODO Save distance and check when it's covered (GPS)
            } else if (running.getTimeP() != -1.0) {
                //TODO Show min time on screen

                if (running.getTimeMaxP() != -1.0) {
                    //Stop training when max time is reached.
                    chronometer.setOnChronometerTickListener(new Chronometer
                            .OnChronometerTickListener() {
                        @Override
                        public void onChronometerTick(Chronometer chronometer) {
                            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                            if (time >= (running.getTimeP()*1000)) {
                                saveExerciseData(time);
                                Utilities.buildNotification(TrainingActivity.this,
                                        "Información",
                                        "Carrera finalizada. Iniciado el siguiente ejercicio.");
                                resetDataAndMoveToNextExercise();
                            }
                        }
                    });
                }
            }
        } else if (currentExercise.getType() == Exercise.TYPE_REST) {
            final Rest rest = (Rest) currentExercise;
            Log.d(TAG, "Rest Exercise");

            infoTextView.setText("En recuperación: "+rest.getRestp()/60+" min");
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                    if (time >= rest.getRestp()*1000) {
                        saveExerciseData(time);
                        saveTrainingDataAndFinish();
                    }
                }
            });
        } else {
            //TODO Handle this situation (exercise not supported or another error)
            finish();
        }
    }

    private void checkSharedPreferencesAndParseTraining() {
        //Check if the user wants to use Zephyr Sensor
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        zephyrEnabled = prefs.getBoolean(StaticVariables.KEY_PREF_ZEPHYR_ENABLED, false);

        String tr = prefs.getString(StaticVariables.KEY_TRAINING_TO_BE_DONE, "");

        if (!tr.equals("")) { //Not empty
            //Training available.
            Gson gson = new Gson();
            training = gson.fromJson(tr, WearableTraining.class);
            currentExerciseIndex = 0;
            currentExercise = training.getExercises().get(currentExerciseIndex);

            prepareCurrentExercise();

            training.setStartDate(SystemClock.elapsedRealtime());

        } else {
            // Training not available
            Intent intent = new Intent (TrainingActivity.this,
                    ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                    "No hay ningún entrenamiento disponible");
            startActivity(intent);
            finish();
        }
    }

    private void startChronometer(boolean allowed, long baseTime) {
        if (allowed) { //Check if chronometer is already started.
            infoTextView.setText(currentExercise.getTitle());
            chronometer.setBase(baseTime);
            chronometer.start();
        }
    }

    private void resetDataAndMoveToNextExercise() {
        //TODO Also reset distance
        chronometer.stop();
        startChronometer(true, SystemClock.elapsedRealtime()); //Reset chronometer
        heartRateList = new ArrayList<>(); //Reset heart rate data
        currentExerciseIndex++;

        if (currentExerciseIndex == training.getExercises().size()) {
            saveTrainingDataAndFinish();
        }

        currentExercise = training.getExercises().get(currentExerciseIndex);
        prepareCurrentExercise();

    }

    private void saveTrainingDataAndFinish() {
        training.setEndDate(SystemClock.elapsedRealtime());
        training.setState(WearableTraining.NOT_UPLOADED);
        training.setExercises(exercisesCompleted);

        Gson gson = new Gson();
        SharedPreferences.Editor prefsEditor = PreferenceManager
                .getDefaultSharedPreferences(this).edit();
        prefsEditor.putString(StaticVariables.KEY_TRAINING_DONE, gson.toJson(training));
        prefsEditor.apply();
        Log.d(TAG, "Training saved at date: "+training.getEndDate());

        //Clear training to be done
        prefsEditor.putString(StaticVariables.KEY_TRAINING_TO_BE_DONE, "");
        prefsEditor.apply();

//        Utilities.buildNotification(TrainingActivity.this, "Información",
//                "Entrenamiento finalizado y listo para ser sincronizado.");

        Intent intent = new Intent (TrainingActivity.this,
                ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                "Entrenamiento completado correctamente");
        startActivity(intent);
        finish();
    }

    private void saveHeartRate(long timeMark, int heartRate) {
        HeartRate hr = new HeartRate(timeMark, heartRate);
        heartRateList.add(hr);
    }

    private void saveExerciseData (long timeElapsed) {
        if (currentExercise instanceof Running) {
            Running running = (Running) currentExercise;
            running.setTimeR(timeElapsed);
            running.setHeartRateList(heartRateList);
            exercisesCompleted.add(running);
        } else if (currentExercise instanceof Rest) {
            Rest rest = (Rest) currentExercise;
            rest.setRestr((int) timeElapsed);
            rest.setHeartRateList(heartRateList);
            exercisesCompleted.add(rest);
        }
    }

    /* Methods for internal heart rate sensor */

    @Override
    public void onSensorChanged(SensorEvent event) {

        startChronometer(chronoAllowedToStart, SystemClock.elapsedRealtime());
        chronoAllowedToStart = false;
        pauseActionImgView.setOnClickListener(TrainingActivity.this);

        if (!sessionPaused) {
            float heartRateFloat = event.values[0];
            int heartRateInt = Math.round(heartRateFloat);
            long timeMark = SystemClock.elapsedRealtime() - chronometer.getBase();

            saveHeartRate(timeMark, heartRateInt);

            heartRateTextView.setText(Integer.toString(heartRateInt));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Nothing to do here.
    }
}
