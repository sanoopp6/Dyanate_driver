package com.fast_prog.dynate.views.removed

//import kotlinx.android.synthetic.main.activity_sender_receiver.*
//import kotlinx.android.synthetic.main.content_sender_receiver.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fast_prog.dynate.R

class SenderReceiverActivity : AppCompatActivity() {
//        , GregorianDatePickerDialog.OnDateSetListener, HijriDatePickerDialog.OnDateSetListener {

//    internal var dateTimeUpdate: Thread? = null
//
//    internal var runThread: Boolean? = null
//
//    internal lateinit var gpsTracker: GPSTracker
//
//    internal lateinit var animationToLeft1: Animation
//    internal lateinit var animationToLeft2: Animation
//
//    internal var clickedImg: String = ""
//
//    private var simpleDateFormat1 = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
//    private val simpleDateFormat2 = SimpleDateFormat("yyyy/MM/dd hh:mm aa", Locale.ENGLISH)
//    private val simpleDateFormat3 = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
//
//    internal lateinit var sharedPreferences: SharedPreferences
//
//    internal val PICK_CONTACT = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sender_receiver)
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
//        customTitle(resources.getString(R.string.SenderReceiverDetails))
//
//        try {
//            Ride.instance
//        } catch (e: Exception) {
//            Ride.instance = Ride()
//        }
//
//        countryCodePicker_from.registerCarrierNumberEditText(edit_from_mobile)
//        countryCodePicker_to.registerCarrierNumberEditText(edit_to_mobile)
//
//        edit_from_name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                val name = edit_from_name.text.toString().trim()
//
//                if (name.isEmpty()) {
//                    UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                            resources.getText(R.string.InvalidSenderName).toString(), resources.getString(R.string.Ok).toString(),
//                            "", false, false, {}, {})
//                } else {
//                    Ride.instance.fromName = name
//                }
//            }
//        }
//
//        edit_from_mobile.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                val mobileNo = countryCodePicker_from.selectedCountryCodeWithPlus + edit_from_mobile.text.removePrefix("0")
//
//                if (!countryCodePicker_from.isValidFullNumber) {
//                    UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                            resources.getText(R.string.InvalidSenderMob).toString(), resources.getString(R.string.Ok).toString(),
//                            "", false, false, {}, {})
//                } else {
//                    Ride.instance.fromMobile = mobileNo
//                }
//            }
//        }
//
//        img_from_mobile.setOnClickListener {
//            clickedImg = "sender"
//
//            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
//            startActivityForResult(intent, PICK_CONTACT)
//        }
//
//        edit_to_name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                val name = edit_to_name.text.toString().trim()
//
//                if (name.isEmpty()) {
//                    UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                            resources.getText(R.string.InvalidReceiverName).toString(), resources.getString(R.string.Ok).toString(),
//                            "", false, false, {}, {})
//                } else {
//                    Ride.instance.toName = name
//                }
//            }
//        }
//
//        edit_to_mobile.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                val mobileNo = countryCodePicker_to.selectedCountryCodeWithPlus + edit_to_mobile.text.removePrefix("0")
//
//                if (!countryCodePicker_to.isValidFullNumber) {
//                    UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                            resources.getText(R.string.InvalidReceiverMob).toString(), resources.getString(R.string.Ok).toString(),
//                            "", false, false, {}, {})
//                } else {
//                    Ride.instance.toMobile = mobileNo
//                }
//            }
//        }
//
//        img_to_mobile.setOnClickListener {
//            clickedImg = "receiver"
//
//            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
//            startActivityForResult(intent, PICK_CONTACT)
//        }
//
//        gpsTracker = GPSTracker(this@SenderReceiverActivity)
//
//        btn_book_vehicle.setOnClickListener {
//            hideSoftKeyboard()
//
//            if (validate()) {
//                gpsTracker = GPSTracker(this@SenderReceiverActivity)
//
//                if (!gpsTracker.canGetLocation()) {
//                    gpsTracker.showSettingsAlert()
//
//                } else {
//                    Ride.instance.isMessage = false
//                    Ride.instance.pickUpLatitude = null
//                    Ride.instance.pickUpLongitude = null
//                    Ride.instance.pickUpLocation = null
//                    Ride.instance.dropOffLatitude = null
//                    Ride.instance.dropOffLongitude = null
//                    Ride.instance.dropOffLongitude = null
//                    startActivity(Intent(this@SenderReceiverActivity, SenderLocationActivity::class.java))
//                }
//            }
//        }
//
//        txt_datepicker1.text = Ride.instance.date
//        txt_datepicker2.text = Ride.instance.hijriDate
//        txt_timepicker.text = Ride.instance.time
//        edit_from_name.setText(Ride.instance.fromName)
//        if (Ride.instance.fromMobile.isNotEmpty()) countryCodePicker_from.fullNumber = Ride.instance.fromMobile
//        edit_to_name.setText(Ride.instance.toName)
//        if (Ride.instance.toMobile.isNotEmpty())countryCodePicker_to.fullNumber = Ride.instance.toMobile
//
//        val newCalendar = Calendar.getInstance()
//
//        txt_datepicker1.setOnClickListener({ v ->
//            hideSoftKeyboard()
//
//            val now = Calendar.getInstance()
//            val gregorianDatePickerDialog = GregorianDatePickerDialog.newInstance(this@SenderReceiverActivity,
//                    now.get(Calendar.YEAR),
//                    now.get(Calendar.MONTH),
//                    now.get(Calendar.DAY_OF_MONTH))
//            gregorianDatePickerDialog.minDate = now
//            gregorianDatePickerDialog.setVersion(GregorianDatePickerDialog.Version.VERSION_2)
//            //now.add(Calendar.YEAR, 100);
//            //gregorianDatePickerDialog.setMaxDate(now);
//            gregorianDatePickerDialog.show(fragmentManager, "GregorianDatePickerDialog")
//        })
//
//        txt_datepicker2.setOnClickListener({ v ->
//            hideSoftKeyboard()
//
//            val now = UmmalquraCalendar()
//            val hijriDatePickerDialog = HijriDatePickerDialog.newInstance(this@SenderReceiverActivity,
//                    now.get(UmmalquraCalendar.YEAR),
//                    now.get(UmmalquraCalendar.MONTH),
//                    now.get(UmmalquraCalendar.DAY_OF_MONTH))
//            hijriDatePickerDialog.minDate = now
//            hijriDatePickerDialog.setVersion(HijriDatePickerDialog.Version.VERSION_2)
//            hijriDatePickerDialog.show(fragmentManager, "HijriDatePickerDialog")
//        })
//
//        txt_timepicker.setOnClickListener({ v ->
//            hideSoftKeyboard()
//
//            val toDatePickerDialog = TimePickerDialog(this@SenderReceiverActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//                val AM_PM: String
//                var time: String
//
//                if (hourOfDay < 12) {
//                    AM_PM = "AM"
//                } else {
//                    AM_PM = "PM"
//                }
//
//                if (hourOfDay < 10) {
//                    time = "0$hourOfDay:"
//                } else {
//                    time = hourOfDay.toString() + ":"
//                }
//
//                if (minute < 10) {
//                    time += "0$minute $AM_PM"
//                } else {
//                    time += minute.toString() + " " + AM_PM
//                }
//
//                txt_timepicker.text = time
//                Ride.instance.time = time
//            }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false)
//
//            toDatePickerDialog.show()
//        })
//
//        val display = windowManager.defaultDisplay
//        val width = display.width
//
//        if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
//            animationToLeft1 = TranslateAnimation(0f, (260 - width).toFloat(), 0f, 0f)
//            animationToLeft2 = TranslateAnimation(0f, (280 - width).toFloat(), 0f, 0f)
//
//        } else {
//            animationToLeft1 = TranslateAnimation(0f, (width - 250).toFloat(), 0f, 0f)
//            animationToLeft2 = TranslateAnimation(0f, (width - 270).toFloat(), 0f, 0f)
//        }
//
//        animationToLeft1.duration = 12000
//        animationToLeft1.repeatMode = Animation.RESTART
//        animationToLeft1.repeatCount = 0
//
//        animationToLeft2.duration = 12000
//        animationToLeft2.repeatMode = Animation.RESTART
//        animationToLeft2.repeatCount = 0
//
//        sender_det_title.animation = animationToLeft1
//
//        animationToLeft1.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {}
//
//            override fun onAnimationEnd(animation: Animation) {
//                animation.cancel()
//                animation.reset()
//                receiver_det_title.startAnimation(animationToLeft2)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })
//
//        animationToLeft2.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {}
//
//            override fun onAnimationEnd(animation: Animation) {
//                animation.cancel()
//                animation.reset()
//                sender_det_title.startAnimation(animationToLeft1)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })
//
//        runThread = true
//
//        dateTimeUpdate = object : Thread() {
//            override fun run() {
//                try {
//                    while (!isInterrupted && runThread!!) {
//                        runOnUiThread { dateTimeUpdateTextView() }
//                        Thread.sleep(10000)
//                    }
//                } catch (ignored: InterruptedException) {
//                }
//
//            }
//        }
//        dateTimeUpdate!!.start()
//    }
//
//    override fun onDateSet(view: HijriDatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
//        var monthOfYear = monthOfYear
//        monthOfYear += 1
//        var dateString: String
//
//        if (monthOfYear < 10)
//            dateString = year.toString() + "/0" + monthOfYear
//        else
//            dateString = year.toString() + "/" + monthOfYear
//
//        if (dayOfMonth < 10)
//            dateString += "/0" + dayOfMonth
//        else
//            dateString += "/" + dayOfMonth
//
//        if (ConnectionDetector.isConnected(this@SenderReceiverActivity)) {
//            GetDateBackground(false, dateString).execute()
//        } else {
//            ConnectionDetector.errorSnackbar(coordinator_layout)
//        }
//    }
//
//    override fun onDateSet(view: GregorianDatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
//        var monthOfYear = monthOfYear
//        monthOfYear += 1
//        var dateString: String
//
//        if (monthOfYear < 10)
//            dateString = year.toString() + "/0" + monthOfYear
//        else
//            dateString = year.toString() + "/" + monthOfYear
//
//        if (dayOfMonth < 10)
//            dateString += "/0" + dayOfMonth
//        else
//            dateString += "/" + dayOfMonth
//
//        if (ConnectionDetector.isConnected(this@SenderReceiverActivity)) {
//            GetDateBackground(true, dateString).execute()
//        } else {
//            ConnectionDetector.errorSnackbar(coordinator_layout)
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//
//        if (dateTimeUpdate != null) {
//            runThread = false
//            dateTimeUpdate!!.interrupt()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        runThread = true
//
//        dateTimeUpdate = object : Thread() {
//            override fun run() {
//                try {
//                    while (!isInterrupted && runThread!!) {
//                        runOnUiThread { dateTimeUpdateTextView() }
//                        Thread.sleep(10000)
//                    }
//                } catch (ignored: InterruptedException) {
//                }
//
//            }
//        }
//        dateTimeUpdate!!.start()
//    }
//
//    private fun dateTimeUpdateTextView() {
//        val cal = Calendar.getInstance()
//        cal.add(Calendar.MINUTE, 5)
//        val date = cal.time
//        var temp: String
//        val getMyDateTime = txt_datepicker1.text.toString().trim()
//
//        if (getMyDateTime.isNotEmpty()) {
//            var getMyDate: Date? = null
//
//            try {
//                getMyDate = simpleDateFormat1.parse(getMyDateTime)
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
//
//            if (getMyDate!!.before(date) || getMyDate == date) {
//                temp = simpleDateFormat3.format(date)
//                Ride.instance.time = temp
//                txt_timepicker.text = temp
//                temp = simpleDateFormat1.format(date)
//
//                if (ConnectionDetector.isConnected(this@SenderReceiverActivity)) {
//                    GetDateBackground(true, temp).execute()
//                }
//            }
//        } else {
//            temp = simpleDateFormat3.format(date)
//            Ride.instance.time = temp
//            txt_timepicker.text = temp
//            temp = simpleDateFormat1.format(date)
//
//            if (ConnectionDetector.isConnected(this@SenderReceiverActivity)) {
//                GetDateBackground(true, temp).execute()
//            }
//        }
//    }
//
//    private fun validate(): Boolean {
//        val fromName = edit_from_name.text.toString().trim()
//        val fromNumber = countryCodePicker_from.selectedCountryCodeWithPlus + edit_from_mobile.text.toString().trim().removePrefix("0")
//        val dateText1 = txt_datepicker1.text.toString().trim()
//        val dateText2 = txt_datepicker2.text.toString().trim()
//        val timeText = txt_timepicker.text.toString().trim()
//        val toName = edit_to_name.text.toString().trim()
//        val toNumber = countryCodePicker_to.selectedCountryCodeWithPlus + edit_to_mobile.text.toString().trim().removePrefix("0")
//
//        if (fromName.isEmpty()) {
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getText(R.string.InvalidSenderName).toString(), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//            return false
//        }
//
//        if (!countryCodePicker_from.isValidFullNumber) {
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getText(R.string.InvalidSenderMob).toString(), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//            return false
//        }
//
//        if (dateText1.isEmpty() || dateText2.isEmpty() || timeText.isEmpty()) {
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getText(R.string.InvalidDate).toString(), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//            return false
//        }
//
//        val getMyDateTime = dateText1.toString() + " " + timeText.toString()
//        var getCurrentDate: Date? = null
//        var getMyDate: Date? = null
//
//        try {
//            getCurrentDate = simpleDateFormat2.parse(simpleDateFormat2.format(Date()))
//            getMyDate = simpleDateFormat2.parse(getMyDateTime)
//
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//
//        if (getMyDate!!.before(getCurrentDate) || getMyDate == getCurrentDate) {
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getText(R.string.PreviousDateOrTime).toString(), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//            return false
//        }
//
//        if (toName.isEmpty()) {
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getText(R.string.InvalidReceiverName).toString(), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//            return false
//        }
//
//        if (!countryCodePicker_to.isValidFullNumber) {
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getText(R.string.InvalidReceiverMob).toString(), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//            return false
//        }
//
//        if (fromNumber.equals(toNumber, true)) {
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getText(R.string.FromMobileAndToMobileSame).toString(), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//            return false
//        }
//
//        Ride.instance.isFromSelf = false
//        Ride.instance.fromName = fromName
//        Ride.instance.fromMobile = fromNumber
//        Ride.instance.date = dateText1
//        Ride.instance.hijriDate = dateText2
//        Ride.instance.time = timeText
//        Ride.instance.isToSelf = false
//        Ride.instance.toName = toName
//        Ride.instance.toMobile = toNumber
//        Ride.instance.isMessage = false
//
//        return true
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private inner class GetDateBackground internal constructor(internal var isHijri: Boolean?, internal var dateString: String) : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["data"] = dateString
//
//            var BASE_URL = "https://dyanate.fast-prog.com/JBLCalendarWebService.asmx/GetGregorianJson"
//
//            if (isHijri!!) {
//                BASE_URL = "https://dyanate.fast-prog.com/JBLCalendarWebService.asmx/GethijiriJson"
//            }
//
//            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
//        }
//
//        override fun onPostExecute(response: JSONObject?) {
//            if (response != null) {
//                try {
//                    if (response.getBoolean("status")) {
//                        val date = response.getString("data")
//                        val curDate = simpleDateFormat1.parse(simpleDateFormat1.format(Date()))
//                        var getMyDate: Date? = null
//
//                        if (isHijri!!) {
//                            try {
//                                getMyDate = simpleDateFormat1.parse(dateString)
//                            } catch (e: ParseException) {
//                                e.printStackTrace()
//                            }
//
//                            if (getMyDate!!.after(curDate) || getMyDate == curDate) {
//                                txt_datepicker1.text = dateString
//                                txt_datepicker2.text = date
//                                Ride.instance.hijriDate = date
//                                Ride.instance.date = dateString
//                            }
//
//                        } else {
//                            try {
//                                getMyDate = simpleDateFormat1.parse(date)
//                            } catch (e: ParseException) {
//                                e.printStackTrace()
//                            }
//
//                            if (getMyDate!!.after(curDate) || getMyDate == curDate) {
//                                txt_datepicker1.text = date
//                                txt_datepicker2.text = dateString
//                                Ride.instance.hijriDate = dateString
//                                Ride.instance.date = date
//                            }
//                        }
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                } catch (e: ParseException) {
//                    e.printStackTrace()
//                }
//
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
//            contactPicked(data)
//        }
//    }
//
//    private fun contactPicked(data: Intent) {
//        var cursor: Cursor? = null
//        try {
//            var phoneNo: String? = null
//            val name: String? = null
//            // getData() method will have the Content Uri of the selected contact
//            val uri = data.data
//            //Query the content uri
//            cursor = contentResolver.query(uri!!, null, null, null, null)
//            cursor!!.moveToFirst()
//            // column index of the phone number
//            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//            // column index of the contact name
//            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
//            phoneNo = cursor.getString(phoneIndex)
//            //name = cursor.getString(nameIndex);
//            //fromMobile.setText(phoneNo.replaceAll("\\D+",""));
//
//            if (clickedImg.equals("sender", ignoreCase = true)) {
//                countryCodePicker_from.fullNumber = phoneNo.replace("[^\\d]".toRegex(), "")
//            } else {
//                countryCodePicker_to.fullNumber = phoneNo.replace("[^\\d]".toRegex(), "")
//            }
//
//            cursor.close()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            UtilityFunctions.showAlertOnActivity(this@SenderReceiverActivity,
//                    resources.getString(R.string.NoMobileNumber), resources.getString(R.string.Ok).toString(),
//                    "", false, false, {}, {})
//        }

    }

}
