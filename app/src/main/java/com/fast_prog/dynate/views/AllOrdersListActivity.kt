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
import android.widget.LinearLayout
import android.widget.TextView
import com.fast_prog.dynate.R
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

    internal var glassExtra: String = ""
    internal var modeExtra: String = ""
    internal var modeTitle: String = ""

    internal lateinit var sharedPreferences: SharedPreferences

    internal var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        glassExtra = intent.getStringExtra("glass")
        modeExtra = intent.getStringExtra("mode")
        modeTitle = intent.getStringExtra("modeStr")

        val title: String
        if (glassExtra.equals("true", true)) {
            title = resources.getString(R.string.TripsFromGlass)
        } else {
            title = resources.getString(R.string.TripsFromOthers)
        }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = title
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@AllOrdersListActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        all_orders_button.setOnClickListener {
            top_layout.layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            all_orders_button.visibility = View.GONE
        }

        enter_port_priority_text_view.text = modeTitle.toUpperCase()

        recycler_home.setHasFixedSize(true)
        homeLayoutManager = LinearLayoutManager(this@AllOrdersListActivity)
        recycler_home.layoutManager = homeLayoutManager
        mHomeAdapter = MyOrdersAdapter()
        recycler_home.adapter = mHomeAdapter
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

            if (glassExtra.equals("true", true)) {
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
            params.put("ArgTripDStatus", modeExtra)

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
                    // Parsing json object response
                    // response will be a json object
                    if (jsonObject.getBoolean("status")) {
                        val ordersJSONArray = jsonObject.getJSONArray("data")
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
                                    order.tripFromSelf = java.lang.Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf"))
                                } catch (e: Exception) {
                                    order.tripFromSelf = false
                                }

                                order.tripFromName = ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim { it <= ' ' }
                                order.tripFromMob = ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim { it <= ' ' }
                                order.tripToAddress = ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim { it <= ' ' }
                                order.tripToLat = ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim { it <= ' ' }
                                order.tripToLng = ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim { it <= ' ' }
                                try {
                                    order.tripToSelf = java.lang.Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf"))
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
                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim { it <= ' ' }

                                ordersArrayList!!.add(order)
                            }
                        }
                        mHomeAdapter.notifyDataSetChanged()

                    } else {
                        ordersArrayList = ArrayList()
                        mHomeAdapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            if (ordersArrayList == null || ordersArrayList!!.size <= 5) {
                all_orders_button.visibility = View.GONE
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
                val order = ordersArrayList!![position]

                val intent = Intent(this@AllOrdersListActivity, ReplyActivity::class.java)
                intent.putExtra("order", order)
                intent.putExtra("alarm", false)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return if (ordersArrayList != null) ordersArrayList!!.size else 0
        }

    }
}
