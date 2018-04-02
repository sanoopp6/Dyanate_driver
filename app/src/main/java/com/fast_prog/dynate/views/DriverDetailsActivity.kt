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
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_driver_details.*
import kotlinx.android.synthetic.main.content_driver_details.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DriverDetailsActivity : AppCompatActivity() {

    companion object {
        internal var driverDetail: JSONObject? = null
    }

    internal lateinit var sharedPreferences: SharedPreferences

    private var lat: String? = null
    private var lon: String? = null
    private var idImgUrl: String? = null
    private var carFormImgUrl: String? = null
    private var cardImgUrl: String? = null
    private var otherImgUrl: String? = null
    private var rejectReason: String? = null

    internal lateinit var fullname: String
    internal lateinit var phoneNo: String
    internal lateinit var email: String
    internal lateinit var licenseNo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_details)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.Registrations)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@DriverDetailsActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        button_accept.alpha = 0.5f
        button_reject.alpha = 0.5f

        countryCodePicker.registerCarrierNumberEditText(editText_mobile)

        if (driverDetail == null) {
            finish()

        } else {
            lat = driverDetail?.getString("DmLatitude")?.trim()
            lon = driverDetail?.getString("DmLongitude")?.trim()

            textView_username.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Username), driverDetail?.getString("DmUserId")?.trim())
            textView_regDate.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Date), driverDetail?.getString("DmUserId")?.trim())
            textView_name.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Name), driverDetail?.getString("DmName")?.trim())
            textView_locText.text = driverDetail?.getString("DmAddress")?.trim()

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equals("ar", true)) {
                textView_locText.textDirection = View.TEXT_DIRECTION_RTL
            } else {
                textView_locText.textDirection = View.TEXT_DIRECTION_LTR
            }

            imageView_locText.setOnClickListener {
                val uri = String.format(Locale.getDefault(), "geo:0,0?q=%s,%s", lat, lon)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.`package` = "com.google.android.apps.maps";
                startActivity(intent)
            }

            if (driverDetail?.getString("DmEmailId")?.trim()!!.isEmpty()) {
                textView_email.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Email), resources.getString(R.string.Nil))
            } else {
                textView_email.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Email), driverDetail?.getString("DmEmailId")?.trim())
            }

            textView_mobile.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Mobile), driverDetail?.getString("DmMobNumber")?.trim()?.trimStart { it <= '+' })
            textView_mobile.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", driverDetail?.getString("DmMobNumber")?.trim(), null))
                startActivity(intent)
            }

            textView_vehicle.text = String.format(Locale.getDefault(), "%s : %s", resources.getString(R.string.Vehicle), driverDetail?.getString("DmVmoId")?.trim())

            fullname = driverDetail?.getString("DmName")?.trim()!!
            editText_full_name.setText(fullname)

            email = driverDetail?.getString("DmEmailId")?.trim()!!
            editText_email.setText(email)

            phoneNo = driverDetail?.getString("DmMobNumber")?.trim()!!
            countryCodePicker.fullNumber = phoneNo

            licenseNo = driverDetail?.getString("DmLicenseNo")?.trim()!!
            editText_license_no.setText(licenseNo)

            button_accept.setOnClickListener {
                hideSoftKeyboard()

                if (validate()) {
                    UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                            resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                            resources.getString(R.string.No).toString(), true, false,
                            { UpdateDriverMasterBackground().execute() }, {})
                }
            }

            button_reject.setOnClickListener {
                hideSoftKeyboard()

                UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                        resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                        resources.getString(R.string.No).toString(), true, false,
                        {
                            val builder = AlertDialog.Builder(this@DriverDetailsActivity)
                            val inflater = this@DriverDetailsActivity.layoutInflater
                            val view = inflater.inflate(R.layout.alert_dialog_wak_regn_reject, null)
                            builder.setView(view)
                            val alertDialog = builder.create()
                            alertDialog.setCancelable(true)

                            val buttonConfirm = view.findViewById<View>(R.id.button_confirm) as Button
                            val editTextRejReason = view.findViewById<View>(R.id.editText_rej_reason) as EditText
                            val textViewReasonError = view.findViewById<View>(R.id.textView_reason_error) as TextView

                            buttonConfirm.setOnClickListener {
                                if (editTextRejReason.text.toString().trim().isEmpty()) {
                                    textViewReasonError.text = resources.getString(R.string.PleaseEnterReason)
                                } else {
                                    alertDialog.dismiss()

                                    rejectReason = editTextRejReason.text.toString().trim()
                                    //AdminRejectWakeelBackground().execute()
                                }
                            }

                            editTextRejReason.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(p0: Editable?) {
                                    if (editTextRejReason.text.toString().trim().isNotEmpty()) {
                                        textViewReasonError.text = ""
                                    }
                                }

                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                }

                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                }
                            })

                            alertDialog.show()
                        }, {})
            }

            if (ConnectionDetector.isConnected(applicationContext)) {
                RegFilesListBackground().execute()
            } else {
                ConnectionDetector.errorSnackbar(coordinator_layout)
            }
        }
    }

    private fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class RegFilesListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@DriverDetailsActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgRFDmId"] = driverDetail?.getString("DmId")?.trim()!!

            var BASE_URL = Constants.BASE_URL_EN + "RegFilesList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar",true)) {
                BASE_URL = Constants.BASE_URL_AR + "RegFilesList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (jsonObject != null) {
                try {
                    if (jsonObject.getBoolean("status")) {
                        val jsonObj = jsonObject.getJSONObject("data");

                        idImgUrl = Constants.IMG_URL + jsonObj.getString("URL").trim()
                        carFormImgUrl = Constants.IMG_URL + jsonObj.getString("URL").trim()
                        cardImgUrl = Constants.IMG_URL + jsonObj.getString("URL").trim()
                        otherImgUrl = Constants.IMG_URL + jsonObj.getString("URL").trim()

                        Picasso.with(this@DriverDetailsActivity).load(idImgUrl).placeholder(R.drawable.image_progress_view).error(R.drawable.logo_1).into(imageView_id)
                        Picasso.with(this@DriverDetailsActivity).load(carFormImgUrl).placeholder(R.drawable.image_progress_view).error(R.drawable.logo_1).into(imageView_car_form)
                        Picasso.with(this@DriverDetailsActivity).load(cardImgUrl).placeholder(R.drawable.image_progress_view).error(R.drawable.logo_1).into(imageView_card)
                        Picasso.with(this@DriverDetailsActivity).load(otherImgUrl).placeholder(R.drawable.image_progress_view).error(R.drawable.logo_1).into(imageView_other)

                        imageView_id.setOnClickListener {
                            ShowPDFImageActivity.imgURL = idImgUrl
                            ShowPDFImageActivity.docType = resources.getString(R.string.ImageID)
                            val intent = Intent(this@DriverDetailsActivity, ShowPDFImageActivity::class.java)
                            startActivity(intent)
                        }

                        imageView_car_form.setOnClickListener {
                            ShowPDFImageActivity.imgURL = carFormImgUrl
                            ShowPDFImageActivity.docType = resources.getString(R.string.CarForm)
                            val intent = Intent(this@DriverDetailsActivity, ShowPDFImageActivity::class.java)
                            startActivity(intent)
                        }

                        imageView_card.setOnClickListener {
                            ShowPDFImageActivity.imgURL = cardImgUrl
                            ShowPDFImageActivity.docType = resources.getString(R.string.Card)
                            val intent = Intent(this@DriverDetailsActivity, ShowPDFImageActivity::class.java)
                            startActivity(intent)
                        }

                        imageView_other.setOnClickListener {
                            ShowPDFImageActivity.imgURL = otherImgUrl
                            ShowPDFImageActivity.docType = resources.getString(R.string.Other)
                            val intent = Intent(this@DriverDetailsActivity, ShowPDFImageActivity::class.java)
                            startActivity(intent)
                        }
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

    private fun validate(): Boolean {
        fullname = editText_full_name.text.toString().trim()
        phoneNo = countryCodePicker.selectedCountryCodeWithPlus + editText_mobile.text.removePrefix("0")
        email = editText_email.text.toString().trim()
        licenseNo = editText_license_no.text.toString().trim()

        if (fullname.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                    resources.getText(R.string.InvalidName).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            return false
        }

        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                    resources.getText(R.string.InvalidEmail).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false,
                    { editText_email.requestFocus() }, {})
            return false
        }

        if (!countryCodePicker.isValidFullNumber) {
            UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                    resources.getText(R.string.InvalidMobileNumber).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false,
                    { editText_mobile.requestFocus() }, {})
            return false
        }

        if (licenseNo.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                    resources.getText(R.string.InvalidLicenseNo).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false,
                    { editText_license_no.requestFocus() }, {})
            return false
        }

        return true
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateDriverMasterBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@DriverDetailsActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgDmName"] = fullname
            params["ArgDmNameAr"] = fullname
            params["ArgDmMobNumber"] = phoneNo
            params["ArgDmEmailId"] = email
            params["ArgDmAddress"] = driverDetail?.getString("DmAddress")?.trim()!!
            params["ArgDmLatitude"] = driverDetail?.getString("DmLatitude")?.trim()!!
            params["ArgDmLongitude"] = driverDetail?.getString("DmLongitude")?.trim()!!
            params["ArgDmUserId"] = driverDetail?.getString("DmUserId")?.trim()!!
            params["ArgDmPassWord"] = ""
            params["ArgDmLoginType"] = driverDetail?.getString("DmLoginType")?.trim()!!
            params["ArgDmVmoId"] = driverDetail?.getString("DmVmoId")?.trim()!!
            params["ArgDmLicenseNo"] = licenseNo
            params["ArgDmLicenseNoAr"] = licenseNo
            params["ArgDmRemarks"] = ""

            var BASE_URL = Constants.BASE_URL_EN + "UpdateDriverMaster"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar",true)) {
                BASE_URL = Constants.BASE_URL_AR + "UpdateDriverMaster"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (jsonObject != null) {
                try {
                    if (jsonObject.getBoolean("status")) {
                        UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                                resources.getString(R.string.DriverApproved).toString(), resources.getString(R.string.Ok).toString(),
                                "", false, false,
                                { finish() }, {})
                    } else {
                        UtilityFunctions.showAlertOnActivity(this@DriverDetailsActivity,
                                jsonObject.getString("message"), resources.getString(R.string.Ok).toString(),
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

//    @SuppressLint("StaticFieldLeak")
//    private inner class AdminRejectWakeelBackground : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            UtilityFunctions.showProgressDialog (this@WakeelDetailsActivity)
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")
//            params["userId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")
//            params["token"] = sharedPreferences.getString(Constants.PREFS_USER_TOKEN, "")
//            params["id"] = wakeelID.toString()
//            params["reason"] = rejectReason.toString()
//
//            return jsonParser.makeHttpRequest(Constants.BASE_URL + "Admin.asmx/AdminRejectWakeel", "POST", params)
//        }
//
//        override fun onPostExecute(jsonObject: JSONObject?) {
//            UtilityFunctions.dismissProgressDialog()
//
//            if (jsonObject != null) {
//                try {
//                    if (jsonObject.getBoolean("status")) {
//                        UtilityFunctions.showAlertOnActivity(this@WakeelDetailsActivity,
//                                resources.getString(R.string.WakeelRejected).toString(), resources.getString(R.string.Ok).toString(),
//                                "", false, false,
//                                { finish() }, {})
//                    } else {
//                        UtilityFunctions.showAlertOnActivity(this@WakeelDetailsActivity,
//                                jsonObject.getString("message"), resources.getString(R.string.Ok).toString(),
//                                "", false, false, {}, {})
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            } else {
//                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
//                snackbar.show()
//            }
//        }
//    }
}
