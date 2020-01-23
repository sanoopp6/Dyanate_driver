package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.extensions.hideKeyboard
import com.fast_prog.dynate.models.PlaceItem
import com.fast_prog.dynate.models.RegisterUser
import com.fast_prog.dynate.utilities.*
import com.fast_prog.dynate.utilities.Constants.PREFS_REGSTD_STATUS
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.content_register.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class RegisterActivity : AppCompatActivity() {

    internal lateinit var registerUser: RegisterUser

    internal var name: String? = null
    //    internal var mobile: String? = null
    internal var mail: String? = null
    internal var address: String? = null
    //internal var username: String? = null
    //internal var password: String? = null
    internal var vSizeName: String? = null
    internal var vMakeName: String? = null
    //    internal var vModelName: String? = null
    internal var vCompName: String? = null
    internal var withGlass: Boolean = false
    internal var withCompany: Boolean = false
    internal var longitude: Double? = null
    internal var latitude: Double? = null
    internal var vSizeId: Int? = null
    internal var vMakeId: Int? = null
    //    internal var vModelId: Int? = null
    internal var vCompId: Int? = null
    internal var companyName: String? = null
    internal var vehicleNumber: String? = null

    internal lateinit var vehicleSizeDataList: MutableList<String>
    internal lateinit var vehicleSizeIdList: MutableList<Int>
    internal lateinit var vehicleMakeDataList: MutableList<String>
    internal lateinit var vehicleMakeIdList: MutableList<Int>
    internal lateinit var vehicleModelDataList: MutableList<String>
    internal lateinit var vehicleModelIdList: MutableList<Int>
    internal lateinit var vehicleCompanyDataList: MutableList<String>
    internal lateinit var vehicleCompanyIdList: MutableList<Int>

    internal lateinit var vehicleSizeAdapter: ArrayAdapter<String>
    internal lateinit var vehicleMakeAdapter: ArrayAdapter<String>
    internal lateinit var vehicleModelAdapter: ArrayAdapter<String>
    internal lateinit var vehicleCompanyAdapter: ArrayAdapter<String>

    internal lateinit var gpsTracker: GPSTracker

    internal var REQUEST_CODE = 1122

    internal var isFilled = false
    //private var passwordVisible = false

    internal lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { backPressed() }

        customTitle(resources.getString(R.string.Register))

        //textView_nameTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Name) + " <font color=#E81C4F>*</font>")
        //textView_usernameTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Username) + " <font color=#E81C4F>*</font>")
        //textView_mobileTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Mobile) + " <font color=#E81C4F>*</font>")
        //textView_passwordTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Password) + " <font color=#E81C4F>*</font>")
        //textView_addressTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Address) + " <font color=#E81C4F>* ${resources.getString(R.string.ChooseFromGoogleMap)}</font>")
        //textView_vehicleSizeTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Size) + " <font color=#E81C4F>*</font>")
        //textView_vehicleMakeTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Make) + " <font color=#E81C4F>*</font>")
        //textView_vehicleModelTitle.text = UtilityFunctions.fromHtml(resources.getString(R.string.Model) + " <font color=#E81C4F>*</font>")

        gpsTracker = GPSTracker(this@RegisterActivity)
//        countryCodePicker.registerCarrierNumberEditText(editText_mobile)

        //button_password_visibility.setOnClickListener {
        //    if (passwordVisible) {
        //        editText_password.transformationMethod = null
        //        button_password_visibility.setImageDrawable(resources.getDrawable(R.drawable.ic_visibility))
        //    } else {
        //        editText_password.transformationMethod = PasswordTransformationMethod()
        //        button_password_visibility.setImageDrawable(resources.getDrawable(R.drawable.ic_visibility_off))
        //    }
        //
        //    passwordVisible = !passwordVisible
        //    editText_password.setSelection(editText_password.text.length)
        //}

        button_address.setOnClickListener {
            gpsTracker.getLocation()

            if (gpsTracker.canGetLocation()) {
                val intent = Intent(this@RegisterActivity, MapLocationPickerActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)

            } else {
                gpsTracker.showSettingsAlert()
            }
        }

        txt_terms_conditions.setOnClickListener { getTermsAndConditions(true) }

        radioButton_yes_glass.setOnClickListener {
            isFilled = true
            withGlass = true

            radioButton_yes_glass.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.whiteColor))
            radioButton_no_glass.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.colorToggleGreen))
        }

        radioButton_no_glass.setOnClickListener {
            isFilled = true
            withGlass = false

            radioButton_no_glass.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.whiteColor))
            radioButton_yes_glass.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.colorToggleGreen))
        }

        radioButton_yes_company.setOnClickListener {
            isFilled = true
            withCompany = true

            editText_company_name.visibility = View.VISIBLE
            textView_withCompanyError.visibility = View.VISIBLE

            radioButton_yes_company.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.whiteColor))
            radioButton_no_company.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.colorToggleGreen))
        }

        radioButton_no_company.setOnClickListener {
            isFilled = true
            withCompany = false

            editText_company_name.visibility = View.GONE
            textView_withCompanyError.visibility = View.GONE

            radioButton_no_company.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.whiteColor))
            radioButton_yes_company.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.colorToggleGreen))
        }

        textView_withGlass1.text = String.format(Locale.getDefault(), "%s %s", resources.getString(R.string.WorkingWith), resources.getString(R.string.GlassFactory))

        val glassFactoryClick = object : ClickableSpan() {
            override fun onClick(view: View) {
                val builder = AlertDialog.Builder(this@RegisterActivity)
                val inflater = this@RegisterActivity.layoutInflater
                val view1 = inflater.inflate(R.layout.alert_dialog, null)
                builder.setView(view1)
                val txtAlert = view1.findViewById(R.id.txt_alert) as TextView
                txtAlert.setText(R.string.AboutGlass1)
                val txtAlert1 = view1.findViewById(R.id.txt_alert1) as TextView
                txtAlert1.setText(R.string.AboutGlass2)
                txtAlert1.visibility = View.VISIBLE
                val txtAlert2 = view1.findViewById(R.id.txt_alert2) as TextView
                txtAlert2.setText(R.string.AboutGlass3)
                txtAlert2.visibility = View.VISIBLE
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                view1.findViewById<Button>(R.id.btn_cancel).visibility = View.GONE
                val btnOK = view1.findViewById(R.id.btn_ok) as Button
                btnOK.setOnClickListener { alertDialog!!.dismiss() }
                btnOK.text = resources.getString(R.string.Ok)
                alertDialog!!.show()
            }
        }

        UtilityFunctions.makeLinks(textView_withGlass1, arrayOf(resources.getString(R.string.GlassFactory)), arrayOf(glassFactoryClick))

        btn_register.setOnClickListener {
            if (validate()) {
                if (ConnectionDetector.isConnected(this@RegisterActivity)) {
                    InsertDriverData().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }

        spnr_vehicleSize.setOnTouchListener { v, event ->
            hideKeyboard()
            false
        }

        spnr_vehicleSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val index = parent.selectedItemPosition
                val selectedText = parent.getChildAt(0) as TextView

                if (index != 0) {
                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.blackColor))
                } else {
                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.darkest_gray))
                }

                vSizeId = vehicleSizeIdList[index]
                vSizeName = vehicleSizeDataList[index]

                if (vSizeId!! > 0) {
                    isFilled = true
//                    ListVehicleModelBackground().execute()
                } else {
//                    vehicleModelIdList = ArrayList()
//                    vehicleModelDataList = ArrayList()
//                    vehicleModelIdList.add(0)
//                    vehicleModelDataList.add(resources.getString(R.string.SelectModel))
//                    vehicleModelAdapter = MySpinnerAdapter(this@RegisterActivity, android.R.layout.select_dialog_item, vehicleModelDataList)
//                    spnr_vehicleModel.adapter = vehicleModelAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spnr_vehicleMake.setOnTouchListener { v, event ->
            hideKeyboard()
            false
        }

        spnr_vehicleMake.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val index = parent.selectedItemPosition
                val selectedText = parent.getChildAt(0) as TextView

                if (index != 0) {
                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.blackColor))
                } else {
                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.darkest_gray))
                }

                vMakeId = vehicleMakeIdList[index]
                vMakeName = vehicleMakeDataList[index]

                if (vMakeId!! > 0) {
                    isFilled = true
//                    ListVehicleModelBackground().execute()
                } else {
//                    vehicleModelIdList = ArrayList()
//                    vehicleModelDataList = ArrayList()
//                    vehicleModelIdList.add(0)
//                    vehicleModelDataList.add(resources.getString(R.string.SelectModel))
//                    vehicleModelAdapter = MySpinnerAdapter(this@RegisterActivity, android.R.layout.select_dialog_item, vehicleModelDataList)
//                    spnr_vehicleModel.adapter = vehicleModelAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

//        spnr_vehicleModel.setOnTouchListener { v, event ->
//            hideKeyboard()
//            false
//        }
//
//        spnr_vehicleModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                val index = parent.selectedItemPosition
//                val selectedText = parent.getChildAt(0) as TextView
//
//                if (index != 0) {
//                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.blackColor))
//                } else {
//                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.darkest_gray))
//                }
//
//                vModelId = vehicleModelIdList[index]
//                vModelName = vehicleModelDataList[index]
//
//                if (vModelId!! > 0) { isFilled = true }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }

        spnr_withCompany.setOnTouchListener { v, event ->
            hideKeyboard()
            false
        }

        spnr_withCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val index = parent.selectedItemPosition
                val selectedText = parent.getChildAt(0) as TextView

                if (index != 0) {
                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.blackColor))
                } else {
                    selectedText.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.darkest_gray))
                }

                vCompId = vehicleCompanyIdList[index]
                vCompName = vehicleCompanyDataList[index]

                if (vCompId!! > 0) {
                    isFilled = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //editText_username.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        //    if (!hasFocus) {
        //        val numericPattern = "^[0-9]*$"
        //        val alphaPattern = "^[a-zA-Z0-9]*$"
        //        val username = editText_username.text.toString().trim()
        //
        //        if (username.isEmpty()) {
        //            textView_usernameError.text = this@RegisterActivity.resources.getText(R.string.InvalidUsername)
        //
        //        } else if (username.length < 4) {
        //            textView_usernameError.text = this@RegisterActivity.resources.getText(R.string.Username4Char)
        //
        //        } else if (username.length > 10) {
        //            textView_usernameError.text = this@RegisterActivity.resources.getText(R.string.Username10Char)
        //
        //        } else if (!username.matches(alphaPattern.toRegex())) {
        //            textView_usernameError.text = this@RegisterActivity.resources.getText(R.string.OnlyEnglishCharacters)
        //
        //        } else if (username.matches(numericPattern.toRegex())) {
        //            textView_usernameError.text = this@RegisterActivity.resources.getText(R.string.AlphanumericUsername)
        //
        //        } else {
        //            textView_usernameError.text = ""
        //            this.username = username
        //
        //            if (ConnectionDetector.isConnected(this@RegisterActivity)) {
        //                //CheckUserIDOrMobileNoExistBackground(true).execute()
        //            }
        //        }
        //    }
        //}
        //
        //editText_password.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        //    if (!hasFocus) {
        //        isFilled = true
        //
        //        val alphanumericPattern = "^[A-Za-z0-9_@./#&+-]*$"
        //        val password = editText_password.text.toString().trim()
        //
        //        if (password.isEmpty()) {
        //            textView_passwordError.text = this@RegisterActivity.resources.getText(R.string.InvalidPassword)
        //
        //        } else if (password.length < 6) {
        //            textView_passwordError.text = this@RegisterActivity.resources.getText(R.string.PasswordMin6Char)
        //
        //        } else if (!password.matches(alphanumericPattern.toRegex())) {
        //            textView_passwordError.text = this@RegisterActivity.resources.getText(R.string.PasswordInvalidChar)
        //
        //        } else {
        //            textView_passwordError.text = ""
        //            this.password = password
        //        }
        //    }
        //}

//        editText_mobile.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                isFilled = true
//
//                val mobileNo = countryCodePicker.selectedCountryCodeWithPlus + editText_mobile.text.removePrefix("0")
//
//                if (!countryCodePicker.isValidFullNumber) {
//                    textView_mobileError.text = this@RegisterActivity.resources.getText(R.string.InvalidMobileNumber)
//
//                } else {
//                    textView_mobileError.text = ""
//                    this.mobile = mobileNo
//
//                    if (ConnectionDetector.isConnected(this@RegisterActivity)) {
//                        //CheckUserIDOrMobileNoExistBackground(false).execute()
//                    }
//                }
//            }
//        }

        if (ConnectionDetector.isConnected(applicationContext)) {
            ListVehicleMakeListVehicleCompanyBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    private fun getTermsAndConditions(isTermCond: Boolean) {
        val assetManager = assets
        var `in`: InputStream? = null
        var out: OutputStream? = null
        val file = File(filesDir, "terms_conditions.pdf")

        try {
            if (isTermCond) {
                `in` = assetManager.open("terms_conditions.pdf")
            } else {
                `in` = assetManager.open("terms_conditions.pdf")
            }
            out = openFileOutput(file.name, Context.MODE_PRIVATE)
            copyFile(`in`, out)
            `in`!!.close()
            `in` = null
            out!!.flush()
            out.close()
            out = null
        } catch (e: Exception) {
            //Log.e("tag_", e.getMessage());
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW)

            val apkURI = FileProvider.getUriForFile(this@RegisterActivity, applicationContext.packageName + ".provider", file)

            intent.setDataAndType(apkURI, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(applicationContext, R.string.NoAppPDF, Toast.LENGTH_SHORT).show()
        }

    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream?, out: OutputStream?) {
        val buffer = ByteArray(1024)
        var read: Int = -1
        while ({ read = `in`!!.read(buffer); read }() != -1) {
            out!!.write(buffer, 0, read)
        }
    }

    override fun onBackPressed() {
        backPressed()
    }

    private fun backPressed() {
        name = editText_name.text.toString().trim()
        mail = editText_email.text.toString().trim()
        //username = editText_username.text.toString().trim()
        //password = editText_password.text.toString().trim()
//        mobile = countryCodePicker.selectedCountryCodeWithPlus + editText_mobile.text.removePrefix("0")

        isFilled = isFilled || name!!.isNotEmpty() || /*mobile!!.isNotEmpty() || */ mail!!.isNotEmpty() ||
                address!!.isNotEmpty() || /*username!!.isNotEmpty() || password!!.isNotEmpty() ||*/ chk_i_agree.isChecked

        if (isFilled) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.FilledDataWillBeLost), resources.getString(R.string.Yes),
                    resources.getString(R.string.No), true, false,
                    { finish() }, {})

        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            isFilled = true

            val placeItem: PlaceItem = data?.getSerializableExtra("PlaceItem") as PlaceItem

            button_address.text = placeItem.plName
            address = placeItem.plName.toString()
            latitude = placeItem.pLatitude?.toDouble()
            longitude = placeItem.pLongitude?.toDouble()
        }
    }

    private fun validate(): Boolean {
        name = editText_name.text.toString().trim()
        mail = editText_email.text.toString().trim()
        vehicleNumber = editText_vehicle_number.text.toString().trim()
        companyName = editText_company_name.text.toString().trim()

        //username = editText_username.text.toString().trim()
        //password = editText_password.text.toString().trim()
//        mobile = countryCodePicker.selectedCountryCodeWithPlus + editText_mobile.text.removePrefix("0")

        val numericPattern = "^[0-9]*$"
        val alphaPattern = "^[a-zA-Z0-9]*$"
        val alphanumericPattern = "^[A-Za-z0-9_@./#&+-]*$"

        if (name!!.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.InvalidName), resources.getString(R.string.Ok),
                    "", false, false,
                    { editText_name.requestFocus() }, {})
            return false
        }

        //if (username!!.isEmpty()) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.InvalidUsername), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_username.requestFocus() }, {})
        //    return false
        //
        //} else if (username!!.length < 4) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.Username4Char), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_username.requestFocus() }, {})
        //    return false
        //
        //} else if (username!!.length > 10) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.Username10Char), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_username.requestFocus() }, {})
        //    return false
        //
        //} else if (!username!!.matches(alphaPattern.toRegex())) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.OnlyEnglishCharacters), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_username.requestFocus() }, {})
        //    return false
        //
        //} else if (username!!.matches(numericPattern.toRegex())) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.AlphanumericUsername), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_username.requestFocus() }, {})
        //    return false
        //}

//        if (!countryCodePicker.isValidFullNumber) {
//            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
//                    resources.getString(R.string.InvalidMobileNumber), resources.getString(R.string.Ok),
//                    "", false, false,
//                    { editText_mobile.requestFocus() }, {})
//            return false
//        }

        if (!mail!!.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.InvalidEmail), resources.getString(R.string.Ok),
                    "", false, false,
                    { editText_email.requestFocus() }, {})
            return false
        }

        //if (password!!.isEmpty()) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.InvalidPassword), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_password.requestFocus() }, {})
        //    return false
        //
        //} else if (password!!.length < 6) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.PasswordMin6Char), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_password.requestFocus() }, {})
        //    return false
        //
        //} else if (!password!!.matches(alphanumericPattern.toRegex())) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.PasswordInvalidChar), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_password.requestFocus() }, {})
        //    return false
        //
        //} else if (password == username) {
        //    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
        //            resources.getString(R.string.UsernameAndPasswordSame), resources.getString(R.string.Ok),
        //            "", false, false,
        //            { editText_password.requestFocus() }, {})
        //    return false
        //}

        if (address?.trim().isNullOrEmpty() || latitude == null || longitude == null) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.InvalidAddress), resources.getString(R.string.Ok),
                    "", false, false,
                    {}, {})
            return false
        }

        if (vSizeId == null || vSizeId == 0) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.VehicleSizeNotSelected), resources.getString(R.string.Ok),
                    "", false, false,
                    {}, {})
            return false
        }

        if (vMakeId == null || vMakeId == 0) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.VehicleMakeNotSelected), resources.getString(R.string.Ok),
                    "", false, false,
                    {}, {})
            return false
        }

        if (vCompId == null || vCompId == 0) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.YouMustSelectCompany), resources.getString(R.string.Ok),
                    "", false, false,
                    {}, {})
            return false
        }

//        if (withCompany && (vCompId == null || vModelId == 0)) {
//            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
//                    resources.getString(R.string.YouMustSelectCompany), resources.getString(R.string.Ok),
//                    "", false, false,
//                    {}, {})
//            return false
//        }

        if (withCompany && companyName.isNullOrEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    getString(R.string.pls_extract_company_name), resources.getString(R.string.Ok),
                    "", false, false,
                    {}, {})
            return false
        }

        if (!chk_i_agree.isChecked) {
            UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                    resources.getString(R.string.YouMustAgreeToTermsAndConditions), resources.getString(R.string.Ok),
                    "", false, false,
                    { chk_i_agree.requestFocus() }, {})
            return false
        }

        return true
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ListVehicleMakeListVehicleCompanyBackground : AsyncTask<Void, Void, JSONObject>() {
        internal var jsonObjectVehicleSize: JSONObject? = null
        internal var jsonObjectVehicleMake: JSONObject? = null
        internal var jsonObjectVehicleCompany: JSONObject? = null

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@RegisterActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParserVehicleSize = JsonParser()
            val jsonParserVehicleMake = JsonParser()
            val jsonParserVehicleCompany = JsonParser()

            val params = HashMap<String, String>()
            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")!!

            jsonObjectVehicleSize = jsonParserVehicleSize.makeHttpRequest(Constants.BASE_URL + "driver/get_vehicle_size", "POST", params)
            jsonObjectVehicleMake = jsonParserVehicleMake.makeHttpRequest(Constants.BASE_URL + "driver/get_vehicle_model", "POST", params)
            jsonObjectVehicleCompany = jsonParserVehicleCompany.makeHttpRequest(Constants.BASE_URL + "driver/get_vehicle_company", "POST", params)

            return jsonObjectVehicleCompany
        }

        override fun onPostExecute(jsonObject: JSONObject) {
            UtilityFunctions.dismissProgressDialog()

            if (jsonObjectVehicleSize != null) {
                try {
                    if (jsonObjectVehicleSize!!.getBoolean("status")) {
                        val vehicleSizeArray = jsonObjectVehicleSize!!.getJSONArray("data")

                        if (vehicleSizeArray.length() > 0) {
                            vehicleSizeIdList = ArrayList()
                            vehicleSizeDataList = ArrayList()
                            vehicleSizeIdList.add(0)
                            vehicleSizeDataList.add(resources.getString(R.string.SelectSize))

                            for (i in 0 until vehicleSizeArray.length()) {
                                if (Integer.parseInt(vehicleSizeArray.getJSONObject(i).getString("id").trim()) > 0) {
                                    vehicleSizeIdList.add(vehicleSizeArray.getJSONObject(i).getString("id").toInt())
                                    vehicleSizeDataList.add(vehicleSizeArray.getJSONObject(i).getString("size").trim())
                                }
                            }
                            vehicleSizeAdapter = MySpinnerAdapter(this@RegisterActivity, android.R.layout.select_dialog_item, vehicleSizeDataList)
                            spnr_vehicleSize.adapter = vehicleSizeAdapter
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            if (jsonObjectVehicleMake != null) {
                try {
                    if (jsonObjectVehicleMake!!.getBoolean("status")) {
                        val vehicleMakeArray = jsonObjectVehicleMake!!.getJSONArray("data")

                        if (vehicleMakeArray.length() > 0) {
                            vehicleMakeIdList = ArrayList()
                            vehicleMakeDataList = ArrayList()
                            vehicleMakeIdList.add(0)
                            vehicleMakeDataList.add(resources.getString(R.string.SelectMake))

                            for (i in 0 until vehicleMakeArray.length()) {
                                if (Integer.parseInt(vehicleMakeArray.getJSONObject(i).getString("id").trim()) > 0) {
                                    vehicleMakeIdList.add(vehicleMakeArray.getJSONObject(i).getString("id").toInt())
                                    vehicleMakeDataList.add(vehicleMakeArray.getJSONObject(i).getString("model").trim())
                                }
                            }
                            vehicleMakeAdapter = MySpinnerAdapter(this@RegisterActivity, android.R.layout.select_dialog_item, vehicleMakeDataList)
                            spnr_vehicleMake.adapter = vehicleMakeAdapter
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            if (jsonObjectVehicleCompany != null) {
                try {
                    if (jsonObjectVehicleCompany!!.getBoolean("status")) {
                        val vehicleCompanyArray = jsonObjectVehicleCompany!!.getJSONArray("data")

                        if (vehicleCompanyArray.length() > 0) {
                            vehicleCompanyIdList = ArrayList()
                            vehicleCompanyDataList = ArrayList()
                            vehicleCompanyIdList.add(0)
                            vehicleCompanyDataList.add(resources.getString(R.string.SelectCompany))

                            for (i in 0 until vehicleCompanyArray.length()) {
                                if (Integer.parseInt(vehicleCompanyArray.getJSONObject(i).getString("id").trim()) > 0) {
                                    vehicleCompanyIdList.add(vehicleCompanyArray.getJSONObject(i).getString("id").toInt())
                                    vehicleCompanyDataList.add(vehicleCompanyArray.getJSONObject(i).getString("company_name").trim())
                                }
                            }
                            vehicleCompanyAdapter = MySpinnerAdapter(this@RegisterActivity, android.R.layout.select_dialog_item, vehicleCompanyDataList)
                            spnr_withCompany.adapter = vehicleCompanyAdapter
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private inner class MySpinnerAdapter internal constructor(context: Context, resource: Int, items: List<String>) : ArrayAdapter<String>(context, resource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent) as TextView
            if (Build.VERSION.SDK_INT < 23) {
                view.setTextAppearance(this@RegisterActivity, R.style.FontSizeFourteen)
            } else {
                view.setTextAppearance(R.style.FontSizeFourteen)
            }
            view.gravity = Gravity.CENTER
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent) as TextView
            if (Build.VERSION.SDK_INT < 23) {
                view.setTextAppearance(this@RegisterActivity, R.style.FontSizeFourteen)
            } else {
                view.setTextAppearance(R.style.FontSizeFourteen)
            }
            return view
        }
    }

    //Vehicle model spinner hidden

//    @SuppressLint("StaticFieldLeak")
//    private inner class ListVehicleModelBackground : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            UtilityFunctions.showProgressDialog (this@RegisterActivity)
//        }
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["ArgVmoVMId"] = vMakeId.toString() + ""
//            params["ArgVmoVsId"] = vSizeId.toString() + ""
//
//            var BASE_URL = Constants.BASE_URL_EN + "ListVehicleModel"
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//                BASE_URL = Constants.BASE_URL_AR + "ListVehicleModel"
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
//                        val vehicleModelArray = response.getJSONArray("data")
//
//                        if (vehicleModelArray.length() > 0) {
//                            vehicleModelIdList = ArrayList()
//                            vehicleModelDataList = ArrayList()
//                            vehicleModelIdList.add(0)
//                            vehicleModelDataList.add(resources.getString(R.string.SelectModel))
//
//                            for (i in 0 until vehicleModelArray.length()) {
//                                if (Integer.parseInt(vehicleModelArray.getJSONObject(i).getString("VmoId").trim()) > 0) {
//                                    vehicleModelIdList.add(vehicleModelArray.getJSONObject(i).getString("VmoId").toInt())
//                                    vehicleModelDataList.add(vehicleModelArray.getJSONObject(i).getString("VmoName").trim())
//                                }
//                            }
//                            vehicleModelAdapter = MySpinnerAdapter(this@RegisterActivity, android.R.layout.select_dialog_item, vehicleModelDataList)
//                            spnr_vehicleModel.adapter = vehicleModelAdapter
//                        }
//                    }
//                } catch (e: JSONException) { e.printStackTrace() }
//            }
//        }
//    }

//    @SuppressLint("StaticFieldLeak")
//    private inner class CheckUserIDOrMobileNoExistBackground /*internal constructor(internal var isUsername: Boolean?)*/ : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            //if (isUsername!!) {
//            //    params["ArgUserName"] = username!!
//            //} else {
//            //}
//            params["ArgUserName"] = mobile!!
//
//            var BASE_URL = Constants.BASE_URL_EN + "CheckUserIDOrMobileNoExist"
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//                BASE_URL = Constants.BASE_URL_AR + "CheckUserIDOrMobileNoExist"
//            }
//
//            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
//        }
//
//        override fun onPostExecute(response: JSONObject?) {
//
//            editText_mobile.isEnabled = true
//            countryCodePicker.isEnabled = true
//            //editText_password.isEnabled = true
//            editText_name.isEnabled = true
//            editText_email.isEnabled = true
//            chk_i_agree.isEnabled = true
//            btn_register.isEnabled = true
//            button_address.isEnabled = true
//            //editText_username.isEnabled = true
//
//            if (response != null) {
//                try {
//                    if (response.getBoolean("status")) {
//                        UtilityFunctions.dismissProgressDialog()
//                        var msg = ""
//
//                        //if (isUsername!!) {
//                        //    editText_mobile.isEnabled = false
//                        //    countryCodePicker.isEnabled = false
//                        //    editText_password.isEnabled = false
//                        //    editText_name.isEnabled = false
//                        //    editText_email.isEnabled = false
//                        //    chk_i_agree.isEnabled = false
//                        //    btn_register.isEnabled = false
//                        //    button_address.isEnabled = false
//                        //
//                        //    msg = resources.getString(R.string.DuplicateUsername)
//                        //
//                        //} else {
//                        //    editText_password.isEnabled = false
//                        //    editText_username.isEnabled = false
//                        editText_name.isEnabled = false
//                        editText_email.isEnabled = false
//                        chk_i_agree.isEnabled = false
//                        btn_register.isEnabled = false
//                        button_address.isEnabled = false
//
//                        msg = resources.getString(R.string.DuplicateMobile)
//                        //}
//
//                        UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
//                                msg, resources.getString(R.string.Ok),
//                                "", false, false, {}, {})
//
//                    } else {
//                        //if (isUsername!!) {
//                        //    CheckUserIDOrMobileNoExistBackground(false).execute()
//                        //} else {
//                            UtilityFunctions.dismissProgressDialog()
//
//                            val intent = Intent(this@RegisterActivity, UploadDocsActivity::class.java)
//                            registerUser = RegisterUser()
//                            registerUser.name = name
//                            registerUser.nameArabic = name
//                            registerUser.mobile = mobile
//                            registerUser.mail = mail
//                            registerUser.address = address
//                            //registerUser.username = username
//                            //registerUser.password = password
//                            registerUser.latitude = latitude.toString()
//                            registerUser.longitude = longitude.toString()
//                            registerUser.loginMethod = Constants.LOG_CONST_NORMAL
//                            registerUser.vModelId = vModelId.toString()
//                            registerUser.vModelName = vModelName
//
//                            if (withCompany) {
//                                registerUser.vCompId = vCompId.toString()
//                                registerUser.vCompName = vCompName
//                            } else {
//                                registerUser.vCompId = "1"
//                                registerUser.vCompName = resources.getString(R.string.SelectCompany)
//                            }
//
//                            registerUser.licenseNo = ""
//                            registerUser.licenseNoArabic = ""
//                            registerUser.withGlass = withGlass
//
//                            intent.putExtra("registerUser", registerUser)
//                            startActivity(intent)
//                        //}
//                    }
//                } catch (e: JSONException) {
//                    UtilityFunctions.dismissProgressDialog()
//                    e.printStackTrace()
//                }
//
//            } else {
//                UtilityFunctions.dismissProgressDialog()
//                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
//                snackbar.show()
//            }
//        }
//    }

    private inner class InsertDriverData : AsyncTask<Void, Void, JSONObject?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog(this@RegisterActivity)
        }

        override fun doInBackground(vararg p0: Void?): JSONObject? {

            val jsonParser = JsonParser()
            val params = HashMap<String, String>()
            params["lang"] = sharedPreferences.getString(Constants.PREFS_LANG, "en")!!
            params["user_id"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")!!
            params["name"] = name!!
            params["email"] = mail!!
            params["address"] = address!!
            params["size_id"] = vSizeId!!.toString()
            params["type_id"] = vMakeId!!.toString()
            params["company_id"] = vCompId!!.toString()
            params["vehicle_number"] = vehicleNumber!!
            if (withCompany) {
                params["is_under_company"] = "1"
            } else {
                params["is_under_company"] = "0"
            }
            params["company_name"] = companyName!!
            if (withGlass) {
                params["is_work_with_glass_factory"] = "1"
            } else {
                params["is_work_with_glass_factory"] = "0"
            }

            return jsonParser.makeHttpRequest(Constants.BASE_URL + "driver/insert_driver_data", "POST", params)
        }

        override fun onPostExecute(result: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()
            if (result != null) {
                if (result.getBoolean("status")) {

                    sharedPreferences!!.edit().putString(PREFS_REGSTD_STATUS, "2").commit()
                    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                            getString(R.string.data_inserted_will_get_notification), resources.getString(R.string.Ok).toString(),
                            resources.getString(R.string.Cancel).toString(), true, setCancelable = false, actionOk = {
                        finish()
                    }, actionCancel = {})


                } else {
                    UtilityFunctions.showAlertOnActivity(this@RegisterActivity,
                            result.getString("message"), resources.getString(R.string.Ok).toString(),
                            "", false, true, {}, {})
                }

            }
        }
    }

}
