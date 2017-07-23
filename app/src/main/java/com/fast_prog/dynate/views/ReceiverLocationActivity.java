package com.fast_prog.dynate.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.PlaceItem;
import com.fast_prog.dynate.models.Ride;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.CustomTypefaceSpan;
import com.fast_prog.dynate.utilities.GPSTracker;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.SetOffline;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ReceiverLocationActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Ride ride;

    Boolean editRide;

    private GoogleMap mMap;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    GPSTracker gpsTracker;

    private LatLng latLng;

    private static TextView typeLocationTextView;
    private static TextView provinceNameTextView;
    private static TextView addressTextView;

    private static Location userLocation;

    private ImageView changeMapView;

    private boolean mapViewSatellite;

    TextView usernameTextView;
    //TextView titleView;
    TextView titleAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_location);

        face = Typeface.createFromAsset(ReceiverLocationActivity.this.getAssets(), Constants.FONT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        String title = getResources().getString(R.string.select_stop_place);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ride = (Ride) getIntent().getSerializableExtra("ride");
        editRide = getIntent().getBooleanExtra("editRide", false);

        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        usernameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        usernameTextView.setText(preferences.getString(Constants.PREFS_USER_NAME, ""));

        for (int i=0;i<menu.size();i++) {
            MenuItem mi = menu.getItem(i);
            SpannableString s = new SpannableString(mi.getTitle());
            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s);
        }

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

        Button previewButton = (Button) findViewById(R.id.btn_preview);
        previewButton.setTypeface(face);

        TextView youAreHereTextView = (TextView) findViewById(R.id.you_are_here_text_view);
        youAreHereTextView.setTypeface(face);

        typeLocationTextView = (TextView) findViewById(R.id.type_location_text_view);
        typeLocationTextView.setTypeface(face);

        provinceNameTextView = (TextView) findViewById(R.id.text_view_province);
        provinceNameTextView.setTypeface(face);

        addressTextView = (TextView) findViewById(R.id.txt_address);
        addressTextView.setTypeface(face);

        //titleView = (TextView) findViewById(R.id.title_view);
        //titleView.setTypeface(face);
        //titleView.startAnimation(getBlinkAnimation());

        titleAddress = (TextView) findViewById(R.id.title_address);
        titleAddress.setTypeface(face);

        View locationSelectImageView = findViewById(R.id.location_select_gps_image_view);
        View searchLocationImageView = findViewById(R.id.search_location_image_view);

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

        changeMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    if(!mapViewSatellite) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mapViewSatellite = true;
                        changeMapView.setImageResource(R.drawable.ic_earth1);

                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mapViewSatellite = false;
                        changeMapView.setImageResource(R.drawable.ic_earth2);
                    }

                    mMap.getUiSettings().setZoomControlsEnabled(false);

                    latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();

                    if (ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(false);
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

                    if (!gpsTracker.canGetLocation()) {
                        gpsTracker.showSettingsAlert();

                    } else {
                        latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }
        });

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(typeLocationTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.type_your_location)))) {
                    ride.setDropOffLocation(typeLocationTextView.getText().toString());
                    ride.setDropOffLatitude(String.valueOf(userLocation.getLatitude()));
                    ride.setDropOffLongitude(String.valueOf(userLocation.getLongitude()));

                    Location loc1 = new Location("SenderLocation");
                    Location loc2 = new Location("ReceiverLocation");

                    try {
                        loc1.setLatitude(Double.parseDouble(ride.getPickUpLatitude()));
                        loc1.setLongitude(Double.parseDouble(ride.getPickUpLongitude()));
                        loc2.setLatitude(Double.parseDouble(ride.getDropOffLatitude()));
                        loc2.setLongitude(Double.parseDouble(ride.getDropOffLongitude()));

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
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();

                                Intent intent = new Intent(ReceiverLocationActivity.this, ConfirmFromToActivity.class);
                                intent.putExtra("ride", ride);
                                startActivity(intent);
                            }
                        });
                        view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        btnOk.setTypeface(face);
                        btnCancel.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();

                    } else {
                        Intent intent = new Intent(ReceiverLocationActivity.this, ConfirmFromToActivity.class);
                        intent.putExtra("ride", ride);
                        startActivity(intent);
                    }
                    //new GetDistance().execute();
                } else {
                    addressTextView.setError(getResources().getString(R.string.required));
                }
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back_option) {
            finish();
        }

        if (id == R.id.exit_option) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverLocationActivity.this);
            LayoutInflater inflater = ReceiverLocationActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.are_you_sure);
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if(prefs.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                        new SetOffline(prefs.getString(Constants.PREFS_USER_ID, "")).execute();
                    }
                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
                    //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
                    editor.putString(Constants.PREFS_USER_ID, "0");
                    editor.putString(Constants.PREFS_CUST_ID, "0");
                    editor.putString(Constants.PREFS_USER_NAME, "0");
                    editor.putString(Constants.PREFS_USER_MOBILE, "");
                    editor.putString(Constants.PREFS_SHARE_URL, "");
                    editor.putString(Constants.PREFS_LATITUDE, "");
                    editor.putString(Constants.PREFS_LONGITUDE, "");
                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
                    editor.putString(Constants.PREFS_IS_FACTORY, "");
                    editor.commit();

                    Intent intent = new Intent(ReceiverLocationActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(ReceiverLocationActivity.this);
                    startActivity(intent);
                    finish();
                }
            });
            Button btnOK = (Button) view.findViewById(R.id.btn_ok);
            btnOK.setTypeface(face);
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
            btnCancel.setTypeface(face);
            txtAlert.setTypeface(face);
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(ReceiverLocationActivity.this, HomeActivity.class));
        }

        //if (id == R.id.nav_orders) {
        //    startActivity(new Intent(ReceiverLocationActivity.this, MyOrdersActivity.class));
        //}
        //if (id == R.id.nav_agent) {
        //    final MyCircularProgressDialog progressDialog;
        //    progressDialog = new MyCircularProgressDialog(ReceiverLocationActivity.this);
        //    progressDialog.setCancelable(false);
        //    progressDialog.show();
        //
        //    Handler handler = new Handler();
        //    handler.postDelayed(new Runnable() {
        //        public void run() {
        //            progressDialog.dismiss();
        //
        //            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        //
        //            SharedPreferences.Editor editor = preferences.edit();
        //            editor.putBoolean(Constants.PREFS_USER_AGENT, true);
        //            editor.commit();
        //
        //            startActivity(new Intent(ReceiverLocationActivity.this, HomeActivity.class));
        //            finish();
        //        }
        //    }, 2000);
        //}

        if (id == R.id.nav_language) {
            startActivity(new Intent(ReceiverLocationActivity.this, ChangeLanguageActivity.class));
        }

        if (id == R.id.nav_share) {
            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.download_dynate) + " " + preferences.getString(Constants.PREFS_SHARE_URL, ""));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverLocationActivity.this);
            LayoutInflater inflater = ReceiverLocationActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.are_you_sure);
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if(prefs.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                        new SetOffline(prefs.getString(Constants.PREFS_USER_ID, "")).execute();
                    }
                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
                    //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
                    editor.putString(Constants.PREFS_USER_ID, "0");
                    editor.putString(Constants.PREFS_CUST_ID, "0");
                    editor.putString(Constants.PREFS_USER_NAME, "0");
                    editor.putString(Constants.PREFS_USER_MOBILE, "");
                    editor.putString(Constants.PREFS_SHARE_URL, "");
                    editor.putString(Constants.PREFS_LATITUDE, "");
                    editor.putString(Constants.PREFS_LONGITUDE, "");
                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
                    editor.putString(Constants.PREFS_IS_FACTORY, "");
                    editor.commit();

                    Intent intent = new Intent(ReceiverLocationActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(ReceiverLocationActivity.this);
                    startActivity(intent);
                    finish();
                }
            });
            Button btnOK = (Button) view.findViewById(R.id.btn_ok);
            btnOK.setTypeface(face);
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
            btnCancel.setTypeface(face);
            txtAlert.setTypeface(face);
            dialog.show();
        }

        //if (id == R.id.nav_exit) {
        //    SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        //
        //    if(preferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
        //        SharedPreferences.Editor editor = preferences.edit();
        //        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
        //        editor.commit();
        //
        //        new SetOffline(preferences.getString(Constants.PREFS_USER_ID, "")).execute();
        //    }
        //
        //    ActivityCompat.finishAffinity(ReceiverLocationActivity.this);
        //    finish();
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        gpsTracker = new GPSTracker(ReceiverLocationActivity.this);

        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }

        if (editRide)
            try {
                latLng = new LatLng(Double.parseDouble(ride.getDropOffLatitude()), Double.parseDouble(ride.getDropOffLongitude()));
            } catch (Exception e) {
                latLng = new LatLng(0, 0);
            }
        else
            latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        Location mLocation = new Location("");
        mLocation.setLatitude(latLng.latitude);
        mLocation.setLongitude(latLng.longitude);
        userLocation = mLocation;

        if (ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));

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

    private class SetLocationNameBackground extends AsyncTask<Void, Void, JSONArray> {
        private JsonParser locationNameParser;
        private SharedPreferences sharedPreferences;
        private Double latitude, longitude;

        SetLocationNameBackground(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            locationNameParser = new JsonParser();
            sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
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
                        addressTextView.setText(locationName);

                    } else {
                        typeLocationTextView.setText(R.string.use_pin_location);
                    }
                    typeLocationTextView.setSelected(true);
                    addressTextView.setSelected(true);

                    if (ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(ReceiverLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
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
                mLocation.setLatitude(Double.parseDouble(currentPlaceItem.getpLatitude()));
                mLocation.setLongitude(Double.parseDouble(currentPlaceItem.getpLongitude()));
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
    //                                Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
    //                                Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
    //                                view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
    //                                    @Override
    //                                    public void onClick(View v) {
    //                                        dialog1.dismiss();
    //
    //                                        Intent intent = new Intent(ReceiverLocationActivity.this, ConfirmFromToActivity.class);
    //                                        intent.putExtra("ride", ride);
    //                                        startActivity(intent);
    //                                    }
    //                                });
    //                                view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
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
    //                            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    //                            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
    //                            btnOk.setText(R.string.ok);
    //                            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
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
