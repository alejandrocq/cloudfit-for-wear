package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.activities.MainActivity;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.userInfo;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.Utils;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDB;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;

/**
 * Created by alejandrocq on 4/05/16.
 */
public class LoginTask extends AsyncTask<Void, String, Boolean> {

    private Activity context;
    private CloudFitService cloudFitService;
    private boolean userFound;
    private String username, password;
    private zDB db;

    public LoginTask(Activity context,
                     CloudFitService cloudFitService, String username, String password) {
        this.context = context;
        this.cloudFitService = cloudFitService;
        this.username = username;
        this.password = password;
        db = new zDB (context);
        db.open();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        userInfo user = new userInfo();
        user.setUsername(username);
        user.setPass(password);
        userFound = CloudFitCloud.login(cloudFitService, user);

        if (userFound) {
            cloudFitService.getFit().getSetting().setUsername(username);
            cloudFitService.getFit().getSetting().setPassword(password);
            boolean userSavedOnDB = zDBFunctions.
                    saveSetting(db,cloudFitService.getFit().getSetting());
            Utils.print("SETTING","SAVED:"+userSavedOnDB);
            db.close();
        }

        return userFound;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();
            Intent startMainActivityIntent = new Intent(context, MainActivity.class);
            context.startActivity(startMainActivityIntent);
            context.finish();
        } else {
            Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
        }

        super.onPostExecute(result);
    }
}
