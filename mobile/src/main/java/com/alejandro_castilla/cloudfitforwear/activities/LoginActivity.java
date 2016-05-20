package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.asynctask.LoginTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.Utils;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDB;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.gc.materialdesign.views.ButtonFlat;
import com.github.florent37.materialtextfield.MaterialTextField;

public class LoginActivity extends com.blunderer.materialdesignlibrary.activities.Activity {

    private final String TAG = LoginActivity.class.getSimpleName();

    private MaterialTextField userMaterialTextField;
    private MaterialTextField passMaterialTextField;
    private EditText userTextField;
    private EditText passTextField;
    private ButtonFlat loginButton;

    private String username, password;
    private zDB db;
    private CloudFitService cloudFitService;

    /**
     * ServiceConnection to connect to CloudFit service.
     */
    private ServiceConnection cloudFitServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Connected to CloudFit service");
            CloudFitService.MyBinder cloudFitServiceBinder = (CloudFitService.MyBinder) service;
            cloudFitService = cloudFitServiceBinder.getService();

//            Setting cloudFitSetting = zDBFunctions.getSetting(db);
//            cloudFitService.getFit().setSetting(cloudFitSetting);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from CloudFit service");
        }
    };

    /**
     * Checks if directory and databases are created. If not, creates all of them.
     */
    private void checkDatabaseAndDirectory() {
        Utils.checkDataBaseTimestamp();
        Utils.checkDirectory();
        db = new zDB(this);
        db.open();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);

        setTitle(""); //Don't need an activity title here.

        userMaterialTextField = (MaterialTextField) findViewById(R.id.userEditText);
        passMaterialTextField = (MaterialTextField) findViewById(R.id.passEditText);
        userTextField = userMaterialTextField.getEditText();
        passTextField = passMaterialTextField.getEditText();
        loginButton = (ButtonFlat) findViewById(R.id.loginButton);

        checkDatabaseAndDirectory();
        Intent cloudFitServiceIntent = new Intent(LoginActivity.this, CloudFitService.class);
        bindService(cloudFitServiceIntent, cloudFitServiceConnection, Context.BIND_AUTO_CREATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userTextField.getText().toString();
                password = passTextField.getText().toString();
                new LoginTask(LoginActivity.this, cloudFitService, "alejandrocq", "asdf")
                        .execute(); //Temporary trick to skip typing user and password

            }
        });


    }

    @Override
    protected void onDestroy() {
        unbindService(cloudFitServiceConnection);
        db.close();
        super.onDestroy();
    }

    /* Material Design Library methods */

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return null;
    }

    @Override
    protected boolean enableActionBarShadow() {
        return false;
    }



}
