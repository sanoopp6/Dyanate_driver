package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.models.Ride
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_confirm_details.*
import kotlinx.android.synthetic.main.content_confirm_details.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ConfirmDetailsActivity : AppCompatActivity() {

    private var tripID: String? = null

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.ConfirmDetail))

        try {
            Ride.instance
        } catch (e: Exception) {
            Ride.instance = Ride()
        }

        shipmentTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Shipment))
        fromNameTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Name))
        fromMobTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Mobile))
        engDateTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Date))
        arDateTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Date))
        timeTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Time))
        toNameTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Name))
        toMobTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Mobile))

        val flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this@ConfirmDetailsActivity, R.color.lightBlueColor))
        val builder = SpannableStringBuilder()

        val spannableString = SpannableString(resources.getString(R.string.Subject) + " : ")
        spannableString.setSpan(colorSpan, 0, spannableString.length, flag)
        builder.append(spannableString)
        builder.append(Ride.instance.subject)
        subjectTextView.text = builder

        shipmentTextView.text = Ride.instance.shipment

        //builder = SpannableStringBuilder()
        //spannableString = SpannableString(resources.getString(R.string.Size) + " : ")
        //spannableString.setSpan(colorSpan, 0, spannableString.length, flag)
        //builder.append(spannableString)
        //builder.append(Ride.instance.vehicleSizeName)
        //vehicleTextView.text = builder

        fromNameTextView.text = Ride.instance.fromName
        fromMobTextView.text = Ride.instance.fromMobile.trimStart{ it <= '+'}
        engDateTextView.text = Ride.instance.date
        arDateTextView.text = Ride.instance.hijriDate
        timeTextView.text = Ride.instance.time
        toNameTextView.text = Ride.instance.toName
        toMobTextView.text = Ride.instance.toMobile.trimStart{ it <= '+'}

        confirmTripButton.setOnClickListener {
            UtilityFunctions.showAlertOnActivity(this@ConfirmDetailsActivity,
                    resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                    resources.getString(R.string.No), true, false,
                    {
                        UtilityFunctions.showAlertOnActivity(this@ConfirmDetailsActivity,
                                resources.getString(R.string.TripWillbeAssignedToYou), resources.getString(R.string.Yes),
                                resources.getString(R.string.No), true, false,
                                {
                                    if (ConnectionDetector.isConnected(this@ConfirmDetailsActivity)) {
                                        AddTripMasterBackground().execute()
                                    } else {
                                        ConnectionDetector.errorSnackbar(coordinator_layout)
                                    }
                                }, {})
                    }, {})
        }

        //btn_edit_details.setOnClickListener { startActivity(Intent(this@ConfirmDetailsActivity, ShipmentDetActivity::class.java)) }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AddTripMasterBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@ConfirmDetailsActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgTripMCustId"] = sharedPreferences.getString(Constants.PREFS_CUST_ID, "")
            params["ArgTripMScheduleDate"] = Ride.instance.date
            params["ArgTripMScheduleTime"] = Ride.instance.time
            params["ArgTripMFromLat"] = Ride.instance.pickUpLatitude!!
            params["ArgTripMFromLng"] = Ride.instance.pickUpLongitude!!
            params["ArgTripMFromAddress"] = Ride.instance.pickUpLocation!!
            params["ArgTripMFromIsSelf"] = "false"
            params["ArgTripMFromName"] = Ride.instance.fromName
            params["ArgTripMFromMob"] = Ride.instance.fromMobile
            params["ArgTripMToLat"] = Ride.instance.dropOffLatitude!!
            params["ArgTripMToLng"] = Ride.instance.dropOffLongitude!!
            params["ArgTripMToAddress"] = Ride.instance.dropOffLocation!!
            params["ArgTripMToIsSelf"] = "false"
            params["ArgTripMToName"] = Ride.instance.toName
            params["ArgTripMToMob"] = Ride.instance.toMobile
            params["ArgTripMSubject"] = Ride.instance.subject
            params["ArgTripMNotes"] = Ride.instance.shipment
            params["ArgTripMVsId"] = Ride.instance.vehicleSizeId!!
            params["ArgTripMCustLat"] = "0"
            params["ArgTripMCustLng"] = "0"
            params["ArgTripMNoOfDrivers"] = "0"
            params["ArgTripMDistanceRadiusKm"] = "0"
            params["ArgTripMDistanceString"] = Ride.instance.distanceStr!!

            var BASE_URL = Constants.BASE_URL_EN + "AddTripMaster"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "AddTripMaster"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        tripID = response.getString("data").toInt().toString()
                        AddTripDetailsBackground().execute()

                    } else {
                        UtilityFunctions.dismissProgressDialog()
                        UtilityFunctions.showAlertOnActivity(this@ConfirmDetailsActivity,
                                response.getString("message"), resources.getString(R.string.Ok),
                                "", false, false, {}, {})
                    }

                } catch (e: JSONException) {
                    UtilityFunctions.dismissProgressDialog()
                    e.printStackTrace()
                }

            } else {
                UtilityFunctions.dismissProgressDialog()
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AddTripDetailsBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgTripDMID"] = tripID!!
            params["ArgTripDDmId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")
            params["ArgTripDRate"] = "0"
            params["ArgTripDIsNegotiable"] = "false"

            var BASE_URL = Constants.BASE_URL_EN + "AddTripDetails"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "AddTripDetails"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        TripDIsNotifiedListBackground().execute()

                    } else {
                        UtilityFunctions.dismissProgressDialog()
                        UtilityFunctions.showAlertOnActivity(this@ConfirmDetailsActivity,
                                response.getString("message"), resources.getString(R.string.Ok),
                                "", false, false, {}, {})
                    }

                } catch (e: JSONException) {
                    UtilityFunctions.dismissProgressDialog()
                    e.printStackTrace()
                }

            } else {
                UtilityFunctions.dismissProgressDialog()
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDIsNotifiedListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgTripDDmId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "0")

            var BASE_URL = Constants.BASE_URL_EN + "TripDIsNotifiedList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripDIsNotifiedList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val ordersJSONArray = response.getJSONArray("data")

                        if (ordersJSONArray.length() > 0) {
                            for (i in 0 until ordersJSONArray.length()) {
                                val order = Order()

                                order.tripId = ordersJSONArray.getJSONObject(i).getString("TripMID").trim()
                                order.tripNo = ordersJSONArray.getJSONObject(i).getString("TripMNo").trim()
                                order.tripFromAddress = ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim()
                                order.tripFromLat = ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim()
                                order.tripFromLng = ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim()
                                try {
                                    order.tripFromSelf = java.lang.Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf"))
                                } catch (e: Exception) {
                                    order.tripFromSelf = false
                                }

                                order.tripFromName = ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim()
                                order.tripFromMob = ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim()
                                order.tripToAddress = ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim()
                                order.tripToLat = ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim()
                                order.tripToLng = ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim()
                                try {
                                    order.tripToSelf = java.lang.Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf"))
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
                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim()

                                val intent = Intent(this@ConfirmDetailsActivity, ReplyActivity::class.java)
                                intent.putExtra("alarm", true)
                                intent.putExtra("order", order)
                                intent.putExtra("fromTripAdd", true)
                                startActivity(intent)

                                Ride.instance = Ride()
                            }
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

}
