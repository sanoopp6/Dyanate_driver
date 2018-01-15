package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.RegisterUser
import com.fast_prog.dynate.models.UploadFiles
import com.fast_prog.dynate.utilities.*
import kotlinx.android.synthetic.main.activity_verify_otp.*
import kotlinx.android.synthetic.main.content_verify_otp.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VerifyOTPActivity : AppCompatActivity() {

    internal var otp: String = ""

    internal var isRegistered: Boolean = false

    internal lateinit var receiver: SMSReceiver

    internal var custID: Int = 0

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var otpArray: MutableList<String>

    companion object {
        @SuppressLint("StaticFieldLeak")
        internal lateinit var oTPEditText: EditText
        internal var otpExtra: String = ""
        internal var registerUserExtra: RegisterUser? = null
        internal var uploadFiles: UploadFiles? = null

        fun updateData(otp: String) {
            oTPEditText.setText(otp)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.VerifyAccount)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@VerifyOTPActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        oTPEditText = findViewById<EditText>(R.id.edit_otp)

        txt_otp2.text = String.format("%s %s", this@VerifyOTPActivity.resources.getString(R.string.WeSendAMessageToNumber), registerUserExtra!!.mobile)

        object : CountDownTimer(45000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                txt_timer.setText(String.format(Locale.getDefault(), "00: %d", millisUntilFinished / 1000))
            }

            override fun onFinish() {
                txt_timer.visibility = View.GONE
                btn_resend_otp.isEnabled = true
                btn_resend_otp.paintFlags = btn_resend_otp.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                btn_resend_otp.setTextColor(resources.getColor(R.color.edit_text_focused_color))
            }
        }.start()

        btn_update.setOnClickListener {
            if (validate()) {
                if (ConnectionDetector.isConnected(this@VerifyOTPActivity)) {
                    AddDriverMasterBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }

        btn_resend_otp.setOnClickListener {
            if (ConnectionDetector.isConnected(this@VerifyOTPActivity)) {
                SendOTPDMBackground().execute()
            } else {
                ConnectionDetector.errorSnackbar(coordinator_layout)
            }
        }

        if (!isRegistered) {
            receiver = SMSReceiver()
            val filter = IntentFilter()
            filter.addAction("android.provider.Telephony.SMS_RECEIVED")
            registerReceiver(receiver, filter)
            isRegistered = true
        }

        otpArray = ArrayList()
        otpArray.add(otpExtra)
    }

    override fun onResume() {
        super.onResume()

        if (!isRegistered) {
            receiver = SMSReceiver()
            val filter = IntentFilter()
            filter.addAction("android.provider.Telephony.SMS_RECEIVED")
            registerReceiver(receiver, filter)
            isRegistered = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isRegistered) {
            unregisterReceiver(receiver)
            isRegistered = false
        }
    }

    private fun useLoop(targetValue: String): Boolean {
        for (s in otpArray) {
            if (s == targetValue)
                return true
        }
        return false
    }

    private fun validate(): Boolean {
        otp = oTPEditText.text.toString().trim()

        if (otp.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                    resources.getString(R.string.InvalidOTPNumber), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            return false
        }

        if (!useLoop(otpExtra)) {
            UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                    resources.getString(R.string.InvalidOTPNumber), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            return false
        }

        return true
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AddDriverMasterBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@VerifyOTPActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgDmName", registerUserExtra!!.name!!)
            params.put("ArgDmNameAr", registerUserExtra!!.nameArabic!!)
            params.put("ArgDmMobNumber", registerUserExtra!!.mobile!!)
            params.put("ArgDmEmailId", registerUserExtra!!.mail!!)
            params.put("ArgDmAddress", registerUserExtra!!.address!!)
            params.put("ArgDmLatitude", registerUserExtra!!.latitude!!)
            params.put("ArgDmLongitude", registerUserExtra!!.longitude!!)
            params.put("ArgDmUserId", registerUserExtra!!.username!!)
            params.put("ArgDmPassWord", registerUserExtra!!.password!!)
            params.put("ArgDmVmoId", registerUserExtra!!.vModelId!!)
            params.put("ArgDmLicenseNo", registerUserExtra!!.licenseNo!!)
            params.put("ArgDmLicenseNoAr", registerUserExtra!!.licenseNoArabic!!)
            params.put("ArgDmIsFactory", registerUserExtra!!.withGlass.toString())
            params.put("ArgDmLoginType", registerUserExtra!!.loginMethod!!)
            params.put("ArgVcId", registerUserExtra!!.vCompId!!)

            var BASE_URL = Constants.BASE_URL_EN + "AddDriverMaster"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "AddDriverMaster"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {

                        try {
                            custID = response.getString("data").toInt()
                        } catch (ignored: Exception) { }

                        AddRegFilesBackground().execute()

                    } else {
                        UtilityFunctions.dismissProgressDialog()
                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
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
    private inner class AddRegFilesBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            var json: JSONObject?

            var BASE_URL = Constants.BASE_URL_EN + "AddRegFiles"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "AddRegFiles"
            }

            var params = HashMap<String, String>()

            params.put("ArgBase64", uploadFiles!!.base64Encoded1!!)
            params.put("ArgRFCaption", uploadFiles!!.imageName1!!)
            params.put("ArgRFDmId", custID.toString() + "")

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)

            params = HashMap()

            params.put("ArgBase64", uploadFiles!!.base64Encoded2!!)
            params.put("ArgRFCaption", uploadFiles!!.imageName2!!)
            params.put("ArgRFDmId", custID.toString() + "")

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)

            params = HashMap()

            params.put("ArgBase64", uploadFiles!!.base64Encoded3!!)
            params.put("ArgRFCaption", uploadFiles!!.imageName3!!)
            params.put("ArgRFDmId", custID.toString() + "")

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)

            params = HashMap()

            params.put("ArgBase64", uploadFiles!!.base64Encoded4!!)
            params.put("ArgRFCaption", uploadFiles!!.imageName4!!)
            params.put("ArgRFDmId", custID.toString() + "")

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)


            return json
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        startActivity(Intent(this@VerifyOTPActivity, SupportActivity::class.java))
                        ActivityCompat.finishAffinity(this@VerifyOTPActivity)

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }

                } catch (e: JSONException) { e.printStackTrace() }

            } else {
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SendOTPDMBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@VerifyOTPActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgMobNo", registerUserExtra!!.mobile!!)
            params.put("ArgIsDB", "false")

            var BASE_URL = Constants.BASE_URL_EN + "SendOTPDM"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "SendOTPDM"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP")
                        otpArray.add(otpExtra)
                        btn_resend_otp.isEnabled = false
                        txt_timer.visibility = View.VISIBLE

                        object : CountDownTimer(45000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                txt_timer.text = String.format(Locale.getDefault(), "00: %d", millisUntilFinished / 1000)
                            }

                            override fun onFinish() {
                                txt_timer.visibility = View.GONE
                                btn_resend_otp.isEnabled = true
                                btn_resend_otp.paintFlags = btn_resend_otp.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                                btn_resend_otp.setTextColor(resources.getColor(R.color.edit_text_focused_color))
                            }
                        }.start()

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }

                } catch (e: JSONException) { e.printStackTrace() }

            } else {
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }
}
