package com.fast_prog.dynate.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.utilities.*
import kotlinx.android.synthetic.main.activity_admin_home.*
import kotlinx.android.synthetic.main.content_admin_home.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AdminHomeActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var permissionsList: MutableList<String>

    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        customTitle(resources.getString(R.string.Home))

        val MyVersion = Build.VERSION.SDK_INT

        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            permissionsList = ArrayList()

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission()
            }
        }

        if (ConnectionDetector.isConnected(this@AdminHomeActivity)) {
            if (sharedPreferences.getString(Constants.PREFS_FCM_TOKEN, "").isNotEmpty()) {
                Log.e("refreshedToken", sharedPreferences.getString(Constants.PREFS_FCM_TOKEN, ""))
                UpdateFCMToken(this@AdminHomeActivity, true, sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute()
            }
        }

        textView_nav_name.text = String.format("%s : %s", resources.getString(R.string.Welcome), sharedPreferences.getString(Constants.PREFS_USER_NAME, ""))

        layout_registrations.setOnClickListener {
            val intent = Intent(this@AdminHomeActivity, DriverListActivity::class.java)
            intent.putExtra("verified", false)
            startActivity(intent)
        }

        layout_feedbacks.setOnClickListener {
            startActivity(Intent(this@AdminHomeActivity, ShowFeedbackListActivity::class.java))
        }

        layout_settings.setOnClickListener {
            startActivity(Intent(this@AdminHomeActivity, SettingsActivity::class.java))
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class IsAppLiveBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgAppPackageName"] = Constants.APP_NAME
            params["ArgAppVersionNo"] = Constants.APP_VERSION

            return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "IsAppLive", "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (!response.getBoolean("status")) {
                        ActivityCompat.finishAffinity(this@AdminHomeActivity)
                        val intent = Intent(this@AdminHomeActivity, UpdateActivity::class.java)
                        intent.putExtra("message", response.getString("data"))
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }

    override fun onBackPressed() {
        UtilityFunctions.showAlertOnActivity(this@AdminHomeActivity,
                resources.getString(R.string.DoYouWantToExit), resources.getString(R.string.Yes),
                resources.getString(R.string.No), true, false,
                {
                    ActivityCompat.finishAffinity(this@AdminHomeActivity)
                    finish()
                }, {})
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.exit_option) {
            UtilityFunctions.showAlertOnActivity(this@AdminHomeActivity,
                    resources.getString(R.string.AreYouSure), resources.getString(R.string.Yes),
                    resources.getString(R.string.No), true, false,
                    {
                        val editor = sharedPreferences.edit()

                        if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "")!!.equals("online", true)) {
                            editor.putString(Constants.PREFS_ONLINE_STATUS, "offline")
                            SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")!!).execute()
                        }

                        editor.putBoolean(Constants.PREFS_IS_LOGIN, false)
                        editor.putString(Constants.PREFS_USER_ID, "0")
                        editor.putString(Constants.PREFS_CUST_ID, "0")
                        editor.putString(Constants.PREFS_USER_NAME, "0")
                        editor.putString(Constants.PREFS_USER_MOBILE, "")
                        editor.putString(Constants.PREFS_SHARE_URL, "")
                        editor.putString(Constants.PREFS_LATITUDE, "")
                        editor.putString(Constants.PREFS_LONGITUDE, "")
                        editor.putString(Constants.PREFS_USER_CONSTANT, "")
                        //editor.putString(Constants.PREFS_IS_FACTORY, "")
                        editor.commit()

                        val intent = Intent(this@AdminHomeActivity, NoLoginActivity::class.java)
                        ActivityCompat.finishAffinity(this@AdminHomeActivity)
                        startActivity(intent)
                        finish()

                    }, {})
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        var result = true

        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permission5 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val permission6 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        val permission7 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (!(permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
            result = false
        }

        if (!(permission3 == PackageManager.PERMISSION_GRANTED && permission4 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result = false
        }

        if (!(permission5 == PackageManager.PERMISSION_GRANTED && permission6 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.READ_SMS)
            result = false
        }

        if (permission7 != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA)
            result = false
        }

        return result
    }

    private fun requestForSpecificPermission() {
        val stringArr = permissionsList.toTypedArray<String>()
        ActivityCompat.requestPermissions(this@AdminHomeActivity, stringArr, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> {
                }
                else -> finish()
            }//granted
        //System.exit(0);
        //not granted
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onResume() {
        super.onResume()

        if (ConnectionDetector.isConnected(this@AdminHomeActivity)) {
            IsAppLiveBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

//    @SuppressLint("StaticFieldLeak")
//    private inner class HomeScreenCountBackground : AsyncTask<Void, Void, JSONObject>() {
//
//        override fun doInBackground(vararg param: Void): JSONObject? {
//            val jsonParser = JsonParser()
//            val params = HashMap<String, String>()
//
//            params["userId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")
//
//            return jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "Admin.asmx/HomeScreenCount", "POST", params)
//        }
//
//        override fun onPostExecute(response: JSONObject?) {
//            if (response != null) {
//                try {
//                    if (!response.getBoolean("status")) {
//                        val WakeelApprWaiting = response.getJSONObject("data").getString("WakeelApprWaiting").trim().toInt()
//                        val UnreadSugg = response.getJSONObject("data").getString("UnreadSugg").trim().toInt()
//                        val UnreadComp = response.getJSONObject("data").getString("UnreadComp").trim().toInt()
//
//                        if (WakeelApprWaiting > 0) {
//                            textView_registrations.visibility = View.VISIBLE
//                            textView_registrations.text = String.format(Locale.getDefault(), "%d", WakeelApprWaiting)
//                        } else {
//                            textView_registrations.visibility = View.GONE
//                        }
//                        if (UnreadSugg > 0 || UnreadComp > 0) {
//                            textView_feedbacks.visibility = View.VISIBLE
//                            textView_feedbacks.text = String.format(Locale.getDefault(), "%d", UnreadSugg+UnreadComp)
//                        } else {
//                            textView_feedbacks.visibility = View.GONE
//                        }
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
