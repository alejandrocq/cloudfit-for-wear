package com.alejandro_castilla.cloudfitforwear.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.asynctask.LoginTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.Utils;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDB;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.gc.materialdesign.views.ButtonFlat;
import com.github.florent37.materialtextfield.MaterialTextField;

public class LoginActivity extends com.blunderer.materialdesignlibrary.activities.Activity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private final int PERMISSIONS_REQUEST_CODE = 0xFF;

    private EditText userTextField;
    private EditText passTextField;

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
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(LoginActivity.this);
            String cloudFitUrl = prefs.getString(StaticVariables.KEY_PREF_CLOUDFIT_URL,
                    "http://cloudfit-for-wear.appspot.com");
            cloudFitService.getFit().setUrl(cloudFitUrl);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from CloudFit service");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();

        setTitle(""); //Don't need an activity title here.

        MaterialTextField userMaterialTextField = (MaterialTextField)
                findViewById(R.id.userEditText);
        MaterialTextField passMaterialTextField = (MaterialTextField)
                findViewById(R.id.passEditText);
        ButtonFlat loginButton = (ButtonFlat) findViewById(R.id.loginButton);
        ImageView settingsImgView = (ImageView) findViewById(R.id.settingsImg);

        Intent cloudFitServiceIntent = new Intent(LoginActivity.this, CloudFitService.class);
        bindService(cloudFitServiceIntent, cloudFitServiceConnection, Context.BIND_AUTO_CREATE);

        userTextField = userMaterialTextField.getEditText();
        passTextField = passMaterialTextField.getEditText();

        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.checkInternetConnection(LoginActivity.this)) {
                        username = userTextField.getText().toString();
                        password = passTextField.getText().toString();
                        new LoginTask(LoginActivity.this, cloudFitService, "alejandrocq", "asdf")
                                .execute(); //Temporary trick to skip typing user and password
                    } else {
                        showNoInternetConnectionDialog();
                    }
                }
            });
        } else {
            Log.e(TAG, "Login button is null!");
        }

        if (settingsImgView != null) {
            settingsImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(LoginActivity.this);
                    String cloudFitUrl = prefs.getString(StaticVariables.KEY_PREF_CLOUDFIT_URL,
                            "http://cloudfit-for-wear.appspot.com");
                    new MaterialDialog.Builder(LoginActivity.this)
                            .title("Configuración URL CloudFit")
                            .content("Introduzca aquí la URL de la plataforma CloudFit")
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .positiveText("Establecer URL")
                            .input("URL de CloudFit", cloudFitUrl,
                                    new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    SharedPreferences.Editor prefsEditor =
                                            PreferenceManager.getDefaultSharedPreferences
                                                    (LoginActivity.this).edit();
                                    prefsEditor.putString(StaticVariables.KEY_PREF_CLOUDFIT_URL,
                                            input.toString());
                                    prefsEditor.apply();
                                    cloudFitService.getFit().setUrl(input.toString());
                                    Toast.makeText(LoginActivity.this,
                                            "URL establecida correctamente",
                                            Toast.LENGTH_LONG).show();
                                }
                            }).show();
                }
            });
        } else {
            Log.e(TAG, "Settings image is null!");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionsGranted = true;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:

                for (int result : grantResults) {
                    if (!(result == PackageManager.PERMISSION_GRANTED)) {
                        permissionsGranted = false;
                    }
                }

                if (permissionsGranted) {
                    checkCloudFitDatabase();
                } else {
                    Toast.makeText(LoginActivity.this, "Permisos insuficientes",
                            Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(cloudFitServiceConnection);
        db.close();
        super.onDestroy();
    }

    /**
     * Checks if directory and database are created. If not, creates all of them.
     */
    private void checkCloudFitDatabase() {
        //Database for CloudFit API
        Utils.checkDataBaseTimestamp();
        boolean res = Utils.checkDirectory();
        if (!res) {
            Toast.makeText(this, "Ha ocurrido un error al acceder al sistema de archivos",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        db = new zDB(this);
        db.open();
    }

    private void checkPermissions() {
        boolean readGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        boolean writeGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!readGranted || !writeGranted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            Log.d(TAG, "Permissions requested");
        } else {
            checkCloudFitDatabase();
        }
    }

    public void showNoInternetConnectionDialog() {
        String noInternetConnectionDescription = "Parece ser que no está conectado " +
                "a Internet. Compruebe su conexión e inténtelo de nuevo.";

        MaterialDialog.Builder noConnectionDialog = new
                MaterialDialog.Builder(LoginActivity.this)
                .title("Sin conexión a Internet")
                .content(noInternetConnectionDescription)
                .positiveText("Entendido");
        noConnectionDialog.show();
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
