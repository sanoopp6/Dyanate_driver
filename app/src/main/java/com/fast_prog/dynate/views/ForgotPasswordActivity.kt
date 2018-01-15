package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.content_forgot_password.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ForgotPasswordActivity : AppCompatActivity() {

    internal lateinit var number: String

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.ForgotPassword)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@ForgotPasswordActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        countryCodePicker.registerCarrierNumberEditText(edt_mobile)

        btn_send.setOnClickListener {
            if (countryCodePicker.isValidFullNumber) {
                number = countryCodePicker.selectedCountryCodeWithPlus + edt_mobile.text.removePrefix("0")

                if (ConnectionDetector.isConnectedOrConnecting(applicationContext)) {
                    SendOTPDMBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            } else {
                UtilityFunctions.showAlertOnActivity(this@ForgotPasswordActivity,
                        resources.getText(R.string.InvalidMobileNumber).toString(), resources.getString(R.string.Ok).toString(),
                        "", false, false,
                        {}, {})
                edt_mobile.requestFocus()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SendOTPDMBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@ForgotPasswordActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgMobNo", "966" + number)
            params.put("ArgIsDB", "true")

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
                        UtilityFunctions.showAlertOnActivity(this@ForgotPasswordActivity,
                                resources.getString(R.string.Send), resources.getString(R.string.Ok).toString(),
                                "", false, false,
                                { finish() }, {})

//                        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
//                        intent.putExtra("MobNo", number)
//                        intent.putExtra("OTP", response.getJSONArray("data").getJSONObject(0).getString("OTP"))
//                        intent.putExtra("UserId", response.getJSONArray("data").getJSONObject(0).getString("DmId"))
//                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
//                        startActivity(intent)
//                        finish()

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@ForgotPasswordActivity,
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
