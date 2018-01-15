package com.fast_prog.dynate.utilities

import android.os.AsyncTask

import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap

/**
 * Created by sarathk on 1/24/17.
 */
class SetOffline(private val userId: String) : AsyncTask<Void, Void, JSONObject>() {

    override fun doInBackground(vararg param: Void): JSONObject? {
        val jsonParser = JsonParser()

        val params = HashMap<String, String>()

        params.put("ArgUsrId", userId)
        params.put("ArgLat", "0")
        params.put("ArgLng", "0")
        params.put("ArgTripStatus", "2")

        return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "UpdateLatLongDM", "POST", params)
    }

    override fun onPostExecute(response: JSONObject?) {
        if (response != null) {
            try {
                // Parsing json object response
                // response will be a json object
                if (response.getBoolean("status")) {
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }
}
