package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.content_all_orders.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AllOrdersActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.TripsFromOthers))


        newTripButton.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("mode", "7")
            intent.putExtra("modeStr", resources.getString(R.string.NewOrders))
            startActivity(intent)
        }

        rejectedByMeButton.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("mode", "5")
            intent.putExtra("modeStr", resources.getString(R.string.TripsRejectedByMe))
            startActivity(intent)
        }

        acceptedByMeButton.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("mode", "1")
            intent.putExtra("modeStr", resources.getString(R.string.TripsAcceptedByMe))
            startActivity(intent)
        }

        customerRejectedButton.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("mode", "3")
            intent.putExtra("modeStr", resources.getString(R.string.RejectedByCustomer))
            startActivity(intent)
        }

        customerAcceptedButton.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("mode", "2")
            intent.putExtra("modeStr", resources.getString(R.string.AcceptedByCustomer))
            startActivity(intent)
        }

        customerCancelledButton.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("mode", "4")
            intent.putExtra("modeStr", resources.getString(R.string.CancelledByCustomer))
            startActivity(intent)
        }

        completedButton.setOnClickListener {
            val intent = Intent(this@AllOrdersActivity, AllOrdersListActivity::class.java)
            intent.putExtra("mode", "6")
            intent.putExtra("modeStr", resources.getString(R.string.CompletedTrips))
            startActivity(intent)
        }

        tabLayout_feedback.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                hideAllFrames()

                when {
                    tab.position == 0 -> {
                        newTripFrameLayout.visibility = View.VISIBLE
                    }
                    tab.position == 1 -> {
                        acceptedByMeFrameLayout.visibility = View.VISIBLE
                        customerAcceptedFrameLayout.visibility = View.VISIBLE
                        completedFrameLayout.visibility = View.VISIBLE
                    }
                    tab.position == 2 -> {
                        customerRejectedFrameLayout.visibility = View.VISIBLE
                        customerCancelledFrameLayout.visibility = View.VISIBLE
                        rejectedByMeFrameLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
    }

    private fun hideAllFrames() {
        newTripFrameLayout.visibility = View.GONE
        rejectedByMeFrameLayout.visibility = View.GONE
        acceptedByMeFrameLayout.visibility = View.GONE
        customerRejectedFrameLayout.visibility = View.GONE
        customerAcceptedFrameLayout.visibility = View.GONE
        customerCancelledFrameLayout.visibility = View.GONE
        completedFrameLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        TripDetailsMasterListCountBackground("1", acceptedByMeTextView).execute()
        TripDetailsMasterListCountBackground("2", customerAcceptedTextView).execute()
        TripDetailsMasterListCountBackground("3", customerRejectedTextView).execute()
        TripDetailsMasterListCountBackground("4", customerCancelledTextView).execute()
        TripDetailsMasterListCountBackground("5", rejectedByMeTextView).execute()
        TripDetailsMasterListCountBackground("6", completedTextView).execute()
        TripDetailsMasterListCountBackground("7", newTripTextView).execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsMasterListCountBackground internal constructor(internal var status: String, internal var view: TextView) : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            //if (glassExtra.equals("true", ignoreCase = true)) {
            //params["ArgTripMCustId"] = "1"
            //params["ArgExcludeCustId"] = "0"
            //} else {

            params["ArgTripMCustId"] = "0"
            params["ArgExcludeCustId"] = "0"
            params["ArgTripDDmId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "0")
            params["ArgTripMID"] = "0"
            params["ArgTripDID"] = "0"
            params["ArgTripMStatus"] = "0"
            params["ArgTripDStatus"] = status

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
                            view.startAnimation(UtilityFunctions.blinkAnimation)

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
