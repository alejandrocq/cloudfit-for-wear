package com.alejandro_castilla.cloudfitforwear.data;

import com.alejandro_castilla.cloudfitforwear.data.PracticeSession.HeartRate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alejandrocq on 24/04/16.
 */
public class PracticeJSONParser {

    private PracticeSession practiceSession;

    public PracticeJSONParser(PracticeSession practiceSession) {
        this.practiceSession = practiceSession;
    }

    public String writeToJSON() {
        try {
            JSONObject mainObj = new JSONObject();
            JSONObject sessionJSONObj = new JSONObject();
            sessionJSONObj.put("elapsed_time", practiceSession.getElapsedTime());

            JSONObject heartRateJSONObj = new JSONObject();
            JSONArray timeMarksArray = new JSONArray();
            JSONArray heartRateArray = new JSONArray();

            for (HeartRate heartRate : practiceSession.getHeartRateList()) {
                timeMarksArray.put(heartRate.getTimeMark());
                heartRateArray.put(heartRate.getHeartRateValue());
            }

            heartRateJSONObj.put("time_marks", timeMarksArray);
            heartRateJSONObj.put("heart_rate_values", heartRateArray);

            sessionJSONObj.put("heart_rate", heartRateJSONObj);

            mainObj.put("session", sessionJSONObj);

            return mainObj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

}
