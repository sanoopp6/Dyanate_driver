package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_show_feedback_list.*
import kotlinx.android.synthetic.main.content_show_feedback_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ShowFeedbackListActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var linearLayoutManagerFeedbackList: LinearLayoutManager

    internal lateinit var recyclerViewAdapterFeedbackList: RecyclerView.Adapter<*>

    internal var jsonArrayFeedbackList: JSONArray? = null

    internal var isCommon = false

    internal var isComplaint = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_feedback_list)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        toolbar.setNavigationOnClickListener { finish() }

        isCommon = intent.getBooleanExtra("isCommon", false)

        val titleTextView = TextView(applicationContext)
        titleTextView.text = if (isCommon) resources.getString(R.string.CommonFeedbacks) else resources.getString(R.string.ShowFeedbacks)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@ShowFeedbackListActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        recyclerView_feedbacks_list.setHasFixedSize(true)
        linearLayoutManagerFeedbackList = LinearLayoutManager(this@ShowFeedbackListActivity)
        recyclerView_feedbacks_list.layoutManager = linearLayoutManagerFeedbackList
        recyclerViewAdapterFeedbackList = FeedbackListAdapter()
        recyclerView_feedbacks_list.adapter = recyclerViewAdapterFeedbackList

        checkBox_complaints.setOnClickListener {
            checkBox_suggections.isChecked = false
            checkBox_complaints.isChecked = true

            if (!isComplaint) {
                isComplaint = true

                if (ConnectionDetector.isConnected(applicationContext)) {
                    FeedbackListBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }

        checkBox_suggections.setOnClickListener {
            checkBox_complaints.isChecked = false
            checkBox_suggections.isChecked = true

            if (isComplaint) {
                isComplaint = false

                if (ConnectionDetector.isConnected(applicationContext)) {
                    FeedbackListBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (ConnectionDetector.isConnected(applicationContext)) {
            FeedbackListBackground().execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FeedbackListBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@ShowFeedbackListActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgFeedId"] = "0"
            params["ArgFeedIsComplaint"] = isComplaint.toString()
            params["ArgFeedIsComon"] = isCommon.toString()

            if (sharedPreferences.getString(Constants.PREFS_USER_TYPE, "")!!.equals(Constants.USER_TYPE_CONST_ADMIN, true)) {
                params["ArgFeedUserId"] = "0"
            } else {
                params["ArgFeedUserId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")
            }

            var BASE_URL = Constants.BASE_URL_EN + if (isCommon) "FeedbackIsComonList" else "FeedbackList"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
                BASE_URL = Constants.BASE_URL_AR + if (isCommon) "FeedbackIsComonList" else "FeedbackList"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (jsonObject != null) {
                try {
                    if (jsonObject.getBoolean("status")) {
                        jsonArrayFeedbackList = jsonObject.getJSONArray("data")
                        recyclerViewAdapterFeedbackList.notifyDataSetChanged()

                    } else {
                        jsonArrayFeedbackList = JSONArray()
                        recyclerViewAdapterFeedbackList.notifyDataSetChanged()
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

    internal inner class FeedbackListAdapter : RecyclerView.Adapter<FeedbackListAdapter.ViewHolder>() {

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var textViewSlNo: TextView = v.findViewById<View>(R.id.textView_slNo) as TextView
            var textViewName: TextView = v.findViewById<View>(R.id.textView_name) as TextView
            var textViewDate: TextView = v.findViewById<View>(R.id.textView_date) as TextView
            var imageViewAttachment: ImageView = v.findViewById<View>(R.id.imageView_attachment) as ImageView
            var linearLayoutBack: LinearLayout = v.findViewById<View>(R.id.linearLayout_back) as LinearLayout
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_feedbacks_list, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setIsRecyclable(false)

            try {
                holder.textViewSlNo.text = (position + 1).toString()
                holder.textViewName.text = jsonArrayFeedbackList?.getJSONObject(position)?.getString("FeedUserId")?.trim()
                holder.textViewDate.text = jsonArrayFeedbackList?.getJSONObject(position)?.getString("FeedCrDtTm")?.trim()

                if (!jsonArrayFeedbackList?.getJSONObject(position)?.getString("FeedIsRead")?.trim()?.toBoolean()!!) {
                    holder.textViewSlNo.setTypeface(holder.textViewSlNo.typeface, Typeface.BOLD)
                    holder.textViewName.setTypeface(holder.textViewName.typeface, Typeface.BOLD)
                    holder.textViewDate.setTypeface(holder.textViewDate.typeface, Typeface.BOLD)
                    holder.linearLayoutBack.setBackgroundColor(ContextCompat.getColor(this@ShowFeedbackListActivity, R.color.lighter_gray))
                } else {
                    holder.textViewSlNo.setTypeface(holder.textViewSlNo.typeface, Typeface.NORMAL)
                    holder.textViewName.setTypeface(holder.textViewName.typeface, Typeface.NORMAL)
                    holder.textViewDate.setTypeface(holder.textViewDate.typeface, Typeface.NORMAL)
                    holder.linearLayoutBack.setBackgroundColor(Color.WHITE)
                }

                if (jsonArrayFeedbackList?.getJSONObject(position)?.getString("FeedDocs")?.trim()?.equals("nil", true)!! ||
                        jsonArrayFeedbackList?.getJSONObject(position)?.getString("FeedDocs")?.trim().isNullOrEmpty()) {
                    holder.imageViewAttachment.visibility = View.INVISIBLE
                } else {
                    holder.imageViewAttachment.visibility = View.VISIBLE
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            holder.itemView.setOnClickListener {
                val jsonObj = jsonArrayFeedbackList?.getJSONObject(position)

                FeedbackDetailActivity.readStatus = jsonObj?.getString("FeedIsRead")?.trim()?.toBoolean()!!
                FeedbackDetailActivity.isComplaint = isComplaint
                FeedbackDetailActivity.jsonObject = jsonObj

                val intent = Intent(this@ShowFeedbackListActivity, FeedbackDetailActivity::class.java)
                startActivity(intent)

                jsonArrayFeedbackList?.getJSONObject(position)?.put("FeedIsRead", "1")
                recyclerViewAdapterFeedbackList.notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return jsonArrayFeedbackList?.length()?:0
        }
    }
}
