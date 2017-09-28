package com.fast_prog.dynate.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.DirectionsJSONParser;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
        //, NavigationView.OnNavigationItemSelectedListener {

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    private GoogleMap mMap;

    private ImageView changeMapView;

    private Order order;

    private Boolean viewMap;

    private boolean mapViewSatellite;

    private Button switchButton;

    FrameLayout mapLayout;

    ScrollView detailsLayout;

    LatLng start;
    LatLng stop;

    Marker startMarker;
    Marker stopMarker;

    LatLngBounds.Builder builder;

    LatLngBounds bounds;

    TextView titleTrinNoTextView;
    TextView tripNoTextView;
    TextView titleVehicleTextView;
    TextView vehicleTextView;
    TextView fromTitleTextView;
    TextView fromNameTextView;
    TextView fromMobileTextView;
    TextView toTitleTextView;
    TextView toNameTextView;
    TextView toMobileTextView;
    TextView fromBadgeTextView;
    TextView fromAddrTextView;
    TextView toBadgeTextView;
    TextView toAddrTextView;
    TextView scheduleBadgeTextView;
    TextView scheduleDateTextView;
    TextView scheduleTimeTextView;
    TextView notesBadgeTextView;
    TextView subjectTextView;
    TextView notesTextView;

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    //TextView usernameTextView;
    //View tempView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(DetailActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.show_route);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        order = (Order) getIntent().getSerializableExtra("order");
        if(order == null) finish();

        builder = new LatLngBounds.Builder();

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
//        for (int i=0;i<menu.size();i++) {
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
        //tempView = findViewById(R.id.view_temp);

        mapViewSatellite = false;

        mapLayout = (FrameLayout) findViewById(R.id.map_frame);
        detailsLayout = (ScrollView) findViewById(R.id.detail_layout);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        changeMapView = (ImageView) findViewById(R.id.image_view_map_change_icon);

        Button cancelButton = (Button) findViewById(R.id.btn_cancel_trip);
        cancelButton.setTypeface(face);

        Button detailButton = (Button) findViewById(R.id.btn_replies);
        detailButton.setTypeface(face);

        switchButton = (Button) findViewById(R.id.btn_switch);
        switchButton.setTypeface(face);

        titleTrinNoTextView = (TextView) findViewById(R.id.title_trip_no);
        titleTrinNoTextView.setTypeface(face);

        tripNoTextView = (TextView) findViewById(R.id.txt_trip_no);
        tripNoTextView.setTypeface(face);
        tripNoTextView.setText(order.tripNo);

        titleVehicleTextView = (TextView) findViewById(R.id.title_vehicle);
        titleVehicleTextView.setTypeface(face);

        vehicleTextView = (TextView) findViewById(R.id.txt_vehicle);
        vehicleTextView.setTypeface(face);
        vehicleTextView.setText(order.vehicleModel);

        fromTitleTextView = (TextView) findViewById(R.id.from_title);
        fromTitleTextView.setTypeface(face);

        fromNameTextView = (TextView) findViewById(R.id.from_name);
        fromNameTextView.setTypeface(face);
        fromNameTextView.setText(order.tripFromName);

        fromMobileTextView = (TextView) findViewById(R.id.from_mobile);
        fromMobileTextView.setTypeface(face);

        toTitleTextView = (TextView) findViewById(R.id.to_title);
        toTitleTextView.setTypeface(face);

        toNameTextView = (TextView) findViewById(R.id.to_name);
        toNameTextView.setTypeface(face);
        toNameTextView.setText(order.tripToName);

        toMobileTextView = (TextView) findViewById(R.id.to_mobile);
        toMobileTextView.setTypeface(face);

        fromBadgeTextView = (TextView) findViewById(R.id.content_from_badge);
        fromBadgeTextView.setTypeface(face);

        fromAddrTextView = (TextView) findViewById(R.id.content_from_address);
        fromAddrTextView.setTypeface(face);
        fromAddrTextView.setText(order.tripFromAddress);

        toBadgeTextView = (TextView) findViewById(R.id.content_to_badge);
        toBadgeTextView.setTypeface(face);

        toAddrTextView = (TextView) findViewById(R.id.content_to_address);
        toAddrTextView.setTypeface(face);
        toAddrTextView.setText(order.tripToAddress);

        scheduleBadgeTextView = (TextView) findViewById(R.id.content_schedule_date_badge);
        scheduleBadgeTextView.setTypeface(face);

        scheduleDateTextView = (TextView) findViewById(R.id.content_schedule_date);
        scheduleDateTextView.setTypeface(face);
        scheduleDateTextView.setText(order.scheduleDate);

        scheduleTimeTextView = (TextView) findViewById(R.id.content_schedule_time);
        scheduleTimeTextView.setTypeface(face);
        scheduleTimeTextView.setText(order.scheduleTime);

        notesBadgeTextView = (TextView) findViewById(R.id.content_notes_badge);
        notesBadgeTextView.setTypeface(face);

        subjectTextView = (TextView) findViewById(R.id.content_subject);
        subjectTextView.setTypeface(face);
        subjectTextView.setText(order.tripSubject);

        notesTextView = (TextView) findViewById(R.id.content_notes);
        notesTextView.setTypeface(face);
        notesTextView.setText(order.tripNotes);

        if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
            vehicleTextView.setTextDirection(View.TEXT_DIRECTION_RTL);
            //fromMobileTextView.setText(String.format("%s+", order.getTripFromMob().replace("+", "")));
            //toMobileTextView.setText(String.format("%s+", order.getTripToMob().replace("+", "")));
        } else {
            vehicleTextView.setTextDirection(View.TEXT_DIRECTION_LTR);
            //fromMobileTextView.setText(order.getTripFromMob());
            //toMobileTextView.setText(order.getTripToMob());
        }

        fromMobileTextView.setText(order.tripFromMob);
        toMobileTextView.setText(order.tripToMob);

        viewMap = true;

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewMap) {
                    viewMap = false;

                    //tempView.setVisibility(View.VISIBLE);
                    detailsLayout.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.GONE);
                    switchButton.setText(getResources().getString(R.string.show_route));

                    String title = getResources().getString(R.string.shipment_details);
                    TextView titleTextView = new TextView(getApplicationContext());
                    titleTextView.setText(title);
                    titleTextView.setTextSize(16);
                    titleTextView.setAllCaps(true);
                    titleTextView.setTypeface(face, Typeface.BOLD);
                    titleTextView.setTextColor(Color.WHITE);
                    getSupportActionBar().setCustomView(titleTextView);

                } else {
                    viewMap = true;

                    //tempView.setVisibility(View.GONE);
                    detailsLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.VISIBLE);
                    switchButton.setText(getResources().getString(R.string.show_details));

                    String title = getResources().getString(R.string.show_route);
                    TextView titleTextView = new TextView(getApplicationContext());
                    titleTextView.setText(title);
                    titleTextView.setTextSize(16);
                    titleTextView.setAllCaps(true);
                    titleTextView.setTypeface(face, Typeface.BOLD);
                    titleTextView.setTextColor(Color.WHITE);
                    getSupportActionBar().setCustomView(titleTextView);
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
                        changeMapView.setImageResource(R.drawable.ic_earth2);

                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mapViewSatellite = false;
                        changeMapView.setImageResource(R.drawable.ic_earth1);
                    }

                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);
                    mMap.getUiSettings().setRotateGesturesEnabled(false);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                }
            }
        });

        if(order.tripStatus.matches("3|4")) {
            cancelButton.setVisibility(View.GONE);
        }

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ShowDetailsActivity.class);
                intent.putExtra("item", order);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
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

                        new TripMasterStatusUpdateBackground(order.tripId).execute();
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

        new UpdateTripNotifiedCustStatusBackground(order.tripId).execute();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }

        if (myCircularProgressDialog != null && myCircularProgressDialog.isShowing()) {
            myCircularProgressDialog.cancel();
        }
    }

    private class UpdateTripNotifiedCustStatusBackground extends AsyncTask<Void, Void, JSONObject> {
        String tripId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        UpdateTripNotifiedCustStatusBackground(String tripId) {
            this.tripId = tripId;
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripDMId", tripId);
            params.put("ArgTripDIsNotifiedCust", "true");

            String BASE_URL = Constants.BASE_URL_EN + "UpdateTripNotifiedCustStatus";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "UpdateTripNotifiedCustStatus";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(final JSONObject response) {
            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        //Log.e("success");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
//            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
//            LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(DetailActivity.this);
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
//            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(DetailActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(DetailActivity.this);
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
//        //            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(DetailActivity.this, ChangeLanguageActivity.class));
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
//            LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(DetailActivity.this);
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
//        //    ActivityCompat.finishAffinity(DetailActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            start = new LatLng(Double.parseDouble(order.tripFromLat), Double.parseDouble(order.tripFromLng));
            stop = new LatLng(Double.parseDouble(order.tripToLat), Double.parseDouble(order.tripToLng));
        } catch (Exception e) {
            start = new LatLng(0,0);
            stop = new LatLng(0,0);
        }

        builder.include(start);
        builder.include(stop);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                bounds = builder.build();

                Location l1 = new Location("l1");
                l1.setLatitude(start.latitude);
                l1.setLongitude(start.longitude);

                Location l2 = new Location("l2");
                l2.setLatitude(stop.latitude);
                l2.setLongitude(stop.longitude);

                Float distanceTo = l1.distanceTo(l2);

                if(distanceTo > 100)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15.0f));
            }
        });

        View marker1 = getLayoutInflater().inflate(R.layout.cab_marker_green, null);

        if (marker1 != null) {
            startMarker = googleMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(DetailActivity.this, marker1))));
        }

        View marker2 = getLayoutInflater().inflate(R.layout.cab_marker_red, null);

        if (marker2 != null) {
            stopMarker = googleMap.addMarker(new MarkerOptions().position(stop).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(DetailActivity.this, marker2))));
        }

        String url = getDirectionsUrl(start, stop);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        }catch(Exception e){
            e.printStackTrace();

        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);

            } catch(Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            //MarkerOptions markerOptions = new MarkerOptions();
            //String distance = "";
            //String duration = "";

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0) {    // Get distance from the list
                        //distance = (String)point.get("distance");
                        continue;
                    } else if(j==1) { // Get duration from the list
                        //duration = (String)point.get("duration");
                        continue;
                    }

                    try {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        builder.include(position);
                        points.add(position);

                    } catch (Exception ignored) {
                    }
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null)
                mMap.addPolyline(lineOptions);
        }
    }

    private class TripMasterStatusUpdateBackground extends AsyncTask<Void, Void, JSONObject> {
        String tripDMId;

        TripMasterStatusUpdateBackground(String tripDMId) {
            this.tripDMId = tripDMId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(DetailActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripMID", tripDMId);
            params.put("ArgTripMStatus", "4");

            String BASE_URL = Constants.BASE_URL_EN + "TripMasterStatusUpdate";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "TripMasterStatusUpdate";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }


        protected void onPostExecute(final JSONObject response) {
            myCircularProgressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.canceled_succesfully);
                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                finish();
                            }
                        });
                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        alertDialog.show();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.canceling_failed);
                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
