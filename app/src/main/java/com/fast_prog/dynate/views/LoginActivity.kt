package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import com.fast_prog.dynate.views.removed.ForgotPasswordActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LoginActivity : AppCompatActivity() {

    internal var countryCode: String? = null
    internal var mobileNumber: String? = null

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.Login))

        countryCodePicker.registerCarrierNumberEditText(edt_username)

        if (sharedPreferences.getString(Constants.PREFS_USER_TYPE, "") == Constants.USER_TYPE_CONST_DRIVER) {
            userTypeTextView.text = resources.getString(R.string.Driver)
        } else {
            userTypeTextView.text = resources.getString(R.string.Admin)
        }

        btn_login.setOnClickListener {
            if (validate()){
                if (ConnectionDetector.isConnectedOrConnecting(applicationContext)) {
                    LoginBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }
    }

    private fun validate(): Boolean {

        countryCode = countryCodePicker.selectedCountryCode
        mobileNumber = edt_username.text.toString().trim().removePrefix("0")

        if (!countryCodePicker.isValidFullNumber) {
            UtilityFunctions.showAlertOnActivity(this@LoginActivity,
                    resources.getString(R.string.InvalidMobileNumber), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            edt_username.requestFocus()
            return false
        } else {
            edt_username.error = null
        }

        return true
    }

    private inner class LoginBackground : AsyncTask<Void, Void, JSONObject?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@LoginActivity)
        }

        override fun doInBackground(vararg p0: Void?): JSONObject? {

            val jsonParser = JsonParser()
            val params = HashMap<String, String>()
            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")!!
            params["country_code"] = countryCode!!
            params["mobile_number"] = mobileNumber!!

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/login", "POST", params)
        }

        override fun onPostExecute(result: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()
            if (result != null) {
                if (result.getBoolean("status")) {

                    SendOTPBackground(result.getJSONObject("data").getString("user_id")).execute()
                    VerifyOTPActivity.userId = result.getJSONObject("data").getString("user_id")
                    VerifyOTPActivity.otpAction = "login"
                    VerifyOTPActivity.mobileNumber = countryCode!!+ mobileNumber!!
                    startActivity(Intent(this@LoginActivity, VerifyOTPActivity::class.java))


                } else {
                    UtilityFunctions.showAlertOnActivity(this@LoginActivity,
                            result.getString("message"), resources.getString(R.string.Ok).toString(),
                            "", false, true, {}, {})
                }

            }
        }
    }

    private inner class SendOTPBackground internal constructor(internal var userID: String): AsyncTask<Void, Void, JSONObject?>() {

        override fun doInBackground(vararg p0: Void?): JSONObject? {

            val jsonParser = JsonParser()
            val params = HashMap<String, String>()
            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")!!
            params["user_id"] = userID
            params["country_code"] = countryCode!!
            params["mobile_number"] = mobileNumber!!
            params["action"] = "login"

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/generate_otp", "POST", params)
        }
    }

//    @SuppressLint("StaticFieldLeak")
//    private inner class CheckDriverLoginBackground : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            UtilityFunctions.showProgressDialog (this@LoginActivity)
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["ArgDmUserId"] = username
//            params["ArgDmPassWord"] = username
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
//                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
//                        ActivityCompat.finishAffinity(this@LoginActivity)
//                        startActivity(intent)
//                        finish()
//
//                    } else {
//                        UtilityFunctions.showAlertOnActivity(this@LoginActivity,
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
}
