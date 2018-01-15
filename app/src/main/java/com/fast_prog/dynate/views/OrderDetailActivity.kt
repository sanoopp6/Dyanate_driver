package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.ConnectionDetector
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.JsonParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.content_order_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    internal lateinit var sharedPreferences: SharedPreferences

    private var order: Order? = null

    //private var mMap: GoogleMap? = null
    //private var viewMap: Boolean? = null
    //private var mapViewSatellite: Boolean = false
    //internal lateinit var start: LatLng
    //internal lateinit var stop: LatLng
    //internal lateinit var startMarker: Marker
    //internal lateinit var stopMarker: Marker
    //internal lateinit var builder: LatLngBounds.Builder
    //internal lateinit var bounds: LatLngBounds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

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
            titleTextView.setTextAppearance(this@OrderDetailActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        order = intent.getSerializableExtra("order") as Order?
        if (order == null) finish()

        //builder = LatLngBounds.Builder()
        //mapViewSatellite = false
        //// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        //mapFragment.getMapAsync(this)

        txt_trip_no.text = order?.tripNo
        txt_vehicle.text = order?.vehicleModel
        from_name.text = order?.tripFromName
        from_mobile.text = order?.tripFromMob
        to_name.text = order?.tripToName
        to_mobile.text = order?.tripToMob
        content_from_address.text = order?.tripFromAddress
        content_to_address.text = order?.tripToAddress
        content_schedule_time.text = order?.scheduleDate
        content_schedule_date.text = order?.scheduleTime
        content_subject.text = order?.tripSubject
        content_notes.text = order?.tripNotes

        btn_switch.setOnClickListener {
            val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s,%s(%s)&daddr=%s,%s(%s)", order!!.tripFromLat, order!!.tripFromLng, order!!.tripFromAddress, order!!.tripToLat, order!!.tripToLng, order!!.tripToAddress)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.`package` = "com.google.android.apps.maps";
            startActivity(intent)

            //if (viewMap!!) {
            //    viewMap = false
            //
            //    map_frame.visibility = View.GONE
            //    detail_layout.visibility = View.VISIBLE
            //
            //    btn_switch.text = resources.getString(R.string.ShowRoute)
            //
            //    titleTextView.text = resources.getString(R.string.ShipmentDetails)
            //    if (Build.VERSION.SDK_INT < 23) {
            //        titleTextView.setTextAppearance(this@OrderDetailActivity, R.style.FontBoldSixteen)
            //    } else {
            //        titleTextView.setTextAppearance(R.style.FontBoldSixteen)
            //    }
            //    titleTextView.setAllCaps(true)
            //    titleTextView.setTextColor(Color.WHITE)
            //    supportActionBar?.customView = titleTextView
            //
            //} else {
            //    viewMap = true
            //
            //    detail_layout.visibility = View.GONE
            //    map_frame.visibility = View.VISIBLE
            //
            //    btn_switch.text = resources.getString(R.string.ShowDetails)
            //
            //    titleTextView.text = resources.getString(R.string.ShowRoute)
            //    if (Build.VERSION.SDK_INT < 23) {
            //        titleTextView.setTextAppearance(this@OrderDetailActivity, R.style.FontBoldSixteen)
            //    } else {
            //        titleTextView.setTextAppearance(R.style.FontBoldSixteen)
            //    }
            //    titleTextView.setAllCaps(true)
            //    titleTextView.setTextColor(Color.WHITE)
            //    supportActionBar?.customView = titleTextView
            //}
        }

        //image_view_map_change_icon.setOnClickListener {
        //    if (mMap != null) {
        //        if (!mapViewSatellite) {
        //            mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        //            mapViewSatellite = true
        //            image_view_map_change_icon.setColorFilter(Color.WHITE)
        //
        //        } else {
        //            mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        //            mapViewSatellite = false
        //            image_view_map_change_icon.setColorFilter(Color.BLACK)
        //        }
        //
        //        mMap!!.uiSettings.isZoomControlsEnabled = false
        //        mMap!!.uiSettings.isMyLocationButtonEnabled = false
        //        mMap!!.uiSettings.isCompassEnabled = false
        //        mMap!!.uiSettings.isRotateGesturesEnabled = false
        //        mMap!!.uiSettings.isMapToolbarEnabled = false
        //    }
        //}

        if (order!!.tripStatus!!.matches("3|4".toRegex())) {
            btn_cancel_trip.visibility = View.GONE
        }

        btn_replies.setOnClickListener {
            val intent = Intent(this@OrderDetailActivity, DriverRepliesActivity::class.java)
            intent.putExtra("item", order)
            startActivity(intent)
        }

        btn_cancel_trip.setOnClickListener {
            UtilityFunctions.showAlertOnActivity(this@OrderDetailActivity,
                    resources.getText(R.string.AreYouSure).toString(), resources.getString(R.string.Yes).toString(),
                    resources.getString(R.string.No).toString(), true, false,
                    {
                        if (ConnectionDetector.isConnected(this@OrderDetailActivity)) {
                            TripMasterStatusUpdateBackground(order?.tripId!!).execute()
                        } else {
                            ConnectionDetector.errorSnackbar(coordinator_layout)
                        }
                    }, {})
        }

        if (ConnectionDetector.isConnected(this@OrderDetailActivity)) {
            UpdateTripNotifiedCustStatusBackground(order?.tripId!!).execute()
        } else {
            ConnectionDetector.errorSnackbar(coordinator_layout)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateTripNotifiedCustStatusBackground internal constructor(internal var tripId: String) : AsyncTask<Void, Void, JSONObject>() {

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgTripDMId", tripId)
            params.put("ArgTripDIsNotifiedCust", "true")

            var BASE_URL = Constants.BASE_URL_EN + "UpdateTripNotifiedCustStatus"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "UpdateTripNotifiedCustStatus"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        //Log.e("success");
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }

    //override fun onMapReady(googleMap: GoogleMap) {
    //    mMap = googleMap
    //
    //    try {
    //        start = LatLng(java.lang.Double.parseDouble(order!!.tripFromLat), java.lang.Double.parseDouble(order!!.tripFromLng))
    //        stop = LatLng(java.lang.Double.parseDouble(order!!.tripToLat), java.lang.Double.parseDouble(order!!.tripToLng))
    //    } catch (e: Exception) {
    //        start = LatLng(0.0, 0.0)
    //        stop = LatLng(0.0, 0.0)
    //    }
    //
    //    builder.include(start)
    //    builder.include(stop)
    //
    //    mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
    //    mMap!!.uiSettings.isMyLocationButtonEnabled = false
    //    mMap!!.uiSettings.isCompassEnabled = false
    //    mMap!!.uiSettings.isRotateGesturesEnabled = false
    //    mMap!!.uiSettings.isMapToolbarEnabled = false
    //
    //    mMap!!.setOnMapLoadedCallback {
    //        bounds = builder.build()
    //
    //        val l1 = Location("l1")
    //        l1.latitude = start.latitude
    //        l1.longitude = start.longitude
    //
    //        val l2 = Location("l2")
    //        l2.latitude = stop.latitude
    //        l2.longitude = stop.longitude
    //
    //        val distanceTo = l1.distanceTo(l2)
    //
    //        if (distanceTo > 100)
    //            mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    //        else
    //            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15.0f))
    //    }
    //
    //    val marker1 = layoutInflater.inflate(R.layout.cab_marker_green, null)
    //
    //    if (marker1 != null) {
    //        startMarker = googleMap.addMarker(MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@OrderDetailActivity, marker1))))
    //    }
    //
    //    val marker2 = layoutInflater.inflate(R.layout.cab_marker_red, null)
    //
    //    if (marker2 != null) {
    //        stopMarker = googleMap.addMarker(MarkerOptions().position(stop).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@OrderDetailActivity, marker2))))
    //    }
    //
    //    val url = getDirectionsUrl(start, stop)
    //    val downloadTask = DownloadTask()
    //    downloadTask.execute(url)
    //}
    //
    //private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
    //
    //    val str_origin = "origin=" + origin.latitude + "," + origin.longitude
    //    val str_dest = "destination=" + dest.latitude + "," + dest.longitude
    //    val sensor = "sensor=false"
    //    val parameters = "$str_origin&$str_dest&$sensor"
    //    val output = "json"
    //
    //    return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    //}
    //
    //@Throws(IOException::class)
    //private fun downloadUrl(strUrl: String): String {
    //    var data = ""
    //    var iStream: InputStream? = null
    //    var urlConnection: HttpURLConnection? = null
    //
    //    try {
    //        val url = URL(strUrl)
    //        urlConnection = url.openConnection() as HttpURLConnection
    //        urlConnection.connect()
    //        iStream = urlConnection.inputStream
    //        val br = BufferedReader(InputStreamReader(iStream!!))
    //        val sb = StringBuilder()
    //        var line: String?
    //        do {
    //            line = br.readLine()
    //            sb.append(line)
    //        } while (line != null)
    //        data = sb.toString()
    //        br.close()
    //
    //    } catch (e: Exception) {
    //        e.printStackTrace()
    //
    //    } finally {
    //        iStream!!.close()
    //        urlConnection!!.disconnect()
    //    }
    //
    //    return data
    //}
    //
    //@SuppressLint("StaticFieldLeak")
    //private inner class DownloadTask : AsyncTask<String, Void, String>() {
    //    override fun doInBackground(vararg url: String): String {
    //        var data = ""
    //
    //        try {
    //            data = downloadUrl(url[0])
    //
    //        } catch (e: Exception) {
    //            e.printStackTrace()
    //        }
    //
    //        return data
    //    }
    //
    //    override fun onPostExecute(result: String) {
    //        super.onPostExecute(result)
    //
    //        val parserTask = ParserTask()
    //        parserTask.execute(result)
    //    }
    //}
    //
    //@SuppressLint("StaticFieldLeak")
    //private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {
    //
    //    // Parsing the data in non-ui thread
    //    override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
    //
    //        val jObject: JSONObject
    //        var routes: List<List<HashMap<String, String>>>? = null
    //
    //        try {
    //            jObject = JSONObject(jsonData[0])
    //            val parser = DirectionsJSONParser()
    //
    //            // Starts parsing data
    //            routes = parser.parse(jObject)
    //        } catch (e: Exception) {
    //            e.printStackTrace()
    //        }
    //
    //        return routes
    //    }
    //
    //    // Executes in UI thread, after the parsing process
    //    override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
    //        var points: ArrayList<LatLng>? = null
    //        var lineOptions: PolylineOptions? = null
    //
    //        // Traversing through all the routes
    //        for (i in result.indices) {
    //            points = ArrayList()
    //            lineOptions = PolylineOptions()
    //
    //            // Fetching i-th route
    //            val path = result[i]
    //
    //            // Fetching all the points in i-th route
    //            for (j in path.indices) {
    //                val point = path[j]
    //
    //                if (j == 0) {    // Get distance from the list
    //                    continue
    //                } else if (j == 1) { // Get duration from the list
    //                    continue
    //                }
    //
    //                val lat = java.lang.Double.parseDouble(point["lat"])
    //                val lng = java.lang.Double.parseDouble(point["lng"])
    //                val position = LatLng(lat, lng)
    //                builder.include(position)
    //                points.add(position)
    //            }
    //
    //            // Adding all the points in the route to LineOptions
    //            lineOptions.addAll(points)
    //            lineOptions.width(5f)
    //            lineOptions.color(Color.RED)
    //        }
    //
    //        // Drawing polyline in the Google Map for the i-th route
    //        if (lineOptions != null)
    //            mMap!!.addPolyline(lineOptions)
    //    }
    //}

    @SuppressLint("StaticFieldLeak")
    private inner class TripMasterStatusUpdateBackground internal constructor(internal var tripDMId: String) : AsyncTask<Void, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            UtilityFunctions.showProgressDialog (this@OrderDetailActivity)
        }

        override fun doInBackground(vararg param: Void): JSONObject? {
            val jsonParser = JsonParser()
            val params = HashMap<String, String>()

            params.put("ArgTripMID", tripDMId)
            params.put("ArgTripMStatus", "4")

            var BASE_URL = Constants.BASE_URL_EN + "TripMasterStatusUpdate"

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                BASE_URL = Constants.BASE_URL_AR + "TripMasterStatusUpdate"
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params)
        }

        override fun onPostExecute(response: JSONObject?) {
            UtilityFunctions.dismissProgressDialog()

            if (response != null) {
                try {
                    if (response.getBoolean("status")) {
                        UtilityFunctions.showAlertOnActivity(this@OrderDetailActivity,
                                resources.getString(R.string.CancelSuccess), resources.getString(R.string.Ok).toString(),
                                "", false, false,
                                { finish() }, {})

                    } else {
                        UtilityFunctions.showAlertOnActivity(this@OrderDetailActivity,
                                resources.getString(R.string.CancelFailed), resources.getString(R.string.Ok).toString(),
                                "", false, false, {}, {})
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }

    //fun createDrawableFromView(context: Context, view: View): Bitmap {
    //    val displayMetrics = DisplayMetrics()
    //    (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    //    view.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
    //    view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
    //    view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
    //    view.buildDrawingCache()
    //    val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    //
    //    val canvas = Canvas(bitmap)
    //    view.draw(canvas)
    //
    //    return bitmap
    //}

}
