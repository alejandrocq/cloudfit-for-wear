package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.RequestsFragment;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.TrainingsFragment;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetUserInfoTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.alejandro_castilla.cloudfitforwear.services.WearableService;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.activities.NavigationDrawerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsMenuHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerBottomHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerStyleHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerTopHandler;
import com.blunderer.materialdesignlibrary.models.Account;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends NavigationDrawerActivity implements ActivityInterface {

    private final String TAG = MainActivity.class.getSimpleName();

    private TrainingsFragment trainingsFragment;
    private RequestsFragment requestsFragment;
    private MaterialDialog sendingToWearableDialog;

    private CloudFitService cloudFitService;
    private User cloudFitUser;
    private ArrayList<RequestTrainer> requests;
    private ArrayList<CalendarEvent> calendarEvents;

    private Intent wearableServiceIntent;
    private Messenger wearableServiceMessenger;
    private boolean isWearableConnected;

    /**
     * ServiceConnection to connect to CloudFit service.
     */
    private ServiceConnection cloudFitServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Connected to CloudFit service");
            CloudFitService.MyBinder cloudFitServiceBinder = (CloudFitService.MyBinder) service;
            cloudFitService = cloudFitServiceBinder.getService();
            new GetUserInfoTask(MainActivity.this, cloudFitService, MainActivity.this).execute();
            Log.d(TAG, "Getting user data");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from CloudFit service");
        }
    };

    private final Handler MessageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StaticVariables.MSG_WEARABLESERVICE_MESSENGER:
                    wearableServiceMessenger = (Messenger) msg.obj;
                    Log.d(TAG, "Messenger from WearableService received.");
                    break;
                case StaticVariables.MSG_WEARABLE_STATE:
                    Bundle bundle = (Bundle) msg.obj;
                    isWearableConnected = bundle.getBoolean(StaticVariables.BUNDLE_WEARABLE_STATE);
//                    Toast.makeText(MainActivity.this, "Estado del reloj: " + isWearableConnected,
//                            Toast.LENGTH_LONG).show();
                    break;
                case StaticVariables.MSG_SEND_TRAINING_TO_WEARABLE_ACK:
                    sendingToWearableDialog.dismiss();
                    showTrainingSentDialog();
                    break;
                case StaticVariables.MSG_TRAINING_RECEIVED_FROM_WEARABLE:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Messenger mainActivityMessenger = new Messenger(MessageHandler);

    ////////////////////////////////
    /* Activity interface methods */
    ////////////////////////////////

    /**
     * Saves user info obtained from GetUserInfoTask on this activity.
     * @param cloudFitUser User data from CloudFit platform.
     * @param requests Requests from trainers sent by the platform.
     */
    @Override
    public void saveUserInfo(User cloudFitUser, ArrayList<RequestTrainer> requests) {
        this.cloudFitUser = cloudFitUser;

        if (cloudFitUser == null) {
            //Temporary solution when activity gets killed by Android.
            Log.d(TAG, "cloudFitUser null");
            Intent restartIntent = new Intent (MainActivity.this, LoginActivity.class);
            startActivity(restartIntent);
            finish();
        } else if (cloudFitUser.getRol() == StaticReferences.ROL_USER) {
            Log.d(TAG, "ROL USER OK");
            this.cloudFitUser.setUsername(cloudFitService.getFit().getSetting().getUsername());
//            Log.d(TAG, "ROLE: " + cloudFitService.getFit().getSetting().getRole());
            cloudFitService.getFit().getSetting().setUserID(cloudFitUser.getId()+"");

            if (requests != null && requests.size()>0) {
                this.requests = requests;
//                Log.d(TAG, "Request length: " + this.requests.size());
//                Log.d(TAG, "Request trainer ID: " + this.requests.get(0).getTrainerid());
//                Toast.makeText(this, "Número de solicitudes: " + this.requests.size(),
//                        Toast.LENGTH_SHORT).show();
                zDBFunctions.saveSetting(cloudFitService.getDB(),
                        cloudFitService.getFit().getSetting());
//                new ReplyToRequestTask(this, cloudFitService,
//                        Long.parseLong(cloudFitService.getFit().getSetting().getUserID()),
//                        this.requests.get(0).getTrainerid(), StaticReferences.REQUEST_ACCEPT)
//                        .execute();
            } else {
//                Toast.makeText(this, "No hay solicitudes.", Toast.LENGTH_SHORT).show();
            }
            new GetTrainingsTask(this, this, cloudFitService, -1, StaticVariables.GET_ALL_TRAININGS)
                    .execute();
        }
    }

    @Override
    public void stopRefreshing() {
        //Not needed.
    }

    @Override
    public void updateTrainingsList(ArrayList<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
        trainingsFragment.setCalendarEvents(calendarEvents);
    }

    @Override
    public void saveAndParseTraining(Training training) {
        sendingToWearableDialog = new MaterialDialog.Builder(this)
                .title("Enviando entrenamiento al reloj")
                .content("Espere...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        sendingToWearableDialog.show();
        sendingToWearableDialogTimer(5000, sendingToWearableDialog);

        try {
            Log.d(TAG, "Parsing training with ID: " + training.getId());
            WearableTraining wearableTraining;
            wearableTraining = Utilities.trainingToWearableTraining(training);
//        Toast.makeText(this, "Tiempo mínimo running exercise: "
//                + wearableTraining.getRunningExercise().getTimeP(), Toast.LENGTH_LONG).show();
            Gson gson = new Gson();
            String wearableTrainingJSON = gson.toJson(wearableTraining);

            if (isWearableConnected) {
                Message msg = Message.obtain(null, StaticVariables.MSG_SEND_TRAINING_TO_WEARABLE);
                msg.obj = wearableTrainingJSON;
                wearableServiceMessenger.send(msg);
            } else {
                sendingToWearableDialog.dismiss();
                showTrainingNotSentDialog();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadTrainingToBeSyncedWithWearable(CalendarEvent calendarEvent) {
        new GetTrainingsTask(this, this, cloudFitService, calendarEvent.getId(),
                StaticVariables.GET_SINGLE_TRAINING).execute();
    }

    ///////////////////
    /* Other methods */
    ///////////////////

    public void sendingToWearableDialogTimer(long time, final MaterialDialog dialog) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!dialog.isCancelled()) {
                    dialog.dismiss();
                    showTrainingNotSentDialog();
                }

            }
        }, time);
    }

    public void showTrainingSentDialog() {
        if (isWearableConnected) {
            String DialogDescription = "El entrenamiento ha sido enviado correctamente al reloj." +
                    " Puede comenzarlo en cualquier momento.";
            new MaterialDialog.Builder(MainActivity.this)
                    .title("Entrenamiento enviado correctamente")
                    .content(DialogDescription)
                    .positiveText("Entendido")
                    .show();
        }
    }

    public void showTrainingNotSentDialog() {
        String DialogDescription = "No se ha podido enviar el entrenamiento al reloj. " +
                "Compruebe si está conectado y si la aplicación CloudFit For Wear está abierta.";
        new MaterialDialog.Builder(MainActivity.this)
                .title("Ha ocurrido un error")
                .content(DialogDescription)
                .positiveText("Entendido")
                .show();
    }

    @Override
    public CloudFitService getCloudFitService() {
        return cloudFitService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.ic_cloudfit_actionbar);
        }

        Intent cloudFitServiceIntent = new Intent(MainActivity.this, CloudFitService.class);
        bindService(cloudFitServiceIntent, cloudFitServiceConnection, Context.BIND_AUTO_CREATE);

        wearableServiceIntent = new Intent (MainActivity.this, WearableService.class);
        wearableServiceIntent.putExtra("messenger", mainActivityMessenger);
        startService(wearableServiceIntent);
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        unbindService(cloudFitServiceConnection);
        stopService(wearableServiceIntent);
        super.onDestroy();
    }

    /////////////////////////////////////
    /* Material Desing Library methods */
    ////////////////////////////////////

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return new ActionBarDefaultHandler(this);
    }

    @Override
    public NavigationDrawerAccountsHandler getNavigationDrawerAccountsHandler() {
        return new NavigationDrawerAccountsHandler(this)
                .addAccount("Alejandro", "acastillaquesada@gmail.com",
                        R.drawable.ic_user_default, R.drawable.ic_running_background);
    }

    @Override
    public NavigationDrawerStyleHandler getNavigationDrawerStyleHandler() {
        return new NavigationDrawerStyleHandler();
    }

    @Override
    public NavigationDrawerAccountsMenuHandler getNavigationDrawerAccountsMenuHandler() {
        return new NavigationDrawerAccountsMenuHandler(this)
                .addItem("Cerrar sesión", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent closeSessionIntent = new Intent(MainActivity.this,
                                LoginActivity.class);
                        startActivity(closeSessionIntent);
                        finish();
                    }
                });
    }

    @Override
    public void onNavigationDrawerAccountChange(Account account) {

    }

    @Override
    public NavigationDrawerTopHandler getNavigationDrawerTopHandler() {
        trainingsFragment = new TrainingsFragment();
        requestsFragment = new RequestsFragment();
        return new NavigationDrawerTopHandler(this)
                .addSection("CloudFit")
                .addItem(R.string.exercises_menu_name, trainingsFragment)
                .addItem(R.string.requests_menu_name, requestsFragment)
                .addDivider();
    }

    @Override
    public NavigationDrawerBottomHandler getNavigationDrawerBottomHandler() {
        return new NavigationDrawerBottomHandler(this)
                .addSettings(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    @Override
    public int defaultNavigationDrawerItemSelectedPosition() {
        return 0;
    }

    @Override
    public boolean overlayActionBar() {
        return false;
    }

    @Override
    public boolean replaceActionBarTitleByNavigationDrawerItemTitle() {
        return true;
    }

    @Override
    protected boolean enableActionBarShadow() {
        return false;
    }
}
