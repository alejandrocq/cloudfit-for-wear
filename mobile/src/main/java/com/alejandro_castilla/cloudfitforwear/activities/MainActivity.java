package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.TrainingsFragment;
import com.alejandro_castilla.cloudfitforwear.activities.fragments.RequestsFragment;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetUserInfoTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.trainings.Training;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.alejandro_castilla.cloudfitforwear.services.WearableService;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.blunderer.materialdesignlibrary.activities.NavigationDrawerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsMenuHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerBottomHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerStyleHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerTopHandler;
import com.blunderer.materialdesignlibrary.models.Account;

import java.util.ArrayList;

public class MainActivity extends NavigationDrawerActivity implements ActivityInterface {

    private final String TAG = MainActivity.class.getSimpleName();

    private Button downloadButton;
    private TrainingsFragment trainingsFragment;

    private CloudFitService cloudFitService;
    private User cloudFitUser;
    private ArrayList<RequestTrainer> requests;
    private ArrayList<CalendarEvent> calendarEvents;

    private Intent wearableServiceIntent;

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

    /**
     * Saves user info obtained from GetUserInfoTask on this activity.
     * @param cloudFitUser User data from CloudFit platform.
     * @param requests Requests from trainers sent by the platform.
     */
    @Override
    public void saveUserInfo(User cloudFitUser, ArrayList<RequestTrainer> requests) {
        this.cloudFitUser = cloudFitUser;

        if (cloudFitUser.getRol() == StaticReferences.ROL_USER) {
            Log.d(TAG, "ROL USER OK");
            this.cloudFitUser.setUsername(cloudFitService.getFit().getSetting().getUsername());
//            Log.d(TAG, "ROLE: " + cloudFitService.getFit().getSetting().getRole());
            cloudFitService.getFit().getSetting().setUserID(cloudFitUser.getId()+"");

            if (requests != null && requests.size()>0) {
                this.requests = requests;
                Log.d(TAG, "Request length: " + this.requests.size());
                Log.d(TAG, "Request trainer ID: " + this.requests.get(0).getTrainerid());
                Toast.makeText(this, "Número de solicitudes: " + this.requests.size(),
                        Toast.LENGTH_SHORT).show();
                zDBFunctions.saveSetting(cloudFitService.getDB(),
                        cloudFitService.getFit().getSetting());
//                new ReplyToRequestTask(this, cloudFitService,
//                        Long.parseLong(cloudFitService.getFit().getSetting().getUserID()),
//                        this.requests.get(0).getTrainerid(), StaticReferences.REQUEST_ACCEPT)
//                        .execute();
            } else {
                Toast.makeText(this, "No hay solicitudes.", Toast.LENGTH_SHORT).show();
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
        //Not needed.
    }



    @Override
    public CloudFitService getCloudFitService() {
        return cloudFitService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        downloadButton = (Button) findViewById(R.id.downloadButton);

        Intent cloudFitServiceIntent = new Intent(MainActivity.this, CloudFitService.class);
        bindService(cloudFitServiceIntent, cloudFitServiceConnection, Context.BIND_AUTO_CREATE);

        wearableServiceIntent = new Intent (MainActivity.this, WearableService.class);
        startService(wearableServiceIntent);

//        downloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                new ReplyToRequestTask(MainActivity.this, cloudFitService,
////                        Long.parseLong(cloudFitService.getFit().getSetting().getUserID()),
////                        requests.get(0).getTrainerid(), StaticReferences.REQUEST_ACCEPT)
////                        .execute();
//                new GetTrainingsTask(MainActivity.this, cloudFitService, MainActivity.this, 6,
//                        StaticVariables.GET_SINGLE_TRAINING).execute();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unbindService(cloudFitServiceConnection);
        stopService(wearableServiceIntent);
        super.onDestroy();
    }

    /* Material Desing Library methods */

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return new ActionBarDefaultHandler(this);
    }

    @Override
    public NavigationDrawerAccountsHandler getNavigationDrawerAccountsHandler() {
        return new NavigationDrawerAccountsHandler(this)
                .addAccount("Alejandro", "acastillaquesada@gmail.com",
                        R.drawable.ic_user, R.drawable.ic_running_background);
    }

    @Override
    public NavigationDrawerStyleHandler getNavigationDrawerStyleHandler() {
        return null;
    }

    @Override
    public NavigationDrawerAccountsMenuHandler getNavigationDrawerAccountsMenuHandler() {
        return null;
    }

    @Override
    public void onNavigationDrawerAccountChange(Account account) {

    }

    @Override
    public NavigationDrawerTopHandler getNavigationDrawerTopHandler() {
        trainingsFragment = new TrainingsFragment();
        return new NavigationDrawerTopHandler(this)
                .addItem(R.string.exercises_menu_name, trainingsFragment)
                .addItem(R.string.requests_menu_name, new RequestsFragment());
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
