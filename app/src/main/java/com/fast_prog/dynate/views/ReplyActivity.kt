package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.*
import kotlinx.android.synthetic.main.activity_reply.*
import kotlinx.android.synthetic.main.content_reply.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ReplyActivity : AppCompatActivity() {

    internal var order: Order? = null

    internal var tripDId: String? = null

    internal var negotiable: Boolean? = null
    internal var price: Double = 0.toDouble()
    internal var rate: Double = 0.toDouble()
    internal var alarmOn: Boolean = false
    internal var fromTripAdd: Boolean = false

    private var alarmController: AlarmController? = null

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.ReplyOrder)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@ReplyActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        order = intent.getSerializableExtra("order") as Order?
        if (order == null) finish()

        alarmOn = intent.getBooleanExtra("alarm", false)
        fromTripAdd = intent.getBooleanExtra("fromTripAdd", false)

        if (fromTripAdd) {
            if (sharedPreferences.getBoolean(Constants.PREFS_IS_LOGIN, false)) {
                UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                        resources.getText(R.string.NowOfflineChangeToOnline).toString(), resources.getString(R.string.Yes).toString(),
                        resources.getString(R.string.No).toString(), true, false,
                        {
                            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
                            val date = Date()

                            val editor = sharedPreferences.edit()
                            editor.putString(Constants.PREFS_STATUS_TIME, simpleDateFormat.format(date))
                            editor.putString(Constants.PREFS_ONLINE_STATUS, "online")
                            editor.commit()
                        },
                        {
                            startActivity(Intent(this@ReplyActivity, HomeActivity::class.java))
                            finish()
                        })
            }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))
            toolbar.setNavigationOnClickListener { finish() }
        }

        if (alarmOn) {
            alarmController = AlarmController(this@ReplyActivity)
            alarmController!!.playSound("android.resource://" + packageName + "/" + R.raw.new_trip)
        }

        sub_title.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Subject), order!!.tripSubject)
        ship_title.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Shipment), order!!.tripNotes)
        type_title.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Make), order!!.vehicleType)
        model_title.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Model), order!!.vehicleModel)
        text_from_name.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Name), order!!.tripFromName)
        text_from_mobile.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Mobile), order!!.tripFromMob!!.trimStart{ it <= '+'})
        date_title.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Date), order!!.scheduleDate)
        time_title.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Time), order!!.scheduleTime)
        text_to_name.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Name), order!!.tripToName)
        text_to_mobile.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Mobile), order!!.tripToMob!!.trimStart{ it <= '+'})
        from_addr.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.From), order!!.tripFromAddress)
        to_addr.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.To), order!!.tripToAddress)

        from_loc.setOnClickListener {
            val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s,%s(%s)&daddr=%s,%s(%s)", order!!.tripFromLat, order!!.tripFromLng, order!!.tripFromAddress, order!!.tripToLat, order!!.tripToLng, order!!.tripToAddress)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.`package` = "com.google.android.apps.maps";
            startActivity(intent)
        }

        to_loc.setOnClickListener {
            val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s,%s(%s)&daddr=%s,%s(%s)", order!!.tripFromLat, order!!.tripFromLng, order!!.tripFromAddress, order!!.tripToLat, order!!.tripToLng, order!!.tripToAddress)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.`package` = "com.google.android.apps.maps";
            startActivity(intent)
        }

        if (ConnectionDetector.isConnected(applicationContext)) {
            TripDetailsListUpdateTripNotifiedStatusBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }

        edit_price.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim().equals("0", true)) {
                    edit_price.setText("")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        btn_agree.setOnClickListener {
            if (alarmOn && alarmController != null) { alarmController!!.stopSound() }

            try {
                price = edit_price.text.toString().trim().toDouble()

                if (rate != price) {
                    negotiable = chk_negotiable.isChecked

                    if (price <= 0) {
                        UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                resources.getString(R.string.YouMustEnterPrice), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                                resources.getString(R.string.No).toString(), true, false,
                                {
                                    if (tripDId != null) TripDetailsUpdateBackground().execute()
                                }, {})
                    }
                }

            } catch (e: Exception) {
                UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                        resources.getString(R.string.YouMustEnterPrice), resources.getString(R.string.Ok).toString(),
                        "", false, false, {}, {})
            }
        }

        btn_disagree!!.setOnClickListener {
            if (alarmOn && alarmController != null) { alarmController!!.stopSound() }

            UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                    resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                    resources.getString(R.string.No).toString(), true, false,
                    {
                        if (tripDId != null) TripDetailsStatusUpdateBackground(true).execute()
                    }, {})
        }

        if (alarmOn && !fromTripAdd) {
            val SPLASH_TIME_OUT = 60000
            Handler().postDelayed({
                if (alarmController != null) {
                    alarmController!!.stopSound()
                    alarmController = null
                }
            }, SPLASH_TIME_OUT.toLong())
        }

        edit_price.filters += DecimalDigitsInputFilter(5,2)
    }

    override fun onPause() {
        super.onPause()
        if (alarmController != null) alarmController!!.stopSound()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsListUpdateTripNotifiedStatusBackground : AsyncTask<Void, Void, JSONObject>() {
        internal var jsonObjectOne: JSONObject? = null
        internal var jsonObjectTwo: JSONObject? = null

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@ReplyActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParserOne = JsonParser()
            val jsonParserTwo = JsonParser()

            var params = HashMap<String, String>()

            params.put("ArgTripDID", "0")
            params.put("ArgTripDMID", order!!.tripId!!)
            params.put("ArgTripDDmId", sharedPreferences.getString(Constants.PREFS_USER_ID, "0"))

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsList", "POST", params)

            } else {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsList", "POST", params)
            }

            params = HashMap()

            params.put("ArgTripDId", order!!.tripDId!!)
            params.put("ArgTripDIsNotified", "true")

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                jsonObjectTwo = jsonParserTwo.makeHttpRequest(Constants.BASE_URL_AR + "UpdateTripNotifiedStatus", "POST", params)

            } else {
                jsonObjectTwo = jsonParserTwo.makeHttpRequest(Constants.BASE_URL_EN + "UpdateTripNotifiedStatus", "POST", params)
            }

            return jsonObjectOne
        }

        override fun onPostExecute(response1: JSONObject) {
            UtilityFunctions.dismissProgressDialog()

            if (jsonObjectOne != null) {
                try {
                    if (jsonObjectOne!!.getBoolean("status")) {
                        val orderDetail = jsonObjectOne!!.getJSONArray("data")

                        if (orderDetail.length() > 0) {
                            rate = try {
                                orderDetail.getJSONObject(0).getString("TripDRate").trim().toDouble()
                            } catch (e: Exception) {
                                0.0
                            }

                            if (rate == 0.0) {
                                edit_price.setText("")
                            } else {
                                edit_price.setText(rate.toString())
                            }

                            try {
                                chk_negotiable.isChecked = orderDetail.getJSONObject(0).getString("TripDIsNegotiable").trim().toBoolean()
                            } catch (ignored: Exception) {
                            }

                            tripDId = orderDetail.getJSONObject(0).getString("TripDID")
                            val status = orderDetail.getJSONObject(0).getString("TripDStatus")

                            if (!status.matches("1|7".toRegex())) {
                                edit_price.isEnabled = false
                                chk_negotiable.isEnabled = false
                                btn_agree.visibility = View.GONE
                                btn_disagree.visibility = View.GONE
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

                            } else {
                                edit_price.isEnabled = true
                                chk_negotiable.isEnabled = true
                                btn_agree.visibility = View.VISIBLE
                                btn_disagree.visibility = View.VISIBLE
                            }
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsUpdateBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@ReplyActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgTripDID", tripDId!!)
            params.put("ArgTripDRate", price.toString() + "")
            params.put("ArgTripDIsNegotiable", negotiable!!.toString() + "")

            var BASE_URL = Constants.BASE_URL_EN + "TripDetailsUpdate"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsUpdate"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                resources.getString(R.string.YourReplyUpdated), resources.getString(R.string.Ok).toString(),
                                "", false, false,
                                {
                                    if (fromTripAdd) {
                                        startActivity(Intent(this@ReplyActivity, HomeActivity::class.java))
                                        ActivityCompat.finishAffinity(this@ReplyActivity)
                                        finish()

                                    } else { finish() }
                                }, {})

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TripDetailsStatusUpdateBackground internal constructor(internal var showDialog: Boolean) : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@ReplyActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgTripDID", tripDId!!)
            params.put("ArgTripDStatus", "5")

            var BASE_URL = Constants.BASE_URL_EN + "TripDetailsStatusUpdate"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsStatusUpdate"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        if (showDialog) {
                            UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                    resources.getString(R.string.RejectiosSuccess), resources.getString(R.string.Ok).toString(),
                                    "", false, false,
                                    {
                                        if (fromTripAdd) {
                                            startActivity(Intent(this@ReplyActivity, HomeActivity::class.java))
                                            ActivityCompat.finishAffinity(this@ReplyActivity)
                                            finish()

                                        } else { finish() }
                                    }, {})
                        } else {
                            if (fromTripAdd) {
                                startActivity(Intent(this@ReplyActivity, HomeActivity::class.java))
                                ActivityCompat.finishAffinity(this@ReplyActivity)
                                finish()

                            } else { finish() }
                        }

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }

}
