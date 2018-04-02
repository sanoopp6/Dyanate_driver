package com.fast_prog.dynate.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.models.PlaceItem
import com.fast_prog.dynate.utilities.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import kotlinx.android.synthetic.main.content_map_location_picker.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MapLocationPickerActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    internal val REQUEST_CODE_AUTOCOMPLETE = 1

    internal var userLocation: Location? = null
    internal var currentLocation: Location? = null
    internal var locationLoaded = false

    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99

    internal var mMap: GoogleMap? = null

    internal lateinit var gpsTracker: GPSTracker

    internal lateinit var latLng: LatLng

    internal var mapViewSatellite: Boolean = false

    internal lateinit var currentPlaceItem: PlaceItem

    internal var placeItem: PlaceItem = PlaceItem()

    internal var placeItemSelectedKey: String? = null

    internal lateinit var sharedPreferences: SharedPreferences

    internal var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_location_picker)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.home_up_icon))

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        toolbar.setNavigationOnClickListener { finish() }

        val titleTextView = TextView(applicationContext)
        titleTextView.text = resources.getString(R.string.SelectAddress)
        if (Build.VERSION.SDK_INT < 23) {
            titleTextView.setTextAppearance(this@MapLocationPickerActivity, R.style.FontBoldSixteen)
        } else {
            titleTextView.setTextAppearance(R.style.FontBoldSixteen)
        }
        titleTextView.setAllCaps(true)
        titleTextView.setTextColor(Color.WHITE)
        supportActionBar?.customView = titleTextView

        mapViewSatellite = false

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        type_location_text_view.setOnClickListener {
            val intent = Intent(this@MapLocationPickerActivity, PickLocationActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        }

        location_select_gps_image_view.setOnClickListener {
            val intent = Intent(this@MapLocationPickerActivity, PickLocationActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        }

        search_location_image_view.setOnClickListener {
            val intent = Intent(this@MapLocationPickerActivity, PickLocationActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        }

        bookmark_location_image_view.setOnClickListener {
            if (placeItemSelectedKey.isNullOrEmpty()) {
                UtilityFunctions.showAlertOnActivity(this@MapLocationPickerActivity,
                        resources.getText(R.string.BookmarkLocation).toString(), resources.getString(R.string.Yes).toString(),
                        resources.getString(R.string.No).toString(), true, false,
                        {
                            placeItem = PlaceItem()
                            placeItem.pLatitude = userLocation?.latitude.toString()
                            placeItem.pLongitude = userLocation?.longitude.toString()
                            placeItem.plName = type_location_text_view.text.toString()
                            placeItem.pVicinity = text_view_province.text.toString()

                            try {
                                val snappyDB = DBFactory.open(this@MapLocationPickerActivity, Constants.DYNA_DB)
                                val keys = snappyDB.findKeys(Constants.DYNA_DB_KEY)
                                snappyDB.put(Constants.DYNA_DB_KEY+"_"+keys.size, placeItem)
                                snappyDB.close()

                                placeItemSelectedKey = Constants.DYNA_DB_KEY+"_"+keys.size
                                bookmark_location_image_view.setColorFilter(Color.parseColor(Constants.FILTER_COLOR))

                            } catch (e: SnappydbException) {
                                Log.e("", e.message)
                                e.printStackTrace()
                            }
                        }, {})

            } else {
                UtilityFunctions.showAlertOnActivity(this@MapLocationPickerActivity,
                        resources.getText(R.string.DeleteBookmarkedLocation).toString(), resources.getString(R.string.Yes).toString(),
                        resources.getString(R.string.No).toString(), true, false,
                        {
                            try {
                                val snappyDB = DBFactory.open(this@MapLocationPickerActivity, Constants.DYNA_DB)
                                snappyDB.del(placeItemSelectedKey)
                                snappyDB.close()

                                bookmark_location_image_view.setColorFilter(Color.TRANSPARENT)
                                placeItemSelectedKey = null

                            } catch (e: SnappydbException) {
                                e.printStackTrace()
                            }
                        }, {})
            }
        }

        image_view_map_change_icon.setOnClickListener {
            if (mMap != null) {
                if (!mapViewSatellite) {
                    mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    mapViewSatellite = true
                    image_view_map_change_icon.setColorFilter(Color.WHITE)
                    image_view_current_location_icon.setColorFilter(Color.WHITE)

                } else {
                    mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                    mapViewSatellite = false
                    image_view_map_change_icon.setColorFilter(Color.BLACK)
                    image_view_current_location_icon.setColorFilter(Color.BLACK)
                }

                mMap!!.uiSettings.isZoomControlsEnabled = false

                latLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)

                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(17f).build()

                if (ActivityCompat.checkSelfPermission(this@MapLocationPickerActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MapLocationPickerActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this@MapLocationPickerActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                }

                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                mMap!!.uiSettings.isCompassEnabled = false
                mMap!!.uiSettings.isRotateGesturesEnabled = false
                mMap!!.uiSettings.isMapToolbarEnabled = false
                mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }

        image_view_current_location_icon.setOnClickListener {
            if (mMap != null) {
                mMap!!.uiSettings.isZoomControlsEnabled = false
                gpsTracker.getLocation()

                if (gpsTracker.canGetLocation()) {
                    if (ActivityCompat.checkSelfPermission(this@MapLocationPickerActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MapLocationPickerActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this@MapLocationPickerActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }

                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

                    if (currentLocation != null) {
                        locationLoaded = true
                        userLocation = currentLocation
                        changeMap(currentLocation)
                    }

                } else {
                    gpsTracker.showSettingsAlert()
                }
            }
        }

        btn_select_location.setOnClickListener {
            if (!type_location_text_view.text.toString().equals(resources.getString(R.string.TypeYourLocation), true)) {
                val placeItem = PlaceItem()
                placeItem.plName = type_location_text_view.text.toString()
                placeItem.pLatitude = userLocation!!.latitude.toString()
                placeItem.pLongitude = userLocation!!.longitude.toString()

                val intent = Intent()
                intent.putExtra("PlaceItem", placeItem)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        gpsTracker = GPSTracker(this@MapLocationPickerActivity)

        btn_select_location.isEnabled = false
        btn_select_location.alpha = 0.5f

        you_are_here_text_view.text = resources.getString(R.string.YouAreHere)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
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

    override fun onResume() {
        super.onResume()

        gpsTracker.getLocation()

        if (checkPlayServices()) {
            if (gpsTracker.canGetLocation()) {
                if (mGoogleApiClient == null || !mGoogleApiClient!!.isConnected) {
                    buildGoogleApiClient()
                }
            } else {
                gpsTracker.showSettingsAlert()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap!!.setOnCameraChangeListener { cameraPosition ->
            latLng = cameraPosition.target

            try {
                val mLocation = Location("")
                mLocation.latitude = latLng.latitude
                mLocation.longitude = latLng.longitude
                userLocation = mLocation

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mMap!!.setOnCameraIdleListener {
            if (ConnectionDetector.isConnected(this@MapLocationPickerActivity)) {
                SetLocationNameBackground(userLocation!!.latitude, userLocation!!.longitude).execute()
            }
        }
    }

    private fun showProgressBarMarker(show: Boolean) {
        if (show) {
            progress_bar_marker.visibility = View.VISIBLE
            you_are_here_text_view.visibility = View.GONE
            btn_select_location.isEnabled = false
            btn_select_location.alpha = 0.5f

        } else {
            progress_bar_marker.visibility = View.GONE
            you_are_here_text_view.visibility = View.VISIBLE
            btn_select_location.isEnabled = true
            btn_select_location.alpha = 1.0f
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    @Synchronized protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()

        try {
            mGoogleApiClient!!.connect()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPlayServices(): Boolean {
        val gApi = GoogleApiAvailability.getInstance()
        val resultCode = gApi.isGooglePlayServicesAvailable(this@MapLocationPickerActivity)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gApi.isUserResolvableError(resultCode)) {
                gApi.getErrorDialog(this@MapLocationPickerActivity, resultCode, 100).show()
            } else {
                Log.e("TAG", "This device is not supported.")
                //finish();
            }
            return false
        }
        return true
    }

    private fun changeMap(location: Location?) {
        if (mMap != null) {
            mMap!!.uiSettings.isZoomControlsEnabled = false

            if (location != null) {
                latLng = LatLng(location.latitude, location.longitude)
                userLocation = Location("")
                userLocation!!.latitude = latLng.latitude
                userLocation!!.longitude = latLng.longitude

                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(17f).build()

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this@MapLocationPickerActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                }

                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                mMap!!.uiSettings.isCompassEnabled = false
                mMap!!.uiSettings.isRotateGesturesEnabled = false
                mMap!!.uiSettings.isMapToolbarEnabled = false
                mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@MapLocationPickerActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (currentLocation != null) {
            locationLoaded = true
            changeMap(currentLocation)

        } else {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)

            } catch (e: Exception) { e.printStackTrace() }
        }

        try {
            val mLocationRequest = LocationRequest()
            mLocationRequest.interval = 10000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnectionSuspended(i: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e("TAG", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.errorCode)
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            currentLocation = location
            locationLoaded = true
            changeMap(currentLocation)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SetLocationNameBackground internal constructor(private val latitude: Double, private val longitude: Double) : AsyncTask<Void, Void, JSONArray>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressBarMarker(true)
        }

        override fun doInBackground(vararg voids: Void): JSONArray? {
            val locationNameParser = JsonParser()
            val params = HashMap<String, String>()

            params["latlng"] = latitude.toString() + "," + longitude
            params["sensor"] = "true"
            params["key"] = Constants.GOOGLE_API_KEY

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en")!!.equals("ar", ignoreCase = true)) {
                params["language"] = "ar"
            }
            val locationNameObject = locationNameParser.makeHttpRequest(Constants.GOOGLE_LOCATION_NAME_URL, "GET", params)

            if (locationNameObject != null) {
                try {
                    val locationNameArray = locationNameObject.getJSONArray("results")
                    if (locationNameArray != null) {
                        return locationNameArray
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            return null
        }

        override fun onPostExecute(locationArray: JSONArray?) {
            super.onPostExecute(locationArray)
            showProgressBarMarker(false)

            if (locationArray != null) {
                try {
                    //String locationName = locationArray.getJSONObject(0).getString("formatted_address");
                    var provinceName = ""
                    var locationName = ""
                    val addressComponents = locationArray.getJSONObject(0).getJSONArray("address_components")

                    for (i in 0 until addressComponents.length()) {
                        val types = addressComponents.getJSONObject(i).getJSONArray("types")

                        if (types.getString(0).equals("route", ignoreCase = true) || types.getString(0).equals("locality", ignoreCase = true) || types.length() > 1 && types.getString(1).equals("sublocality", ignoreCase = true)) {

                            if (locationName.trim().length > 0) {
                                locationName = locationName + ", " + addressComponents.getJSONObject(i).getString("long_name")

                            } else {
                                locationName = addressComponents.getJSONObject(i).getString("long_name")
                            }

                            if (types.getString(0).equals("locality", ignoreCase = true)) {
                                provinceName = addressComponents.getJSONObject(i).getString("long_name")
                            }
                        }
                    }

                    text_view_province.text = provinceName
                    val regex = "\\d+"

                    if (!locationName.matches(regex.toRegex())) {
                        type_location_text_view.text = locationName

                    } else {
                        type_location_text_view.setText(R.string.YouAreHere)
                    }
                    type_location_text_view.isSelected = true

                    placeItemSelectedKey = null
                    bookmark_location_image_view.setColorFilter(Color.TRANSPARENT)

                    val locationOne = Location("")
                    val locationTwo = Location("")

                    locationOne.latitude = latitude
                    locationOne.longitude = longitude

                    try {
                        val snappyDB = DBFactory.open(this@MapLocationPickerActivity, Constants.DYNA_DB)

                        val keys = snappyDB.findKeys(Constants.DYNA_DB_KEY)
                        var i=0

                        while(i<keys.size) {
                            placeItem = snappyDB.get(keys[i], PlaceItem::class.java)

                            locationTwo.latitude = placeItem.pLatitude?.toDouble()!!
                            locationTwo.longitude = placeItem.pLongitude?.toDouble()!!

                            if (locationOne.distanceTo(locationTwo) <= 10) {
                                bookmark_location_image_view.setColorFilter(Color.parseColor(Constants.FILTER_COLOR))
                                placeItemSelectedKey = keys[i]
                                break
                            }

                            i++
                        }
                        snappyDB.close()

                    } catch (e: SnappydbException) {
                        e.printStackTrace()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == RESULT_OK) {
            currentPlaceItem = data?.getSerializableExtra("PlaceItem") as PlaceItem

            try {
                val mLocation = Location("")
                mLocation.latitude = currentPlaceItem.pLatitude?.toDouble()!!
                mLocation.longitude = currentPlaceItem.pLongitude?.toDouble()!!
                userLocation = mLocation
            } catch (ignored: Exception) {
            }

            latLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
            val cameraPosition = CameraPosition.Builder().target(latLng).zoom(17f).build()
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

}
