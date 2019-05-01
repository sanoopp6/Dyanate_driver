package com.fast_prog.dynate.views.removed

//import kotlinx.android.synthetic.main.activity_forgot_password.*
//import kotlinx.android.synthetic.main.content_forgot_password.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fast_prog.dynate.R

class ForgotPasswordActivity : AppCompatActivity() {

//    internal lateinit var number: String
//
//    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forgot_password)
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
//        customTitle(resources.getString(R.string.ForgotPassword))
//
//        countryCodePicker.registerCarrierNumberEditText(edt_mobile)
//
//        btn_send.setOnClickListener {
//            if (countryCodePicker.isValidFullNumber) {
//                number = countryCodePicker.selectedCountryCodeWithPlus + edt_mobile.text.removePrefix("0")
//
//                if (ConnectionDetector.isConnectedOrConnecting(applicationContext)) {
//                    ForgotPasswordDMBackground().execute()
//                } else {
//                    ConnectionDetector.errorSnackbar(coordinator_layout)
//                }
//            } else {
//                UtilityFunctions.showAlertOnActivity(this@ForgotPasswordActivity,
//                        resources.getText(R.string.InvalidMobileNumber).toString(), resources.getString(R.string.Ok).toString(),
//                        "", false, false,
//                        {}, {})
//                edt_mobile.requestFocus()
//            }
//        }
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private inner class ForgotPasswordDMBackground : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            UtilityFunctions.showProgressDialog(this@ForgotPasswordActivity)
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["ArgMobNo"] = number
//
//            var BASE_URL = Constants.BASE_URL_EN + "ForgotPasswordDM"
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//                BASE_URL = Constants.BASE_URL_AR + "ForgotPasswordDM"
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
//                        UtilityFunctions.showAlertOnActivity(this@ForgotPasswordActivity,
//                                resources.getString(R.string.Send), resources.getString(R.string.Ok).toString(),
//                                "", false, false,
//                                { finish() }, {})
//                    } else {
//                        UtilityFunctions.showAlertOnActivity(this@ForgotPasswordActivity,
//                                response.getString("message"), resources.getString(R.string.Ok).toString(),
//                                "", false, false, {}, {})
//                    }
//                } catch (e: JSONException) { e.printStackTrace() }
//
//            } else {
//                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
//                snackbar.show()
//            }
//        }
    }

}
