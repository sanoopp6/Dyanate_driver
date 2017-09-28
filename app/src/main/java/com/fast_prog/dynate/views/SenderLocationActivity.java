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

public class SenderLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
        //NavigationView.OnNavigationItemSelectedListener,

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Ride ride;

    Boolean editRide;

    GoogleMap mMap;

    static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    GPSTracker gpsTracker;

    LatLng latLng;

    Button selectLocationButton;

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
    //private static TextView addressTextView;
    //TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(SenderLocationActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.select_start_place);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        ride = (Ride) getIntent().getSerializableExtra("ride");
        editRide = getIntent().getBooleanExtra("editRide", false);

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

        //if(preferences.getBoolean(Constants.PREFS_USER_AGENT, false)) {
        //    menu.findItem (R.id.nav_orders).setVisible(true);
        //    menu.findItem (R.id.nav_agent).setVisible(false);
        //
        //} else {
        //    menu.findItem (R.id.nav_orders).setVisible(false);
        //    menu.findItem (R.id.nav_agent).setVisible(true);
        //}

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mapViewSatellite = false;

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
                Intent intent = new Intent(SenderLocationActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        locationSelectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SenderLocationActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        searchLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SenderLocationActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        bookmarkLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (placeItemSelectedIndex == -1) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SenderLocationActivity.this);
                    LayoutInflater inflater1 = SenderLocationActivity.this.getLayoutInflater();
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
                            //for (int i = 0; i < placeItemList.size(); i++) {
                            //    if (placeItemList.get(i) == null) {
                            //        placeItemList.remove(i);
                            //    }
                            //}

                            PlaceItem placeItem = new PlaceItem();
                            placeItem.pLatitude = String.valueOf(userLocation.getLatitude());
                            placeItem.pLongitude = String.valueOf(userLocation.getLongitude());
                            placeItem.plName = typeLocationTextView.getText().toString();
                            placeItem.pVicinity = provinceNameTextView.getText().toString();
                            placeItemList.add(placeItem);
                            placeItemList.add(null);

                            try {
                                DB snappyDB = DBFactory.open(SenderLocationActivity.this, Constants.DYNA_DB);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SenderLocationActivity.this);
                        LayoutInflater inflater1 = SenderLocationActivity.this.getLayoutInflater();
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
                                    DB snappyDB = DBFactory.open(SenderLocationActivity.this, Constants.DYNA_DB);
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
                        //changeMapView.setImageResource(R.drawable.ic_earth2);

                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mapViewSatellite = false;
                        changeMapView.setColorFilter(Color.TRANSPARENT);
                        //changeMapView.setImageResource(R.drawable.ic_earth1);
                    }

                    mMap.getUiSettings().setZoomControlsEnabled(false);

                    latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();

                    if (ActivityCompat.checkSelfPermission(SenderLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(SenderLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(SenderLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

                    //if (locationLoaded) {
                    //    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    //    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                    //    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    //
                    //} else {
                    gpsTracker.getLocation();

                    if (gpsTracker.canGetLocation()) {
                        if (ActivityCompat.checkSelfPermission(SenderLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(SenderLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(SenderLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }

                        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                        if (currentLocation != null) {
                            locationLoaded = true;
                            userLocation = currentLocation;
                            changeMap(currentLocation);
                            //latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            //CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                            //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SenderLocationActivity.this);
                LayoutInflater inflater = SenderLocationActivity.this.getLayoutInflater();
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

                        if (!(typeLocationTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.type_your_location)))) {
                            ride.pickUpLocation = typeLocationTextView.getText().toString();
                            ride.pickUpLatitude = String.valueOf(userLocation.getLatitude());
                            ride.pickUpLongitude = String.valueOf(userLocation.getLongitude());

                            Intent intent = new Intent(SenderLocationActivity.this, ReceiverLocationActivity.class);
                            intent.putExtra("ride", ride);
                            intent.putExtra("editRide", editRide);
                            startActivity(intent);
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

        gpsTracker = new GPSTracker(SenderLocationActivity.this);

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
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(SenderLocationActivity.this);
//            LayoutInflater inflater = SenderLocationActivity.this.getLayoutInflater();
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
//                    if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
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
//                    Intent intent = new Intent(SenderLocationActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(SenderLocationActivity.this);
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
//            startActivity(new Intent(SenderLocationActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(SenderLocationActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(SenderLocationActivity.this);
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
//        //            startActivity(new Intent(SenderLocationActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(SenderLocationActivity.this, ChangeLanguageActivity.class));
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(SenderLocationActivity.this);
//            LayoutInflater inflater = SenderLocationActivity.this.getLayoutInflater();
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
//                    if (sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
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
//                    Intent intent = new Intent(SenderLocationActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(SenderLocationActivity.this);
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
//        //    ActivityCompat.finishAffinity(SenderLocationActivity.this);
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
            DB snappyDB = DBFactory.open(SenderLocationActivity.this, Constants.DYNA_DB);
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
        //    placeItemList.remove(i);
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
        //        if (!gpsTracker.canGetLocation()) {
        //            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        //                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //                // TODO: Consider calling
        //                //    ActivityCompat#requestPermissions
        //                // here to request the missing permissions, and then overriding
        //                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                //                                          int[] grantResults)
        //                // to handle the case where the user grants the permission. See the documentation
        //                // for ActivityCompat#requestPermissions for more details.
        //                return;
        //            }
        //
        //            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //
        //            if (currentLocation != null) {
        //                locationLoaded = true;
        //                userLocation = currentLocation;
        //                changeMap(currentLocation);
        //                //latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //                //CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
        //                //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //            }
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

        //Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //
        //if (editRide) {
        //    try {
        //        mLocation = new Location("");
        //        mLocation.setLatitude(Double.parseDouble(ride.pickUpLatitude));
        //        mLocation.setLongitude(Double.parseDouble(ride.pickUpLongitude));
        //    } catch (Exception e) {
        //    }
        //}
        //
        //latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        //userLocation = mLocation;
        //
        //if (gpsTracker.canGetLocation()) {
        //    currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //    locationLoaded = true;
        //}
        //
        //changeMap(userLocation);
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
            mLocation.setLatitude(Double.parseDouble(ride.pickUpLatitude));
            mLocation.setLongitude(Double.parseDouble(ride.pickUpLongitude));
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
                if (ConnectionDetector.isConnected(SenderLocationActivity.this)) {
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
        int resultCode = gApi.isGooglePlayServicesAvailable(SenderLocationActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gApi.isUserResolvableError(resultCode)) {
                gApi.getErrorDialog(SenderLocationActivity.this, resultCode, 100).show();
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

                    ActivityCompat.requestPermissions(SenderLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

            ActivityCompat.requestPermissions(SenderLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
                        //if (placeItemList.get(i) != null) {
                        //placeOneLat = Math.round(Double.parseDouble(placeItemList.get(i).pLatitude) * 1000.0) / 1000.0;
                        //placeOneLon = Math.round(Double.parseDouble(placeItemList.get(i).pLongitude) * 1000.0) / 1000.0;
                        //placeTwoLat = Math.round(latitude * 1000.0) / 1000.0;
                        //placeTwoLon = Math.round(longitude * 1000.0) / 1000.0;

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
                        //}
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
}
