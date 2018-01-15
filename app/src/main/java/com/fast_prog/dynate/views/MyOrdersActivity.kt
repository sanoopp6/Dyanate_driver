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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_my_orders.*
import kotlinx.android.synthetic.main.content_my_orders.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MyOrdersActivity : AppCompatActivity() {

    internal var ordersArrayList: MutableList<Order>? = null

    internal lateinit var orderSelected: Order

    internal lateinit var homeLayoutManager: LinearLayoutManager

    internal lateinit var mHomeAdapter: RecyclerView.Adapter<*>

    internal var selectedId: String? = null

    internal lateinit var sharedPreferences: SharedPreferences

    internal var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.app_name)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@MyOrdersActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        show_button.setOnClickListener {
            val intent = Intent(this@MyOrdersActivity, OrderDetailActivity::class.java)
            intent.putExtra("order", orderSelected)
            startActivity(intent)
        }

        if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
            time_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
            subject_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
            notes_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
            from_addr_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
            to_addr_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
            veh_name_value_text_view.textDirection = View.TEXT_DIRECTION_RTL
        } else {
            time_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
            subject_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
            notes_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
            from_addr_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
            to_addr_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
            veh_name_value_text_view.textDirection = View.TEXT_DIRECTION_LTR
        }

        recycler_my_orders.setHasFixedSize(true)
        homeLayoutManager = LinearLayoutManager(this@MyOrdersActivity)
        recycler_my_orders.layoutManager = homeLayoutManager
        mHomeAdapter = MyOrdersAdapter()
        recycler_my_orders.adapter = mHomeAdapter
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

            params.put("ArgTripMID", "0")
            params.put("ArgTripMCustId", sharedPreferences.getString(Constants.PREFS_CUST_ID, "0"))
            params.put("ArgTripMStatus", "0")

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
                                order.vehicleModel = ordersJSONArray.getJSONObject(i).getString("VsName").trim { it <= ' ' }
                                order.scheduleDate = ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim { it <= ' ' }
                                order.scheduleTime = ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim { it <= ' ' }
                                order.userName = ordersJSONArray.getJSONObject(i).getString("UsrName").trim { it <= ' ' }
                                order.userMobile = ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim { it <= ' ' }
                                order.tripFilter = ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim { it <= ' ' }
                                order.tripStatus = ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim { it <= ' ' }
                                order.tripSubject = ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim { it <= ' ' }
                                order.tripNotes = ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim { it <= ' ' }

                                ordersArrayList!!.add(order)

                                if (selectedId != null && selectedId == order.tripId) {
                                    orderSelected = order

                                    show_button.isEnabled = true

                                    order_no_value_text_view.text = orderSelected.tripNo
                                    veh_name_value_text_view.text = orderSelected.vehicleModel
                                    from_addr_value_text_view.text = orderSelected.tripFromAddress
                                    to_addr_value_text_view.text = orderSelected.tripToAddress
                                    date_value_text_view.text = orderSelected.scheduleDate
                                    time_value_text_view.text = orderSelected.scheduleTime
                                    subject_value_text_view.text = orderSelected.tripSubject
                                    notes_value_text_view.text = orderSelected.tripNotes
                                    status_value_text_view.text = orderSelected.tripFilter
                                }
                            }
                            mHomeAdapter.notifyDataSetChanged()

                        } else {
                            UtilityFunctions.showAlertOnActivity(this@MyOrdersActivity,
                                    response.getString("message"), resources.getString(R.string.Ok).toString(),
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
            var orderNo: TextView = v.findViewById(R.id.id_text_view) as TextView
            var address: TextView = v.findViewById(R.id.id_text_from_to) as TextView
            var view1: TextView = v.findViewById(R.id.show_details) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_all_my_orders, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //View view = holder.itemView;
            holder.setIsRecyclable(false)

            holder.orderNo.text = ordersArrayList!![position].tripNo
            holder.address.text = String.format("%s - %s", ordersArrayList!![position].tripFromAddress, ordersArrayList!![position].tripToAddress)

            holder.view1.setOnClickListener {
                orderSelected = ordersArrayList!![position]
                selectedId = orderSelected.tripId

                show_button.isEnabled = true

                order_no_value_text_view.text = ordersArrayList!![position].tripNo
                veh_name_value_text_view.text = orderSelected.vehicleModel
                from_addr_value_text_view.text = orderSelected.tripFromAddress
                to_addr_value_text_view.text = orderSelected.tripToAddress
                date_value_text_view.text = orderSelected.scheduleDate
                time_value_text_view.text = orderSelected.scheduleTime
                subject_value_text_view.text = orderSelected.tripSubject
                notes_value_text_view.text = orderSelected.tripNotes
                status_value_text_view.text = orderSelected.tripFilter
            }
        }

        override fun getItemCount(): Int {
            return if (ordersArrayList != null) ordersArrayList!!.size else 0
        }
    }
}
