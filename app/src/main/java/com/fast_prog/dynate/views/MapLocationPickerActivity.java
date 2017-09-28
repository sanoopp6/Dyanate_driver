package com.fast_prog.dynate.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.PlaceItem;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.GPSTracker;
import com.fast_prog.dynate.utilities.JsonParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapLocationPickerActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Typeface face;

    GoogleMap mMap;

    static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    GPSTracker gpsTracker;

    LatLng latLng;

    Button selectLocationButton;

    TextView youAreHereTextView;
    TextView typeLocationTextView;
    TextView provinceNameTextView;

    static Location userLocation;
    static Location currentLocation;
    static boolean locationLoaded = false;

    ImageView changeMapView;
    ImageView bookmarkLocationImageView;

    boolean mapViewSatellite;

    ProgressBar progressBarMarker;

    PlaceItem currentPlaceItem;

    List<PlaceItem> placeItemList;

    int placeItemSelectedIndex;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    GoogleApiClient mGoogleApiClient;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;

    //Boolean selectList;
    //TextView addressTextView;
    //View locationSelectImageView;
    //View searchLocationImageView;
    //ImageView currentLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location_picker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(MapLocationPickerActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.map_location_picker);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        mapViewSatellite = false;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        selectLocationButton = (Button) findViewById(R.id.btn_select_location);
        selectLocationButton.setTypeface(face);

        progressBarMarker = (ProgressBar) findViewById(R.id.progress_bar_marker);

        youAreHereTextView = (TextView) findViewById(R.id.you_are_here_text_view);
        youAreHereTextView.setTypeface(face);

        typeLocationTextView = (TextView) findViewById(R.id.type_location_text_view);
        typeLocationTextView.setTypeface(face);

        provinceNameTextView = (TextView) findViewById(R.id.text_view_province);
        provinceNameTextView.setTypeface(face);

        //addressTextView = (TextView) findViewById(R.id.txt_address);
        //addressTextView.setTypeface(face);

        View locationSelectImageView = findViewById(R.id.location_select_gps_image_view);
        View searchLocationImageView = findViewById(R.id.search_location_image_view);

        ImageView currentLocationView = (ImageView) findViewById(R.id.image_view_current_location_icon);
        changeMapView = (ImageView) findViewById(R.id.image_view_map_change_icon);

        bookmarkLocationImageView = (ImageView) findViewById(R.id.bookmark_location_image_view);

        //if(selectList) { }
        typeLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapLocationPickerActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        locationSelectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapLocationPickerActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        searchLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapLocationPickerActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        bookmarkLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (placeItemSelectedIndex == -1) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MapLocationPickerActivity.this);
                    LayoutInflater inflater1 = MapLocationPickerActivity.this.getLayoutInflater();
                    final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                    builder1.setView(view1);
                    TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                    txtAlert1.setText(R.string.do_you_want_add_bookmark);
                    alertDialog = builder1.create();
                    alertDialog.setCancelable(false);
                    Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
                    Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                    view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();

                            PlaceItem placeItem = new PlaceItem();
                            placeItem.pLatitude = String.valueOf(userLocation.getLatitude());
                            placeItem.pLongitude = String.valueOf(userLocation.getLongitude());
                            placeItem.plName = typeLocationTextView.getText().toString();
                            placeItem.pVicinity = provinceNameTextView.getText().toString();
                            placeItemList.add(placeItem);
                            placeItemList.add(null);

                            try {
                                DB snappyDB = DBFactory.open(MapLocationPickerActivity.this, Constants.DYNA_DB);
                                snappyDB.del(Constants.DYNA_DB_KEY);
                                snappyDB.put(Constants.DYNA_DB_KEY, placeItemList);
                                snappyDB.close();

                                bookmarkLocationImageView.setColorFilter(Color.parseColor(Constants.FILTER_COLOR));
                                placeItemList.remove(placeItemList.size() - 1);
                                placeItemSelectedIndex = placeItemList.size()-1;

                            } catch (SnappydbException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    btnCancel.setTypeface(face);
                    btnOk.setTypeface(face);
                    txtAlert1.setTypeface(face);
                    alertDialog.show();

                } else {
                    if (placeItemList != null) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapLocationPickerActivity.this);
                        LayoutInflater inflater1 = MapLocationPickerActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.do_you_want_remove_bookmark);
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                                PlaceItem placeItem = placeItemList.get(placeItemSelectedIndex);
                                placeItemList.remove(placeItem);
                                placeItemList.add(null);

                                try {
                                    DB snappyDB = DBFactory.open(MapLocationPickerActivity.this, Constants.DYNA_DB);
                                    snappyDB.del(Constants.DYNA_DB_KEY);
                                    snappyDB.put(Constants.DYNA_DB_KEY, placeItemList);
                                    snappyDB.close();

                                    bookmarkLocationImageView.setColorFilter(Color.TRANSPARENT);
                                    placeItemList.remove(placeItemList.size()-1);
                                    placeItemSelectedIndex = -1;

                                } catch (SnappydbException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        btnCancel.setTypeface(face);
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        alertDialog.show();
                    }
                }
            }
        });

        changeMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    if(!mapViewSatellite) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mapViewSatellite = true;
                        changeMapView.setColorFilter(Color.parseColor(Constants.FILTER_COLOR));
                        //changeMapView.setImageResource(R.drawable.ic_earth1);

                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mapViewSatellite = false;
                        changeMapView.setColorFilter(Color.TRANSPARENT);
                        //changeMapView.setImageResource(R.drawable.ic_earth2);
                    }

                    mMap.getUiSettings().setZoomControlsEnabled(false);

                    latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();

                    if (ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MapLocationPickerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }

                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);
                    mMap.getUiSettings().setRotateGesturesEnabled(false);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        currentLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    gpsTracker.getLocation();

                    //if (locationLoaded) {
                    //    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    //    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                    //    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    //
                    //} else {
                    gpsTracker.getLocation();

                    if (gpsTracker.canGetLocation()) {
                        if (ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MapLocationPickerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }

                        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                        if (currentLocation != null) {
                            locationLoaded = true;
                            userLocation = currentLocation;
                            changeMap(currentLocation);
                        }

                    } else {
                        gpsTracker.showSettingsAlert();
                    }
                    //}
                }
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(typeLocationTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.type_your_location)))) {
                    Intent intent = new Intent();
                    intent.putExtra("location", typeLocationTextView.getText());
                    intent.putExtra("latitude", userLocation.getLatitude());
                    intent.putExtra("longitude", userLocation.getLongitude());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        gpsTracker = new GPSTracker(MapLocationPickerActivity.this);

        selectLocationButton.setEnabled(false);
        selectLocationButton.setAlpha(0.5f);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//
//        MenuItem menuLogout = menu.findItem(R.id.exit_option);
//        menuLogout.setVisible(false);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.back_option) {
//            finish();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        gpsTracker.getLocation();

        if (checkPlayServices()) {
            if (gpsTracker.canGetLocation()) {
                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    buildGoogleApiClient();
                }
            } else {
                gpsTracker.showSettingsAlert();
            }
        }

        placeItemList = new ArrayList<>();

        try {
            DB snappyDB = DBFactory.open(MapLocationPickerActivity.this, Constants.DYNA_DB);
            PlaceItem[] placeItemArray = snappyDB.getObjectArray(Constants.DYNA_DB_KEY, PlaceItem.class);
            if (placeItemArray != null && placeItemArray.length > 0) {
                placeItemList = new LinkedList<>(Arrays.asList(placeItemArray));
            }
            snappyDB.close();

        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        //for (int i = 0; i < placeItemList.size(); i++) {
        //    Log.e("index_" + i + "_plName",placeItemList.get(i).plName);
        //    Log.e("index_" + i + "_pLatitude",placeItemList.get(i).pLatitude);
        //    Log.e("index_" + i + "_pLongitude",placeItemList.get(i).pLongitude);
        //}
        //
        //for (int i = placeItemList.size()-1; i >= 0; i--) {
        //    placeItemList.remove(i);
        //}
        //
        //try {
        //    DB snappyDB = DBFactory.open(SenderLocationActivity.this, Constants.DYNA_DB);
        //    snappyDB.put(Constants.DYNA_DB_KEY, placeItemList);
        //    snappyDB.close();
        //
        //} catch (SnappydbException e) {
        //    e.printStackTrace();
        //}
        //
        //if (gpsTracker != null && mMap != null) {
        //    //if (locationLoaded) {
        //    //    latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        //    //    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
        //    //    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //    //
        //    //} else {
        //    if (!locationLoaded) {
        //        gpsTracker.getLocation();
        //
        //        if (gpsTracker.canGetLocation()) {
        //            currentLocation = new Location("");
        //            currentLocation.setLatitude(gpsTracker.getLatitude());
        //            currentLocation.setLongitude(gpsTracker.getLongitude());
        //            userLocation = currentLocation;
        //            locationLoaded = true;
        //            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
        //            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //
        //        } else {
        //            gpsTracker.showSettingsAlert();
        //        }
        //    }
        //}
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //gpsTracker = new GPSTracker(MapLocationPickerActivity.this);
        //
        //if (!gpsTracker.canGetLocation()) {
        //    gpsTracker.showSettingsAlert();
        //}
        //
        //latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        //
        //Location mLocation = new Location("");
        //mLocation.setLatitude(latLng.latitude);
        //mLocation.setLongitude(latLng.longitude);
        //userLocation = mLocation;
        //
        //if (gpsTracker.canGetLocation()) {
        //    currentLocation = new Location("");
        //    currentLocation.setLatitude(latLng.latitude);
        //    currentLocation.setLongitude(latLng.longitude);
        //    locationLoaded = true;
        //}
        //
        //if (ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        //        && ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //    // TODO: Consider calling
        //    //    ActivityCompat#requestPermissions
        //    // here to request the missing permissions, and then overriding
        //    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //    //                                          int[] grantResults)
        //    // to handle the case where the user grants the permission. See the documentation
        //    // for ActivityCompat#requestPermissions for more details.
        //    return;
        //}
        //
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mMap.getUiSettings().setCompassEnabled(false);
        //mMap.getUiSettings().setRotateGesturesEnabled(false);
        //mMap.getUiSettings().setMapToolbarEnabled(false);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                latLng = cameraPosition.target;

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(latLng.latitude);
                    mLocation.setLongitude(latLng.longitude);
                    userLocation = mLocation;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {
                new SetLocationNameBackground(userLocation.getLatitude(), userLocation.getLongitude()).execute();
            }
        });
    }

    private void showProgressBarMarker(boolean show) {
        if (show) {
            progressBarMarker.setVisibility(View.VISIBLE);
            youAreHereTextView.setVisibility(View.GONE);
            selectLocationButton.setEnabled(false);
            selectLocationButton.setAlpha(0.5f);

        } else {
            progressBarMarker.setVisibility(View.GONE);
            youAreHereTextView.setVisibility(View.VISIBLE);
            selectLocationButton.setEnabled(true);
            selectLocationButton.setAlpha(1.0f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int resultCode = gApi.isGooglePlayServicesAvailable(MapLocationPickerActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gApi.isUserResolvableError(resultCode)) {
                gApi.getErrorDialog(MapLocationPickerActivity.this, resultCode, 100).show();
            } else {
                Log.e("TAG", "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);

            if (location != null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                userLocation = new Location("");
                userLocation.setLatitude(latLng.latitude);
                userLocation.setLongitude(latLng.longitude);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MapLocationPickerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapLocationPickerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (currentLocation != null) {
            locationLoaded = true;
            changeMap(currentLocation);

        } else {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLocation = location;
            locationLoaded = true;
            changeMap(currentLocation);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private class SetLocationNameBackground extends AsyncTask<Void, Void, JSONArray> {
        private Double latitude, longitude;

        SetLocationNameBackground(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBarMarker(true);
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            JsonParser locationNameParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("latlng", latitude + "," + longitude);
            params.put("sensor", "true");

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                params.put("language", "ar");
            }
            JSONObject locationNameObject = locationNameParser.makeHttpRequest(Constants.GOOGLE_LOCATION_NAME_URL, "GET", params);

            if (locationNameObject != null) {
                try {
                    JSONArray locationNameArray = locationNameObject.getJSONArray("results");
                    if (locationNameArray != null) {
                        return locationNameArray;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray locationArray) {
            super.onPostExecute(locationArray);
            showProgressBarMarker(false);

            if (locationArray != null) {
                try {
                    //String locationName = locationArray.getJSONObject(0).getString("formatted_address");
                    String provinceName = "";
                    String locationName = "";
                    JSONArray addressComponents = locationArray.getJSONObject(0).getJSONArray("address_components");

                    for (int i = 0; i < addressComponents.length(); i++) {
                        JSONArray types = addressComponents.getJSONObject(i).getJSONArray("types");

                        if ((types.getString(0).equalsIgnoreCase("route") || types.getString(0).equalsIgnoreCase("locality"))
                                || (types.length() > 1 && types.getString(1).equalsIgnoreCase("sublocality"))) {

                            if (locationName.trim().length() > 0) {
                                locationName = locationName + ", " + addressComponents.getJSONObject(i).getString("long_name");

                            } else {
                                locationName = addressComponents.getJSONObject(i).getString("long_name");
                            }

                            if (types.getString(0).equalsIgnoreCase("locality")) {
                                provinceName = addressComponents.getJSONObject(i).getString("long_name");
                            }
                        }
                    }

                    provinceNameTextView.setText(provinceName);
                    String regex = "\\d+";

                    if (!locationName.matches(regex)) {
                        typeLocationTextView.setText(locationName);

                    } else {
                        typeLocationTextView.setText(R.string.use_pin_location);
                    }
                    typeLocationTextView.setSelected(true);

                    placeItemSelectedIndex = -1;
                    bookmarkLocationImageView.setColorFilter(Color.TRANSPARENT);

                    Location locationOne, locationTwo;

                    for (int i = 0; i < placeItemList.size(); i++) {
                        locationOne = new Location("");
                        locationTwo = new Location("");

                        locationOne.setLatitude(Double.parseDouble(placeItemList.get(i).pLatitude));
                        locationOne.setLongitude(Double.parseDouble(placeItemList.get(i).pLongitude));
                        locationTwo.setLatitude(latitude);
                        locationTwo.setLongitude(longitude);

                        if (locationOne.distanceTo(locationTwo) <= 10) {
                            bookmarkLocationImageView.setColorFilter(Color.parseColor(Constants.FILTER_COLOR));
                            placeItemSelectedIndex = i;
                        }
                    }

//                    if (userLocation != null && currentLocation != null) {
//                        if (userLocation.distanceTo(currentLocation) > 50) {
//                            youAreHereTextView.setText(R.string.i_live_here);
//
//                        } else {
//                            youAreHereTextView.setText(R.string.you_are_here);
//                        }
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == RESULT_OK) {
            currentPlaceItem = (PlaceItem) data.getSerializableExtra("PlaceItem");

            try {
                Location mLocation = new Location("");
                mLocation.setLatitude(Double.parseDouble(currentPlaceItem.pLatitude));
                mLocation.setLongitude(Double.parseDouble(currentPlaceItem.pLongitude));
                userLocation = mLocation;
            } catch (Exception ignored){
            }

            latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

}
