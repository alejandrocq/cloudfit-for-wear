package com.alejandro_castilla.cloudfitforwear.asynctask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.activities.MainActivity;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.UserInfo;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.CloudFitCloud;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDB;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.zDBFunctions;

public class LoginTask extends AsyncTask<Void, String, Boolean> {

    private Activity context;
    private MaterialDialog progressDialog;
    private CloudFitService cloudFitService;
    private String username, password;
    private zDB db;

    public LoginTask(Activity context,
                     CloudFitService cloudFitService,
                     String username,
                     String password) {

        this.context = context;
        this.cloudFitService = cloudFitService;
        this.username = username;
        this.password = password;
        db = new zDB (context);
        db.open();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new MaterialDialog.Builder(context)
                .title("Iniciando sesión...")
                .content("Espere...")
                .progress(true, 0).build();
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPass(password);
        boolean loginResult = CloudFitCloud.login(cloudFitService, user);

        if (loginResult) {
            cloudFitService.getFit().getSetting().setUsername(username);
            cloudFitService.getFit().getSetting().setPassword(password);
            zDBFunctions.saveSetting(db,cloudFitService.getFit().getSetting());
            db.close();
        }

        return loginResult;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();
        String errorDialogDescription = "Ha ocurrido un error al iniciar la sesión. " +
                "Compruebe su usuario y contraseña e inténtelo de nuevo. " +
                "¿Es correcta la URL de CloudFit?";

        if (result) {
            Intent startMainActivityIntent = new Intent(context, MainActivity.class);
            context.startActivity(startMainActivityIntent);
            context.finish();
        } else {
            new MaterialDialog.Builder(context)
                    .title("Ha ocurrido un error")
                    .content(errorDialogDescription)
                    .positiveText("Entendido")
                    .show();
        }

        super.onPostExecute(result);
    }
}
