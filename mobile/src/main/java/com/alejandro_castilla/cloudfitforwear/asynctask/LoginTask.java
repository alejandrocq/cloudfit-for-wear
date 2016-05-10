package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.cloudfit.models.userInfo;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDB;

/**
 * Created by alejandrocq on 4/05/16.
 */
public class LoginTask extends AsyncTask<Void, String, Boolean> {

    private Context context;
    private CloudFitService cloudFitService;
    private boolean userFound;
    private String username, password;
    private zDB db;

    public LoginTask(Context context,
                     CloudFitService cloudFitService, String username, String password, zDB db) {
        this.context = context;
        this.cloudFitService = cloudFitService;
        this.username = username;
        this.password = password;
        this.db = db;
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
//            boolean userSavedOnDB = zDBFunctions.
//                    saveSetting(db,cloudFitService.getFit().getSetting());
//            Utils.print("SETTING","SAVED:"+userSavedOnDB);
            db.close();
        }

        return userFound;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }
}
