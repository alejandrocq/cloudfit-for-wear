package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.RequestsFragment;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.TrainingsCompletedFragment;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.TrainingsFragment;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetRequestsTask;
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
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;
import com.alejandro_castilla.cloudfitforwear.services.WearableService;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.TrainingsDb;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

public class MainActivity extends MaterialNavigationDrawer implements CloudFitDataHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    private TrainingsFragment trainingsFragment;
    private RequestsFragment requestsFragment;
    private TrainingsCompletedFragment trainingsCompletedFragment;
    private MaterialSection trainingsSection;
    private MaterialSection requestsSection;
    private MaterialSection trainingsCompletedSection;
    private MaterialDialog sendingToWearableDialog;
    private MaterialAccount account;

    private CloudFitService cloudFitService;
    private User cloudFitUser;

    private Intent wearableServiceIntent;
    private Messenger wearableServiceMessenger;
    private boolean isWearableConnected;

    private TrainingsDb db;

    /**
     * ServiceConnection to connect to CloudFit service.
     */
    private ServiceConnection cloudFitServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Connected to CloudFit service");
            CloudFitService.MyBinder cloudFitServiceBinder = (CloudFitService.MyBinder) service;
            cloudFitService = cloudFitServiceBinder.getService();
            new GetUserInfoTask(MainActivity.this, cloudFitService)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                    break;
                case StaticVariables.MSG_WEARABLE_STATE:
                    Bundle bundle = (Bundle) msg.obj;
                    isWearableConnected = bundle.getBoolean(StaticVariables.BUNDLE_WEARABLE_STATE);
                    break;
                case StaticVariables.MSG_SEND_TRAINING_TO_WEARABLE_ACK:
                    sendingToWearableDialog.dismiss();
                    showTrainingSentDialog();
                    break;
                case StaticVariables.MSG_TRAINING_RECEIVED_FROM_WEARABLE:
                    Bundle b = (Bundle) msg.obj;
                    Gson gson = new Gson();
                    WearableTraining trDone = gson
                            .fromJson(b.getString(StaticVariables.BUNDLE_WEARABLE_TRAINING_DONE),
                                    WearableTraining.class);
                    boolean res = db.insertTraining(trDone, cloudFitUser.getId());

                    if (res) {
                        showTrainingReceivedDialog();
                        //Training received correctly. Send an ACK to wearable device.
                        try {
                            Message ack = Message.obtain(null,
                                    StaticVariables.MSG_TRAINING_RECEIVED_FROM_WEARABLE_ACK);
                            wearableServiceMessenger.send(ack);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        showTrainingReceivedErrorDialog();
                    }

                    trainingsCompletedSection.setNotifications(db
                            .getAllTrainings(cloudFitUser.getId()).size());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Messenger mainActivityMessenger = new Messenger(MessageHandler);

    @Override
    public void init(Bundle savedInstanceState) {
        db = new TrainingsDb(this);

        trainingsFragment = new TrainingsFragment();
        requestsFragment = new RequestsFragment();
        trainingsCompletedFragment = new TrainingsCompletedFragment();

        trainingsCompletedFragment.setDb(db);

        account = new MaterialAccount(this.getResources(),"",
                "", R.drawable.ic_user_default,
                R.drawable.ic_running_background);
        this.addAccount(account);

        MaterialSection closeSessionSection = newSection("Cerrar sesión",
                R.drawable.ic_action_exit);
        closeSessionSection.setOnClickListener(new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        this.addAccountSection(closeSessionSection);

        trainingsSection = newSection("Entrenamientos", R.drawable.ic_action_event,
                trainingsFragment);
        requestsSection = newSection("Peticiones", R.drawable.ic_social_group_add,
                requestsFragment);
        this.addSection(trainingsSection);
        this.addSection(requestsSection);

        this.addDivisor();

        trainingsCompletedSection = newSection("Completados",
                R.drawable.ic_action_done, trainingsCompletedFragment);

        this.addSection(trainingsCompletedSection);

        MaterialSection aboutSection = newSection("Acerca de", R.drawable.ic_action_help,
                new Intent(MainActivity.this, AboutActivity.class));
        this.addBottomSection(aboutSection);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.ic_cloudfit_actionbar);
        }

        Intent cloudFitServiceIntent = new Intent(MainActivity.this, CloudFitService.class);
        bindService(cloudFitServiceIntent, cloudFitServiceConnection, Context.BIND_AUTO_CREATE);

        wearableServiceIntent = new Intent (MainActivity.this, WearableService.class);
        wearableServiceIntent.putExtra("messenger", mainActivityMessenger);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(wearableServiceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(wearableServiceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(cloudFitServiceConnection);
        stopService(wearableServiceIntent);
    }

    //////////////////////////////
    /* CloudFit Handler methods */
    //////////////////////////////

    @Override
    public void processUserData(User cloudFitUser) {
        this.cloudFitUser = cloudFitUser;

        if (cloudFitUser == null) {
            //Session has expired.
            Intent restartIntent = new Intent (MainActivity.this, LoginActivity.class);
            startActivity(restartIntent);
            finish();
        } else if (cloudFitUser.getRol() == StaticReferences.ROL_USER) {
            this.cloudFitUser.setUsername(cloudFitService.getFit().getSetting().getUsername());
            cloudFitService.getFit().getSetting().setUserID(cloudFitUser.getId()+"");
            zDBFunctions.saveSetting(cloudFitService.getDB(),
                    cloudFitService.getFit().getSetting());

            new GetTrainingsTask(this, cloudFitService, -1, StaticVariables.GET_ALL_TRAININGS)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new GetRequestsTask(this, cloudFitService)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //Update account with user data obtained from the platform
            account.setTitle(cloudFitUser.getName());
            account.setSubTitle(cloudFitUser.getEmail());
            notifyAccountDataChanged();

            trainingsCompletedFragment.setCloudFitUser(cloudFitUser);
            trainingsCompletedSection.setNotifications(db
                    .getAllTrainings(cloudFitUser.getId()).size());

        } else if (cloudFitUser.getRol() == StaticReferences.ROL_TRAINER) {
            Toast.makeText(this, "Esta aplicación no está disponible para entrenadores",
                    Toast.LENGTH_LONG).show();
            Intent startLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(startLogin);
            finish();
        }
    }

    @Override
    public void saveRequests(ArrayList<RequestTrainer> requests) {
        requestsSection.setNotifications(requests.size());
        requestsFragment.setRequests(requests);
    }

    @Override
    public void updateTrainingsList(ArrayList<CalendarEvent> calendarEvents) {
        trainingsSection.setNotifications(calendarEvents.size());
        trainingsFragment.setCalendarEvents(calendarEvents);
        trainingsFragment.setRefreshing(false);
    }

    @Override
    public void processTrainingDownloaded(Training training) {
        Log.d(TAG, "Parsing training with ID: " + training.getId());
        WearableTraining wearableTraining;
        wearableTraining = Utilities.trainingToWearableTraining(training, cloudFitUser);

        Gson gson = new Gson();
        String wearableTrainingJSON = gson.toJson(wearableTraining);

        sendTrainingToWearable(wearableTrainingJSON);
    }

    @Override
    public void downloadTrainingToBeSyncedWithWearable(CalendarEvent calendarEvent) {
        showSendingToWearableDialog();
        new GetTrainingsTask(this, cloudFitService, calendarEvent.getId(),
                StaticVariables.GET_SINGLE_TRAINING).execute();
    }

    @Override
    public void updateTrainingsCompletedNotifications(int trainingsNumber) {
        trainingsCompletedSection.setNotifications(trainingsNumber);
    }

    @Override
    public CloudFitService getCloudFitService() {
        return cloudFitService;
    }

    ///////////////////
    /* Other methods */
    ///////////////////

    private void sendTrainingToWearable(String trJson) {
        try {
            if (isWearableConnected) {
                Message msg = Message.obtain(null, StaticVariables.MSG_SEND_TRAINING_TO_WEARABLE);
                msg.obj = trJson;
                wearableServiceMessenger.send(msg);
            } else {
                sendingToWearableDialog.dismiss();
                showTrainingNotSentDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSendingToWearableDialog() {
        sendingToWearableDialog = new MaterialDialog.Builder(this)
                .title("Enviando entrenamiento al reloj")
                .content("Espere...")
                .progress(true, 0)
                .cancelable(false)
                .titleColorRes(R.color.md_grey_800)
                .contentColorRes(R.color.md_grey_800)
                .backgroundColorRes(R.color.md_white_1000)
                .build();
        sendingToWearableDialog.show();
        sendingToWearableDialogTimer(5000, sendingToWearableDialog);
    }

    private void sendingToWearableDialogTimer(long time, final MaterialDialog dialog) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!dialog.isCancelled()) {
                    dialog.dismiss();
                    showTrainingNotSentDialog();
                }

            }
        }, time);
    }

    private void showTrainingSentDialog() {
        if (isWearableConnected) {
            String DialogDescription = "El entrenamiento ha sido enviado correctamente al reloj." +
                    " Puede comenzarlo en cualquier momento.";
            new MaterialDialog.Builder(MainActivity.this)
                    .title("Entrenamiento enviado correctamente")
                    .content(DialogDescription)
                    .positiveText("Entendido")
                    .titleColorRes(R.color.md_grey_800)
                    .contentColorRes(R.color.md_grey_800)
                    .backgroundColorRes(R.color.md_white_1000)
                    .show();
        }
    }

    private void showTrainingNotSentDialog() {
        String dialogDescription = "No se ha podido enviar el entrenamiento al reloj. " +
                "Compruebe si está conectado y si la aplicación CloudFit For Wear está abierta.";
        new MaterialDialog.Builder(MainActivity.this)
                .title("Ha ocurrido un error")
                .content(dialogDescription)
                .positiveText("Entendido")
                .titleColorRes(R.color.md_grey_800)
                .contentColorRes(R.color.md_grey_800)
                .backgroundColorRes(R.color.md_white_1000)
                .show();
    }

    private void showTrainingReceivedDialog() {
        String dialogDescription = "El entrenamiento ha sido guardado correctamente. " +
                "Los resultados del mismo están disponibles en la sección de entrenamientos " +
                "completados.";
        new MaterialDialog.Builder(MainActivity.this)
                .title("Entrenamiento recibido")
                .content(dialogDescription)
                .positiveText("Entendido")
                .titleColorRes(R.color.md_grey_800)
                .contentColorRes(R.color.md_grey_800)
                .backgroundColorRes(R.color.md_white_1000)
                .show();
    }

    private void showTrainingReceivedErrorDialog() {
        String dialogDescription = "El entrenamiento no ha podido ser procesado correctamente. " +
                "Inténtelo de nuevo.";
        new MaterialDialog.Builder(MainActivity.this)
                .title("Ha ocurrido un error")
                .content(dialogDescription)
                .positiveText("Entendido")
                .titleColorRes(R.color.md_grey_800)
                .contentColorRes(R.color.md_grey_800)
                .backgroundColorRes(R.color.md_white_1000)
                .show();
    }
}
