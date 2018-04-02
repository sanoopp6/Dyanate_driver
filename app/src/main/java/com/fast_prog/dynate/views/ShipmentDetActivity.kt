package com.fast_prog.dynate.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.models.Ride
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_shipment_det.*
import kotlinx.android.synthetic.main.content_shipment_det.*
import java.util.*

class ShipmentDetActivity : AppCompatActivity() {

    private lateinit var orderList: List<Order>

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_det)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.ShipmentDetails)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@ShipmentDetActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        orderList = ArrayList()

        try {
            Ride.instance
        } catch (e: Exception) {
            Ride.instance = Ride()
        }

        Ride.instance.vehicleSizeId = sharedPreferences.getString(Constants.PREFS_VMS_ID, "0")

        btn_book_vehicle.setOnClickListener {
            hideSoftKeyboard()

            if (validate()) {
                if (ConnectionDetector.isConnected(this@ShipmentDetActivity)) {
                    startActivity(Intent(this@ShipmentDetActivity, SenderReceiverActivity::class.java))

                } else { ConnectionDetector.errorSnackbar(coordinator_layout) }
            }
        }

        edit_subject.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val subject = edit_subject.text.toString().trim()

                if (subject.isEmpty()) {
                    UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                            resources.getText(R.string.InvalidSubject).toString(), resources.getString(R.string.Ok).toString(),
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
                            resources.getText(R.string.InvalidShipment).toString(), resources.getString(R.string.Ok).toString(),
                            "", false, false, {}, {})
                } else {
                    Ride.instance.shipment = shipment
                }
            }
        }

        edit_subject.setText(Ride.instance.shipment)
        edit_shipment.setText(Ride.instance.subject)
    }

    private fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun validate(): Boolean {
        val subject = edit_subject.text.toString()
        val shipment = edit_shipment.text.toString()

        if (subject.trim().isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getText(R.string.InvalidSubject).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            return false
        }

        if (shipment.trim().isEmpty()) {
            UtilityFunctions.showAlertOnActivity(this@ShipmentDetActivity,
                    resources.getText(R.string.InvalidShipment).toString(), resources.getString(R.string.Ok).toString(),
                    "", false, false, {}, {})
            return false
        }

        Ride.instance.subject = subject
        Ride.instance.shipment = shipment

        return true
    }
}
