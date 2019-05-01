package com.fast_prog.dynate.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.content_settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.Settings))

        btn_change_lang.setOnClickListener {
            UtilityFunctions.showAlertOnActivity(this@SettingsActivity,
                    resources.getText(R.string.ChangeLanguageRestart).toString(), resources.getString(R.string.Yes).toString(),
                    resources.getString(R.string.No).toString(), true, false,
                    {
                        UtilityFunctions.showProgressDialog(this@SettingsActivity)

                        Handler().postDelayed({
                            UtilityFunctions.dismissProgressDialog()

                            var lang = "ar"

                            if (sharedPreferences.getString(Constants.PREFS_LANG, "").equals("ar", true)) {
                                lang = "en"
                            }

                            val locale = Locale(lang)
                            Locale.setDefault(locale)
                            val confg = Configuration()
                            confg.locale = locale
                            baseContext.resources.updateConfiguration(confg, baseContext.resources.displayMetrics)

                            val editor = sharedPreferences.edit()
                            editor.putString(Constants.PREFS_LANG, lang)
                            editor.commit()

                            startActivity(Intent(this@SettingsActivity, HomeActivity::class.java))
                            ActivityCompat.finishAffinity(this@SettingsActivity)
                            finish()
                        }, 2000)
                    }, {})
        }
    }
}
