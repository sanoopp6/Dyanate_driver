package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_driver_replies.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DriverRepliesActivity : AppCompatActivity() {

    internal var order: Order? = null

    internal var rate: String = ""
    internal var negotiable: String? = null

    internal var orderList: MutableList<Order>? = null

    internal lateinit var showDetailLayoutManager: LinearLayoutManager

    internal lateinit var mShowDetailAdapter: RecyclerView.Adapter<*>

    internal lateinit var sharedPreferences: SharedPreferences

    internal var selectedIndex : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_replies)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.DriverReplies)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@DriverRepliesActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        order = intent.getSerializableExtra("item") as Order?
        if (order == null) finish()

        recycler_show_details.setHasFixedSize(true)
        showDetailLayoutManager = LinearLayoutManager(this@DriverRepliesActivity)
        recycler_show_details.layoutManager = showDetailLayoutManager
        mShowDetailAdapter = ShowDetailAdapter()
        recycler_show_details.adapter = mShowDetailAdapter

        if (ConnectionDetector.isConnected(applicationContext)) {
            TripDetailsMasterListBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsMasterListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@DriverRepliesActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgTripMCustId", "0")
            params.put("ArgTripDDmId", "0")
            params.put("ArgTripMID", order!!.tripId!!)
            params.put("ArgTripDID", "0")
            params.put("ArgTripMStatus", "0")
            params.put("ArgTripDStatus", "0")
            params.put("ArgExcludeCustId", "0")

            var BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val ordersJSONArray = response.getJSONArray("data")
                        orderList = ArrayList()

                        for (i in 0 until ordersJSONArray.length()) {
                            if (!ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim().equals("7", true)) {
                                val order = Order()

                                order.tripId = ordersJSONArray.getJSONObject(i).getString("TripMID").trim { it <= ' ' }
                                order.tripNo = ordersJSONArray.getJSONObject(i).getString("TripMNo").trim { it <= ' ' }
                                order.tripFromAddress = ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim { it <= ' ' }
                                order.tripFromLat = ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim { it <= ' ' }
                                order.tripFromLng = ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim { it <= ' ' }
                                try {
                                    order.tripFromSelf = ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf").trim().toBoolean()
                                } catch (e: Exception) {
                                    order.tripFromSelf = false
                                }

                                order.tripFromName = ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim { it <= ' ' }
                                order.tripFromMob = ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim { it <= ' ' }
                                order.tripToAddress = ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim { it <= ' ' }
                                order.tripToLat = ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim { it <= ' ' }
                                order.tripToLng = ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim { it <= ' ' }
                                try {
                                    order.tripToSelf = ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf").trim().toBoolean()
                                } catch (e: Exception) {
                                    order.tripToSelf = false
                                }

                                order.tripToName = ordersJSONArray.getJSONObject(i).getString("TripMToName").trim { it <= ' ' }
                                order.tripToMob = ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim { it <= ' ' }
                                order.vehicleModel = ordersJSONArray.getJSONObject(i).getString("VMName").trim { it <= ' ' }
                                order.vehicleType = ordersJSONArray.getJSONObject(i).getString("VmoName").trim { it <= ' ' }
                                order.scheduleDate = ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim { it <= ' ' }
                                order.scheduleTime = ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim { it <= ' ' }
                                order.userName = ordersJSONArray.getJSONObject(i).getString("UsrName").trim { it <= ' ' }
                                order.userMobile = ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim { it <= ' ' }
                                order.tripFilter = ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim { it <= ' ' }
                                order.tripStatus = ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim { it <= ' ' }
                                order.tripSubject = ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim { it <= ' ' }
                                order.tripNotes = ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim { it <= ' ' }
                                order.vehicleImage = ordersJSONArray.getJSONObject(i).getString("VmoURL").trim { it <= ' ' }
                                order.tripDId = ordersJSONArray.getJSONObject(i).getString("TripDID").trim { it <= ' ' }
                                order.tripDNo = ordersJSONArray.getJSONObject(i).getString("TripDNo").trim { it <= ' ' }
                                order.tripDStatus = ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim { it <= ' ' }
                                order.tripDDmId = ordersJSONArray.getJSONObject(i).getString("TripDDmId").trim { it <= ' ' }
                                order.tripDIsNegotiable = ordersJSONArray.getJSONObject(i).getString("TripDIsNegotiable").trim { it <= ' ' }
                                order.tripDRate = ordersJSONArray.getJSONObject(i).getString("TripDRate").trim { it <= ' ' }
                                order.tripDDateTime = ordersJSONArray.getJSONObject(i).getString("TripDDateTime").trim { it <= ' ' }
                                order.tripDFilterName = ordersJSONArray.getJSONObject(i).getString("TripDFilterName").trim { it <= ' ' }
                                order.dmName = ordersJSONArray.getJSONObject(i).getString("DmName").trim { it <= ' ' }
                                order.dmMobNumber = ordersJSONArray.getJSONObject(i).getString("DmMobNumber").trim { it <= ' ' }
                                order.distanceKm = ordersJSONArray.getJSONObject(i).getString("DistanceKm").trim { it <= ' ' }
                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim { it <= ' ' }

                                orderList!!.add(order)
                            }
                        }

                        mShowDetailAdapter.notifyDataSetChanged()

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }

    internal inner class ShowDetailAdapter : RecyclerView.Adapter<ShowDetailAdapter.ViewHolder>() {

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var txtOrderNo: TextView = v.findViewById(R.id.txt_order_no) as TextView
            var txtDate: TextView = v.findViewById(R.id.txt_date) as TextView
            var textDistance: TextView = v.findViewById(R.id.text_distance) as TextView
            var edittextRate: EditText = v.findViewById(R.id.edittext_rate) as EditText
            var textNegotiable: TextView = v.findViewById(R.id.text_negotiable) as TextView
            var textStatus: TextView = v.findViewById(R.id.text_status) as TextView
            var textDriver: TextView = v.findViewById(R.id.text_driver) as TextView
            var textDriverNo: TextView = v.findViewById(R.id.text_driver_no) as TextView
            var btnApprove: Button = v.findViewById(R.id.btn_approve) as Button
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowDetailAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_driver_replies, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //View view = holder.itemView;
            holder.setIsRecyclable(false)

            holder.txtOrderNo.text = orderList!![position].tripDNo
            holder.txtDate.text = orderList!![position].tripDDateTime
            holder.textDistance.text = orderList!![position].distanceKm
            holder.edittextRate.setText(orderList!![position].tripDRate)
            holder.textNegotiable.text = orderList!![position].tripDIsNegotiable
            holder.textStatus.text = orderList!![position].tripDFilterName
            holder.textDriver.text = orderList!![position].dmName
            holder.textDriverNo.text = orderList!![position].dmMobNumber

            val tripDStatus = orderList!![position].tripDStatus

            if (orderList!![position].tripDIsNegotiable!!.equals("true", true)) {
                holder.textNegotiable.text = resources.getString(R.string.Yes)
                holder.edittextRate.visibility = View.GONE
                if (tripDStatus!!.matches("1".toRegex())) { holder.edittextRate.isEnabled = true }

            } else {
                holder.textNegotiable.text = resources.getString(R.string.No)
                holder.edittextRate.isEnabled = false
            }

            if (!tripDStatus!!.equals("1", true)) {
                holder.btnApprove.visibility = View.GONE
            }

            holder.btnApprove.setOnClickListener {
                UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
                        resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                        resources.getString(R.string.No).toString(), true, false,
                        {
                            selectedIndex = position
                            rate = holder.edittextRate.text.toString().trim()
                            negotiable = orderList!![selectedIndex].tripDIsNegotiable
                            TripDetailsBackground(orderList!![selectedIndex].tripDId!!).execute()
                        }, {})
            }
        }

        override fun getItemCount(): Int {
            return if (orderList != null) orderList!!.size else 0
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsBackground internal constructor(internal var tripDId: String) : AsyncTask<Void, Void, JSONObject>() {
        internal var jsonStatus: JSONObject? = null
        internal var jsonDetails: JSONObject? = null

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@DriverRepliesActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParserStatus = JsonParser()
            val jsonParserDetails = JsonParser()

            var params = HashMap<String, String>()

            params.put("ArgTripDID", tripDId)
            params.put("ArgTripDRate", rate)
            params.put("ArgTripDIsNegotiable", negotiable!!)

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                jsonDetails = jsonParserDetails.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsUpdate", "POST", params)
            } else {
                jsonDetails = jsonParserDetails.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsUpdate", "POST", params)
            }

            params = HashMap()

            params.put("ArgTripDID", tripDId)
            params.put("ArgTripDStatus", "2")

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                jsonStatus = jsonParserStatus.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsStatusUpdate", "POST", params)
            } else {
                jsonStatus = jsonParserStatus.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsStatusUpdate", "POST", params)
            }

            return jsonStatus
        }

        override fun onPostExecute(jsonObject: JSONObject) {
            UtilityFunctions.dismissProgressDialog()

            if (jsonDetails != null && jsonStatus != null) {
                try {
                    if (jsonDetails!!.getBoolean("status") && jsonStatus!!.getBoolean("status")) {
                        UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
                                resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                                resources.getString(R.string.No).toString(), true, false,
                                {
                                    orderList!![selectedIndex].tripDStatus =  "2"
                                    orderList!![selectedIndex].tripDIsNegotiable = rate
                                    orderList!![selectedIndex].tripDRate = negotiable

                                    mShowDetailAdapter.notifyDataSetChanged()
                                }, {})

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
                                jsonStatus!!.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

}
