package com.fast_prog.dynate.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Docs
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.content_feedback.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FeedbackActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    internal lateinit var linearLayoutManagerImagesList: LinearLayoutManager

    internal lateinit var recyclerViewAdapterImagesList: RecyclerView.Adapter<*>

    internal var docsList: ArrayList<Docs> = ArrayList()

    internal val simpleDateFormat = SimpleDateFormat("yyyyMMddhhmmss", Locale.ENGLISH)

    private val RESULT_LOAD_IMAGE = 101
    private val TAKE_PHOTO_CODE = 102

    private var isComplaintsClicked = false
    private var type = ""
    private var feedbackMsg = ""
    private var uploadedDocs = ""
    private var isCommon = false

    private var mCurrentPhotoPath: String? = null

    private val MY_PERMISSIONS_REQUEST_CAMERA = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.Feedback)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@FeedbackActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        recyclerView_feedbacks_images.setHasFixedSize(true)
        linearLayoutManagerImagesList = LinearLayoutManager(this@FeedbackActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView_feedbacks_images.layoutManager = linearLayoutManagerImagesList
        recyclerViewAdapterImagesList = ImagesAdapter()
        recyclerView_feedbacks_images.adapter = recyclerViewAdapterImagesList

        button_attach_images.setOnClickListener {
            val builder = AlertDialog.Builder(this@FeedbackActivity)
            val inflater = this@FeedbackActivity.getLayoutInflater()
            val view = inflater.inflate(R.layout.alert_dialog_add_image, null)
            builder.setView(view)
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            val linearLayoutTakePhoto = view.findViewById<LinearLayout>(R.id.linearLayout_take_photo)
            val linearLayoutChooseFromGallery = view.findViewById<LinearLayout>(R.id.linearLayout_choose_from_gallery)
            val linearLayoutCancel = view.findViewById<LinearLayout>(R.id.linearLayout_cancel)

            linearLayoutTakePhoto.setOnClickListener {
                alertDialog.dismiss()

                if (ActivityCompat.checkSelfPermission(this@FeedbackActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@FeedbackActivity, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)

                } else {
                    UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                            resources.getString(R.string.ShootClearly).toString(), resources.getString(R.string.Ok).toString(),
                            "", false, false,
                            {
                                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                try {
                                    val photoFile = UtilityFunctions.createImageFile()
                                    mCurrentPhotoPath = photoFile.absolutePath
                                    val uri = FileProvider.getUriForFile(this@FeedbackActivity, applicationContext.packageName + ".provider", photoFile)
                                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_CODE)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }, {})
                }
            }

            linearLayoutChooseFromGallery.setOnClickListener {
                alertDialog.dismiss()

                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, RESULT_LOAD_IMAGE)
            }

            linearLayoutCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val scaleUpAnimation = AnimationUtils.loadAnimation(this@FeedbackActivity, R.anim.slide_down)
        val scaleDownAnimation = AnimationUtils.loadAnimation(this@FeedbackActivity, R.anim.slide_up)
        val scaleDownAnimationComplaints = AnimationUtils.loadAnimation(this@FeedbackActivity, R.anim.slide_up)

        scaleUpAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}
        })

        scaleDownAnimationComplaints!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                if (isComplaintsClicked) {
                    editText_complaints!!.visibility = View.GONE
                } else {
                    editText_suggestions!!.visibility = View.GONE
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        scaleDownAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                if (isComplaintsClicked) {
                    editText_suggestions!!.visibility = View.GONE
                } else {
                    editText_complaints!!.visibility = View.GONE
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        button_complaints!!.setOnClickListener {
            isComplaintsClicked = true
            editText_suggestions!!.setText("")

            if (editText_suggestions!!.visibility == View.VISIBLE) {
                editText_suggestions!!.startAnimation(scaleDownAnimation)
            }

            if (editText_complaints!!.visibility == View.VISIBLE) {
                editText_complaints!!.startAnimation(scaleDownAnimationComplaints)
                type = ""

            } else {
                editText_complaints!!.visibility = View.VISIBLE
                editText_complaints!!.startAnimation(scaleUpAnimation)
                type = "complaints"
            }
        }

        button_suggestions!!.setOnClickListener {
            isComplaintsClicked = false
            editText_complaints!!.setText("")

            if (editText_complaints!!.visibility == View.VISIBLE) {
                editText_complaints!!.startAnimation(scaleDownAnimation)
            }

            if (editText_suggestions!!.visibility == View.VISIBLE) {
                editText_suggestions!!.startAnimation(scaleDownAnimationComplaints)
                type = ""

            } else {
                editText_suggestions!!.visibility = View.VISIBLE
                editText_suggestions!!.startAnimation(scaleUpAnimation)
                type = "suggestions"
            }
        }

        button_submit.setOnClickListener {
            if (validate()) {
                if (ConnectionDetector.isConnected(this@FeedbackActivity)) {
                    uploadedDocs = ""

                    if (docsList.size > 0) {
                        for (i in docsList.indices) {
                            uploadedDocs = String.format(Locale.getDefault(), "%s%s,", uploadedDocs, docsList[i].docPath)
                        }
                        uploadedDocs = uploadedDocs.substring(0, uploadedDocs.length - 1)
                        FeedbackAddImgBackground(true).execute()

                    } else {
                        FeedbackAddBackground(true).execute()
                    }
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }

        checkBox_common.setOnCheckedChangeListener{ buttonView, isChecked ->
            isCommon = isChecked
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun validate(): Boolean {
        val suggestionMsg = editText_suggestions.text.toString().trim()
        val complaintMsg = editText_complaints.text.toString().trim()
        feedbackMsg = ""

        if ((suggestionMsg.isEmpty() && complaintMsg.isEmpty()) || type.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                    resources.getString(R.string.SuggectionComplaintEmpty).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            return false
        }

        if (isComplaintsClicked && type.equals("complaints", false)) {
            if (complaintMsg.isEmpty()) {
                UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                        resources.getString(R.string.ComplaintEmpty).toString(), resources.getString(R.string.Ok).toString(),
                        "", false, false, {}, {})
                return false
            } else {
                feedbackMsg = complaintMsg
            }
        }

        if (!isComplaintsClicked && type.equals("suggestions", false)) {
            if (suggestionMsg.isEmpty()) {
                UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                        resources.getString(R.string.SuggectionEmpty).toString(), resources.getString(R.string.Ok).toString(),
                        "", false, false, {}, {})
                return false
            } else {
                feedbackMsg = suggestionMsg
            }
        }

        return true
    }

    internal inner class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var textViewSlNo: TextView = v.findViewById<View>(R.id.textView_slNo) as TextView
            var imageViewFile: ImageView = v.findViewById<View>(R.id.imageView_file) as ImageView
            var imageViewIdProofDelete: ImageView = v.findViewById<View>(R.id.imageView_idProof_delete) as ImageView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_image_list, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setIsRecyclable(true)

            holder.textViewSlNo.text = String.format("%s.",(position + 1).toString())
            holder.imageViewFile.setImageBitmap(docsList[position].docBm)
            holder.imageViewIdProofDelete.visibility = View.VISIBLE

            holder.imageViewIdProofDelete.setOnClickListener {
                UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                        resources.getString(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                        resources.getString(R.string.No).toString(), true, false,
                        {
                            docsList.removeAt(position)
                            recyclerViewAdapterImagesList.notifyDataSetChanged() }, {})
            }
        }

        override fun getItemCount(): Int {
            return docsList.size
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            val f = File(picturePath)
            var bm = UtilityFunctions.decodeFile(f)
            bm = UtilityFunctions.scaleDownBitmap(bm, 800, this@FeedbackActivity)
            val baos = ByteArrayOutputStream()

            bm!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteImage_photo = baos.toByteArray()

            val cal = Calendar.getInstance()
            val date = cal.time
            val temp = simpleDateFormat.format(date)

            val doc = Docs()
            doc.docBm = bm
            doc.docbase64 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT)
            doc.docPath = "feedback" + sharedPreferences.getString(Constants.PREFS_USER_ID, "") + "_" + temp + ".jpg"

            docsList.add(doc)

        } else if (requestCode == TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            val baos = ByteArrayOutputStream()
            val f = File(mCurrentPhotoPath)
            var bm1 = UtilityFunctions.decodeFile(f)
            f.delete()
            bm1 = UtilityFunctions.scaleDownBitmap(bm1, 800, this@FeedbackActivity)
            bm1!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteImage_photo = baos.toByteArray()

            val cal = Calendar.getInstance()
            val date = cal.time
            val temp = simpleDateFormat.format(date)

            val doc = Docs()
            doc.docBm = bm1
            doc.docbase64 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT)
            doc.docPath = "feedback" + sharedPreferences.getString(Constants.PREFS_USER_ID, "") + "_" + temp + ".jpg"

            docsList.add(doc)
        }

        recyclerViewAdapterImagesList.notifyDataSetChanged()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FeedbackAddImgBackground internal constructor(internal var showDialog: Boolean) : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (showDialog) { UtilityFunctions.showProgressDialog(this@FeedbackActivity) }
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgImgName"] = docsList[0].docPath
            params["ArgBase64"] = docsList[0].docbase64

            var BASE_URL = Constants.BASE_URL_EN + "FeedbackAddImg"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
                BASE_URL = Constants.BASE_URL_AR + "FeedbackAddImg"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        docsList.removeAt(0)

                        if (docsList.size > 0) {
                            FeedbackAddImgBackground(false).execute()
                        } else {
                            FeedbackAddBackground(false).execute()
                        }

                    } else {
                        UtilityFunctions.dismissProgressDialog()

                        UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                UtilityFunctions.dismissProgressDialog()

                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FeedbackAddBackground internal constructor(internal var showDialog: Boolean) : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (showDialog) { UtilityFunctions.showProgressDialog(this@FeedbackActivity) }
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params["ArgFeedUserId"] = sharedPreferences.getString(Constants.PREFS_USER_ID, "")
            params["ArgFeedIsComplaint"] = type.equals("complaints", false).toString()
            params["ArgFeedIsComon"] = isCommon.toString()
            params["ArgFeedRequest"] = feedbackMsg
            params["ArgFeedDocs"] = uploadedDocs

            var BASE_URL = Constants.BASE_URL_EN + "FeedbackAdd"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", true)) {
                BASE_URL = Constants.BASE_URL_AR + "FeedbackAdd"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                                resources.getString(R.string.FeedbackUpdated).toString(), resources.getString(R.string.Ok).toString(),
                                "", false, false, { finish() }, {})

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@FeedbackActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
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

}
