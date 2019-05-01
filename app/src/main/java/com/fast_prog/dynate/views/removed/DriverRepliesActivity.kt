package com.fast_prog.dynate.views.removed

//import kotlinx.android.synthetic.main.activity_driver_replies.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fast_prog.dynate.R

class DriverRepliesActivity : AppCompatActivity() {

//    internal var rate: Double = 0.0
//
//    internal var orderList: MutableList<Order>? = null
//
//    internal var selectedIndex : Int? = null
//
//    internal var tripID = 0
//
//    private lateinit var showDetailLayoutManager: LinearLayoutManager
//
//    internal lateinit var mShowDetailAdapter: RecyclerView.Adapter<*>
//
//    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_driver_replies)
//
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
//
//        toolbar.setNavigationOnClickListener { finish() }
//
//        customTitle(resources.getString(R.string.DriverReplies))
//
//        tripID = intent.getIntExtra("tripID", 0)
//
//        recycler_show_details.setHasFixedSize(true)
//        showDetailLayoutManager = LinearLayoutManager(this@DriverRepliesActivity)
//        recycler_show_details.layoutManager = showDetailLayoutManager
//        mShowDetailAdapter = ShowDetailAdapter()
//        recycler_show_details.adapter = mShowDetailAdapter
//
//        if (tripID > 0) {
//            if (ConnectionDetector.isConnected(applicationContext)) {
//                TripDetailsMasterListBackground(true).execute()
//            } else {
//                ConnectionDetector.errorSnackbar(coordinator_layout)
//            }
//        } else {
//            finish()
//        }
//
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private inner class TripDetailsMasterListBackground internal constructor(internal var showDialog: Boolean) : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            if (showDialog) { UtilityFunctions.showProgressDialog (this@DriverRepliesActivity) }
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["ArgTripMCustId"] = "0"
//            params["ArgTripDDmId"] = "0"
//            params["ArgTripMID"] = tripID.toString()
//            params["ArgTripDID"] = "0"
//            params["ArgTripMStatus"] = "0"
//            params["ArgTripDStatus"] = "0"
//            params["ArgExcludeCustId"] = "0"
//
//            var BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterList"
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
//                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterList"
//            }
//
//            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
//        }
//
//        override fun onPostExecute(response: JSONObject?) {
//            if (showDialog) { UtilityFunctions.dismissProgressDialog() }
//
//            if (response != null) {
//                try {
//                    if (response.getBoolean("status")) {
//                        val ordersJSONArray = response.getJSONArray("data")
//                        orderList = ArrayList()
//
//                        for (i in 0 until ordersJSONArray.length()) {
//                            if (!ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim().equals("7", true)) {
//                                val order = Order()
//
//                                order.tripId = ordersJSONArray.getJSONObject(i).getString("TripMID").trim()
//                                order.tripNo = ordersJSONArray.getJSONObject(i).getString("TripMNo").trim()
//                                order.tripFromAddress = ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim()
//                                order.tripFromLat = ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim()
//                                order.tripFromLng = ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim()
//                                try {
//                                    order.tripFromSelf = ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf").trim().toBoolean()
//                                } catch (e: Exception) {
//                                    order.tripFromSelf = false
//                                }
//
//                                order.tripFromName = ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim()
//                                order.tripFromMob = ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim()
//                                order.tripToAddress = ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim()
//                                order.tripToLat = ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim()
//                                order.tripToLng = ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim()
//                                try {
//                                    order.tripToSelf = ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf").trim().toBoolean()
//                                } catch (e: Exception) {
//                                    order.tripToSelf = false
//                                }
//
//                                order.tripToName = ordersJSONArray.getJSONObject(i).getString("TripMToName").trim()
//                                order.tripToMob = ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim()
//                                order.vehicleModel = ordersJSONArray.getJSONObject(i).getString("VMName").trim()
//                                order.vehicleType = ordersJSONArray.getJSONObject(i).getString("VmoName").trim()
//                                order.scheduleDate = ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim()
//                                order.scheduleTime = ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim()
//                                order.userName = ordersJSONArray.getJSONObject(i).getString("UsrName").trim()
//                                order.userMobile = ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim()
//                                order.tripFilter = ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim()
//                                order.tripStatus = ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim()
//                                order.tripSubject = ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim()
//                                order.tripNotes = ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim()
//                                order.vehicleImage = ordersJSONArray.getJSONObject(i).getString("VmoURL").trim()
//                                order.tripDId = ordersJSONArray.getJSONObject(i).getString("TripDID").trim()
//                                order.tripDNo = ordersJSONArray.getJSONObject(i).getString("TripDNo").trim()
//                                order.tripDStatus = ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim()
//                                order.tripDDmId = ordersJSONArray.getJSONObject(i).getString("TripDDmId").trim()
//                                order.tripDIsNegotiable = ordersJSONArray.getJSONObject(i).getString("TripDIsNegotiable").trim()
//                                order.tripDRate = ordersJSONArray.getJSONObject(i).getString("TripDRate").trim()
//                                order.tripDDateTime = ordersJSONArray.getJSONObject(i).getString("TripDDateTime").trim()
//                                order.tripDFilterName = ordersJSONArray.getJSONObject(i).getString("TripDFilterName").trim()
//                                order.dmName = ordersJSONArray.getJSONObject(i).getString("DmName").trim()
//                                order.dmMobNumber = ordersJSONArray.getJSONObject(i).getString("DmMobNumber").trim()
//                                order.distanceKm = ordersJSONArray.getJSONObject(i).getString("DistanceKm").trim()
//                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim()
//
//                                (orderList as ArrayList<Order>).add(order)
//                            }
//                        }
//
//                        mShowDetailAdapter.notifyDataSetChanged()
//
//                    } else {
//                        UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
//                                response.getString("message"), resources.getString(R.string.Ok).toString(),
//                                "", false, false, {}, {})
//                    }
//                } catch (e: JSONException) { e.printStackTrace() }
//            }
//        }
//    }
//
//    internal inner class ShowDetailAdapter : RecyclerView.Adapter<ShowDetailAdapter.ViewHolder>() {
//
//        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//            var txtOrderNo: TextView = v.findViewById(R.id.txt_order_no) as TextView
//            var txtDate: TextView = v.findViewById(R.id.txt_date) as TextView
//            var textDistance: TextView = v.findViewById(R.id.text_distance) as TextView
//            var edittextRate: EditText = v.findViewById(R.id.edittext_rate) as EditText
//            var textNegotiable: TextView = v.findViewById(R.id.text_negotiable) as TextView
//            var textStatus: TextView = v.findViewById(R.id.text_status) as TextView
//            var textDriver: TextView = v.findViewById(R.id.text_driver) as TextView
//            var textDriverNo: TextView = v.findViewById(R.id.text_driver_no) as TextView
//            var btnApprove: Button = v.findViewById(R.id.btn_approve) as Button
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_driver_replies, parent, false)
//            return ViewHolder(v)
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            //View view = holder.itemView;
//            holder.setIsRecyclable(false)
//
//            holder.txtOrderNo.text = orderList!![position].tripDNo
//            holder.txtDate.text = orderList!![position].tripDDateTime
//            holder.textDistance.text = orderList!![position].distanceKm
//            holder.edittextRate.setText(orderList!![position].tripDRate)
//            holder.textNegotiable.text = orderList!![position].tripDIsNegotiable
//            holder.textStatus.text = orderList!![position].tripDFilterName
//            holder.textDriver.text = orderList!![position].dmName
//            holder.textDriverNo.text = orderList!![position].dmMobNumber
//
//            val tripDStatus = orderList!![position].tripDStatus
//
//            if (orderList!![position].tripDIsNegotiable!!.equals("true", true)) {
//                holder.textNegotiable.text = resources.getString(R.string.Yes)
//                if (tripDStatus!!.matches("1".toRegex())) {
//                    holder.edittextRate.isEnabled = true
//                    holder.edittextRate.textAlignment = View.TEXT_ALIGNMENT_CENTER
//                    holder.edittextRate.background = ContextCompat.getDrawable(applicationContext, R.drawable.layout_border_black_rounded_white_squre)
//                }
//            } else { holder.textNegotiable.text = resources.getString(R.string.No) }
//
//            if (!tripDStatus!!.equals("1", true)) {
//                holder.btnApprove.visibility = View.GONE
//            }
//
//            holder.btnApprove.setOnClickListener {
//                UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
//                        resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
//                        resources.getString(R.string.No).toString(), true, false,
//                        {
//                            selectedIndex = position
//                            rate = holder.edittextRate.text.toString().trim().toDouble()
//                            TripDetailsBackground().execute()
//                        }, {})
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return if (orderList != null) orderList!!.size else 0
//        }
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private inner class TripDetailsBackground : AsyncTask<Void, Void, JSONObject>() {
//        internal var jsonStatus: JSONObject? = null
//        internal var jsonDetails: JSONObject? = null
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            UtilityFunctions.showProgressDialog(this@DriverRepliesActivity)
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParserStatus = JsonParser()
//            val jsonParserDetails = JsonParser()
//
//            var params = HashMap<String, String>()
//
//            params["ArgTripDID"] = orderList!![selectedIndex!!].tripDId!!
//            params["ArgTripDRate"] = rate.toString()
//            params["ArgTripDIsNegotiable"] = orderList!![selectedIndex!!].tripDIsNegotiable!!
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//                jsonDetails = jsonParserDetails.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsUpdate", "POST", params)
//            } else {
//                jsonDetails = jsonParserDetails.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsUpdate", "POST", params)
//            }
//
//            params = HashMap()
//
//            params["ArgTripDID"] = orderList!![selectedIndex!!].tripDId!!
//            params["ArgTripDStatus"] = "2"
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//                jsonStatus = jsonParserStatus.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsStatusUpdate", "POST", params)
//            } else {
//                jsonStatus = jsonParserStatus.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsStatusUpdate", "POST", params)
//            }
//
//            return jsonStatus
//        }
//
//        override fun onPostExecute(jsonObject: JSONObject) {
//            UtilityFunctions.dismissProgressDialog()
//
//            if (jsonDetails != null && jsonStatus != null) {
//                try {
//                    if (jsonDetails!!.getBoolean("status") && jsonStatus!!.getBoolean("status")) {
//                        UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
//                                resources.getString(R.string.YourReplyUpdated), resources.getString(R.string.Ok).toString(),
//                                "", false, false,
//                                {
//                                    //orderList[selectedIndex!!].tripDStatus =  "2"
//                                    //orderList[selectedIndex!!].tripDRate = rate.toString()
//                                    //mShowDetailAdapter.notifyDataSetChanged()
//
//                                    TripDetailsMasterListBackground(false).execute()
//                                }, {})
//
//                    } else {
//                        UtilityFunctions.showAlertOnActivity(this@DriverRepliesActivity,
//                                jsonStatus!!.getString("message"), resources.getString(R.string.Ok).toString(),
//                                "", false, false, {}, {})
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

}
