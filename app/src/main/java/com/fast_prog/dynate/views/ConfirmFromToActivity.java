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
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.models.Ride;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.CustomTypefaceSpan;
import com.fast_prog.dynate.utilities.DatabaseHandler;
import com.fast_prog.dynate.utilities.DirectionsJSONParser;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.fast_prog.dynate.utilities.SetOffline;
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

import org.json.JSONArray;
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

public class ConfirmFromToActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Ride ride = new Ride();

    private GoogleMap mMap;

    private ImageView changeMapView;

    private boolean mapViewSatellite;

    TextView usernameTextView;
    TextView distanceTextView;

    FrameLayout mapLayout;

    LatLng start;
    LatLng stop;

    Marker startMarker;
    Marker stopMarker;

    Button confirmButton;
    Button detailsButton;

    LatLngBounds.Builder builder;

    LatLngBounds bounds;

    private String tripID;

    String distanceStr = "";
    String durationStr = "";

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_from_to);

        face = Typeface.createFromAsset(ConfirmFromToActivity.this.getAssets(), Constants.FONT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        String title = getResources().getString(R.string.sending_site);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        ride = (Ride) getIntent().getSerializableExtra("ride");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

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

        builder = new LatLngBounds.Builder();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mapViewSatellite = false;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        confirmButton = (Button) findViewById(R.id.btn_confirm_route);
        confirmButton.setTypeface(face);

        detailsButton = (Button) findViewById(R.id.btn_show_details);
        detailsButton.setTypeface(face);

        mapLayout = (FrameLayout) findViewById(R.id.map_frame);
        distanceTextView = (TextView) findViewById(R.id.txt_distance);
        distanceTextView.startAnimation(getBlinkAnimation());

        changeMapView = (ImageView) findViewById(R.id.image_view_map_change_icon);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmFromToActivity.this);
                LayoutInflater inflater = ConfirmFromToActivity.this.getLayoutInflater();
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

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmFromToActivity.this);
                        LayoutInflater inflater1 = ConfirmFromToActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.are_you_ready_to_accept_this_trip);
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                                new BookVehicle().execute();
                            }
                        });
                        Button btnOK = (Button) view1.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
                        btnCancel.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        alertDialog.show();
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

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmFromToActivity.this, ConfirmDetailsActivity.class);
                intent.putExtra("ride", ride);
                startActivity(intent);
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
    }

    public Animation getBlinkAnimation(){
        Animation animation = new AlphaAnimation(1, 0);          // Change alpha from fully visible to invisible
        animation.setDuration(300);                              // duration - half a second
        animation.setInterpolator(new LinearInterpolator());     // do not alter animation rate
        animation.setRepeatCount(-1);                            // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);              // Reverse animation at the end so the button will fade back in

        return animation;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmFromToActivity.this);
            LayoutInflater inflater = ConfirmFromToActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(ConfirmFromToActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(ConfirmFromToActivity.this);
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
            startActivity(new Intent(ConfirmFromToActivity.this, HomeActivity.class));
        }

        //if (id == R.id.nav_orders) {
        //    startActivity(new Intent(ConfirmFromToActivity.this, MyOrdersActivity.class));
        //}
        //if (id == R.id.nav_agent) {
        //    final MyCircularProgressDialog progressDialog;
        //    progressDialog = new MyCircularProgressDialog(ConfirmFromToActivity.this);
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
        //            startActivity(new Intent(ConfirmFromToActivity.this, HomeActivity.class));
        //            finish();
        //        }
        //    }, 2000);
        //}

        if (id == R.id.nav_language) {
            startActivity(new Intent(ConfirmFromToActivity.this, ChangeLanguageActivity.class));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmFromToActivity.this);
            LayoutInflater inflater = ConfirmFromToActivity.this.getLayoutInflater();
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
                    editor.putString(Constants.PREFS_USER_NAME, "0");
                    editor.putString(Constants.PREFS_USER_MOBILE, "");
                    editor.putString(Constants.PREFS_SHARE_URL, "");
                    editor.putString(Constants.PREFS_LATITUDE, "");
                    editor.putString(Constants.PREFS_LONGITUDE, "");
                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
                    editor.putString(Constants.PREFS_IS_FACTORY, "");
                    editor.commit();

                    Intent intent = new Intent(ConfirmFromToActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(ConfirmFromToActivity.this);
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
        //    ActivityCompat.finishAffinity(ConfirmFromToActivity.this);
        //    finish();
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            start = new LatLng(Double.parseDouble(ride.getPickUpLatitude()), Double.parseDouble(ride.getPickUpLongitude()));
            stop = new LatLng(Double.parseDouble(ride.getDropOffLatitude()), Double.parseDouble(ride.getDropOffLongitude()));
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
            startMarker = googleMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(ConfirmFromToActivity.this, marker1))));
        }

        View marker2 = getLayoutInflater().inflate(R.layout.cab_marker_red, null);

        if (marker2 != null) {
            stopMarker = googleMap.addMarker(new MarkerOptions().position(stop).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(ConfirmFromToActivity.this, marker2))));
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

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distanceStr = point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        durationStr = point.get("duration");
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
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
                ride.setDistanceStr(getResources().getString(R.string.distance) + " : " + distanceStr + ", " + getResources().getString(R.string.duration) + " : " + durationStr);
                distanceTextView.setText(ride.getDistanceStr());
            }

            confirmButton.setVisibility(View.VISIBLE);
            detailsButton.setVisibility(View.VISIBLE);
        }
    }

    private class BookVehicle extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyCircularProgressDialog(ConfirmFromToActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgTripMCustId", preferences.getString(Constants.PREFS_CUST_ID, ""));
            params.put("ArgTripMScheduleDate", ride.getDate());
            params.put("ArgTripMScheduleTime", ride.getTime());
            params.put("ArgTripMFromLat", ride.getPickUpLatitude());
            params.put("ArgTripMFromLng", ride.getPickUpLongitude());
            params.put("ArgTripMFromAddress", ride.getPickUpLocation());
            params.put("ArgTripMFromIsSelf", ride.getFromself()+"");
            params.put("ArgTripMFromName", ride.getFromName());
            params.put("ArgTripMFromMob", "966"+ride.getFromMobile());
            params.put("ArgTripMToLat", ride.getDropOffLatitude());
            params.put("ArgTripMToLng", ride.getDropOffLongitude());
            params.put("ArgTripMToAddress", ride.getDropOffLocation());
            params.put("ArgTripMToIsSelf", ride.getToself()+"");
            params.put("ArgTripMToName", ride.getToName());
            params.put("ArgTripMToMob", "966"+ride.getToMobile());
            params.put("ArgTripMSubject", ride.getSubject());
            params.put("ArgTripMNotes", ride.getShipment());
            params.put("ArgTripMVsId", ride.getVehicleSizeId());
            params.put("ArgTripMCustLat", "0");
            params.put("ArgTripMCustLng", "0");
            params.put("ArgTripMNoOfDrivers", "0");
            params.put("ArgTripMDistanceRadiusKm", "0");
            params.put("ArgTripMDistanceString", ride.getDistanceStr());

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "AddTripMaster", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "AddTripMaster", "POST", params);
            }

            return json;
        }

        protected void onPostExecute(final JSONObject response) {
            progressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        DatabaseHandler dbHandler = new DatabaseHandler(ConfirmFromToActivity.this);
                        dbHandler.truncateTable();

                        try {
                            tripID = ((int) Double.parseDouble(response.getString("data"))) + "";
                        } catch (Exception ignored) {
                        }
                        new AddTripDetails().execute();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmFromToActivity.this);
                        LayoutInflater inflater1 = ConfirmFromToActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(response.getString("message"));
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                snackbar = Snackbar.make(coordinatorLayout, R.string.network_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                snackbar.show();
            }
        }
    }

    private class AddTripDetails extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgTripDMID", tripID);
            params.put("ArgTripDDmId", preferences.getString(Constants.PREFS_USER_ID, ""));
            params.put("ArgTripDRate", "0");
            params.put("ArgTripDIsNegotiable", "false");

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "AddTripDetails", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "AddTripDetails", "POST", params);
            }

            return json;
        }

        protected void onPostExecute(final JSONObject response) {
            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        //final SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                        //SharedPreferences.Editor editor = preferences.edit();
                        //editor.putString(Constants.PREFS_ONLINE_STATUS, "online");
                        //editor.commit();
                        //startActivity(new Intent(ConfirmFromToActivity.this, HomeActivity.class));
                        //ActivityCompat.finishAffinity(ConfirmFromToActivity.this);
                        //finish();
                        new ShowTripRequests().execute();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmFromToActivity.this);
                        LayoutInflater inflater1 = ConfirmFromToActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(response.getString("message"));
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ShowTripRequests extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgTripDDmId", preferences.getString(Constants.PREFS_USER_ID, "0"));

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripDIsNotifiedList", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDIsNotifiedList", "POST", params);
            }

            return json;
        }

        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        JSONArray ordersJSONArray = response.getJSONArray("data");

                        if (ordersJSONArray.length() > 0) {
                            //boolean isExist = false;
                            for (int i = 0; i < ordersJSONArray.length(); i++) {
                                final Order order = new Order();

                                order.setTripId(ordersJSONArray.getJSONObject(i).getString("TripMID").trim());
                                order.setTripNo(ordersJSONArray.getJSONObject(i).getString("TripMNo").trim());
                                order.setTripFromAddress(ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim());
                                order.setTripFromLat(ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim());
                                order.setTripFromLng(ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim());
                                try {
                                    order.setTripfromSelf(Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf")));
                                } catch (Exception e) {
                                    order.setTripfromSelf(false);
                                }
                                order.setTripFromName(ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim());
                                order.setTripFromMob(ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim());
                                order.setTripToAddress(ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim());
                                order.setTripToLat(ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim());
                                order.setTripToLng(ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim());
                                try {
                                    order.setTripToSelf(Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf")));
                                } catch (Exception e) {
                                    order.setTripToSelf(false);
                                }
                                order.setTripToName(ordersJSONArray.getJSONObject(i).getString("TripMToName").trim());
                                order.setTripToMob(ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim());
                                order.setVehicleModel(ordersJSONArray.getJSONObject(i).getString("VMName").trim());
                                order.setVehicleType(ordersJSONArray.getJSONObject(i).getString("VmoName").trim());
                                order.setScheduleDate(ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim());
                                order.setScheduleTime(ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim());
                                order.setUserName(ordersJSONArray.getJSONObject(i).getString("UsrName").trim());
                                order.setUserMobile(ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim());
                                order.setTripFilter(ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim());
                                order.setTripStatus(ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim());
                                order.setTripSubject(ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim());
                                order.setTripNotes(ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim());
                                order.setVehicleImage(ordersJSONArray.getJSONObject(i).getString("VmoURL").trim());
                                order.setTripdId(ordersJSONArray.getJSONObject(i).getString("TripDID").trim());
                                order.setDistance(ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim());

                                Intent intent = new Intent(ConfirmFromToActivity.this, ReplyActivity.class);
                                intent.putExtra("alarm", true);
                                intent.putExtra("order", order);
                                intent.putExtra("fromTripAdd", true);
                                startActivity(intent);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //private class AutoAllocateNearestDriverByCustLatLngTripMId extends AsyncTask<Void, Void, JSONObject> {
    //    private JsonParser driverCheckParser;
    //
    //    @Override
    //    protected void onPreExecute() {
    //        super.onPreExecute();
    //    }
    //
    //    @Override
    //    protected JSONObject doInBackground(Void... voids) {
    //        driverCheckParser = new JsonParser();
    //
    //        HashMap<String, String> params = new HashMap<>();
    //
    //        if (isMyLocation) {
    //            params.put("ArgFromlat", latLng.latitude + "");
    //            params.put("ArgFromlng", latLng.longitude + "");
    //
    //        } else {
    //            params.put("ArgFromlat", ride.getPickUpLatitude() + "");
    //            params.put("ArgFromlng", ride.getPickUpLongitude() + "");
    //        }
    //
    //        params.put("ArgTripMID", tripID);
    //        params.put("ArgNoOfDrivers", count+"");
    //        params.put("ArgDistanceRadiusKm", distance+"");
    //
    //        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    //
    //        JSONObject json;
    //
    //        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
    //            json = driverCheckParser.makeHttpRequest(Constants.BASE_URL_AR + "AutoAllocateNearestDriverByCustLatLngTripMId", "POST", params);
    //
    //        } else {
    //            json = driverCheckParser.makeHttpRequest(Constants.BASE_URL_EN + "AutoAllocateNearestDriverByCustLatLngTripMId", "POST", params);
    //        }
    //
    //        return json;
    //    }
    //
    //    @Override
    //    protected void onPostExecute(JSONObject response) {
    //        if (response != null) {
    //            try {
    //                if (response.getBoolean("status")) {
    //                    JSONArray driversJsonArray = response.getJSONArray("data");
    //
    //                    if (driversJsonArray.length() > 0) {
    //                        List<Drivers> drivers = new ArrayList<>();
    //
    //                        for (int i = 0; i < driversJsonArray.length(); i++) {
    //                            Drivers driver = new Drivers();
    //
    //                            driver.setDmId(driversJsonArray.getJSONObject(i).getString("DmId").trim());
    //                            driver.setDmName(driversJsonArray.getJSONObject(i).getString("DmName").trim());
    //                            driver.setDmAddress(driversJsonArray.getJSONObject(i).getString("DmAddress").trim());
    //                            driver.setDmMobNumber(driversJsonArray.getJSONObject(i).getString("DmMobNumber").trim());
    //                            driver.setDmLatitude(driversJsonArray.getJSONObject(i).getString("DmLatitude").trim());
    //                            driver.setDmLongitude(driversJsonArray.getJSONObject(i).getString("DmLongitude").trim());
    //                            driver.setDistanceKm(driversJsonArray.getJSONObject(i).getString("DistanceKm").trim());
    //                            driver.setAccepted(false);
    //                            driver.setRejected(false);
    //
    //                            drivers.add(driver);
    //                        }
    //
    //                        Intent intent = new Intent(ConfirmFromToActivity.this, WaitDriverActivity.class);
    //                        if (isMyLocation) {
    //                            intent.putExtra("from_lat", latLng.latitude);
    //                            intent.putExtra("from_long", latLng.longitude);
    //                        } else {
    //                            intent.putExtra("from_lat", Double.parseDouble(ride.getPickUpLatitude()));
    //                            intent.putExtra("from_long", Double.parseDouble(ride.getPickUpLongitude()));
    //                        }
    //
    //                        intent.putExtra("trip_id", tripID);
    //                        intent.putExtra("drivers", (Serializable) drivers);
    //                        startActivity(intent);
    //
    //                    } else {
    //                        confirmButton.setVisibility(View.GONE);
    //                        detailsButton.setVisibility(View.GONE);
    //                        retryButton.setVisibility(View.VISIBLE);
    //
    //                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmFromToActivity.this);
    //                        LayoutInflater inflater1 = ConfirmFromToActivity.this.getLayoutInflater();
    //                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
    //                        builder1.setView(view1);
    //                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
    //                        txtAlert1.setText(R.string.no_drivers_available);
    //                        final AlertDialog dialog1 = builder1.create();
    //                        dialog1.setCancelable(false);
    //                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    //                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
    //                        btnOk.setText(R.string.ok);
    //                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
    //                            @Override
    //                            public void onClick(View v) {
    //                                dialog1.dismiss();
    //                            }
    //                        });
    //                        btnOk.setTypeface(face);
    //                        txtAlert1.setTypeface(face);
    //                        dialog1.show();
    //                    }
    //                } else {
    //                    confirmButton.setVisibility(View.GONE);
    //                    detailsButton.setVisibility(View.GONE);
    //                    retryButton.setVisibility(View.VISIBLE);
    //
    //                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmFromToActivity.this);
    //                    LayoutInflater inflater1 = ConfirmFromToActivity.this.getLayoutInflater();
    //                    final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
    //                    builder1.setView(view1);
    //                    TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
    //                    txtAlert1.setText(R.string.no_drivers_available);
    //                    final AlertDialog dialog1 = builder1.create();
    //                    dialog1.setCancelable(false);
    //                    view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    //                    Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
    //                    btnOk.setText(R.string.ok);
    //                    view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
    //                        @Override
    //                        public void onClick(View v) {
    //                            dialog1.dismiss();
    //                        }
    //                    });
    //                    btnOk.setTypeface(face);
    //                    txtAlert1.setTypeface(face);
    //                    dialog1.show();
    //                }
    //            } catch (JSONException e) {
    //                confirmButton.setVisibility(View.GONE);
    //                detailsButton.setVisibility(View.GONE);
    //                retryButton.setVisibility(View.VISIBLE);
    //
    //                e.printStackTrace();
    //            }
    //        } else {
    //            confirmButton.setVisibility(View.GONE);
    //            detailsButton.setVisibility(View.GONE);
    //            retryButton.setVisibility(View.VISIBLE);
    //
    //            AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmFromToActivity.this);
    //            LayoutInflater inflater1 = ConfirmFromToActivity.this.getLayoutInflater();
    //            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
    //            builder1.setView(view1);
    //            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
    //            txtAlert1.setText(R.string.no_drivers_available);
    //            final AlertDialog dialog1 = builder1.create();
    //            dialog1.setCancelable(false);
    //            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    //            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
    //            btnOk.setText(R.string.ok);
    //            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View v) {
    //                    dialog1.dismiss();
    //                }
    //            });
    //            btnOk.setTypeface(face);
    //            txtAlert1.setTypeface(face);
    //            dialog1.show();
    //        }
    //    }
    //}

}
