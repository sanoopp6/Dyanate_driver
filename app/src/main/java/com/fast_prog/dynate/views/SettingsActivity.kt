package com.fast_prog.dynate.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.utilities.Constants
import kotlinx.android.synthetic.main.content_settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.Settings)
        titleTextView.textSize = 16f
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        btn_change_lang.setOnClickListener {
            var lang = "ar"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "")!!.equals("ar", ignoreCase = true)) {
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
        }
    }
}
