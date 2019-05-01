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
import kotlinx.android.synthetic.main.activity_my_orders.*
import kotlinx.android.synthetic.main.content_all_orders_list.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MyOrdersActivity : AppCompatActivity() {

    internal var ordersArrayList: MutableList<Order>? = null

    //internal lateinit var orderSelected: Order

    internal lateinit var homeLayoutManager: LinearLayoutManager

    internal lateinit var mHomeAdapter: RecyclerView.Adapter<*>

    //internal var selectedId: String? = null

    internal lateinit var sharedPreferences: SharedPreferences

    internal var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.MyOrders))

        //show_button.setOnClickListener {
        //    val intent = Intent(this@MyOrdersActivity, OrderDetailActivity::class.java)
        //    intent.putExtra("order", orderSelected)
        //    startActivity(intent)
        //}
        //
        //if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
        //    time_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
        //    subject_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
        //    notes_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
        //    from_addr_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
        //    to_addr_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
        //    veh_name_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
        //} else {
        //    time_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
        //    subject_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
        //    notes_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
        //    from_addr_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
        //    to_addr_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
        //    veh_name_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
        //}

        orderListRecyclerView.setHasFixedSize(true)
        homeLayoutManager = LinearLayoutManager(this@MyOrdersActivity)
        orderListRecyclerView.layoutManager = homeLayoutManager
        mHomeAdapter = MyOrdersAdapter()
        orderListRecyclerView.adapter = mHomeAdapter
    }

    override fun onResume() {
        super.onResume()

        if (ConnectionDetector.isConnected(applicationContext)) {
            TripMasterListBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripMasterListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (!isLoaded) { UtilityFunctions.showProgressDialog (this@MyOrdersActivity) }
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgTripMID"] = "0"
            params["ArgTripMCustId"] = sharedPreferences.getString(Constants.PREFS_CUST_ID, "0")
            params["ArgTripMStatus"] = "0"

            var BASE_URL = Constants.BASE_URL_EN + "TripMasterList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripMasterList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (!isLoaded) {
                isLoaded = true
                UtilityFunctions.dismissProgressDialog()
            }

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val ordersJSONArray = response.getJSONArray("data")
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
                                order.vehicleModel = ordersJSONArray.getJSONObject(i).getString("VsName").trim()
                                order.scheduleDate = ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim()
                                order.scheduleTime = ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim()
                                order.userName = ordersJSONArray.getJSONObject(i).getString("UsrName").trim()
                                order.userMobile = ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim()
                                order.tripFilter = ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim()
                                order.tripStatus = ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim()
                                order.tripSubject = ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim()
                                order.tripNotes = ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim()

                                ordersArrayList!!.add(order)

                                //if (selectedId != null && selectedId == order.tripId) {
                                //    orderSelected = order
                                //    show_button.isEnabled = true
                                //    order_no_value_text_view.text = orderSelected.tripNo
                                //    veh_name_value_text_view.text = orderSelected.vehicleModel
                                //    from_addr_value_text_view.text = orderSelected.tripFromAddress
                                //    to_addr_value_text_view.text = orderSelected.tripToAddress
                                //    date_value_text_view.text = orderSelected.scheduleDate
                                //    time_value_text_view.text = orderSelected.scheduleTime
                                //    subject_value_text_view.text = orderSelected.tripSubject
                                //    notes_value_text_view.text = orderSelected.tripNotes
                                //    status_value_text_view.text = orderSelected.tripFilter
                                //}
                            }
                            mHomeAdapter.notifyDataSetChanged()

                        } else {
                            UtilityFunctions.showAlertOnActivity(this@MyOrdersActivity,
                                    response.getString("message"), resources.getString(R.string.Ok),
                                    "", false, false, {}, {})
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
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

                val intent = Intent(this@MyOrdersActivity, ShipmentDetailsActivity::class.java)
                intent.putExtra("order", order)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return ordersArrayList?.size?:0
        }

    }
}
