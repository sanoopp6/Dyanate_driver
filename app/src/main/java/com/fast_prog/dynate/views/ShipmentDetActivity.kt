package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.extensions.hideKeyboard
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.models.Ride
import com.fast_prog.dynate.utilities.*
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import kotlinx.android.synthetic.main.activity_shipment_det.*
import kotlinx.android.synthetic.main.content_shipment_det.*
import net.alhazmy13.hijridatepicker.date.gregorian.GregorianDatePickerDialog
import net.alhazmy13.hijridatepicker.date.hijri.HijriDatePickerDialog
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ShipmentDetActivity : AppCompatActivity(), GregorianDatePickerDialog.OnDateSetListener, HijriDatePickerDialog.OnDateSetListener  {

    internal lateinit var sharedPreferences: SharedPreferences

    private lateinit var orderList: List<Order>

    //internal lateinit var vehicleSizeArray: JSONArray
    //internal lateinit var vehicleSizeDataList: MutableList<String>
    //internal lateinit var vehicleSizeIdList: MutableList<String>
    //internal lateinit var vehicleSizeAdapter: ArrayAdapter<String>

    internal var runThread: Boolean? = null

    private lateinit var gpsTracker: GPSTracker

    private var clickedImg: String = ""

    private var dateTimeUpdate: Thread? = null

    private var simpleDateFormat1 = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
    private val simpleDateFormat2 = SimpleDateFormat("yyyy/MM/dd hh:mm aa", Locale.ENGLISH)
    private val simpleDateFormat3 = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)

    private val PICK_CONTACT = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_det)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        customTitle(resources.getString(R.string.ShipmentDetails))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        orderList = ArrayList()

        try {
            Ride.instance
        } catch (e: Exception) {
            Ride.instance = Ride()
        }

        countryCodePicker_from.registerCarrierNumberEditText(edit_from_mobile)
        countryCodePicker_to.registerCarrierNumberEditText(edit_to_mobile)

        //spnr_veh_size.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        //    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        //        val index = parent.selectedItemPosition
        //
        //        Ride.instance.vehicleSizeId = vehicleSizeIdList[index]
        //        Ride.instance.vehicleSizeName = vehicleSizeDataList[index]
        //    }
        //
        //    override fun onNothingSelected(parent: AdapterView<*>) {}
        //}
        //
        //if (ConnectionDetector.isConnected(applicationContext)) {
        //    ListVehicleSizeBackground().execute()
        //} else {
        //    ConnectionDetector.errorSnackbar(coordinator_layout)
        //}
        //
        //spnr_veh_size.setOnTouchListener { v, event ->
        //    hideKeyboard()
        //    false
        //}

        edit_subject.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val subject = edit_subject.text.toString().trim()

                if (subject.isEmpty()) {
                    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                            resources.getString(R.string.InvalidSubject), resources.getString(R.string.Ok),
                            "", false, false, {}, {})
                } else {
                    Ride.instance.subject = subject
                }
            }
        }

        edit_shipment.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val shipment = edit_shipment.text.toString().trim()

                if (shipment.isEmpty()) {
                    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                            resources.getString(R.string.InvalidShipment), resources.getString(R.string.Ok),
                            "", false, false, {}, {})
                } else {
                    Ride.instance.shipment = shipment
                }
            }
        }

        edit_from_name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val name = edit_from_name.text.toString().trim()

                if (name.isEmpty()) {
                    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                            resources.getString(R.string.InvalidSenderName), resources.getString(R.string.Ok),
                            "", false, false, {}, {})
                } else {
                    Ride.instance.fromName = name
                }
            }
        }

        edit_from_mobile.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val mobileNo = countryCodePicker_from.selectedCountryCodeWithPlus + edit_from_mobile.text.removePrefix("0")

                if (!countryCodePicker_from.isValidFullNumber) {
                    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                            resources.getString(R.string.InvalidSenderMob), resources.getString(R.string.Ok),
                            "", false, false, {}, {})
                } else {
                    Ride.instance.fromMobile = mobileNo
                }
            }
        }

        img_from_mobile.setOnClickListener {
            clickedImg = "sender"

            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT)
        }

        edit_to_name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val name = edit_to_name.text.toString().trim()

                if (name.isEmpty()) {
                    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                            resources.getString(R.string.InvalidReceiverName), resources.getString(R.string.Ok),
                            "", false, false, {}, {})
                } else {
                    Ride.instance.toName = name
                }
            }
        }

        edit_to_mobile.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val mobileNo = countryCodePicker_to.selectedCountryCodeWithPlus + edit_to_mobile.text.removePrefix("0")

                if (!countryCodePicker_to.isValidFullNumber) {
                    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                            resources.getString(R.string.InvalidReceiverMob), resources.getString(R.string.Ok),
                            "", false, false, {}, {})
                } else {
                    Ride.instance.toMobile = mobileNo
                }
            }
        }

        img_to_mobile.setOnClickListener {
            clickedImg = "receiver"

            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT)
        }

        gpsTracker = GPSTracker(this@ShipmentDetActivity)

        edit_subject.setText(Ride.instance.shipment)
        edit_shipment.setText(Ride.instance.subject)
        txt_datepicker1.text = Ride.instance.date
        txt_datepicker2.text = Ride.instance.hijriDate
        txt_timepicker.text = Ride.instance.time
        edit_from_name.setText(Ride.instance.fromName)
        if (Ride.instance.fromMobile.isNotEmpty()) countryCodePicker_from.fullNumber = Ride.instance.fromMobile
        edit_to_name.setText(Ride.instance.toName)
        if (Ride.instance.toMobile.isNotEmpty())countryCodePicker_to.fullNumber = Ride.instance.toMobile

        val newCalendar = Calendar.getInstance()

        txt_datepicker1.setOnClickListener { v ->
            hideKeyboard()

            val now = Calendar.getInstance()
            val gregorianDatePickerDialog = GregorianDatePickerDialog.newInstance(this@ShipmentDetActivity,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH))
            gregorianDatePickerDialog.minDate = now
            gregorianDatePickerDialog.setVersion(GregorianDatePickerDialog.Version.VERSION_2)
            //now.add(Calendar.YEAR, 100);
            //gregorianDatePickerDialog.setMaxDate(now);
            gregorianDatePickerDialog.show(fragmentManager, "GregorianDatePickerDialog")
        }

        txt_datepicker2.setOnClickListener { v ->
            hideKeyboard()

            val now = UmmalquraCalendar()
            val hijriDatePickerDialog = HijriDatePickerDialog.newInstance(this@ShipmentDetActivity,
                    now.get(UmmalquraCalendar.YEAR),
                    now.get(UmmalquraCalendar.MONTH),
                    now.get(UmmalquraCalendar.DAY_OF_MONTH))
            hijriDatePickerDialog.minDate = now
            hijriDatePickerDialog.setVersion(HijriDatePickerDialog.Version.VERSION_2)
            hijriDatePickerDialog.show(fragmentManager, "HijriDatePickerDialog")
        }

        txt_timepicker.setOnClickListener { v ->
            hideKeyboard()

            val toDatePickerDialog = TimePickerDialog(this@ShipmentDetActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val AM_PM: String
                var time: String

                if (hourOfDay < 12) {
                    AM_PM = "AM"
                } else {
                    AM_PM = "PM"
                }

                if (hourOfDay < 10) {
                    time = "0$hourOfDay:"
                } else {
                    time = hourOfDay.toString() + ":"
                }

                if (minute < 10) {
                    time += "0$minute $AM_PM"
                } else {
                    time += minute.toString() + " " + AM_PM
                }

                txt_timepicker.text = time
                Ride.instance.time = time
            }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false)

            toDatePickerDialog.show()
        }

        btn_book_vehicle.setOnClickListener {
            hideKeyboard()

            if (validate()) {
                startActivity(Intent(this@ShipmentDetActivity, ConfirmFromToActivity::class.java))
            }
        }
    }

    override fun onDateSet(view: HijriDatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var monthOfYear = monthOfYear
        monthOfYear += 1
        var dateString: String

        if (monthOfYear < 10)
            dateString = year.toString() + "/0" + monthOfYear
        else
            dateString = year.toString() + "/" + monthOfYear

        if (dayOfMonth < 10)
            dateString += "/0" + dayOfMonth
        else
            dateString += "/" + dayOfMonth

        if (ConnectionDetector.isConnected(this@ShipmentDetActivity)) {
            GetDateBackground(false, dateString).execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    override fun onDateSet(view: GregorianDatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var monthOfYear = monthOfYear
        monthOfYear += 1
        var dateString: String

        if (monthOfYear < 10)
            dateString = year.toString() + "/0" + monthOfYear
        else
            dateString = year.toString() + "/" + monthOfYear

        if (dayOfMonth < 10)
            dateString += "/0" + dayOfMonth
        else
            dateString += "/" + dayOfMonth

        if (ConnectionDetector.isConnected(this@ShipmentDetActivity)) {
            GetDateBackground(true, dateString).execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    override fun onPause() {
        super.onPause()

        if (dateTimeUpdate != null) {
            runThread = false
            dateTimeUpdate!!.interrupt()
        }
    }

    override fun onResume() {
        super.onResume()

        runThread = true

        dateTimeUpdate = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted && runThread!!) {
                        runOnUiThread { dateTimeUpdateTextView() }
                        Thread.sleep(10000)
                    }
                } catch (ignored: InterruptedException) {
                }

            }
        }
        dateTimeUpdate!!.start()
    }

    private fun dateTimeUpdateTextView() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 5)
        val date = cal.time
        var temp: String
        val getMyDateTime = txt_datepicker1.text.toString().trim()

        if (getMyDateTime.isNotEmpty()) {
            var getMyDate: Date? = null

            try {
                getMyDate = simpleDateFormat1.parse(getMyDateTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (getMyDate!!.before(date) || getMyDate == date) {
                temp = simpleDateFormat3.format(date)
                Ride.instance.time = temp
                txt_timepicker.text = temp
                temp = simpleDateFormat1.format(date)

                if (ConnectionDetector.isConnected(this@ShipmentDetActivity)) {
                    GetDateBackground(true, temp).execute()
                }
            }
        } else {
            temp = simpleDateFormat3.format(date)
            Ride.instance.time = temp
            txt_timepicker.text = temp
            temp = simpleDateFormat1.format(date)

            if (ConnectionDetector.isConnected(this@ShipmentDetActivity)) {
                GetDateBackground(true, temp).execute()
            }
        }
    }

    private fun validate(): Boolean {
        val subject = edit_subject.text.toString()
        val shipment = edit_shipment.text.toString()
        val fromName = edit_from_name.text.toString().trim()
        val fromNumber = countryCodePicker_from.selectedCountryCodeWithPlus + edit_from_mobile.text.toString().trim().removePrefix("0")
        val dateText1 = txt_datepicker1.text.toString().trim()
        val dateText2 = txt_datepicker2.text.toString().trim()
        val timeText = txt_timepicker.text.toString().trim()
        val toName = edit_to_name.text.toString().trim()
        val toNumber = countryCodePicker_to.selectedCountryCodeWithPlus + edit_to_mobile.text.toString().trim().removePrefix("0")

        //if (Ride.instance.vehicleSizeId.isNullOrEmpty()) {
        //    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
        //            resources.getString(R.string.InvalidVehicleSize), resources.getString(R.string.Ok),
        //            "", false, false, {}, {})
        //    return false
        //}

        if (subject.trim().isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.InvalidSubject), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        if (shipment.trim().isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.InvalidShipment), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        if (fromName.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.InvalidSenderName), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        if (!countryCodePicker_from.isValidFullNumber) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.InvalidSenderMob), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        if (dateText1.isEmpty() || dateText2.isEmpty() || timeText.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.InvalidDate), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        val getMyDateTime = dateText1.toString() + " " + timeText.toString()
        var getCurrentDate: Date? = null
        var getMyDate: Date? = null

        try {
            getCurrentDate = simpleDateFormat2.parse(simpleDateFormat2.format(Date()))
            getMyDate = simpleDateFormat2.parse(getMyDateTime)

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (getMyDate!!.before(getCurrentDate) || getMyDate == getCurrentDate) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.PreviousDateOrTime), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        if (toName.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.InvalidReceiverName), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        if (!countryCodePicker_to.isValidFullNumber) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.InvalidReceiverMob), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
            return false
        }

        Ride.instance.subject = subject
        Ride.instance.shipment = shipment
        Ride.instance.fromName = fromName
        Ride.instance.fromMobile = fromNumber
        Ride.instance.date = dateText1
        Ride.instance.hijriDate = dateText2
        Ride.instance.time = timeText
        Ride.instance.toName = toName
        Ride.instance.toMobile = toNumber
        Ride.instance.vehicleSizeId = sharedPreferences.getString(Constants.PREFS_VMS_ID, "")

        return true
    }

    //@SuppressLint("StaticFieldLeak")
    //private inner class ListVehicleSizeBackground : AsyncTask<Void, Void, JSONObject>() {
    //
    //    override fun onPreExecute() {
    //        super.onPreExecute()
    //        UtilityFunctions.showProgressDialog (this@ShipmentDetActivity)
    //    }
    //
    //    override fun doInBackground(vararg param: Void): JSONObject? {
    //        val jsonParser = JsonParser()
    //        val params = HashMap<String, String>()
    //
    //        var BASE_URL = Constants.BASE_URL_EN + "ListVehicleSize"
    //
    //        if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
    //            BASE_URL = Constants.BASE_URL_AR + "ListVehicleSize"
    //        }
    //
    //        return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
    //    }
    //
    //    override fun onPostExecute(response: JSONObject?) {
    //        UtilityFunctions.dismissProgressDialog()
    //
    //        if (response != null) {
    //            try {
    //                if (response.getBoolean("status")) {
    //                    vehicleSizeArray = response.getJSONArray("data")
    //
    //                    if (vehicleSizeArray.length() > 0) {
    //                        vehicleSizeIdList = ArrayList()
    //                        vehicleSizeDataList = ArrayList()
    //                        var position = 0
    //
    //                        for (i in 0 until vehicleSizeArray.length()) {
    //                            vehicleSizeIdList.add(vehicleSizeArray.getJSONObject(i).getString("VsId").trim())
    //                            vehicleSizeDataList.add(vehicleSizeArray.getJSONObject(i).getString("VsName").trim())
    //
    //                            if (Ride.instance.vehicleSizeId == vehicleSizeArray.getJSONObject(i).getString("VsId").trim()) {
    //                                position = i
    //                            }
    //                        }
    //                        vehicleSizeAdapter = MySpinnerAdapter(this@ShipmentDetActivity, android.R.layout.select_dialog_item, vehicleSizeDataList)
    //                        spnr_veh_size.adapter = vehicleSizeAdapter
    //
    //                        if (!Ride.instance.vehicleSizeId.isNullOrEmpty()) {
    //                            Ride.instance.vehicleSizeId = vehicleSizeArray.getJSONObject(0).getString("VsId").trim()
    //                            Ride.instance.vehicleSizeName = vehicleSizeArray.getJSONObject(0).getString("VsName").trim()
    //                        } else {
    //                            spnr_veh_size.setSelection(position)
    //                        }
    //                    }
    //                }
    //            } catch (e: JSONException) { e.printStackTrace() }
    //        }
    //    }
    //}
    //
    //private inner class MySpinnerAdapter internal constructor(context: Context, resource: Int, items: List<String>) : ArrayAdapter<String>(context, resource, items) {
    //
    //    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    //        val view = super.getView(position, convertView, parent) as TextView
    //        if (Build.VERSION.SDK_INT < 23) {
    //            view.setTextAppearance(this@ShipmentDetActivity, R.style.FontSizeFourteen)
    //        } else {
    //            view.setTextAppearance(R.style.FontSizeFourteen)
    //        }
    //        view.gravity = Gravity.CENTER
    //        return view
    //    }
    //
    //    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    //        val view = super.getDropDownView(position, convertView, parent) as TextView
    //        if (Build.VERSION.SDK_INT < 23) {
    //            view.setTextAppearance(this@ShipmentDetActivity, R.style.FontSizeFourteen)
    //        } else {
    //            view.setTextAppearance(R.style.FontSizeFourteen)
    //        }
    //        return view
    //    }
    //}
    //
    //override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    //    super.onActivityResult(requestCode, resultCode, data)
    //
    //    if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
    //        contactPicked(data)
    //    }
    //}

    private fun contactPicked(data: Intent) {
        var cursor: Cursor? = null
        try {
            var phoneNo: String? = null
            //val name: String? = null
            val uri = data.data

            cursor = contentResolver.query(uri!!, null, null, null, null)
            cursor!!.moveToFirst()

            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            //val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            phoneNo = cursor.getString(phoneIndex).trimStart{ it <= '0'}

            if (clickedImg.equals("sender", ignoreCase = true)) {
                countryCodePicker_from.fullNumber = phoneNo.replace("[^\\d]".toRegex(), "")
            } else {
                countryCodePicker_to.fullNumber = phoneNo.replace("[^\\d]".toRegex(), "")
            }

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getString(R.string.NoMobileNumber), resources.getString(R.string.Ok),
                    "", false, false, {}, {})
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDateBackground internal constructor(internal var isHijri: Boolean?, internal var dateString: String) : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["data"] = dateString

            var BASE_URL = "https://dyanate.fast-prog.com/JBLCalendarWebService.asmx/GetGregorianJson"

            if (isHijri!!) {
                BASE_URL = "https://dyanate.fast-prog.com/JBLCalendarWebService.asmx/GethijiriJson"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        val date = response.getString("data")
                        val curDate = simpleDateFormat1.parse(simpleDateFormat1.format(Date()))
                        var getMyDate: Date? = null

                        if (isHijri!!) {
                            try {
                                getMyDate = simpleDateFormat1.parse(dateString)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }

                            if (getMyDate!!.after(curDate) || getMyDate == curDate) {
                                txt_datepicker1.text = dateString
                                txt_datepicker2.text = date
                                Ride.instance.date = dateString
                                Ride.instance.hijriDate = date
                            }

                        } else {
                            try {
                                getMyDate = simpleDateFormat1.parse(date)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }

                            if (getMyDate!!.after(curDate) || getMyDate == curDate) {
                                txt_datepicker1.text = date
                                txt_datepicker2.text = dateString
                                Ride.instance.date = date
                                Ride.instance.hijriDate = dateString
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
