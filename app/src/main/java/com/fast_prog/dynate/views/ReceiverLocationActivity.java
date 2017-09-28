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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import com.fast_prog.dynate.models.Ride;
import com.fast_prog.dynate.utilities.ConnectionDetector;
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

public class ReceiverLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
        //NavigationView.OnNavigationItemSelectedListener,

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Ride ride;

    Boolean editRide;

    GoogleMap mMap;

    static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    GPSTracker gpsTracker;

    LatLng latLng;

    Button previewButton;

    TextView typeLocationTextView;
    TextView provinceNameTextView;
    TextView youAreHereTextView;

    static Location userLocation;
    static Location currentLocation;
    static boolean locationLoaded = false;

    ImageView changeMapView;
    ImageView bookmarkLocationImageView;

    boolean mapViewSatellite;

    ProgressBar progressBarMarker;

    SharedPreferences sharedPreferences;

    List<PlaceItem> placeItemList;

    int placeItemSelectedIndex;

    GoogleApiClient mGoogleApiClient;

    AlertDialog alertDialog;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;

    //TextView usernameTextView;
    //TextView titleAddress;
    //TextView addressTextView;
    //TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(ReceiverLocationActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.select_stop_place);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        ride = (Ride) getIntent().getSerializableExtra("ride");
        editRide = getIntent().getBooleanExtra("editRide", false);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        Menu menu = navigationView.getMenu();
//        usernameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
//        usernameTextView.setText(sharedPreferences.getString(Constants.PREFS_USER_NAME, ""));
//
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem mi = menu.getItem(i);
//            SpannableString s = new SpannableString(mi.getTitle());
//            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            mi.setTitle(s);
//        }
//
        //if(preferences.getBoolean(Constants.PREFS_USER_AGENT, false)) {
        //    menu.findItem (R.id.nav_orders).setVisible(true);
        //    menu.findItem (R.id.nav_agent).setVisible(false);
        //
        //} else {
        //    menu.findItem (R.id.nav_orders).setVisible(false);
        //    menu.findItem (R.id.nav_agent).setVisible(true);
        //}

        mapViewSatellite = false;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        previewButton = (Button) findViewById(R.id.btn_preview);
        previewButton.setTypeface(face);

        progressBarMarker = (ProgressBar) findViewById(R.id.progress_bar_marker);

        youAreHereTextView = (TextView) findViewById(R.id.you_are_here_text_view);
        youAreHereTextView.setTypeface(face);

        typeLocationTextView = (TextView) findViewById(R.id.type_location_text_view);
        typeLocationTextView.setTypeface(face);

        provinceNameTextView = (TextView) findViewById(R.id.text_view_province);
        provinceNameTextView.setTypeface(face);

        //addressTextView = (TextView) findViewById(R.id.txt_address);
        //addressTextView.setTypeface(face);
        //titleView = (TextView) findViewById(R.id.title_view);
        //titleView.setTypeface(face);
        //titleView.startAnimation(getBlinkAnimation());
        //titleAddress = (TextView) findViewById(R.id.title_address);
        //titleAddress.setTypeface(face);

        View locationSelectImageView = findViewById(R.id.location_select_gps_image_view);
        View searchLocationImageView = findViewById(R.id.search_location_image_view);

        bookmarkLocationImageView = (ImageView) findViewById(R.id.bookmark_location_image_view);

        ImageView currentLocationView = (ImageView) findViewById(R.id.image_view_current_location_icon);
        changeMapView = (ImageView) findViewById(R.id.image_view_map_change_icon);

        typeLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiverLocationActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        locationSelectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiverLocationActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        searchLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiverLocationActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        bookmarkLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (placeItemSelectedIndex == -1) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ReceiverLocationActivity.this);
                    LayoutInflater inflater1 = ReceiverLocationActivity.this.getLayoutInflater();
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
                                DB snappyDB = DBFactory.open(ReceiverLocationActivity.this, Constants.DYNA_DB);
                                snappyDB.del(Constants.DYNA_DB_KEY);
                                snappyDB.put(Constants.DYNA_DB_KEY, placeItemList);
                                snappyDB.close();

                                bookmarkLocationImageView.setColorFilter(Color.parseColor(Constants.FILTER_COLOR));
                                placeItemList.remove(placeItemList.size() - 1);
                                placeItemSelectedIndex = placeItemList.size() - 1;

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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReceiverLocationActivity.this);
                        LayoutInflater inflater1 = ReceiverLocationActivity.this.getLayoutInflater();
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
                                    DB snappyDB = DBFactory.open(ReceiverLocationActivity.this, Constants.DYNA_DB);
                                    snappyDB.del(Constants.DYNA_DB_KEY);
                                    snappyDB.put(Constants.DYNA_DB_KEY, placeItemList);
                                    snappyDB.close();

                                    bookmarkLocationImageView.setColorFilter(Color.TRANSPARENT);
                                    placeItemList.remove(placeItemList.size() - 1);
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
                    if (!mapViewSatellite) {
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

                    if (ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ReceiverLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
                        if (ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(ReceiverLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverLocationActivity.this);
                LayoutInflater inflater = ReceiverLocationActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.alert_dialog, null);
                builder.setView(view);
                TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                txtAlert.setText(R.string.are_you_sure);
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                        if(!(typeLocationTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.type_your_location)))) {
                            ride.dropOffLocation = typeLocationTextView.getText().toString();
                            ride.dropOffLatitude = String.valueOf(userLocation.getLatitude());
                            ride.dropOffLongitude = String.valueOf(userLocation.getLongitude());

                            Location loc1 = new Location("SenderLocation");
                            Location loc2 = new Location("ReceiverLocation");

                            try {
                                loc1.setLatitude(Double.parseDouble(ride.pickUpLatitude));
                                loc1.setLongitude(Double.parseDouble(ride.pickUpLongitude));
                                loc2.setLatitude(Double.parseDouble(ride.dropOffLatitude));
                                loc2.setLongitude(Double.parseDouble(ride.dropOffLongitude));

                            } catch (Exception e) {
                                gpsTracker.getLocation();

                                loc1.setLatitude(gpsTracker.getLatitude());
                                loc1.setLongitude(gpsTracker.getLongitude());
                                loc2.setLatitude(gpsTracker.getLatitude());
                                loc2.setLongitude(gpsTracker.getLongitude());
                            }

                            float distanceInMeters = loc1.distanceTo(loc2);

                            if(distanceInMeters < 1000.0) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ReceiverLocationActivity.this);
                                LayoutInflater inflater1 = ReceiverLocationActivity.this.getLayoutInflater();
                                final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                                builder1.setView(view1);
                                TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                                txtAlert1.setText(getResources().getString(R.string.distance_within_a_km));
                                alertDialog = builder1.create();
                                alertDialog.setCancelable(false);
                                Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                                Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
                                view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();

                                        Intent intent = new Intent(ReceiverLocationActivity.this, ConfirmFromToActivity.class);
                                        intent.putExtra("ride", ride);
                                        startActivity(intent);
                                    }
                                });
                                view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                btnOk.setTypeface(face);
                                btnCancel.setTypeface(face);
                                txtAlert1.setTypeface(face);
                                alertDialog.show();

                            } else {
                                Intent intent = new Intent(ReceiverLocationActivity.this, ConfirmFromToActivity.class);
                                intent.putExtra("ride", ride);
                                startActivity(intent);
                            }
                            //new GetDistance().execute();
                        }
                    }
                });
                Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                btnOK.setTypeface(face);
                Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                btnCancel.setTypeface(face);
                txtAlert.setTypeface(face);
                alertDialog.show();
            }
        });

        gpsTracker = new GPSTracker(ReceiverLocationActivity.this);

        previewButton.setEnabled(false);
        previewButton.setAlpha(0.5f);
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

    //public Animation getBlinkAnimation(){
    //    Animation animation = new AlphaAnimation(1, 0);         // Change alpha from fully visible to invisible
    //    animation.setDuration(300);                             // duration - half a second
    //    animation.setInterpolator(new LinearInterpolator());    // do not alter animation rate
    //    animation.setRepeatCount(-1);                            // Repeat animation infinitely
    //    animation.setRepeatMode(Animation.REVERSE);             // Reverse animation at the end so the button will fade back in
    //
    //    return animation;
    //}
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
////        if (id == R.id.back_option) {
////            finish();
////        }
//
//        if (id == R.id.exit_option) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverLocationActivity.this);
//            LayoutInflater inflater = ReceiverLocationActivity.this.getLayoutInflater();
//            final View view = inflater.inflate(R.layout.alert_dialog, null);
//            builder.setView(view);
//            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
//            txtAlert.setText(R.string.are_you_sure);
//            alertDialog = builder.create();
//            alertDialog.setCancelable(false);
//            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                    if(sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
//                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
//                        new SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute();
//                    }
//
//                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
//                    editor.putString(Constants.PREFS_USER_ID, "0");
//                    editor.putString(Constants.PREFS_CUST_ID, "0");
//                    editor.putString(Constants.PREFS_USER_NAME, "0");
//                    editor.putString(Constants.PREFS_USER_MOBILE, "");
//                    editor.putString(Constants.PREFS_SHARE_URL, "");
//                    editor.putString(Constants.PREFS_LATITUDE, "");
//                    editor.putString(Constants.PREFS_LONGITUDE, "");
//                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
//                    editor.putString(Constants.PREFS_IS_FACTORY, "");
//                    //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
//                    editor.commit();
//
//                    Intent intent = new Intent(ReceiverLocationActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ReceiverLocationActivity.this);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            Button btnOK = (Button) view.findViewById(R.id.btn_ok);
//            btnOK.setTypeface(face);
//            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
//            btnCancel.setTypeface(face);
//            txtAlert.setTypeface(face);
//            alertDialog.show();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            startActivity(new Intent(ReceiverLocationActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(ReceiverLocationActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(ReceiverLocationActivity.this);
//        //    progressDialog.setCancelable(false);
//        //    progressDialog.show();
//        //
//        //    Handler handler = new Handler();
//        //    handler.postDelayed(new Runnable() {
//        //        public void run() {
//        //            progressDialog.dismiss();
//        //
//        //            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//        //
//        //            SharedPreferences.Editor editor = preferences.edit();
//        //            editor.putBoolean(Constants.PREFS_USER_AGENT, true);
//        //            editor.commit();
//        //
//        //            startActivity(new Intent(ReceiverLocationActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(ReceiverLocationActivity.this, ChangeLanguageActivity.class));
//        }
//
//        if (id == R.id.nav_share) {
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.download_dynate) + " " + sharedPreferences.getString(Constants.PREFS_SHARE_URL, ""));
//            sendIntent.setType("text/plain");
//            startActivity(sendIntent);
//        }
//
//        if (id == R.id.nav_logout) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverLocationActivity.this);
//            LayoutInflater inflater = ReceiverLocationActivity.this.getLayoutInflater();
//            final View view = inflater.inflate(R.layout.alert_dialog, null);
//            builder.setView(view);
//            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
//            txtAlert.setText(R.string.are_you_sure);
//            alertDialog = builder.create();
//            alertDialog.setCancelable(false);
//            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                    if(sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
//                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
//                        new SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute();
//                    }
//
//                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
//                    editor.putString(Constants.PREFS_USER_ID, "0");
//                    editor.putString(Constants.PREFS_CUST_ID, "0");
//                    editor.putString(Constants.PREFS_USER_NAME, "0");
//                    editor.putString(Constants.PREFS_USER_MOBILE, "");
//                    editor.putString(Constants.PREFS_SHARE_URL, "");
//                    editor.putString(Constants.PREFS_LATITUDE, "");
//                    editor.putString(Constants.PREFS_LONGITUDE, "");
//                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
//                    editor.putString(Constants.PREFS_IS_FACTORY, "");
//                    //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
//                    editor.commit();
//
//                    Intent intent = new Intent(ReceiverLocationActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ReceiverLocationActivity.this);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            Button btnOK = (Button) view.findViewById(R.id.btn_ok);
//            btnOK.setTypeface(face);
//            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
//            btnCancel.setTypeface(face);
//            txtAlert.setTypeface(face);
//            alertDialog.show();
//        }
//
//        //if (id == R.id.nav_exit) {
//        //    SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//        //
//        //    if(preferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
//        //        SharedPreferences.Editor editor = preferences.edit();
//        //        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
//        //        editor.commit();
//        //
//        //        new SetOffline(preferences.getString(Constants.PREFS_USER_ID, "")).execute();
//        //    }
//        //
//        //    ActivityCompat.finishAffinity(ReceiverLocationActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
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
            DB snappyDB = DBFactory.open(ReceiverLocationActivity.this, Constants.DYNA_DB);
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

        //gpsTracker = new GPSTracker(ReceiverLocationActivity.this);
        //
        //if (!gpsTracker.canGetLocation()) {
        //    gpsTracker.showSettingsAlert();
        //}
        //
        //if (editRide) {
        //    try {
        //        latLng = new LatLng(Double.parseDouble(ride.dropOffLatitude), Double.parseDouble(ride.dropOffLongitude));
        //    } catch (Exception e) {
        //        latLng = new LatLng(0, 0);
        //    }
        //} else {
        //    latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        //}
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
        //if (ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        //        && ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        if (editRide) {
            Location mLocation = new Location("");
            mLocation.setLatitude(Double.parseDouble(ride.dropOffLatitude));
            mLocation.setLongitude(Double.parseDouble(ride.dropOffLongitude));
            userLocation = mLocation;

            latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

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
                if (ConnectionDetector.isConnected(ReceiverLocationActivity.this)) {
                    new SetLocationNameBackground(userLocation.getLatitude(), userLocation.getLongitude()).execute();
                } else {
                    ConnectionDetector.errorSnackbar(coordinatorLayout);
                }
            }
        });
    }

    private void showProgressBarMarker(boolean show) {
        if (show) {
            progressBarMarker.setVisibility(View.VISIBLE);
            youAreHereTextView.setVisibility(View.GONE);
            previewButton.setEnabled(false);
            previewButton.setAlpha(0.5f);

        } else {
            progressBarMarker.setVisibility(View.GONE);
            youAreHereTextView.setVisibility(View.VISIBLE);
            previewButton.setEnabled(true);
            previewButton.setAlpha(1.0f);
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
        int resultCode = gApi.isGooglePlayServicesAvailable(ReceiverLocationActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gApi.isUserResolvableError(resultCode)) {
                gApi.getErrorDialog(ReceiverLocationActivity.this, resultCode, 100).show();
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

                    ActivityCompat.requestPermissions(ReceiverLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

            ActivityCompat.requestPermissions(ReceiverLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (currentLocation != null) {
            locationLoaded = true;
            if (!editRide) {
                changeMap(currentLocation);
            }

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
            if (!editRide) {
                changeMap(currentLocation);
            }
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == RESULT_OK) {
            PlaceItem currentPlaceItem = (PlaceItem) data.getSerializableExtra("PlaceItem");

            try {
                Location mLocation = new Location("");
                mLocation.setLatitude(Double.parseDouble(currentPlaceItem.pLatitude));
                mLocation.setLongitude(Double.parseDouble(currentPlaceItem.pLongitude));
                userLocation = mLocation;
            } catch (Exception ignored) {
            }

            latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    //private class GetDistance extends AsyncTask<Void, Void, JSONObject> {
    //    JsonParser jsonParser;
    //    MyCircularProgressDialog progressDialog;
    //
    //    @Override
    //    protected void onPreExecute() {
    //        super.onPreExecute();
    //        progressDialog = new MyCircularProgressDialog(ReceiverLocationActivity.this);
    //        progressDialog.setCancelable(false);
    //        progressDialog.show();
    //    }
    //
    //    protected JSONObject doInBackground(Void... param) {
    //        jsonParser = new JsonParser();
    //
    //        HashMap<String, String> params = new HashMap<>();
    //
    //        params.put("ArgoriginLatLng", ride.getPickUpLatitude()+","+ride.getPickUpLongitude());
    //        params.put("ArgdestinationLatLng", ride.getDropOffLatitude()+","+ride.getDropOffLongitude());
    //
    //        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    //
    //        JSONObject json;
    //
    //        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "GetDistance", "POST", params);
    //
    //        } else {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "GetDistance", "POST", params);
    //        }
    //
    //        return json;
    //    }
    //
    //    protected void onPostExecute(final JSONObject response) {
    //        Handler handler = new Handler();
    //        handler.postDelayed(new Runnable() {
    //            public void run() {
    //                progressDialog.dismiss();
    //
    //                if (response != null) {
    //                    try {
    //                        // Parsing json object response
    //                        // response will be a json object
    //                        if (response.getBoolean("status")) {
    //
    //                            if(response.getDouble("data") < 1000.0) {
    //                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ReceiverLocationActivity.this);
    //                                LayoutInflater inflater1 = ReceiverLocationActivity.this.getLayoutInflater();
    //                                final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
    //                                builder1.setView(view1);
    //                                TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
    //                                txtAlert1.setText(getResources().getString(R.string.distance_within_a_km));
    //                                final AlertDialog dialog1 = builder1.create();
    //                                dialog1.setCancelable(false);
    //                                Button btnOk = (Button) view1.findViewById(R.id.btn_green_rounded);
    //                                Button btnCancel = (Button) view1.findViewById(R.id.btn_red_rounded);
    //                                view1.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
    //                                    @Override
    //                                    public void onClick(View v) {
    //                                        dialog1.dismiss();
    //
    //                                        Intent intent = new Intent(ReceiverLocationActivity.this, ConfirmFromToActivity.class);
    //                                        intent.putExtra("ride", ride);
    //                                        startActivity(intent);
    //                                    }
    //                                });
    //                                view1.findViewById(R.id.btn_red_rounded).setOnClickListener(new View.OnClickListener() {
    //                                    @Override
    //                                    public void onClick(View v) {
    //                                        dialog1.dismiss();
    //                                    }
    //                                });
    //                                btnOk.setTypeface(face);
    //                                btnCancel.setTypeface(face);
    //                                txtAlert1.setTypeface(face);
    //                                dialog1.show();
    //
    //                            } else {
    //                                Intent intent = new Intent(ReceiverLocationActivity.this, ConfirmFromToActivity.class);
    //                                intent.putExtra("ride", ride);
    //                                startActivity(intent);
    //                            }
    //
    //                        } else {
    //                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ReceiverLocationActivity.this);
    //                            LayoutInflater inflater1 = ReceiverLocationActivity.this.getLayoutInflater();
    //                            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
    //                            builder1.setView(view1);
    //                            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
    //                            txtAlert1.setText(response.getString("message"));
    //                            final AlertDialog dialog1 = builder1.create();
    //                            dialog1.setCancelable(false);
    //                            view1.findViewById(R.id.btn_red_rounded).setVisibility(View.GONE);
    //                            Button btnOk = (Button) view1.findViewById(R.id.btn_green_rounded);
    //                            btnOk.setText(R.string.ok);
    //                            view1.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
    //                                @Override
    //                                public void onClick(View v) {
    //                                    dialog1.dismiss();
    //                                }
    //                            });
    //                            btnOk.setTypeface(face);
    //                            txtAlert1.setTypeface(face);
    //                            dialog1.show();
    //                        }
    //
    //                    } catch (JSONException e) {
    //                        e.printStackTrace();
    //                    }
    //                } else {
    //                    snackbar = Snackbar.make(coordinatorLayout, R.string.network_error, Snackbar.LENGTH_LONG)
    //                            .setAction(R.string.ok, new View.OnClickListener() {
    //                                @Override
    //                                public void onClick(View view) {
    //                                }
    //                            });
    //                    snackbar.show();
    //                }
    //            }
    //        }, 2000);
    //    }
    //}
}
