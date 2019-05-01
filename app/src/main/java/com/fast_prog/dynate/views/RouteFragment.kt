package com.fast_prog.dynate.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.Order
import com.fast_prog.dynate.utilities.Constants
import com.fast_prog.dynate.utilities.DirectionsJSONParser
import com.fast_prog.dynate.utilities.UtilityFunctions
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_route.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RouteFragment : Fragment(), OnMapReadyCallback {

    internal var mMap: GoogleMap? = null

    internal var mapViewSatellite: Boolean = false

    internal lateinit var start: LatLng

    internal lateinit var stop: LatLng

    internal lateinit var startMarker: Marker

    internal lateinit var stopMarker: Marker

    internal lateinit var builder: LatLngBounds.Builder

    internal lateinit var bounds: LatLngBounds

    internal lateinit var sharedPreferences: SharedPreferences

    private var order: Order? = null

    internal var distanceStr = ""

    internal var durationStr = ""

    internal var distanceStrLabel = ""

    internal var durationStrLabel = ""

    internal var txtDistance: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        sharedPreferences = activity!!.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        val view = inflater.inflate(R.layout.fragment_route, container, false)

        order = (activity as ShipmentDetailsActivity).order

        distanceStrLabel  = resources.getString(R.string.Distance)

        durationStrLabel  = resources.getString(R.string.Duration)

        builder = LatLngBounds.Builder()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        txtDistance = view.txt_distance

        view.image_view_map_change_icon.setOnClickListener {
            if (mMap != null) {
                if (!mapViewSatellite) {
                    mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    mapViewSatellite = true
                    view.image_view_map_change_icon.setColorFilter(Color.WHITE)

                } else {
                    mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                    mapViewSatellite = false
                    view.image_view_map_change_icon.setColorFilter(Color.BLACK)
                }

                mMap!!.uiSettings.isZoomControlsEnabled = false
                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                mMap!!.uiSettings.isCompassEnabled = false
                mMap!!.uiSettings.isRotateGesturesEnabled = false
                mMap!!.uiSettings.isMapToolbarEnabled = false
            }
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            start = LatLng(order?.tripFromLat?.toDouble()?:0.0, order?.tripFromLng?.toDouble()?:0.0)
            stop = LatLng(order?.tripToLat?.toDouble()?:0.0, order?.tripToLng?.toDouble()?:0.0)
        } catch (e: Exception) {
            start = LatLng(0.0, 0.0)
            stop = LatLng(0.0, 0.0)
        }

        builder.include(start)
        builder.include(stop)

        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isMyLocationButtonEnabled = false
        mMap!!.uiSettings.isCompassEnabled = false
        mMap!!.uiSettings.isRotateGesturesEnabled = false
        mMap!!.uiSettings.isMapToolbarEnabled = false

        mMap!!.setOnMapLoadedCallback {
            bounds = builder.build()

            //val l1 = Location("l1")
            //l1.latitude = start.latitude
            //l1.longitude = start.longitude
            //
            //val l2 = Location("l2")
            //l2.latitude = stop.latitude
            //l2.longitude = stop.longitude
            //
            //val distanceTo = l1.distanceTo(l2)

            //if (distanceTo > 100)
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            //else
            //    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15.0f))
        }

        val marker1 = layoutInflater.inflate(R.layout.cab_marker_green, null)

        if (marker1 != null) {
            startMarker = googleMap.addMarker(MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromBitmap(UtilityFunctions.createDrawableFromView(activity!!, marker1))))
        }

        val marker2 = layoutInflater.inflate(R.layout.cab_marker_red, null)

        if (marker2 != null) {
            stopMarker = googleMap.addMarker(MarkerOptions().position(stop).icon(BitmapDescriptorFactory.fromBitmap(UtilityFunctions.createDrawableFromView(activity!!, marker2))))
        }

        UtilityFunctions.showProgressDialog(activity!!)
        val url = getDirectionsUrl(start, stop)
        val downloadTask = DownloadTask()
        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val key = "key=" + Constants.GOOGLE_API_KEY
        val parameters = "$str_origin&$str_dest&$sensor&$key"
        val output = "json"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null

        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream!!))
            val sb = StringBuilder()
            var line: String?
            do {
                line = br.readLine()
                sb.append(line)
            } while (line != null)
            data = sb.toString()
            br.close()

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }

        return data
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg url: String): String {
            var data = ""

            try {
                data = downloadUrl(url[0])

            } catch (e: Exception) {
                UtilityFunctions.dismissProgressDialog()
                e.printStackTrace()
            }

            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val parserTask = ParserTask()
            parserTask.execute(result)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()

                // Starts parsing data
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                UtilityFunctions.dismissProgressDialog()
                e.printStackTrace()
            }

            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            UtilityFunctions.dismissProgressDialog()

            var points: ArrayList<LatLng>? = null
            var lineOptions: PolylineOptions? = null

            // Traversing through all the routes
            for (i in result.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()

                // Fetching i-th route
                val path = result[i]

                // Fetching all the points in i-th route
                for (j in path.indices) {
                    val point = path[j]

                    if (j == 0) {
                        distanceStr = point["distance"].toString()
                        continue
                    } else if (j == 1) {
                        durationStr = point["duration"].toString()
                        continue
                    }

                    val lat = point["lat"]?.toDouble()?:0.0
                    val lng = point["lng"]?.toDouble()?:0.0
                    val position = LatLng(lat, lng)
                    builder.include(position)
                    points.add(position)
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(5f)
                lineOptions.color(Color.RED)
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap!!.addPolyline(lineOptions)
                txtDistance!!.text = String.format("%s : %s, %s : %s", distanceStrLabel, distanceStr, durationStrLabel, durationStr)
                txtDistance!!.startAnimation(UtilityFunctions.blinkAnimation)
            }
        }
    }
}