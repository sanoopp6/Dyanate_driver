package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.utilities.*
import com.fast_prog.dynate.utilities.Constants.PREFS_USER_TYPE
import com.fast_prog.dynate.utilities.Constants.USER_TYPE_CONST_DRIVER
import kotlinx.android.synthetic.main.activity_verify_otp.*
import kotlinx.android.synthetic.main.content_verify_otp.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VerifyOTPActivity : AppCompatActivity() {

    internal var otp: String = ""

    internal var otpExtra: String = ""

    internal var isRegistered: Boolean = false

    internal lateinit var receiver: SMSReceiver

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var otpArray: MutableList<String>

    companion object {

        var userId = ""
        var otpAction = ""
        var mobileNumber = ""

        @SuppressLint("StaticFieldLeak")
        internal lateinit var editTextOTP1: EditText
        @SuppressLint("StaticFieldLeak")
        internal lateinit var editTextOTP2: EditText
        @SuppressLint("StaticFieldLeak")
        internal lateinit var editTextOTP3: EditText
        @SuppressLint("StaticFieldLeak")
        internal lateinit var editTextOTP4: EditText
        internal var username: String? = null
        internal var otp: String? = null

        fun updateData(otp: String) {
            editTextOTP1.setText(otp.split("")[1])
            editTextOTP2.setText(otp.split("")[2])
            editTextOTP3.setText(otp.split("")[3])
            editTextOTP4.setText(otp.split("")[4])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.VerifyAccount))

        editTextOTP1 = findViewById<View>(R.id.editText_otp1) as EditText
        editTextOTP2 = findViewById<View>(R.id.editText_otp2) as EditText
        editTextOTP3 = findViewById<View>(R.id.editText_otp3) as EditText
        editTextOTP4 = findViewById<View>(R.id.editText_otp4) as EditText

        editTextOTP1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    editTextOTP2.requestFocus()
                }
            }
        })

        editTextOTP2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    editTextOTP3.requestFocus()
                } else if (count == 0) {
                    editTextOTP1.requestFocus()
                }
            }
        })

        editTextOTP3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    editTextOTP4.requestFocus()
                } else if (count == 0) {
                    editTextOTP2.requestFocus()
                }
            }
        })

        editTextOTP4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val otp = editTextOTP1.text.toString().trim() + editTextOTP2.text.toString().trim() + editTextOTP3.text.toString().trim() + editTextOTP4.text.toString().trim()

                if (!otp.isEmpty()) {
                    if (otp.length == 4) {
                        button_submit.isEnabled = true
                    }
                }

                if (count == 0) {
                    editTextOTP3.requestFocus()
                }
            }
        })

        editTextOTP2.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (editTextOTP1.text.trim().isEmpty()) {
                    editTextOTP1.requestFocus()
                }
            }
        }

        editTextOTP3.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (editTextOTP1.text.trim().isEmpty()) {
                    editTextOTP1.requestFocus()
                } else if (editTextOTP2.text.trim().isEmpty()) {
                    editTextOTP2.requestFocus()
                }
            }
        }

        editTextOTP4.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                when {
                    editTextOTP1.text.trim().isEmpty() -> editTextOTP1.requestFocus()
                    editTextOTP2.text.trim().isEmpty() -> editTextOTP2.requestFocus()
                    editTextOTP3.text.trim().isEmpty() -> editTextOTP3.requestFocus()
                }
            }
        }

        editTextOTP2.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                editTextOTP1.requestFocus()
            }
            return@setOnKeyListener false
        }

        editTextOTP3.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                editTextOTP2.requestFocus()
            }
            return@setOnKeyListener false
        }

        editTextOTP4.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                editTextOTP3.requestFocus()
            }
            return@setOnKeyListener false
        }

        object : CountDownTimer(45000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textView_timer.text = String.format(Locale.getDefault(), "00: %d", millisUntilFinished / 1000)
            }

            override fun onFinish() {
                textView_timer.visibility = View.GONE
                textView_resend_otp.isEnabled = true
                textView_resend_otp.alpha = 1.0f
            }
        }.start()

        button_submit.setOnClickListener {
            if (validate()) {
                if (ConnectionDetector.isConnected(this@VerifyOTPActivity)) {
                    VerifyOTPBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }

        textView_otp2.text = String.format("%s %s", this@VerifyOTPActivity.resources.getString(R.string.WeSendAMessageToNumber), username)

        textView_resend_otp.setOnClickListener {
            if (ConnectionDetector.isConnected(this@VerifyOTPActivity)) {
                SendOTPBackground().execute()
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
        otpArray.add(otp)
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
        if (editTextOTP1.text.toString().trim().isEmpty() ||
                editTextOTP2.text.toString().trim().isEmpty() ||
                editTextOTP3.text.toString().trim().isEmpty() ||
                editTextOTP4.text.toString().trim().isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                    resources.getString(R.string.InvalidOTPNumber), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            editTextOTP4.requestFocus()
            return false
        }

        otp = editTextOTP1.text.toString().trim() + editTextOTP2.text.toString().trim() + editTextOTP3.text.toString().trim() + editTextOTP4.text.toString().trim()

        if (!useLoop(otpExtra)) {
            UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                    resources.getString(R.string.InvalidOTPNumber), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        return true
    }

    //@SuppressLint("StaticFieldLeak")
    //private inner class AddDriverMasterBackground : AsyncTask<Void, Void, JSONObject>() {
    //
    //    override fun onPreExecute() {
    //        super.onPreExecute()
    //        UtilityFunctions.showProgressDialog (this@VerifyOTPActivity)
    //    }
    //
    //    override fun doInBackground(vararg param: Void): JSONObject? {
    //        val jsonParser = JsonParser()
    //        val params = HashMap<String, String>()
    //
    //        params["ArgDmName"] = registerUserExtra!!.name!!
    //        params["ArgDmNameAr"] = registerUserExtra!!.nameArabic!!
    //        params["ArgDmMobNumber"] = registerUserExtra!!.mobile!!
    //        params["ArgDmEmailId"] = registerUserExtra!!.mail!!
    //        params["ArgDmAddress"] = registerUserExtra!!.address!!
    //        params["ArgDmLatitude"] = registerUserExtra!!.latitude!!
    //        params["ArgDmLongitude"] = registerUserExtra!!.longitude!!
    //        params["ArgDmUserId"] = registerUserExtra!!.username!!
    //        params["ArgDmPassWord"] = registerUserExtra!!.password!!
    //        params["ArgDmVmoId"] = registerUserExtra!!.vModelId!!
    //        params["ArgDmLicenseNo"] = registerUserExtra!!.licenseNo!!
    //        params["ArgDmLicenseNoAr"] = registerUserExtra!!.licenseNoArabic!!
    //        params["ArgDmIsFactory"] = registerUserExtra!!.withGlass.toString()
    //        params["ArgDmLoginType"] = registerUserExtra!!.loginMethod!!
    //        params["ArgVcId"] = registerUserExtra!!.vCompId!!
    //
    //        var BASE_URL = Constants.BASE_URL_EN + "AddDriverMaster"
    //
    //        if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
    //            BASE_URL = Constants.BASE_URL_AR + "AddDriverMaster"
    //        }
    //
    //        return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
    //    }
    //
    //    override fun onPostExecute(response: JSONObject?) {
    //        if (response != null) {
    //            try {
    //                if (response.getBoolean("status")) {
    //
    //                    try {
    //                        custID = response.getString("data").toInt()
    //                    } catch (ignored: Exception) { }
    //
    //                    AddRegFilesBackground().execute()
    //
    //                } else {
    //                    UtilityFunctions.dismissProgressDialog()
    //                    UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
    //                            response.getString("message"), resources.getString(R.string.Ok),
    //                            "", false, false, {}, {})
    //                }
    //
    //            } catch (e: JSONException) {
    //                UtilityFunctions.dismissProgressDialog()
    //                e.printStackTrace()
    //            }
    //
    //        } else {
    //            UtilityFunctions.dismissProgressDialog()
    //            val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
    //            snackbar.show()
    //        }
    //    }
    //}
    //
    //@SuppressLint("StaticFieldLeak")
    //private inner class AddRegFilesBackground : AsyncTask<Void, Void, JSONObject>() {
    //
    //    override fun doInBackground(vararg param: Void): JSONObject? {
    //        val jsonParser = JsonParser()
    //        var json: JSONObject?
    //
    //        var BASE_URL = Constants.BASE_URL_EN + "AddRegFiles"
    //
    //        if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
    //            BASE_URL = Constants.BASE_URL_AR + "AddRegFiles"
    //        }
    //
    //        var params = HashMap<String, String>()
    //
    //        //params["ArgBase64"] = uploadFiles!!.base64Encoded1!!
    //        //params["ArgRFCaption"] = uploadFiles!!.imageName1!!
    //        params["ArgRFDmId"] = custID.toString() + ""
    //
    //        json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)
    //
    //        params = HashMap()
    //
    //        //params["ArgBase64"] = uploadFiles!!.base64Encoded2!!
    //        //params["ArgRFCaption"] = uploadFiles!!.imageName2!!
    //        params["ArgRFDmId"] = custID.toString() + ""
    //
    //        json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)
    //
    //        params = HashMap()
    //
    //        //params["ArgBase64"] = uploadFiles!!.base64Encoded3!!
    //        //params["ArgRFCaption"] = uploadFiles!!.imageName3!!
    //        params["ArgRFDmId"] = custID.toString() + ""
    //
    //        json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)
    //
    //        params = HashMap()
    //
    //        //params["ArgBase64"] = uploadFiles!!.base64Encoded4!!
    //        //params["ArgRFCaption"] = uploadFiles!!.imageName4!!
    //        params["ArgRFDmId"] = custID.toString() + ""
    //
    //        json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params)
    //
    //
    //        return json
    //    }
    //
    //    override fun onPostExecute(response: JSONObject?) {
    //        UtilityFunctions.dismissProgressDialog()
    //
    //        if (response != null) {
    //            try {
    //                if (response.getBoolean("status")) {
    //                    startActivity(Intent(this@VerifyOTPActivity, SupportActivity::class.java))
    //                    ActivityCompat.finishAffinity(this@VerifyOTPActivity)
    //
    //                } else {
    //                    UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
    //                            response.getString("message"), resources.getString(R.string.Ok),
    //                            "", false, false, {}, {})
    //                }
    //
    //            } catch (e: JSONException) { e.printStackTrace() }
    //
    //        } else {
    //            val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
    //            snackbar.show()
    //        }
    //    }
    //}


    private inner class VerifyOTPBackground : AsyncTask<Void, Void, JSONObject?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@VerifyOTPActivity)
        }

        override fun doInBackground(vararg p0: Void?): JSONObject? {

            val jsonParser = JsonParser()
            val params = HashMap<String, String>()
            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")!!
            params["user_id"] = userId!!
            params["otp"] = otp

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "customer/verify_otp", "POST", params)
        }

        override fun onPostExecute(result: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()
            if (result != null) {
                if (otpAction == "login" || otpAction == "login_resend") {
                    if (result.getBoolean("status")) {

                        GetUserDetailBackground().execute()

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                                result.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, true, {}, {})
                    }
                }
            }
        }
    }
//
//    @SuppressLint("StaticFieldLeak")
//    private inner class CheckDriverLoginBackground : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            UtilityFunctions.showProgressDialog (this@VerifyOTPActivity)
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["ArgDmUserId"] = "testdriver" //username?:""
//            params["ArgDmPassWord"] = "123456" //username?:""
//
//            var BASE_URL = Constants.BASE_URL_EN + "CheckDriverLogin"
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//                BASE_URL = Constants.BASE_URL_AR + "CheckDriverLogin"
//            }
//
//            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
//        }
//
//        override fun onPostExecute(response: JSONObject?) {
//            UtilityFunctions.dismissProgressDialog()
//
//            if (response != null) {
//                try {
//                    if (response.getBoolean("status")) {
//                        val editor = sharedPreferences.edit()
//
//                        editor.putString(Constants.PREFS_USER_ID, response.getJSONArray("data").getJSONObject(0).getString("DmId"))
//                        editor.putString(Constants.PREFS_VMO_ID, response.getJSONArray("data").getJSONObject(0).getString("DmVmoId"))
//                        editor.putString(Constants.PREFS_VMS_ID, response.getJSONArray("data").getJSONObject(0).getString("VmoVsId"))
//                        editor.putString(Constants.PREFS_CUST_ID, response.getJSONArray("data").getJSONObject(0).getString("DmCustId"))
//                        editor.putString(Constants.PREFS_USER_MOBILE, response.getJSONArray("data").getJSONObject(0).getString("DmMobNumber"))
//                        editor.putString(Constants.PREFS_LATITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLatitude"))
//                        editor.putString(Constants.PREFS_LONGITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLongitude"))
//                        editor.putString(Constants.PREFS_USER_NAME, response.getJSONArray("data").getJSONObject(0).getString("DmUserId"))
//                        editor.putString(Constants.PREFS_USER_CONSTANT, response.getJSONArray("data").getJSONObject(0).getString("DmLoginToken"))
//                        //editor.putBoolean(Constants.PREFS_IS_FACTORY, response.getJSONArray("data").getJSONObject(0).getBoolean("DmIsFactory"))
//                        editor.putString(Constants.PREFS_SHARE_URL, "https://goo.gl/i7Qasx")
//                        editor.putBoolean(Constants.PREFS_IS_LOGIN, true)
//                        editor.commit()
//
//                        val intent = Intent(this@VerifyOTPActivity, HomeActivity::class.java)
//                        ActivityCompat.finishAffinity(this@VerifyOTPActivity)
//                        startActivity(intent)
//                        finish()
//
//                    } else {
//                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
//                                response.getString("message"), resources.getString(R.string.Ok),
//                                "", false, false, {}, {})
//                    }
//
//                } catch (e: JSONException) { e.printStackTrace() }
//
//            } else {
//                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
//                snackbar.show()
//            }
//        }
//    }

//    @SuppressLint("StaticFieldLeak")
//    private inner class SendOTPDMBackground : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            UtilityFunctions.showProgressDialog (this@VerifyOTPActivity)
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["ArgMobNo"] = username?:""
//            params["ArgIsDB"] = "false"
//
//            var BASE_URL = Constants.BASE_URL_EN + "SendOTPDM"
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//                BASE_URL = Constants.BASE_URL_AR + "SendOTPDM"
//            }
//
//            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
//        }
//
//        override fun onPostExecute(response: JSONObject?) {
//            UtilityFunctions.dismissProgressDialog()
//
//            if (response != null) {
//                try {
//                    if (response.getBoolean("status")) {
//                        otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP")
//                        otpArray.add(otpExtra)
//
//                        textView_timer.visibility = View.VISIBLE
//                        textView_resend_otp.isEnabled = false
//                        textView_resend_otp.alpha = 0.4f
//
//                        object : CountDownTimer(45000, 1000) {
//                            override fun onTick(millisUntilFinished: Long) {
//                                textView_timer.text = String.format(Locale.getDefault(), "00: %d", millisUntilFinished / 1000)
//                            }
//
//                            override fun onFinish() {
//                                textView_timer.visibility = View.GONE
//                                textView_resend_otp.isEnabled = true
//                                textView_resend_otp.alpha = 1.0f
//                            }
//                        }.start()
//
//                    } else {
//                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
//                                response.getString("message"), resources.getString(R.string.Ok),
//                                "", false, false, {}, {})
//                    }
//
//                } catch (e: JSONException) { e.printStackTrace() }
//
//            } else {
//                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
//                snackbar.show()
//            }
//        }
//    }

    private inner class GetUserDetailBackground : AsyncTask<Void, Void, JSONObject?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@VerifyOTPActivity)
        }

        override fun doInBackground(vararg p0: Void?): JSONObject? {

            val jsonParser = JsonParser()
            val params = HashMap<String, String>()
            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")!!
            params["user_id"] = userId!!

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/get_user_detail", "POST", params)
        }

        override fun onPostExecute(result: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()
            if (result != null) {
                if (otpAction == "login" || otpAction == "login_resend") {
                    if (result.getBoolean("status")) {

                        val editor = sharedPreferences!!.edit()
                        editor.putString(Constants.PREFS_USER_MOBILE_WITHOUT_COUNTRY, result.getJSONObject("data").getJSONObject("user_info").getString("mobile_number"))
                        editor.putString(Constants.PREFS_COUNTRY_CODE, result.getJSONObject("data").getJSONObject("user_info").getString("country_code"))
                        editor.putString(Constants.PREFS_USER_ID, result.getJSONObject("data").getJSONObject("user_info").getString("user_id").trim())
                        editor.putString(Constants.PREFS_USER_TOKEN, result.getJSONObject("data").getJSONObject("user_info").getString("token").trim())
                        editor.putString(Constants.PREFS_USER_FULL_MOBILE, result.getJSONObject("data").getJSONObject("user_info").getString("mobile_number").trim())
                        editor.putString(Constants.PREFS_USER_NAME, result.getJSONObject("data").getJSONObject("user_info").getString("name").trim())
                        editor.putString(Constants.PREFS_REGSTD_STATUS, result.getJSONObject("data").getJSONObject("user_info").getString("status"))
                        editor.putBoolean(Constants.PREFS_IS_LOGIN, true)

                        editor.commit()

                        if (sharedPreferences.getString(PREFS_USER_TYPE, "")  == USER_TYPE_CONST_DRIVER) {
                            startActivity(Intent(this@VerifyOTPActivity, HomeActivity::class.java))
                            finishAffinity()
                        } else {
                            startActivity(Intent(this@VerifyOTPActivity, AdminHomeActivity::class.java))
                            finishAffinity()
                        }



                    } else {
                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                                result.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, true, {}, {})
                    }
                }
            }
        }
    }

    private inner class SendOTPBackground: AsyncTask<Void, Void, JSONObject?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@VerifyOTPActivity)
        }

        override fun doInBackground(vararg p0: Void?): JSONObject? {

            val jsonParser = JsonParser()
            val params = HashMap<String, String>()
            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")!!
            params["user_id"] = userId
            params["action"] = "login"

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/generate_otp", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP")
                        otpArray.add(otpExtra)

                        textView_timer.visibility = View.VISIBLE
                        textView_resend_otp.isEnabled = false
                        textView_resend_otp.alpha = 0.4f

                        object : CountDownTimer(45000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                textView_timer.text = String.format(Locale.getDefault(), "00: %d", millisUntilFinished / 1000)
                            }

                            override fun onFinish() {
                                textView_timer.visibility = View.GONE
                                textView_resend_otp.isEnabled = true
                                textView_resend_otp.alpha = 1.0f
                            }
                        }.start()

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@VerifyOTPActivity,
                                response.getString("message"), resources.getString(R.string.Ok),
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
