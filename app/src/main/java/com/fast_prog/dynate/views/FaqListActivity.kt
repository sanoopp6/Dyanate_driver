package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import android.widget.LinearLayout
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.extensions.customTitle
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_faq_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FaqListActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var linearLayoutManagerFaqList: LinearLayoutManager

    internal lateinit var recyclerViewAdapterFaqList: RecyclerView.Adapter<*>

    internal var jsonArrayFaqList: JSONArray? = null

    internal var loaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_list)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        customTitle(resources.getString(R.string.FAQ))

        recyclerView_faq_list.setHasFixedSize(true)
        linearLayoutManagerFaqList = LinearLayoutManager(this@FaqListActivity)
        recyclerView_faq_list.layoutManager = linearLayoutManagerFaqList
        recyclerViewAdapterFaqList = FaqListShortAdapter()
        recyclerView_faq_list.adapter = recyclerViewAdapterFaqList
    }

    override fun onResume() {
        super.onResume()

        if (ConnectionDetector.isConnected(applicationContext)) {
            FAQListBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FAQListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            if (!loaded) { UtilityFunctions.showProgressDialog (this@FaqListActivity) }
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            var BASE_URL = Constants.BASE_URL_EN + "FAQList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
                BASE_URL = Constants.BASE_URL_AR + "FAQList"
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
                        jsonArrayFaqList = jsonObject.getJSONArray("data")
                        recyclerViewAdapterFaqList.notifyDataSetChanged()

                    } else {
                        jsonArrayFaqList = JSONArray()
                        recyclerViewAdapterFaqList.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    internal inner class FaqListShortAdapter : RecyclerView.Adapter<FaqListShortAdapter.ViewHolder>() {

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var textViewQuestion: TextView = v.findViewById<View>(R.id.textView_question) as TextView
            var textViewAnswer: TextView = v.findViewById<View>(R.id.textView_answer) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_faq_list, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setIsRecyclable(false)

            try {
                holder.textViewQuestion.text = jsonArrayFaqList!!.getJSONObject(position).getString("FaqQuestion").trim()
                holder.textViewAnswer.text = jsonArrayFaqList!!.getJSONObject(position).getString("FaqAnswer").trim()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var height = 0

            holder.textViewQuestion.setOnClickListener {
                if (holder.textViewAnswer.visibility != View.VISIBLE) {
                    holder.textViewAnswer.visibility = View.VISIBLE
                    val layoutParams = holder.textViewQuestion.layoutParams as LinearLayout.LayoutParams
                    height = layoutParams.height
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    holder.textViewQuestion.layoutParams = layoutParams
                    holder.textViewQuestion.background = ContextCompat.getDrawable(applicationContext, R.drawable.layout_rect_lightblue_blue)

                } else {
                    holder.textViewAnswer.visibility = View.GONE
                    val layoutParams = holder.textViewQuestion.layoutParams as LinearLayout.LayoutParams
                    layoutParams.height = height
                    holder.textViewQuestion.layoutParams = layoutParams
                    holder.textViewQuestion.setBackgroundColor(ContextCompat.getColor(this@FaqListActivity, R.color.whiteColor))
                }
            }
        }

        override fun getItemCount(): Int {
            return jsonArrayFaqList?.length()?:0
        }
    }
}
