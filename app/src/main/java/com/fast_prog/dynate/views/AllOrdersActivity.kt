package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import kotlinx.android.synthetic.main.content_all_orders.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AllOrdersActivity : AppCompatActivity() {

    internal lateinit var glassExtra: String

    internal lateinit var sharedPreferences: SharedPreferences

    val blinkAnimation: Animation
        get() {
            val animation = AlphaAnimation(1f, 0f)
            animation.duration = 300
            animation.interpolator = LinearInterpolator()
            animation.repeatCount = -1
            animation.repeatMode = Animation.REVERSE

            return animation
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        glassExtra = intent.getStringExtra("glass")

        val title: String
        if (glassExtra.equals("true", ignoreCase = true)) {
            title = resources.getString(R.string.TripsFromGlass)
        } else {
            title = resources.getString(R.string.TripsFromOthers)
        }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = title
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@AllOrdersActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        accepted_by_me_button.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("glass", glassExtra)
            intent.putExtra("mode", "1")
            intent.putExtra("modeStr", accepted_by_me_button.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }

        customer_accepted_button.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("glass", glassExtra)
            intent.putExtra("mode", "2")
            intent.putExtra("modeStr", customer_accepted_button.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }

        customer_rejected_button.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("glass", glassExtra)
            intent.putExtra("mode", "3")
            intent.putExtra("modeStr", customer_rejected_button.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }

        customer_cancelled_button.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("glass", glassExtra)
            intent.putExtra("mode", "4")
            intent.putExtra("modeStr", customer_cancelled_button.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }

        rejected_by_me_button.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("glass", glassExtra)
            intent.putExtra("mode", "5")
            intent.putExtra("modeStr", rejected_by_me_button.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }

        completed_button.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("glass", glassExtra)
            intent.putExtra("mode", "6")
            intent.putExtra("modeStr", completed_button.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }

        new_trips_button.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("glass", glassExtra)
            intent.putExtra("mode", "7")
            intent.putExtra("modeStr", new_trips_button.text.toString().trim { it <= ' ' })
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        TripDetailsMasterListCountBackground("1", accepted_by_me_count).execute()
        TripDetailsMasterListCountBackground("2", customer_accepted_count).execute()
        TripDetailsMasterListCountBackground("3", customer_rejected_count).execute()
        TripDetailsMasterListCountBackground("4", customer_cancelled_count).execute()
        TripDetailsMasterListCountBackground("5", rejected_by_me_count).execute()
        TripDetailsMasterListCountBackground("6", completed_count).execute()
        TripDetailsMasterListCountBackground("7", new_trips_count).execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsMasterListCountBackground internal constructor(internal var status: String, internal var view: TextView) : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            if (glassExtra.equals("true", ignoreCase = true)) {
                params.put("ArgTripMCustId", "1")
                params.put("ArgExcludeCustId", "0")

            } else {
                params.put("ArgTripMCustId", "0")
                params.put("ArgExcludeCustId", "1")
            }

            params.put("ArgTripDDmId", sharedPreferences.getString(Constants.PREFS_USER_ID, "0"))
            params.put("ArgTripMID", "0")
            params.put("ArgTripDID", "0")
            params.put("ArgTripMStatus", "0")
            params.put("ArgTripDStatus", status)

            var BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterListCount"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterListCount"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val jsonArray = response.getJSONArray("data")
                        var count = 0

                        try {
                            count = Integer.parseInt(jsonArray.getJSONObject(0).getString("Cnt"))
                        } catch (ignored: Exception) {
                        }

                        if (count > 0) {
                            view.visibility = View.VISIBLE
                            view.text = String.format(Locale.getDefault(), "%d", count)
                            view.startAnimation(blinkAnimation)

                        } else {
                            view.visibility = View.GONE
                            view.clearAnimation()
                        }
                    }

                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }
}
