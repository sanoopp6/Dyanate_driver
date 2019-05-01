package com.fast_prog.dynate.views

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Ride
import com.fast_prog.dynate.utilities.Constants
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

        gotoNextActivity()
    }

    private fun gotoNextActivity() {
        val SPLASH_TIME_OUT = 3000
        Handler().postDelayed({
            if (sharedPreferences.getBoolean(Constants.PREFS_IS_LOGIN, false)) {
                startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                //val editor = sharedPreferences.edit()
                //editor.putString(Constants.PREFS_USER_TYPE, Constants.USER_TYPE_CONST_ADMIN)
                //editor.commit()
                //startActivity(Intent(this@SplashScreenActivity, AdminHomeActivity::class.java))
            } else {
                startActivity(Intent(this@SplashScreenActivity, NoLoginActivity::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

}
