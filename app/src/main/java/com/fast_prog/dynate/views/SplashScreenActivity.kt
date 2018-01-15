package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v13.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Ride
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    val currentLocale: Locale
        @TargetApi(Build.VERSION_CODES.N)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        Ride.instance = Ride()

        if (sharedPreferences.getString(Constants.PREFS_LANG, "")!!.isEmpty()) {
            val locale = currentLocale
            var lang = "en"

            if (locale.language.equals("ar", ignoreCase = true)) {
                lang = "ar"
            }

            val editor = sharedPreferences.edit()
            editor.putString(Constants.PREFS_LANG, lang)
            editor.commit()

        } else {
            var lang = "en"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "")!!.equals("ar", ignoreCase = true)) {
                lang = "ar"
            }

            val locale = Locale(lang)
            Locale.setDefault(locale)
            val confg = Configuration()
            confg.locale = locale
            baseContext.resources.updateConfiguration(confg, baseContext.resources.displayMetrics)
        }

        IsAppLiveBackground().execute()
    }

    private fun gotoNextActivity() {
        val SPLASH_TIME_OUT = 1000
        Handler().postDelayed({
            if (sharedPreferences.getBoolean(Constants.PREFS_IS_LOGIN, false)) {
                startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
            } else {
                startActivity(Intent(this@SplashScreenActivity, NoLoginActivity::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    @SuppressLint("StaticFieldLeak")
    inner class IsAppLiveBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgAppPackageName", Constants.APP_NAME)
            params.put("ArgAppVersionNo", Constants.APP_VERSION)

            return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "IsAppLive", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (!response.getBoolean("status")) {
                        ActivityCompat.finishAffinity(this@SplashScreenActivity)
                        val intent = Intent(this@SplashScreenActivity, UpdateActivity::class.java)
                        intent.putExtra("message", response.getString("data"))
                        startActivity(intent)
                        finish()

                    } else {
                        gotoNextActivity()
                    }

                } catch (e: JSONException) { e.printStackTrace() }
            } else {
                UtilityFunctions.showAlertOnActivity(this@SplashScreenActivity,
                        resources.getString(R.string.UnableToConnect), resources.getString(R.string.Retry).toString(),
                        "", false, false,
                        { IsAppLiveBackground().execute() }, {})
            }
        }
    }
}
