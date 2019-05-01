package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_instructions.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class InstructionsActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var linearLayoutManagerInstructions: LinearLayoutManager

    internal lateinit var recyclerViewAdapterInstructions: RecyclerView.Adapter<*>

    internal var jsonArrayInstructions: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.Instructions))

        recyclerView_instructions.setHasFixedSize(true)
        linearLayoutManagerInstructions = LinearLayoutManager(this@InstructionsActivity)
        recyclerView_instructions.layoutManager = linearLayoutManagerInstructions
        recyclerViewAdapterInstructions = FaqListShortAdapter()
        recyclerView_instructions.adapter = recyclerViewAdapterInstructions

        if (ConnectionDetector.isConnected(applicationContext)) {
            AppInstructionsListBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AppInstructionsListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@InstructionsActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            var BASE_URL = Constants.BASE_URL_EN + "AppInstructionsList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
                BASE_URL = Constants.BASE_URL_AR + "AppInstructionsList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (jsonObject != null) {
                try {
                    if (jsonObject.getBoolean("status")) {
                        jsonArrayInstructions = jsonObject.getJSONArray("data")

                        val count = jsonArrayInstructions!!.length()
                        val editor = sharedPreferences.edit()
                        editor.putString(Constants.LAST_INSTRUCTION_ID, jsonArrayInstructions!!.getJSONObject(count - 1).getString("AiId").trim())
                        editor.commit()

                        recyclerViewAdapterInstructions.notifyDataSetChanged()

                    } else {
                        jsonArrayInstructions = JSONArray()
                        recyclerViewAdapterInstructions.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    internal inner class FaqListShortAdapter : RecyclerView.Adapter<FaqListShortAdapter.ViewHolder>() {

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var textViewSlNo: TextView = v.findViewById<View>(R.id.textView_slNo) as TextView
            var textViewTopic: TextView = v.findViewById<View>(R.id.textView_topic) as TextView
            var buttonView: Button = v.findViewById<View>(R.id.button_view) as Button
            var linearLayoutBack: LinearLayout = v.findViewById<View>(R.id.linearLayout_back) as LinearLayout
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_instructions, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setIsRecyclable(false)

            try {
                holder.textViewSlNo.text = (position + 1).toString()
                holder.textViewTopic.text = jsonArrayInstructions!!.getJSONObject(position).getString("AiTopic").trim()

                if (position % 2 != 0) {
                    holder.linearLayoutBack.setBackgroundColor(ContextCompat.getColor(this@InstructionsActivity, R.color.colorBG))
                }

                holder.buttonView.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.IMG_URL + jsonArrayInstructions!!.getJSONObject(position).getString("AiLink").trim())))
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun getItemCount(): Int {
            return jsonArrayInstructions?.length()?:0
        }
    }
}
