package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
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
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LoginActivity : AppCompatActivity() {

    internal lateinit var loginMethod: String
    internal lateinit var username: String
    internal lateinit var password: String
    internal lateinit var name: String

    internal lateinit var sharedPreferences: SharedPreferences

    internal var passwordVisible = false
    internal var mobileVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.Login)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@LoginActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        countryCodePicker.registerCarrierNumberEditText(edt_username)

        button_password_visibility.setOnClickListener {
            if (passwordVisible) {
                edt_password.transformationMethod = null
                button_password_visibility.setImageDrawable(ContextCompat.getDrawable(this@LoginActivity, R.drawable.ic_visibility))
            } else {
                edt_password.transformationMethod = PasswordTransformationMethod()
                button_password_visibility.setImageDrawable(ContextCompat.getDrawable(this@LoginActivity, R.drawable.ic_visibility_off))
            }

            passwordVisible = !passwordVisible
            edt_username.setSelection(edt_username.text.length)
        }

        edt_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val numeric = p0.toString().trim().matches("-?\\d+(\\d+)?".toRegex())

                if (numeric && p0.toString().trim().length >= 9) {
                    countryCodePicker.visibility = View.VISIBLE
                    view_border.visibility = View.VISIBLE
                    mobileVisible = true

                } else {
                    countryCodePicker.visibility = View.GONE
                    view_border.visibility = View.GONE
                    mobileVisible = false
                }
            }
        })

        btn_login.setOnClickListener {
            if (validate()){
                if (ConnectionDetector.isConnectedOrConnecting(applicationContext)) {
                    CheckDriverLoginBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }

        txt_forgot.setOnClickListener { startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java)) }

        if (sharedPreferences.getString(Constants.PREFS_SAVED_USERNAME, "").isNotEmpty()) {
            edt_username.setText(sharedPreferences.getString(Constants.PREFS_SAVED_USERNAME, ""))
        }
    }

    private fun validate(): Boolean {
        username = edt_username.text.toString().trim()
        password = edt_password.text.toString().trim()
        loginMethod = Constants.LOG_CONST_NORMAL

        if (username.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@LoginActivity,
                    resources.getText(R.string.InvalidUsername).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            edt_username.requestFocus()
            return false
        } else {
            edt_username.error = null
        }

        if (password.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@LoginActivity,
                    resources.getText(R.string.InvalidPassword).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            edt_password.requestFocus()
            return false
        } else {
            edt_password.error = null
        }

        if (mobileVisible) {
            if (!countryCodePicker.isValidFullNumber) {
                UtilityFunctions.showAlertOnActivity(this@LoginActivity,
                        resources.getText(R.string.InvalidMobileNumber).toString(), resources.getString(R.string.Ok).toString(),
                        "", false, false, {}, {})
                edt_username.requestFocus()
                return false
            } else {
                username = countryCodePicker.selectedCountryCodeWithPlus + edt_username.text.removePrefix("0")
                edt_username.error = null
            }
        }

        return true
    }

    @SuppressLint("StaticFieldLeak")
    private inner class CheckDriverLoginBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@LoginActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgDmUserId", username)
            params.put("ArgDmPassWord", password)

            var BASE_URL = Constants.BASE_URL_EN + "CheckDriverLogin"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "CheckDriverLogin"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val editor = sharedPreferences.edit()

                        editor.putString(Constants.PREFS_USER_ID, response.getJSONArray("data").getJSONObject(0).getString("DmId"))
                        editor.putString(Constants.PREFS_VMO_ID, response.getJSONArray("data").getJSONObject(0).getString("DmVmoId"))
                        editor.putString(Constants.PREFS_VMS_ID, response.getJSONArray("data").getJSONObject(0).getString("VmoVsId"))
                        editor.putString(Constants.PREFS_CUST_ID, response.getJSONArray("data").getJSONObject(0).getString("DmCustId"))
                        editor.putString(Constants.PREFS_USER_MOBILE, response.getJSONArray("data").getJSONObject(0).getString("DmMobNumber"))
                        editor.putString(Constants.PREFS_LATITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLatitude"))
                        editor.putString(Constants.PREFS_LONGITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLongitude"))
                        editor.putString(Constants.PREFS_USER_NAME, response.getJSONArray("data").getJSONObject(0).getString("DmUserId"))
                        editor.putString(Constants.PREFS_USER_CONSTANT, response.getJSONArray("data").getJSONObject(0).getString("DmLoginToken"))
                        editor.putBoolean(Constants.PREFS_IS_FACTORY, response.getJSONArray("data").getJSONObject(0).getBoolean("DmIsFactory"))
                        editor.putString(Constants.PREFS_SHARE_URL, "https://goo.gl/i7Qasx")
                        editor.putBoolean(Constants.PREFS_IS_LOGIN, true)

                        if (loginMethod == Constants.LOG_CONST_NORMAL) {
                            editor.putString(Constants.PREFS_SAVED_USERNAME, response.getJSONArray("data").getJSONObject(0).getString("DmUserId").trim())
                        }

                        editor.commit()

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        ActivityCompat.finishAffinity(this@LoginActivity)
                        startActivity(intent)
                        finish()

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@LoginActivity,
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
