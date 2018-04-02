package com.fast_prog.dynate.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.AsyncTask
import org.json.JSONObject
import java.util.*

/**
 * Created by sarathk on 2/28/17.
 */
@SuppressLint("StaticFieldLeak")
class UpdateFCMToken(private val context: Context, private val status: Boolean, private val userId: String) : AsyncTask<Void, Void, JSONObject>() {

    override fun doInBackground(vararg param: Void): JSONObject? {
        val jsonParser = JsonParser()
        val params = HashMap<String, String>()

        val preferences = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)

        params["ArgUsrID"] = userId

        if (status)
            params["ArgUsrFcmToken"] = preferences.getString(Constants.PREFS_FCM_TOKEN, "")
        else
            params["ArgUsrFcmToken"] = ""

        return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "UpdateUMFcmToken", "POST", params)
    }


    override fun onPostExecute(response: JSONObject?) {
        if (response != null) {
            //Log.e("response", response.toString());
        }
    }
}
