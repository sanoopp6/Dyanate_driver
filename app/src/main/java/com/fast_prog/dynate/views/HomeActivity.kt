package com.fast_prog.dynate.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    internal var online: Boolean = false

    internal var UploadLocationThread: Thread? = null

    internal lateinit var gpsTracker: GPSTracker

    internal lateinit var permissionsList: MutableList<String>

    //internal lateinit var viewList: List<View>
    //internal var i: Int = 0

    internal lateinit var sharedPreferences: SharedPreferences

    internal val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124

    //internal var notnModels: MutableList<NotnModel>? = null

    internal var menuNotfn: MenuItem? = null

    var notfnCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        customTitle(resources.getString(R.string.Home))

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                textView_nav_name.text = String.format("%s : %s", resources.getString(R.string.Welcome), sharedPreferences.getString(Constants.PREFS_USER_NAME, ""))
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        menuNotfn = navigationView.menu.findItem(R.id.nav_notfn)

        val MyVersion = Build.VERSION.SDK_INT

        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            permissionsList = ArrayList()

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission()
            }
        }

        //viewList = ArrayList()

        gpsTracker = GPSTracker(this@HomeActivity)

        if (sharedPreferences.getString(Constants.PREFS_REGSTD_STATUS, "") != "4") {
            statusButton.text = "${resources.getString(R.string.app_name)} ✓"
        } else {
//            val editor = sharedPreferences.edit()
//            editor.putBoolean(Constants.PREFS_REGSTD_STATUS, false)
//            editor.commit()
            statusButton.text = "${resources.getString(R.string.app_name)} ✘"
        }

        statusButton.setOnClickListener {
            if (sharedPreferences.getString(Constants.PREFS_REGSTD_STATUS, "") == "4") {
                statusButton.text = "${resources.getString(R.string.app_name)} ✘"
//                val editor = sharedPreferences.edit()
//                editor.putBoolean(Constants.PREFS_REGSTD_STATUS, false)
//                editor.commit()
            } else {
                statusButton.text = "${resources.getString(R.string.app_name)} ✓"
//                val editor = sharedPreferences.edit()
//                editor.putBoolean(Constants.PREFS_REGSTD_STATUS, true)
//                editor.commit()
            }
        }

        makeOnlineFrameLayout.setOnClickListener {
            if (sharedPreferences.getString(Constants.PREFS_REGSTD_STATUS, "") != "4") {
                if (sharedPreferences.getString(Constants.PREFS_REGSTD_STATUS, "") != "2") {

                    gpsTracker.getLocation()

                    if (!gpsTracker.canGetLocation()) {
                        gpsTracker.showSettingsAlert()

                    } else {
                        if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "") == Constants.STAT_CONST_ONLINE) {
                            if (ConnectionDetector.isConnected(applicationContext)) {
                                UtilityFunctions.showAlertOnActivity(this@HomeActivity,
                                        resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                                        resources.getString(R.string.No), true, false,
                                        { makeOffline(true) }, {})
                            } else {
                                ConnectionDetector.errorSnackbar(coordinator_layout)
                            }
                        } else {
                            if (ConnectionDetector.isConnected(applicationContext)) {
                                UtilityFunctions.showAlertOnActivity(this@HomeActivity,
                                        resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                                        resources.getString(R.string.No), true, false,
                                        { makeOnline(true) }, {})
                            } else {
                                ConnectionDetector.errorSnackbar(coordinator_layout)
                            }
                        }
                    }

                } else {

                    UtilityFunctions.showAlertOnActivity(this@HomeActivity,
                            getString(R.string.waiting_for_administrator_approval), resources.getString(R.string.Ok), "", false, true,
                            {}, {})
                }

            } else {
                startActivity(Intent(this@HomeActivity, RegisterActivity::class.java))
            }
        }

        GetDmTripStatusBackground().execute()
    }

    //fun updateTime() {
    //    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    //
    //    var startTime: Date? = null
    //
    //    try { startTime = sdf.parse(sharedPreferences.getString(Constants.PREFS_STATUS_TIME, ""))
    //    } catch (e: ParseException) { e.printStackTrace() }
    //
    //    val endTime = Date()
    //
    //    if (startTime != null) {
    //        var different = endTime.time - startTime.time
    //
    //        val secondsInMilli: Long = 1000
    //        val minutesInMilli = secondsInMilli * 60
    //        val hoursInMilli = minutesInMilli * 60
    //        val daysInMilli = hoursInMilli * 24
    //
    //        val elapsedDays = different / daysInMilli
    //        different %= daysInMilli
    //
    //        val elapsedHours = different / hoursInMilli
    //        different %= hoursInMilli
    //
    //        val elapsedMinutes = different / minutesInMilli
    //        different %= minutesInMilli
    //
    //        val elapsedSeconds = different / secondsInMilli
    //
    //        val timeStr = ("" + (if (elapsedDays > 0) String.format("%sD ", elapsedDays) else "")
    //                + (if (elapsedHours > 0) String.format("%sH ", elapsedHours) else "")
    //                + (if (elapsedMinutes > 0) String.format("%sM ", elapsedMinutes) else "")
    //                + (if (elapsedSeconds > 0) String.format("%sS ", elapsedSeconds) else "") + "")
    //
    //        make_online_button.text = String.format("%s%s", resources.getString(R.string.MakeOffline), if (timeStr.trim().isNotEmpty()) " - ( $timeStr )" else "")
    //        make_online_button.isSelected = true
    //    }
    //}

    override fun onResume() {
        super.onResume()

        if (ConnectionDetector.isConnected(this@HomeActivity)) {
//            GetNotificationsListByCustIdBackground().execute()
            IsAppLiveBackground().execute()
        }

        //if (sharedPreferences.getBoolean(Constants.PREFS_IS_FACTORY, false)) {
        //    TripDetailsMasterListCountBackground(true).execute()
        //} else {
        //    glass_fact_frame.visibility = View.GONE
        //    glass_fact_button.visibility = View.GONE
        //    glass_fact_count.visibility = View.GONE
        //}
        //TripDetailsMasterListCountBackground(false).execute()
        //TripDetailsMasterListCountCustBackground().execute()
        //if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "").isNullOrEmpty()) {
        //    online = false
        //    val editor = sharedPreferences.edit()
        //    editor.putString(Constants.PREFS_ONLINE_STATUS, "offline")
        //    editor.commit()
        //}
    }

    //override fun onPause() {
    //    super.onPause()
    //    online = false
    //    if (UploadLocationThread != null) UploadLocationThread?.interrupt()
    //}

    private fun makeOffline(showDialog: Boolean) {
        MakeOfflineBackground().execute()

//        UpdateLatLongDMBackground("2", showDialog).execute()

        //make_online_button.background = ContextCompat.getDrawable(this@HomeActivity, R.drawable.layout_rect_red_rounded)
    }

    private fun makeOnline(showDialog: Boolean) {

        MakeOnlineBackground().execute()
        //val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
        //val date = Date()
        //editor.putString(Constants.PREFS_STATUS_TIME, simpleDateFormat.format(date))
        //make_online_button.background = ContextCompat.getDrawable(this@HomeActivity, R.drawable.layout_rect_green_rounded)
    }

    @SuppressLint("StaticFieldLeak")
    inner class MakeOnlineBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")
            params["user_id"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/make_online", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val editor = sharedPreferences.edit()
                        editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_ONLINE)
                        editor.commit()

                        makeOnlineImageView.setColorFilter(ContextCompat.getColor(this@HomeActivity, R.color.greenColor))
                        makeOnlineTextView.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.greenColor))
                        makeOnlineTextView.text = resources.getString(R.string.MakeOffline)
                        makeOnlineFrameLayout.startAnimation(UtilityFunctions.blinkAnimation)

                        if (UploadLocationThread != null) {
                            UploadLocationThread?.interrupt()
                        }

                        online = true

//        UpdateLatLongDMBackground(if (showDialog) "1" else "0", showDialog).execute()

                        UploadLocationThread = Thread(object : Runnable {
                            var handler: Handler = @SuppressLint("HandlerLeak")

                            object : Handler() {
                                override fun handleMessage(msg: Message) {
                                    super.handleMessage(msg)
                                    //updateTime()
                                    UpdateLatLongDMBackground("0", false).execute()
                                }
                            }

                            override fun run() {
                                while (online) {
                                    threadMsg("track")

                                    try {
                                        Thread.sleep(5000)
                                    } catch (e: InterruptedException) {
                                        e.printStackTrace()
                                    }
                                }
                            }

                            private fun threadMsg(msg: String) {
                                if (msg != "") {
                                    val msgObj = handler.obtainMessage()
                                    val b = Bundle()
                                    b.putString("message", msg)
                                    msgObj.data = b
                                    handler.sendMessage(msgObj)
                                }
                            }
                        })

                        UploadLocationThread!!.start()
                    } else {
                        UtilityFunctions.showAlertOnActivity(this@HomeActivity, response.getString("message"), getString(R.string.Ok), "",
                                showCancelButton = false, setCancelable = true, actionOk = {}, actionCancel = {})
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class MakeOfflineBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")
            params["user_id"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/make_offline", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        online = false

                        val editor = sharedPreferences.edit()
                        editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_OFFLINE)
                        editor.commit()

                        makeOnlineImageView.setColorFilter(ContextCompat.getColor(this@HomeActivity, R.color.redColor))
                        makeOnlineTextView.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.redColor))
                        makeOnlineTextView.text = resources.getString(R.string.MakeOnline)
                        makeOnlineFrameLayout.clearAnimation()

                        if (UploadLocationThread != null) UploadLocationThread?.interrupt()
                    } else {
                        UtilityFunctions.showAlertOnActivity(this@HomeActivity, response.getString("message"), getString(R.string.Ok), "",
                                showCancelButton = false, setCancelable = true, actionOk = {}, actionCancel = {})
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class IsAppLiveBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["app_name"] = Constants.APP_NAME
            params["version"] = Constants.APP_VERSION

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/check_app_version", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (!response.getBoolean("status")) {
                        ActivityCompat.finishAffinity(this@HomeActivity)
                        val intent = Intent(this@HomeActivity, UpdateActivity::class.java)
                        intent.putExtra("message", response.getString("message"))
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)

        } else {
            UtilityFunctions.showAlertOnActivity(this@HomeActivity,
                    resources.getString(R.string.DoYouWantToExit), resources.getString(R.string.Yes),
                    resources.getString(R.string.No), true, false,
                    {
                        if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "") == Constants.STAT_CONST_ONLINE) {
                            online = false

                            val editor = sharedPreferences.edit()
                            editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_OFFLINE)
                            editor.commit()

                            SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute()
                        }

                        ActivityCompat.finishAffinity(this@HomeActivity)
                        finish()
                    }, {})
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)

        val notfnOption = menu.findItem(R.id.notfn_option)

        notfnOption.isVisible = (notfnCount > 0)

        if (notfnCount > 0) {
            notfnOption.icon = UtilityFunctions.convertLayoutToImage(this@HomeActivity, notfnCount, R.drawable.ic_notifications_white_24dp)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.exit_option) {
            UtilityFunctions.showAlertOnActivity(this@HomeActivity,
                    resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                    resources.getString(R.string.No), true, false,
                    {
                        online = false

                        val editor = sharedPreferences.edit()

                        if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "") == Constants.STAT_CONST_ONLINE) {
                            editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_OFFLINE)
                            SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute()
                        }

                        editor.putBoolean(Constants.PREFS_IS_LOGIN, false)
                        editor.putString(Constants.PREFS_USER_ID, "0")
                        editor.putString(Constants.PREFS_CUST_ID, "0")
                        editor.putString(Constants.PREFS_USER_NAME, "0")
                        editor.putString(Constants.PREFS_USER_MOBILE, "")
                        editor.putString(Constants.PREFS_SHARE_URL, "")
                        editor.putString(Constants.PREFS_LATITUDE, "")
                        editor.putString(Constants.PREFS_LONGITUDE, "")
                        editor.putString(Constants.PREFS_USER_CONSTANT, "")
                        //editor.putString(Constants.PREFS_IS_FACTORY, "")
                        editor.commit()

                        val intent = Intent(this@HomeActivity, NoLoginActivity::class.java)
                        ActivityCompat.finishAffinity(this@HomeActivity)
                        startActivity(intent)
                        finish()

                    }, {})

        } else if (id == R.id.notfn_option) {
            val intent = Intent(this@HomeActivity, NotificationsListActivity::class.java)
            intent.putExtra("loaded", true)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_home) {
            startActivity(Intent(this@HomeActivity, AllOrdersActivity::class.java))

        } else if (id == R.id.nav_add_trip) {
            startActivity(Intent(this@HomeActivity, SenderLocationActivity::class.java))

        } else if (id == R.id.nav_my_trip) {
            startActivity(Intent(this@HomeActivity, MyOrdersActivity::class.java))

        } else if (id == R.id.nav_feedback) {
            startActivity(Intent(this@HomeActivity, ShowFeedbackListActivity::class.java))

        } else if (id == R.id.nav_settings) {
            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))

        } else if (id == R.id.nav_faq) {
            startActivity(Intent(this@HomeActivity, FaqListActivity::class.java))

        } else if (id == R.id.nav_notfn) {
            val intent = Intent(this@HomeActivity, NotificationsListActivity::class.java)
            intent.putExtra("loaded", true)
            startActivity(intent)

        } else if (id == R.id.nav_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.ShareMessage) + " " + sharedPreferences.getString(Constants.PREFS_SHARE_URL, ""))
            sendIntent.type = "text/plain"
            startActivity(sendIntent)

        } else if (id == R.id.nav_logout) {
            UtilityFunctions.showAlertOnActivity(this@HomeActivity,
                    resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                    resources.getString(R.string.No), true, false,
                    {
                        online = false

                        val editor = sharedPreferences.edit()

                        if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "") == Constants.STAT_CONST_ONLINE) {
                            editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_OFFLINE)
                            SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute()
                        }

                        editor.putBoolean(Constants.PREFS_IS_LOGIN, false)
                        editor.putString(Constants.PREFS_USER_ID, "0")
                        editor.putString(Constants.PREFS_CUST_ID, "0")
                        editor.putString(Constants.PREFS_USER_NAME, "0")
                        editor.putString(Constants.PREFS_USER_MOBILE, "")
                        editor.putString(Constants.PREFS_SHARE_URL, "")
                        editor.putString(Constants.PREFS_LATITUDE, "")
                        editor.putString(Constants.PREFS_LONGITUDE, "")
                        editor.putString(Constants.PREFS_USER_CONSTANT, "")
                        //editor.putString(Constants.PREFS_IS_FACTORY, "")
                        editor.commit()

                        val intent = Intent(this@HomeActivity, NoLoginActivity::class.java)
                        ActivityCompat.finishAffinity(this@HomeActivity)
                        startActivity(intent)
                        finish()

                    }, {})
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateLatLongDMBackground internal constructor(internal var status: String, internal var showDialog: Boolean) : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (showDialog) {
                UtilityFunctions.showProgressDialog(this@HomeActivity)
            }
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            gpsTracker.getLocation()

            params["user_id"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")
            params["latitude"] = gpsTracker.getLatitude().toString() + ""
            params["longitude"] = gpsTracker.getLongitude().toString() + ""
            params["token"] = sharedPreferences.getString(Constants.PREFS_USER_TOKEN, "")

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/update_location", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (showDialog) {
                UtilityFunctions.dismissProgressDialog()
            }

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val jsonArray = response.getJSONArray("data")
                        var count = 0

                        try {
                            count = jsonArray.getJSONObject(0).getString("status").trim().toInt()
                        } catch (ignored: Exception) {
                        }

                        if (count > 0 && (status == "1" || status == "0")) {
                            TripDIsNotifiedListBackground().execute()
                        }

                    } else {
                        online = false

                        if (UploadLocationThread != null) UploadLocationThread!!.interrupt()

                        UtilityFunctions.showAlertOnActivity(this@HomeActivity,
                                response.getString("message"), resources.getString(R.string.Ok),
                                "", false, false,
                                {
                                    online = false

                                    val editor = sharedPreferences.edit()

                                    if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "") == Constants.STAT_CONST_ONLINE) {
                                        editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_OFFLINE)
                                        SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute()
                                    }

                                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false)
                                    editor.putString(Constants.PREFS_USER_ID, "0")
                                    editor.putString(Constants.PREFS_CUST_ID, "0")
                                    editor.putString(Constants.PREFS_USER_NAME, "0")
                                    editor.putString(Constants.PREFS_USER_MOBILE, "")
                                    editor.putString(Constants.PREFS_SHARE_URL, "")
                                    editor.putString(Constants.PREFS_LATITUDE, "")
                                    editor.putString(Constants.PREFS_LONGITUDE, "")
                                    editor.putString(Constants.PREFS_USER_CONSTANT, "")
                                    //editor.putString(Constants.PREFS_IS_FACTORY, "")
                                    editor.commit()

                                    val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                                    ActivityCompat.finishAffinity(this@HomeActivity)
                                    startActivity(intent)
                                    finish()
                                }, {})
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
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
                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim()

                                val intent = Intent(this@HomeActivity, ReplyActivity::class.java)
                                intent.putExtra("alarm", true)
                                intent.putExtra("order", order)
                                startActivity(intent)
                            }
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        var result = true

        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permission5 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val permission6 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        val permission7 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (!(permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
            result = false
        }

        if (!(permission3 == PackageManager.PERMISSION_GRANTED && permission4 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result = false
        }

        if (!(permission5 == PackageManager.PERMISSION_GRANTED && permission6 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.READ_SMS)
            result = false
        }

        if (permission7 != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA)
            result = false
        }

        return result
    }

    private fun requestForSpecificPermission() {
        val stringArr = permissionsList.toTypedArray<String>()
        ActivityCompat.requestPermissions(this@HomeActivity, stringArr, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> {
                }
                else -> finish()
            }//granted
            //System.exit(0);
            //not granted
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDmTripStatusBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgDmId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")

            return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "GetDmTripStatus", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val dmTripStatus = response.getJSONArray("data").getJSONObject(0).getString("DmTripStatus").trim()

                        if (dmTripStatus == "2") {
                            online = false

                            val editor = sharedPreferences.edit()
                            editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_OFFLINE)
                            editor.commit()

                            makeOnlineImageView.setColorFilter(ContextCompat.getColor(this@HomeActivity, R.color.redColor))
                            makeOnlineTextView.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.redColor))
                            makeOnlineTextView.text = resources.getString(R.string.MakeOnline)
                            makeOnlineFrameLayout.clearAnimation()

                            if (UploadLocationThread != null) UploadLocationThread?.interrupt()

                        } else {
                            gpsTracker.getLocation()

                            if (gpsTracker.canGetLocation()) {
                                makeOnline(false)
                            } else {
                                makeOffline(false)
                                gpsTracker.showSettingsAlert()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetNotificationsListByCustIdBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgNfUserId"] = "39" //sharedPreferences.getString(Constants.PREFS_USER_ID, "")

            return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "GetNotificationsListByCustId", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val jsonArray = response.getJSONArray("data")

                        notfnCount = jsonArray.length()
                        //invalidateOptionsMenu()

                        if (notfnCount > 0) {
                            val requestUnreadCntStr = notfnCount.toString()
                            val title = String.format(Locale.getDefault(), "%s    %s ", resources.getString(R.string.Notifications), requestUnreadCntStr)
                            val sColored = SpannableString(title)

                            sColored.setSpan(BackgroundColorSpan(Color.RED), title.length - (requestUnreadCntStr.length + 2), title.length, 0)
                            sColored.setSpan(ForegroundColorSpan(Color.WHITE), title.length - (requestUnreadCntStr.length + 2), title.length, 0);

                            menuNotfn?.title = sColored
                        }

                        //notnModels = ArrayList()
                        //var gotoNoti = false
                        //
                        //for (i in 0 until jsonArray.length()) {
                        //    val notnModel = NotnModel()
                        //
                        //    if (sharedPreferences.getInt(Constants.PREFS_NOTFN_ID,0) < jsonArray.getJSONObject(i).getString("NfId").trim().toInt()) {
                        //        gotoNoti = true
                        //        val editor = sharedPreferences.edit()
                        //        editor.putInt(Constants.PREFS_NOTFN_ID, jsonArray.getJSONObject(i).getString("NfId").trim().toInt())
                        //        editor.commit()
                        //    }
                        //
                        //    notnModel.nfId = jsonArray.getJSONObject(i).getString("NfId").trim()
                        //    notnModel.nfUserId = jsonArray.getJSONObject(i).getString("NfUserId").trim()
                        //    notnModel.nfTripMId = jsonArray.getJSONObject(i).getString("NfTripMId").trim()
                        //    notnModel.nfTitle = jsonArray.getJSONObject(i).getString("NfTitle").trim()
                        //    notnModel.nfBody = jsonArray.getJSONObject(i).getString("NfBody").trim()
                        //    notnModel.nfcategory = jsonArray.getJSONObject(i).getString("Nfcategory").trim()
                        //    notnModel.nfReadStatus = jsonArray.getJSONObject(i).getString("NfReadStatus").trim()
                        //    notnModel.nfActive = jsonArray.getJSONObject(i).getString("NfActive").trim()
                        //    notnModel.nfCreateDtTime = jsonArray.getJSONObject(i).getString("NfCreateDtTime").trim()
                        //    notnModel.nfReadDtTime = jsonArray.getJSONObject(i).getString("NfReadDtTime").trim()
                        //
                        //    (notnModels as ArrayList<NotnModel>).add(notnModel)
                        //}
                        //
                        //if (gotoNoti) {
                        //    NotificationsListActivity.notnModels = notnModels
                        //    val intent = Intent(this@HomeActivity, NotificationsListActivity::class.java)
                        //    startActivity(intent)
                        //}
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    //@SuppressLint("StaticFieldLeak")
    //private inner class TripDetailsMasterListCountBackground internal constructor(internal var fromGlass: Boolean) : AsyncTask<Void, Void, JSONObject>() {
    //
    //    override fun doInBackground(vararg param: Void): JSONObject? {
    //        val jsonParser = JsonParser()
    //        val params = HashMap<String, String>()
    //
    //        if (fromGlass) {
    //            params["ArgTripMCustId"] = "1"
    //            params["ArgExcludeCustId"] = "0"
    //
    //        } else {
    //            params["ArgTripMCustId"] = "0"
    //            params["ArgExcludeCustId"] = "1"
    //        }
    //
    //        params["ArgTripDDmId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "0")
    //        params["ArgTripMID"] = "0"
    //        params["ArgTripDID"] = "0"
    //        params["ArgTripMStatus"] = "0"
    //        params["ArgTripDStatus"] = "0"
    //
    //        return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsMasterListCount", "POST", params)
    //    }
    //
    //    override fun onPostExecute(response: JSONObject?) {
    //        if (response != null) {
    //            try {
    //                if (response.getBoolean("status")) {
    //                    val jsonArray = response.getJSONArray("data")
    //                    var count = 0
    //
    //                    try {
    //                        count = Integer.parseInt(jsonArray.getJSONObject(0).getString("Cnt"))
    //                    } catch (ignored: Exception) {
    //                    }
    //
    //                    if (fromGlass && count > 0) {
    //                        glass_fact_count.visibility = View.VISIBLE
    //                        glass_fact_count.text = String.format(Locale.getDefault(), "%d", count)
    //                        glass_fact_count.startAnimation(UtilityFunctions.blinkAnimation)
    //
    //                    } else if (fromGlass && count <= 0) {
    //                        glass_fact_count.visibility = View.GONE
    //                        glass_fact_count.clearAnimation()
    //
    //                    } else if (!fromGlass && count > 0) {
    //                        other_cus_count.visibility = View.VISIBLE
    //                        other_cus_count.text = String.format(Locale.getDefault(), "%d", count)
    //                        other_cus_count.startAnimation(UtilityFunctions.blinkAnimation)
    //
    //                    } else if (!fromGlass && count <= 0) {
    //                        other_cus_count.visibility = View.GONE
    //                        other_cus_count.clearAnimation()
    //                    }
    //                }
    //
    //            } catch (e: JSONException) { e.printStackTrace() }
    //        }
    //    }
    //}
    //
    //@SuppressLint("StaticFieldLeak")
    //private inner class TripDetailsMasterListCountCustBackground : AsyncTask<Void, Void, JSONObject>() {
    //
    //    override fun doInBackground(vararg param: Void): JSONObject? {
    //        val jsonParser = JsonParser()
    //        val params = HashMap<String, String>()
    //
    //        params["ArgTripMCustId"] = sharedPreferences.getString(Constants.PREFS_CUST_ID, "0")
    //        params["ArgTripDDmId"] = "0"
    //        params["ArgTripMID"] = "0"
    //        params["ArgTripDID"] = "0"
    //        params["ArgTripMStatus"] = "0"
    //        params["ArgTripDStatus"] = "0"
    //        params["ArgExcludeCustId"] = "0"
    //
    //        return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsMasterListCountCust", "POST", params)
    //    }
    //
    //    override fun onPostExecute(response: JSONObject?) {
    //        if (response != null) {
    //            try {
    //                if (response.getBoolean("status")) {
    //                    val jsonArray = response.getJSONArray("data")
    //                    var count = 0
    //
    //                    try {
    //                        count = Integer.parseInt(jsonArray.getJSONObject(0).getString("Cnt"))
    //                    } catch (ignored: Exception) {
    //                    }
    //
    //                    if (count > 0) {
    //                        my_booked_count.visibility = View.VISIBLE
    //                        my_booked_count.text = String.format(Locale.getDefault(), "%d", count)
    //                        my_booked_count.startAnimation(UtilityFunctions.blinkAnimation)
    //
    //                    } else {
    //                        my_booked_count.visibility = View.GONE
    //                        my_booked_count.clearAnimation()
    //                    }
    //                }
    //
    //            } catch (e: JSONException) { e.printStackTrace() }
    //        }
    //    }
    //}
}
