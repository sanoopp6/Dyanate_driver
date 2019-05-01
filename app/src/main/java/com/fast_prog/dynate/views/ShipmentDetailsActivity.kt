package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.Toolbar
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import kotlinx.android.synthetic.main.activity_shipment_details.*
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class ShipmentDetailsActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    var order: Order? = null

    var orderList: MutableList<Order>? = null

    var noRowMsg = ""

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        customTitle(resources.getString(R.string.ShipmentDetails))

        toolbar.setNavigationOnClickListener { finish() }

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        order = intent.getSerializableExtra("order") as Order?
        if (order == null) finish()

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        if (ConnectionDetector.isConnected(this@ShipmentDetailsActivity)) {
            UpdateTripNotifiedCustStatusBackground(order?.tripId!!).execute()
            TripDetailsMasterListBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(main_content)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsMasterListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgTripMCustId"] = "0"
            params["ArgTripDDmId"] = "0"
            params["ArgTripMID"] = order!!.tripId?:""
            params["ArgTripDID"] = "0"
            params["ArgTripMStatus"] = "0"
            params["ArgTripDStatus"] = "0"
            params["ArgExcludeCustId"] = "0"

            var BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val ordersJSONArray = response.getJSONArray("data")
                        orderList = ArrayList()

                        for (i in 0 until ordersJSONArray.length()) {
                            if (!ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim().equals("7", ignoreCase = true)) {
                                val order = Order()

                                order.tripId = ordersJSONArray.getJSONObject(i).getString("TripMID").trim()
                                order.tripNo = ordersJSONArray.getJSONObject(i).getString("TripMNo").trim()
                                order.tripFromAddress = ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim()
                                order.tripFromLat = ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim()
                                order.tripFromLng = ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim()

                                try {
                                    order.tripFromSelf = ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf").trim().toBoolean()
                                } catch (e: Exception) {
                                    order.tripFromSelf = false
                                }

                                order.tripFromName = ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim()
                                order.tripFromMob = ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim()
                                order.tripToAddress = ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim()
                                order.tripToLat = ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim()
                                order.tripToLng = ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim()

                                try {
                                    order.tripToSelf = ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf").trim().toBoolean()
                                } catch (e: Exception) {
                                    order.tripToSelf = false
                                }

                                order.tripToName = ordersJSONArray.getJSONObject(i).getString("TripMToName").trim()
                                order.tripToMob = ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim()
                                order.vehicleModel = ordersJSONArray.getJSONObject(i).getString("VMName").trim()
                                order.vehicleType = ordersJSONArray.getJSONObject(i).getString("VmoName").trim()
                                order.scheduleDate = ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim()
                                order.scheduleTime = ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim()
                                order.userName = ordersJSONArray.getJSONObject(i).getString("UsrName").trim()
                                order.userMobile = ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim()
                                order.tripFilter = ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim()
                                order.tripStatus = ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim()
                                order.tripSubject = ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim()
                                order.tripNotes = ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim()
                                order.vehicleImage = ordersJSONArray.getJSONObject(i).getString("VmoURL").trim()
                                order.tripDId = ordersJSONArray.getJSONObject(i).getString("TripDID").trim()
                                order.tripDNo = ordersJSONArray.getJSONObject(i).getString("TripDNo").trim()
                                order.tripDStatus = ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim()
                                order.tripDDmId = ordersJSONArray.getJSONObject(i).getString("TripDDmId").trim()
                                order.tripDIsNegotiable = ordersJSONArray.getJSONObject(i).getString("TripDIsNegotiable").trim()
                                order.tripDRate = ordersJSONArray.getJSONObject(i).getString("TripDRate").trim()
                                order.tripDDateTime = ordersJSONArray.getJSONObject(i).getString("TripDDateTime").trim()
                                order.tripDFilterName = ordersJSONArray.getJSONObject(i).getString("TripDFilterName").trim()
                                order.dmName = ordersJSONArray.getJSONObject(i).getString("DmName").trim()
                                order.dmMobNumber = ordersJSONArray.getJSONObject(i).getString("DmMobNumber").trim()
                                order.distanceKm = ordersJSONArray.getJSONObject(i).getString("DistanceKm").trim()

                                (orderList as ArrayList<Order>).add(order)
                            }
                        }

                    } else {
                        noRowMsg = response.getString("message").trim()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateTripNotifiedCustStatusBackground internal constructor(internal var tripId: String) : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgTripDMId"] = tripId
            params["ArgTripDIsNotifiedCust"] = "true"

            var BASE_URL = Constants.BASE_URL_EN + "UpdateTripNotifiedCustStatus"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "UpdateTripNotifiedCustStatus"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        //Log.e("success");
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> RouteFragment()
                1 -> DetailsFragment()
                else -> RepliesFragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
