package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.models.Ride;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.DatabaseHandler;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ConfirmDetailsActivity extends AppCompatActivity {
        //implements NavigationView.OnNavigationItemSelectedListener {

    Ride ride;

    static Typeface face;

    Snackbar snackbar;

    CoordinatorLayout coordinatorLayout;

    TextView subTitleTextView;
    TextView shipTitleTextView;
    TextView senderTitleTextView;
    TextView fromNameTextView;
    TextView fromMobileTextView;
    TextView fromMobTitleTextView;
    TextView dateTitleTextView;
    TextView timeTitleTextView;
    TextView receiverTitleTextView;
    TextView toNameTextView;
    TextView toMobileTextView;
    TextView toMobTitleTextView;
    TextView titleTextView1;

    private String tripID;

    AlertDialog alertDialog;

    SharedPreferences sharedPreferences;

    MyCircularProgressDialog myCircularProgressDialog;

    //TextView usernameTextView;
    //TextView typeTitleTextView;
    //TextView modelTitleTextView;
    //ImageView fromMobImageView;
    //ImageView toMobImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(ConfirmDetailsActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.confirm_detail);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        ride = (Ride) getIntent().getSerializableExtra("ride");

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

        Button confirmTripButton = (Button) findViewById(R.id.btn_confirm_route);
        confirmTripButton.setTypeface(face);

        Button editDetailsButton = (Button) findViewById(R.id.btn_edit_details);
        editDetailsButton.setTypeface(face);

        titleTextView1 = (TextView) findViewById(R.id.trip_det_title);
        titleTextView1.setTypeface(face, Typeface.BOLD);

        subTitleTextView = (TextView) findViewById(R.id.sub_title);
        subTitleTextView.setTypeface(face);

        shipTitleTextView = (TextView) findViewById(R.id.ship_title);
        shipTitleTextView.setTypeface(face);

        //modelTitleTextView = (TextView) findViewById(R.id.model_title);
        //modelTitleTextView.setTypeface(face);
        //typeTitleTextView = (TextView) findViewById(R.id.type_title);
        //typeTitleTextView.setTypeface(face);

        senderTitleTextView = (TextView) findViewById(R.id.sender_det_title);
        senderTitleTextView.setTypeface(face, Typeface.BOLD);

        fromNameTextView = (TextView) findViewById(R.id.text_from_name);
        fromNameTextView.setTypeface(face);

        fromMobileTextView = (TextView) findViewById(R.id.text_from_mobile);
        fromMobileTextView.setTypeface(face);

        fromMobTitleTextView = (TextView) findViewById(R.id.title_from_mobile);
        fromMobTitleTextView.setTypeface(face);

        dateTitleTextView = (TextView) findViewById(R.id.date_title);
        dateTitleTextView.setTypeface(face);

        timeTitleTextView = (TextView) findViewById(R.id.time_title);
        timeTitleTextView.setTypeface(face);

        receiverTitleTextView = (TextView) findViewById(R.id.receiver_det_title);
        receiverTitleTextView.setTypeface(face, Typeface.BOLD);

        toNameTextView = (TextView) findViewById(R.id.text_to_name);
        toNameTextView.setTypeface(face);

        toMobTitleTextView = (TextView) findViewById(R.id.title_to_mobile);
        toMobTitleTextView.setTypeface(face);

        toMobileTextView = (TextView) findViewById(R.id.text_to_mobile);
        toMobileTextView.setTypeface(face);

        //fromMobImageView = (ImageView) findViewById(R.id.img_from_mobile);
        //toMobImageView = (ImageView) findViewById(R.id.img_to_mobile);

        String sourceString;

        sourceString = getResources().getString(R.string.subject) + " " + ride.subject;
        subTitleTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.shipment) + " " + ride.shipment;
        shipTitleTextView.setText(Html.fromHtml(sourceString));

        //sourceString = "<b>" + getResources().getString(R.string.vehicle_type) + "</b> " + ride.getVehicleModelName();
        //modelTitleTextView.setText(Html.fromHtml(sourceString));
        //
        //sourceString = "<b>" + getResources().getString(R.string.vehicle_model) + "</b> " + ride.getVehicleTypeName();
        //typeTitleTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.name) + " " + ride.fromName;
        fromNameTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.mobile) + " ";
        fromMobTitleTextView.setText(Html.fromHtml(sourceString));

        //if(ride.getFromISO().equalsIgnoreCase("sa"))
        //    fromMobImageView.setImageDrawable(getResources().getDrawable(R.drawable.country_sa));
        //else
        //    fromMobImageView.setVisibility(View.GONE);

        fromMobileTextView.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        toMobileTextView.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);

        if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
            toMobileTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            fromMobileTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        } else {
            toMobileTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            fromMobileTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        fromMobileTextView.setText(ride.fromMobWithoutISO);

        sourceString = getResources().getString(R.string.date) + " " + ride.date + " - " + ride.hijriDate;
        dateTitleTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.time) + " " + ride.time;
        timeTitleTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.name) + " " + ride.toName;
        toNameTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.mobile) + " ";
        toMobTitleTextView.setText(Html.fromHtml(sourceString));

        //if(ride.getToISO().equalsIgnoreCase("sa"))
        //    toMobImageView.setImageDrawable(getResources().getDrawable(R.drawable.country_sa));
        //else
        //    toMobImageView.setVisibility(View.GONE);

        toMobileTextView.setText(ride.toMobWithoutISO);

        confirmTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmDetailsActivity.this);
                LayoutInflater inflater = ConfirmDetailsActivity.this.getLayoutInflater();
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

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmDetailsActivity.this);
                        LayoutInflater inflater1 = ConfirmDetailsActivity.this.getLayoutInflater();
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

                                new AddTripMasterBackground().execute();
                            }
                        });
                        Button btnOK1 = (Button) view1.findViewById(R.id.btn_ok);
                        btnOK1.setTypeface(face);
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

        editDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmDetailsActivity.this, ShipmentDetailsActivity.class);
                intent.putExtra("ride", ride);
                intent.putExtra("editRide", true);
                startActivity(intent);
            }
        });
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmDetailsActivity.this);
//            LayoutInflater inflater = ConfirmDetailsActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(ConfirmDetailsActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ConfirmDetailsActivity.this);
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
//    public boolean onNavigationItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            startActivity(new Intent(ConfirmDetailsActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(ConfirmDetailsActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(ConfirmDetailsActivity.this);
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
//        //            startActivity(new Intent(ConfirmDetailsActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(ConfirmDetailsActivity.this, ChangeLanguageActivity.class));
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmDetailsActivity.this);
//            LayoutInflater inflater = ConfirmDetailsActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(ConfirmDetailsActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ConfirmDetailsActivity.this);
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
//        //    ActivityCompat.finishAffinity(ConfirmDetailsActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

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

    private class AddTripMasterBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(ConfirmDetailsActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripMCustId", sharedPreferences.getString(Constants.PREFS_CUST_ID, ""));
            params.put("ArgTripMScheduleDate", ride.date);
            params.put("ArgTripMScheduleTime", ride.time);
            params.put("ArgTripMFromLat", ride.pickUpLatitude);
            params.put("ArgTripMFromLng", ride.pickUpLongitude);
            params.put("ArgTripMFromAddress", ride.pickUpLocation);
            params.put("ArgTripMFromIsSelf", ride.isFromSelf+"");
            params.put("ArgTripMFromName", ride.fromName);
            params.put("ArgTripMFromMob", "966"+ride.fromMobile);
            params.put("ArgTripMToLat", ride.dropOffLatitude);
            params.put("ArgTripMToLng", ride.dropOffLongitude);
            params.put("ArgTripMToAddress", ride.dropOffLocation);
            params.put("ArgTripMToIsSelf", ride.isToSelf+"");
            params.put("ArgTripMToName", ride.toName);
            params.put("ArgTripMToMob", "966"+ride.toMobile);
            params.put("ArgTripMSubject", ride.subject);
            params.put("ArgTripMNotes", ride.shipment);
            params.put("ArgTripMVsId", ride.vehicleSizeId);
            params.put("ArgTripMCustLat", "0");
            params.put("ArgTripMCustLng", "0");
            params.put("ArgTripMNoOfDrivers", "0");
            params.put("ArgTripMDistanceRadiusKm", "0");
            params.put("ArgTripMDistanceString", ride.distanceStr);

            String BASE_URL = Constants.BASE_URL_EN + "AddTripMaster";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "AddTripMaster";
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
                        DatabaseHandler dbHandler = new DatabaseHandler(ConfirmDetailsActivity.this);
                        dbHandler.truncateTable();

                        try {
                            tripID = ((int) Double.parseDouble(response.getString("data")))+"";

                        } catch (Exception ignored) {
                        }
                        new AddTripDetailsBackground().execute();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmDetailsActivity.this);
                        LayoutInflater inflater1 = ConfirmDetailsActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(response.getString("message"));
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        alertDialog.show();
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

    private class AddTripDetailsBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripDMID", tripID);
            params.put("ArgTripDDmId", sharedPreferences.getString(Constants.PREFS_USER_ID, ""));
            params.put("ArgTripDRate", "0");
            params.put("ArgTripDIsNegotiable", "false");

            String BASE_URL = Constants.BASE_URL_EN + "AddTripDetails";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "AddTripDetails";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
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
                        //startActivity(new Intent(ConfirmDetailsActivity.this, HomeActivity.class));
                        //ActivityCompat.finishAffinity(ConfirmDetailsActivity.this);
                        //finish();
                        new TripDIsNotifiedListBackground().execute();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmDetailsActivity.this);
                        LayoutInflater inflater1 = ConfirmDetailsActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(response.getString("message"));
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        alertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TripDIsNotifiedListBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripDDmId", sharedPreferences.getString(Constants.PREFS_USER_ID, "0"));

            String BASE_URL = Constants.BASE_URL_EN + "TripDIsNotifiedList";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "TripDIsNotifiedList";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
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

                                order.tripId = ordersJSONArray.getJSONObject(i).getString("TripMID").trim();
                                order.tripNo = ordersJSONArray.getJSONObject(i).getString("TripMNo").trim();
                                order.tripFromAddress = ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim();
                                order.tripFromLat = ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim();
                                order.tripFromLng = ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim();
                                try {
                                    order.tripFromSelf = Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf"));
                                } catch (Exception e) {
                                    order.tripFromSelf = false;
                                }
                                order.tripFromName = ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim();
                                order.tripFromMob = ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim();
                                order.tripToAddress = ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim();
                                order.tripToLat = ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim();
                                order.tripToLng = ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim();
                                try {
                                    order.tripToSelf = Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf"));
                                } catch (Exception e) {
                                    order.tripToSelf = false;
                                }
                                order.tripToName = ordersJSONArray.getJSONObject(i).getString("TripMToName").trim();
                                order.tripToMob = ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim();
                                order.vehicleModel = ordersJSONArray.getJSONObject(i).getString("VMName").trim();
                                order.vehicleType = ordersJSONArray.getJSONObject(i).getString("VmoName").trim();
                                order.scheduleDate = ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim();
                                order.scheduleTime = ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim();
                                order.userName = ordersJSONArray.getJSONObject(i).getString("UsrName").trim();
                                order.userMobile = ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim();
                                order.tripFilter = ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim();
                                order.tripStatus = ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim();
                                order.tripSubject = ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim();
                                order.tripNotes = ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim();
                                order.vehicleImage = ordersJSONArray.getJSONObject(i).getString("VmoURL").trim();
                                order.tripDId = ordersJSONArray.getJSONObject(i).getString("TripDID").trim();
                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim();

                                Intent intent = new Intent(ConfirmDetailsActivity.this, ReplyActivity.class);
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

}
