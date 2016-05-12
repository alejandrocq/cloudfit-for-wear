package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.asynctask.LoginTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.Utils;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDB;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    private EditText userTextField;
    private EditText passTextField;
    private Button loginButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userTextField = (EditText) findViewById(R.id.userEditText);
        passTextField = (EditText) findViewById(R.id.passEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        checkDatabaseAndDirectory();
        Intent cloudFitServiceIntent = new Intent(LoginActivity.this, CloudFitService.class);
        bindService(cloudFitServiceIntent, cloudFitServiceConnection, Context.BIND_AUTO_CREATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userTextField.getText().toString();
                password = passTextField.getText().toString();
                new LoginTask(LoginActivity.this, cloudFitService, username, password)
                        .execute();

            }
        });


    }

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
    protected void onDestroy() {
        unbindService(cloudFitServiceConnection);
        db.close();
        super.onDestroy();
    }
}
