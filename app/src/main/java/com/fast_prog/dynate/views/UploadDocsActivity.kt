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
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.widget.LinearLayout
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.RegisterUser
import com.fast_prog.dynate.models.UploadFiles
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_upload_docs.*
import kotlinx.android.synthetic.main.content_upload_docs.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class UploadDocsActivity : AppCompatActivity() {

    internal lateinit var registerUser: RegisterUser

    internal lateinit var sharedPreferences: SharedPreferences

    internal var selected: String = ""

    internal var uploadFiles = UploadFiles()

    private val RESULT_LOAD_IMAGE = 101
    private val TAKE_PHOTO_CODE = 102

    private val MY_PERMISSIONS_REQUEST_CAMERA = 99

    private var mCurrentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_docs)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        registerUser = intent.getSerializableExtra("registerUser") as RegisterUser

        toolbar.setNavigationOnClickListener { backPressed() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.Register)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@UploadDocsActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        button_id.setOnClickListener {
            selected = "id"
            selectImage()
        }

        button_carform.setOnClickListener {
            selected = "car_form"
            selectImage()
        }

        button_card.setOnClickListener {
            selected = "card"
            selectImage()
        }

        button_other.setOnClickListener {
            selected = "other"
            selectImage()
        }

        btn_register.setOnClickListener {
            if (validate()) {
                if (ConnectionDetector.isConnected(this@UploadDocsActivity)) {
                    SendOTPDMBackground().execute()
                } else {
                    ConnectionDetector.errorSnackbar(coordinator_layout)
                }
            }
        }
    }

    override fun onBackPressed() {
        backPressed()
    }

    private fun backPressed() {
        UtilityFunctions.showAlertOnActivity(this@UploadDocsActivity,
                resources.getString(R.string.FilledDataWillBeLost).toString(), resources.getString(R.string.Yes).toString(),
                resources.getString(R.string.No).toString(), true, false,
                { finish() }, {})
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SendOTPDMBackground : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@UploadDocsActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgMobNo", registerUser.mobile!!)
            params.put("ArgIsDB", "false")

            var BASE_URL = Constants.BASE_URL_EN + "SendOTPDM"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "SendOTPDM"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        VerifyOTPActivity.otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP").trim()
                        VerifyOTPActivity.registerUserExtra = registerUser
                        VerifyOTPActivity.uploadFiles = uploadFiles
                        startActivity(Intent(this@UploadDocsActivity, VerifyOTPActivity::class.java))

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@UploadDocsActivity,
                                response.getString("message"), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }

                } catch (e: JSONException) { e.printStackTrace() }

            } else {
                val snackbar = Snackbar.make(coordinator_layout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
                snackbar.show()
            }
        }
    }

    private fun validate(): Boolean {

        if (uploadFiles.imageName1 == null || uploadFiles.imageName1!!.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@UploadDocsActivity,
                    resources.getText(R.string.IDNotSelected).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})

            return false
        }

        if (uploadFiles.imageName2 == null || uploadFiles.imageName2!!.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@UploadDocsActivity,
                    resources.getText(R.string.CarFormNotSelected).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})

            return false
        }

        if (uploadFiles.imageName3 == null || uploadFiles.imageName3!!.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@UploadDocsActivity,
                    resources.getText(R.string.CardNotSelected).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})

            return false
        }

        if (uploadFiles.imageName4 == null || uploadFiles.imageName4!!.isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@UploadDocsActivity,
                    resources.getText(R.string.OtherNotSelected).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})

            return false
        }

        return true
    }

    private fun selectImage() {
        val builder = AlertDialog.Builder(this@UploadDocsActivity)
        val inflater = this@UploadDocsActivity.getLayoutInflater()
        val view = inflater.inflate(R.layout.alert_dialog_add_image, null)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        val linearLayoutTakePhoto = view.findViewById<LinearLayout>(R.id.linearLayout_take_photo)
        val linearLayoutChooseFromGallery = view.findViewById<LinearLayout>(R.id.linearLayout_choose_from_gallery)
        val linearLayoutCancel = view.findViewById<LinearLayout>(R.id.linearLayout_cancel)

        linearLayoutTakePhoto.setOnClickListener {
            alertDialog.dismiss()

            if (ActivityCompat.checkSelfPermission(this@UploadDocsActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@UploadDocsActivity, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)

            } else {
                UtilityFunctions.showAlertOnActivity(this@UploadDocsActivity,
                        resources.getString(R.string.ShootClearly).toString(), resources.getString(R.string.Ok).toString(),
                        "", false, false,
                        {
                            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            try {
                                val photoFile = UtilityFunctions.createImageFile()
                                mCurrentPhotoPath = photoFile.absolutePath
                                val uri = FileProvider.getUriForFile(this@UploadDocsActivity, applicationContext.packageName + ".provider", photoFile)
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

            val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }

        linearLayoutCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            val f = File(picturePath)
            var bm = UtilityFunctions.decodeFile(f)
            bm = UtilityFunctions.scaleDownBitmap(bm, 400, this@UploadDocsActivity)
            val baos = ByteArrayOutputStream()

            bm!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteImage_photo = baos.toByteArray()

            addToClass(bm, byteImage_photo)

        } else if (requestCode == TAKE_PHOTO_CODE  && resultCode == Activity.RESULT_OK) {
            val baos = ByteArrayOutputStream()
            val f = File(mCurrentPhotoPath)
            var bm1 = UtilityFunctions.decodeFile(f)
            f.delete()
            bm1 = UtilityFunctions.scaleDownBitmap(bm1, 800, this@UploadDocsActivity)
            bm1!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteImage_photo = baos.toByteArray()

            addToClass(bm1, byteImage_photo)
        }
    }

    fun addToClass(bm: Bitmap, byteImage_photo: ByteArray) {
        if (selected.equals("id", ignoreCase = true)) {
            uploadFiles.bm1 = bm
            uploadFiles.base64Encoded1 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT)
            uploadFiles.imageName1 = selected
            imageview_id.setImageBitmap(bm)

        } else if (selected.equals("car_form", ignoreCase = true)) {
            uploadFiles.bm2 = bm
            uploadFiles.base64Encoded2 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT)
            uploadFiles.imageName2 = selected
            imageview_carform.setImageBitmap(bm)

        } else if (selected.equals("card", ignoreCase = true)) {
            uploadFiles.bm3 = bm
            uploadFiles.base64Encoded3 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT)
            uploadFiles.imageName3 = selected
            imageview_card.setImageBitmap(bm)

        } else {
            uploadFiles.bm4 = bm
            uploadFiles.base64Encoded4 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT)
            uploadFiles.imageName4 = selected
            imageview_other.setImageBitmap(bm)
        }
    }
}
