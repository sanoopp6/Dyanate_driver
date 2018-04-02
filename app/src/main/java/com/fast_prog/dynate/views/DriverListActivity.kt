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
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_driver_list.*
import kotlinx.android.synthetic.main.content_driver_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DriverListActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var linearLayoutManagerDriversList: LinearLayoutManager

    internal lateinit var recyclerViewAdapterDriversList: RecyclerView.Adapter<*>

    internal var jsonArrayDriversList: JSONArray? = null

    internal var loaded: Boolean = false

    internal var expanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_list)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.DriversList)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@DriverListActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        button_expand_list.setOnClickListener {
            expanded = true
            linearLayout_top.layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            button_expand_list.visibility = View.GONE
        }

        recycler_drivers_list.setHasFixedSize(true)
        linearLayoutManagerDriversList = LinearLayoutManager(this@DriverListActivity)
        recycler_drivers_list.layoutManager = linearLayoutManagerDriversList
        recyclerViewAdapterDriversList = WakeelsListAdapter()
        recycler_drivers_list.adapter = recyclerViewAdapterDriversList
    }

    override fun onResume() {
        super.onResume()

        if (!loaded) {
            if (ConnectionDetector.isConnected(applicationContext)) {
                ListDriverRegistrationBackground().execute()
            } else {
                ConnectionDetector.errorSnackbar(coordinator_layout)
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ListDriverRegistrationBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (!loaded) { UtilityFunctions.showProgressDialog (this@DriverListActivity) }
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            var BASE_URL = Constants.BASE_URL_EN + "ListDriverRegistration"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "ListDriverRegistration"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            if (!loaded) {
                loaded = true
                UtilityFunctions.dismissProgressDialog()
            }

            if (jsonObject != null) {
                try {
                    if (jsonObject.getBoolean("status")) {
                        jsonArrayDriversList = jsonObject.getJSONArray("data")
                        recyclerViewAdapterDriversList.notifyDataSetChanged()

                    } else {
                        jsonArrayDriversList = JSONArray()
                        recyclerViewAdapterDriversList.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }

            if (jsonArrayDriversList != null && jsonArrayDriversList!!.length() > 5 && !expanded) {
                button_expand_list.visibility = View.VISIBLE
            } else {
                button_expand_list.visibility = View.GONE
            }

            if (jsonArrayDriversList == null || jsonArrayDriversList!!.length() <= 0) {
                textView_all_drivers.setText(R.string.ListEmpty)
            } else {
                textView_all_drivers.text = String.format(Locale.getDefault(), "%d - %s", jsonArrayDriversList!!.length(), resources.getString(R.string.Driver))
            }
        }
    }

    internal inner class WakeelsListAdapter : RecyclerView.Adapter<WakeelsListAdapter.ViewHolder>() {

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var textViewApplnNo: TextView = v.findViewById<View>(R.id.id_text_view) as TextView
            var textViewUsername: TextView = v.findViewById<View>(R.id.id_text_from_to) as TextView
            var textViewShow: TextView = v.findViewById<View>(R.id.show_details) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_all_my_orders, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setIsRecyclable(false)

            try {
                holder.textViewApplnNo.text = jsonArrayDriversList?.getJSONObject(position)?.getString("DmName")?.trim()
                holder.textViewUsername.text = jsonArrayDriversList?.getJSONObject(position)?.getString("DmMobNumber")?.trim()

                holder.textViewShow.setOnClickListener {
                    DriverDetailsActivity.driverDetail =  jsonArrayDriversList?.getJSONObject(position)
                    startActivity(Intent(this@DriverListActivity, DriverDetailsActivity::class.java))
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun getItemCount(): Int {
            return jsonArrayDriversList?.length()?:0
        }
    }
}
