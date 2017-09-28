package com.fast_prog.dynate.utilities;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
/**
 * Created by sarathk on 1/24/17.
 */
public class SetOffline extends AsyncTask<Void, Void, JSONObject> {
    private String userId;

    public SetOffline (String userId) {
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected JSONObject doInBackground(Void... param) {
        JsonParser jsonParser = new JsonParser();

        HashMap<String, String> params = new HashMap<>();

        params.put("ArgUsrId", userId);
        params.put("ArgLat", "0");
        params.put("ArgLng", "0");
        params.put("ArgTripStatus", "2");

        return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "UpdateLatLongDM", "POST", params);
    }

    protected void onPostExecute(JSONObject response) {
        if (response != null) {
            try {
                // Parsing json object response
                // response will be a json object
                if (response.getBoolean("status")) {
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
