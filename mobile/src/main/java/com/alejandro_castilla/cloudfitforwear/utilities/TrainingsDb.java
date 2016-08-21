package com.alejandro_castilla.cloudfitforwear.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class TrainingsDb {

    private final String TAG = TrainingsDb.class.getSimpleName();

    private final String DATABASE_NAME = "trainings_completed.sqlite";
    private final String DATABASE_FULL_PATH = StaticVariables.APP_PATH + DATABASE_NAME;

    private final String TABLE_TRAININGS = "trainings";

    private SQLiteDatabase db;

    public TrainingsDb(Context ctx) {
        Utilities.checkAppDirectory();
        if (!checkIfDatabaseExists()) {
            Utilities.writeAssetFile(ctx, DATABASE_NAME, DATABASE_FULL_PATH);
        }
    }

    private boolean checkIfDatabaseExists() {
        try {
            File f = new File(DATABASE_FULL_PATH);
            if (!f.exists()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openDb() {
        try {
            db = SQLiteDatabase.openDatabase(DATABASE_FULL_PATH, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void closeDb() {
        try {
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Cursor getQuery (String query) {
        try {
            return db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean execQuery (String query) {
        boolean res = true;
        try {
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        }

        return res;
    }

    public ArrayList<WearableTraining> getAllTrainings(long userId) {
        try {
            openDb();
            ArrayList<WearableTraining> trainings = new ArrayList<>();
            Gson gson = new Gson();

            String query = "SELECT * FROM " + TABLE_TRAININGS;
            Cursor cur = getQuery(query);
            Log.d(TAG, query);

            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        Long trID = cur.getLong(cur.getColumnIndex("ID"));
                        String trJSON = cur.getString(cur.getColumnIndex("training_data_json"));

                        WearableTraining tr = gson.fromJson(trJSON, WearableTraining.class);
                        tr.setTrainingId(trID);

                        if (tr.getUserId() == userId) trainings.add(tr);

                    } while (cur.moveToNext());
                }

                cur.close();
                closeDb();
            } else {
                closeDb();
            }

            return trainings;

        } catch (Exception e) {
            e.printStackTrace();
            closeDb();
        }

        return new ArrayList<>();

    }

    public boolean insertTraining (WearableTraining tr, long cloudFitUserId) {
        boolean res;
        String query;
        Gson gson = new Gson();

        tr.setUserId(cloudFitUserId);

        try {
            if (!checkIfTrainingAlreadyExistsOnDatabase(tr.getCloudFitId(), cloudFitUserId)) {
                query = "INSERT INTO " + TABLE_TRAININGS + " (training_data_json) "
                        + "VALUES (" + "'" + gson.toJson(tr) + "'" + ")";
                Log.d(TAG, "QUERY INSERT TRAINING: " + query);
            } else {
                Long trId = getTrainingIdFromCloudFitId(tr.getCloudFitId(), cloudFitUserId);
                query = "UPDATE " + TABLE_TRAININGS + " SET training_data_json='"
                        + gson.toJson(tr) + "' WHERE ID=" + trId;
                Log.d(TAG, "QUERY UPDATE TRAINING: " + query);
            }

            openDb();
            res = execQuery(query);
            closeDb();
        } catch (Exception e) {
            e.printStackTrace();
            closeDb();
            res = false;
        }

        return res;
    }

    public boolean checkIfTrainingAlreadyExistsOnDatabase (long cloudfitId, long cloudFitUserId) {
        ArrayList<WearableTraining> trainings = getAllTrainings(cloudFitUserId);

        for (WearableTraining tr : trainings) {
            if (tr.getCloudFitId() == cloudfitId) {
                return true;
            }
        }

        return false;
    }

    public long getTrainingIdFromCloudFitId (long cloudfitId, long cloudFitUserId) {
        ArrayList<WearableTraining> trainings = getAllTrainings(cloudFitUserId);

        for (WearableTraining tr : trainings) {
            if (tr.getCloudFitId() == cloudfitId) {
                return tr.getTrainingId();
            }
        }

        return -1;
    }

    public boolean deleteTraining (long trainingId) {
        boolean res;
        try {
            openDb();
            String query = "DELETE FROM "+TABLE_TRAININGS+" WHERE ID="+trainingId;
            Log.d(TAG, "DELETE TRAINING QUERY: "+query);

            res = execQuery(query);
            closeDb();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            closeDb();
        }

        return false;
    }

}
