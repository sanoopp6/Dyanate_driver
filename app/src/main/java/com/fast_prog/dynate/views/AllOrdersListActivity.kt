package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_all_orders_list.*
import kotlinx.android.synthetic.main.content_all_orders_list.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AllOrdersListActivity : AppCompatActivity() {

    internal var ordersArrayList: MutableList<Order>? = null

    internal lateinit var homeLayoutManager: LinearLayoutManager

    internal lateinit var mHomeAdapter: RecyclerView.Adapter<*>

    internal var modeExtra: String = ""
    internal var modeTitle: String = ""

    internal lateinit var sharedPreferences: SharedPreferences

    internal var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        modeExtra = intent.getStringExtra("mode")
        modeTitle = intent.getStringExtra("modeStr")

        customTitle(modeTitle.toUpperCase())

        //all_orders_button.setOnClickListener {
        //    top_layout.layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
        //    all_orders_button.visibility = View.GONE
        //}
        //enter_port_priority_text_view.text = modeTitle.toUpperCase()

        orderListRecyclerView.setHasFixedSize(true)
        homeLayoutManager = LinearLayoutManager(this@AllOrdersListActivity)
        orderListRecyclerView.layoutManager = homeLayoutManager
        mHomeAdapter = MyOrdersAdapter()
        orderListRecyclerView.adapter = mHomeAdapter
    }

    override fun onResume() {
        super.onResume()

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
            if (!isLoaded) { UtilityFunctions.showProgressDialog (this@AllOrdersListActivity) }
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgTripMCustId"] = "0"
            params["ArgExcludeCustId"] = "0"
            params["ArgTripDDmId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "0")
            params["ArgTripMID"] = "0"
            params["ArgTripDID"] = "0"
            params["ArgTripMStatus"] = "0"
            params["ArgTripDStatus"] = modeExtra

            var BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            if (!isLoaded) {
                isLoaded = true
                UtilityFunctions.dismissProgressDialog()
            }

            if (jsonObject != null) {
                try {
                    if (jsonObject.getBoolean("status")) {
                        val ordersJSONArray = jsonObject.getJSONArray("data")
                        ordersArrayList = ArrayList()

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

                                ordersArrayList!!.add(order)
                            }
                        }
                    } else {
                        ordersArrayList = ArrayList()
                    }

                    mHomeAdapter.notifyDataSetChanged()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            //if (ordersArrayList == null || ordersArrayList!!.size <= 5) {
            //    all_orders_button.visibility = View.GONE
            //}
        }
    }

    internal inner class MyOrdersAdapter : RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>() {

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var priceTextView: TextView = v.findViewById(R.id.priceTextView) as TextView
            var dateTextView: TextView = v.findViewById(R.id.dateTextView) as TextView
            var detailsButton: Button = v.findViewById(R.id.detailsButton) as Button
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_all_my_orders, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setIsRecyclable(false)

            holder.priceTextView.text = "10"
            holder.dateTextView.text = ordersArrayList!![position].scheduleDate

            holder.detailsButton.setOnClickListener {
                val order = ordersArrayList!![position]

                val intent = Intent(this@AllOrdersListActivity, ReplyActivity::class.java)
                intent.putExtra("order", order)
                intent.putExtra("alarm", false)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return ordersArrayList?.size?:0
        }

    }
}
