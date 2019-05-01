package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.*
import kotlinx.android.synthetic.main.activity_reply.*
import kotlinx.android.synthetic.main.content_reply.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ReplyActivity : AppCompatActivity() {

    internal var order: Order? = null

    internal var tripDId: String? = null

    internal var negotiable: Boolean? = null
    internal var price: Double = 0.toDouble()
    internal var rate: Double = 0.toDouble()
    internal var alarmOn: Boolean = false
    internal var fromTripAdd: Boolean = false
    internal var isEditingStarted: Boolean = false

    private var alarmController: AlarmController? = null

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        customTitle(resources.getString(R.string.ReplyOrder))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        order = intent.getSerializableExtra("order") as Order?
        if (order == null) finish()

        alarmOn = intent.getBooleanExtra("alarm", false)
        fromTripAdd = intent.getBooleanExtra("fromTripAdd", false)

        if (fromTripAdd) {
            if (sharedPreferences.getBoolean(Constants.PREFS_IS_LOGIN, false)) {
                UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                        resources.getString(R.string.NowOfflineChangeToOnline), resources.getString(R.string.Yes),
                        resources.getString(R.string.No), true, false,
                        {
                            val editor = sharedPreferences.edit()
                            editor.putString(Constants.PREFS_ONLINE_STATUS, Constants.STAT_CONST_ONLINE)
                            editor.commit()
                        },
                        {
                            startActivity(Intent(this@ReplyActivity, HomeActivity::class.java))
                            finish()
                        })
            }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))
            toolbar.setNavigationOnClickListener { finish() }
        }

        if (alarmOn) {
            alarmController = AlarmController(this@ReplyActivity)
            alarmController!!.playSound("android.resource://" + packageName + "/" + R.raw.new_trip)
        }

        shipmentTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Shipment))
        fromNameTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Name))
        fromMobTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Mobile))
        fromLocationTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Location))
        engDateTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Date))
        arDateTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Date))
        timeTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Time))
        toNameTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Name))
        toMobTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Mobile))
        toLocationTitleTextView.text = String.format(Locale.getDefault(), "%s :", resources.getString(R.string.Location))

        tripNoTextView.text = "#${order?.tripNo}"

        val flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this@ReplyActivity, R.color.lightBlueColor))
        var builder = SpannableStringBuilder()

        var spannableString = SpannableString(resources.getString(R.string.Subject) + " : ")
        spannableString.setSpan(colorSpan, 0, spannableString.length, flag)
        builder.append(spannableString)
        builder.append(order?.tripSubject)
        subjectTextView.text = builder

        shipmentTextView.text = order?.tripNotes

        builder = SpannableStringBuilder()
        spannableString = SpannableString(resources.getString(R.string.Size) + " : ")
        spannableString.setSpan(colorSpan, 0, spannableString.length, flag)
        builder.append(spannableString)
        builder.append(order?.vehicleModel)
        vehicleTextView.text = builder

        fromNameTextView.text = order?.tripFromName
        fromMobTextView.text = order?.tripFromMob?.trimStart{ it <= '+'}
        fromLocationTextView.text = order?.tripFromAddress
        engDateTextView.text = order?.scheduleDate
        arDateTextView.text = order?.scheduleDate
        timeTextView.text = order?.scheduleTime
        toNameTextView.text = order?.tripToName
        toMobTextView.text = order?.tripToMob?.trimStart{ it <= '+'}
        toLocationTextView.text = order?.tripToAddress

        fromLocationImageView.setOnClickListener {
            val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s,%s(%s)&daddr=%s,%s(%s)", order?.tripFromLat, order?.tripFromLng, order?.tripFromAddress, order?.tripToLat, order?.tripToLng, order?.tripToAddress)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.`package` = "com.google.android.apps.maps";
            startActivity(intent)
        }

        toLocationImageView.setOnClickListener {
            val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s,%s(%s)&daddr=%s,%s(%s)", order?.tripFromLat, order?.tripFromLng, order?.tripFromAddress, order?.tripToLat, order?.tripToLng, order?.tripToAddress)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.`package` = "com.google.android.apps.maps";
            startActivity(intent)
        }

        if (ConnectionDetector.isConnected(applicationContext)) {
            TripDetailsListUpdateTripNotifiedStatusBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }

        priceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isEditingStarted = true
                if (s.toString().trim().equals("0", true)) {
                    priceEditText.setText("")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        agreeButton.setOnClickListener {
            if (alarmOn && alarmController != null) { alarmController!!.stopSound() }

            try {
                price = priceEditText.text.toString().trim().toDouble()

                if (rate != price) {
                    negotiable = negotiableCheckBox.isChecked

                    if (price <= 0) {
                        UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                resources.getString(R.string.YouMustEnterPrice), resources.getString(R.string.Ok),
                                "", false, false, {}, {})

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                                resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                                resources.getString(R.string.No), true, false,
                                {
                                    if (tripDId != null) TripDetailsUpdateBackground().execute()
                                }, {})
                    }
                }

            } catch (e: Exception) {
                UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                        resources.getString(R.string.YouMustEnterPrice), resources.getString(R.string.Ok),
                        "", false, false, {}, {})
            }
        }

        notAgreeButton.setOnClickListener {
            if (alarmOn && alarmController != null) { alarmController!!.stopSound() }

            UtilityFunctions.showAlertOnActivity(this@ReplyActivity,
                    resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                    resources.getString(R.string.No), true, false,
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
                if (!isEditingStarted) { finish() }
            }, SPLASH_TIME_OUT.toLong())
        }

        priceEditText.filters += DecimalDigitsInputFilter(5,2)
    }

    override fun onBackPressed() {
        if (!fromTripAdd) { super.onBackPressed() }
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

            params["ArgTripDID"] = "0"
            params["ArgTripDMID"] = order?.tripId.toString()
            params["ArgTripDDmId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "0")

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsList", "POST", params)
            } else {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsList", "POST", params)
            }

            params = HashMap()

            params["ArgTripDId"] = order?.tripDId.toString()
            params["ArgTripDIsNotified"] = "true"

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
                                priceEditText.setText("")
                            } else {
                                priceEditText.setText(rate.toString())
                            }

                            try {
                                negotiableCheckBox.isChecked = orderDetail.getJSONObject(0).getString("TripDIsNegotiable").trim().toBoolean()
                            } catch (ignored: Exception) {
                            }

                            tripDId = orderDetail.getJSONObject(0).getString("TripDID")
                            val status = orderDetail.getJSONObject(0).getString("TripDStatus")

                            if (!status.matches("1|7".toRegex())) {
                                priceEditText.isEnabled = false
                                negotiableCheckBox.isEnabled = false
                                agreeButton.visibility = View.GONE
                                notAgreeButton.visibility = View.GONE
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                            } else {
                                priceEditText.isEnabled = true
                                negotiableCheckBox.isEnabled = true
                                agreeButton.visibility = View.VISIBLE
                                notAgreeButton.visibility = View.VISIBLE
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

            params["ArgTripDID"] = tripDId!!
            params["ArgTripDRate"] = price.toString() + ""
            params["ArgTripDIsNegotiable"] = negotiable!!.toString() + ""

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
                                resources.getString(R.string.YourReplyUpdated), resources.getString(R.string.Ok),
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
                                response.getString("message"), resources.getString(R.string.Ok),
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

            params["ArgTripDID"] = tripDId!!
            params["ArgTripDStatus"] = "5"

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
                                    resources.getString(R.string.RejectiosSuccess), resources.getString(R.string.Ok),
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
                                response.getString("message"), resources.getString(R.string.Ok),
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
