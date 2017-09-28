package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class CustomerTripActivity extends AppCompatActivity {
        //implements NavigationView.OnNavigationItemSelectedListener {
    //TextView usernameTextView;

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    String glassExtra;

    Button driverAcceptedButton;
    Button driverRejectedButton;
    Button customerRejectedButton;
    Button customerAcceptedButton;
    Button customerCancelledButton;
    Button completedButton;
    Button newTripsButton;

    TextView driverAcceptedCount;
    TextView driverRejectedCount;
    TextView customerRejectedCount;
    TextView customerAcceptedCount;
    TextView customerCancelledCount;
    TextView completedCount;
    TextView newTripsCount;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    //List<Order> ordersArrayList;
    //JSONArray ordersJSONArray;
    //RecyclerView homeRecyclerView;
    //LinearLayoutManager homeLayoutManager;
    //RecyclerView.Adapter mHomeAdapter;
    //TextView searchTitleTextView;
    //Button allOrderButton;
    //LinearLayout topLayout;
    //List<View> viewList;
    //int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_trip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(CustomerTripActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        glassExtra = getIntent().getStringExtra("glass");

        String title;
        if (glassExtra.equalsIgnoreCase("true")) {
            title = getResources().getString(R.string.glass_factory);

        } else {
            title = getResources().getString(R.string.other_customers);
        }

        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

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
        //homeRecyclerView = (RecyclerView) findViewById(R.id.recycler_home);
        //allOrderButton = (Button) findViewById(R.id.all_orders_button);
        //allOrderButton.setTypeface(face);
        //topLayout = (LinearLayout) findViewById(R.id.top_layout);
        //searchTitleTextView = (TextView) findViewById(R.id.enter_port_priority_text_view);
        //searchTitleTextView.setTypeface(face);
        //if (ConnectionDetector.isConnected(getApplicationContext())) {
        //    new GetOrderHistory().execute();
        //} else {
        //    ConnectionDetector.errorSnackbar(coordinatorLayout);
        //}
        //allOrderButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        topLayout.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
        //        allOrderButton.setVisibility(View.GONE);
        //    }
        //});
        //viewList = new ArrayList<>();

        newTripsButton = (Button) findViewById(R.id.new_trips_button);
        driverRejectedButton = (Button) findViewById(R.id.rejected_by_me_button);
        driverAcceptedButton = (Button) findViewById(R.id.accepted_by_me_button);
        customerRejectedButton = (Button) findViewById(R.id.customer_rejected_button);
        customerCancelledButton = (Button) findViewById(R.id.customer_cancelled_button);
        customerAcceptedButton = (Button) findViewById(R.id.customer_accepted_button);
        completedButton = (Button) findViewById(R.id.completed_button);
        driverAcceptedCount = (TextView) findViewById(R.id.accepted_by_me_count);
        driverRejectedCount = (TextView) findViewById(R.id.rejected_by_me_count);
        customerRejectedCount = (TextView) findViewById(R.id.customer_rejected_count);
        customerAcceptedCount = (TextView) findViewById(R.id.customer_accepted_count);
        customerCancelledCount = (TextView) findViewById(R.id.customer_cancelled_count);
        completedCount = (TextView) findViewById(R.id.completed_count);
        newTripsCount = (TextView) findViewById(R.id.new_trips_count);

        newTripsButton.setTypeface(face);
        driverRejectedButton.setTypeface(face);
        driverAcceptedButton.setTypeface(face);
        customerRejectedButton.setTypeface(face);
        customerCancelledButton.setTypeface(face);
        customerAcceptedButton.setTypeface(face);
        completedButton.setTypeface(face);
        driverAcceptedCount.setTypeface(face);
        driverRejectedCount.setTypeface(face);
        customerRejectedCount.setTypeface(face);
        customerAcceptedCount.setTypeface(face);
        customerCancelledCount.setTypeface(face);
        completedCount.setTypeface(face);
        newTripsCount.setTypeface(face);

        //new TripDetailsMasterListCountByDate("1", driverAcceptedCount, false).execute();
        //new TripDetailsMasterListCountByDate("1", driverAcceptedCount).execute();
        //new TripDetailsMasterListCountByDate("5", driverRejectedCount, false).execute();
        //new TripDetailsMasterListCountByDate("5", driverRejectedCount).execute();
        //new TripDetailsMasterListCountByDate("3", customerRejectedCount, false).execute();
        //new TripDetailsMasterListCountByDate("3", customerRejectedCount).execute();
        //new TripDetailsMasterListCountByDate("2", customerAcceptedCount, false).execute();
        //new TripDetailsMasterListCountByDate("2", customerAcceptedCount).execute();
        //new TripDetailsMasterListCountByDate("4", customerCancelledCount, false).execute();
        //new TripDetailsMasterListCountByDate("4", customerCancelledCount).execute();
        //new TripDetailsMasterListCountByDate("6", completedCount, false).execute();
        //new TripDetailsMasterListCountByDate("6", completedCount).execute();
        //new TripDetailsMasterListCountByDate("7", newTripsCount, true).execute();
        //new TripDetailsMasterListCountByDate("7", newTripsCount).execute();

        newTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerTripActivity.this, AllOrdersActivity.class);
                intent.putExtra("glass", glassExtra);
                intent.putExtra("mode", "7");
                intent.putExtra("modeStr", newTripsButton.getText().toString().trim());
                startActivity(intent);
            }
        });

        driverRejectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerTripActivity.this, AllOrdersActivity.class);
                intent.putExtra("glass", glassExtra);
                intent.putExtra("mode", "5");
                intent.putExtra("modeStr", driverRejectedButton.getText().toString().trim());
                startActivity(intent);
            }
        });

        driverAcceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerTripActivity.this, AllOrdersActivity.class);
                intent.putExtra("glass", glassExtra);
                intent.putExtra("mode", "1");
                intent.putExtra("modeStr", driverAcceptedButton.getText().toString().trim());
                startActivity(intent);
            }
        });

        customerRejectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerTripActivity.this, AllOrdersActivity.class);
                intent.putExtra("glass", glassExtra);
                intent.putExtra("mode", "3");
                intent.putExtra("modeStr", customerCancelledButton.getText().toString().trim());
                startActivity(intent);
            }
        });

        customerCancelledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerTripActivity.this, AllOrdersActivity.class);
                intent.putExtra("glass", glassExtra);
                intent.putExtra("mode", "4");
                intent.putExtra("modeStr", customerCancelledButton.getText().toString().trim());
                startActivity(intent);
            }
        });

        customerAcceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerTripActivity.this, AllOrdersActivity.class);
                intent.putExtra("glass", glassExtra);
                intent.putExtra("mode", "2");
                intent.putExtra("modeStr", customerAcceptedButton.getText().toString().trim());
                startActivity(intent);
            }
        });

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerTripActivity.this, AllOrdersActivity.class);
                intent.putExtra("glass", glassExtra);
                intent.putExtra("mode", "6");
                intent.putExtra("modeStr", completedButton.getText().toString().trim());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new TripDetailsMasterListCountBackground("1", driverAcceptedCount).execute();
        new TripDetailsMasterListCountBackground("5", driverRejectedCount).execute();
        new TripDetailsMasterListCountBackground("3", customerRejectedCount).execute();
        new TripDetailsMasterListCountBackground("2", customerAcceptedCount).execute();
        new TripDetailsMasterListCountBackground("4", customerCancelledCount).execute();
        new TripDetailsMasterListCountBackground("6", completedCount).execute();
        new TripDetailsMasterListCountBackground("7", newTripsCount).execute();
        //viewList = new ArrayList<>();
        //new TripDetailsMasterListCountByDate("1", driverAcceptedCount, false).execute();
        //new TripDetailsMasterListCountByDate("7", newTripsCount, true).execute();
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerTripActivity.this);
//            LayoutInflater inflater = CustomerTripActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(CustomerTripActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(CustomerTripActivity.this);
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
//            startActivity(new Intent(CustomerTripActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(CustomerTripActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(CustomerTripActivity.this);
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
//        //            startActivity(new Intent(CustomerTripActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(CustomerTripActivity.this, ChangeLanguageActivity.class));
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerTripActivity.this);
//            LayoutInflater inflater = CustomerTripActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(CustomerTripActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(CustomerTripActivity.this);
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
//        //    ActivityCompat.finishAffinity(CustomerTripActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private class TripDetailsMasterListCountBackground extends AsyncTask<Void, Void, JSONObject> {
        String status;
        TextView view;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        TripDetailsMasterListCountBackground(String status, TextView view) {
            this.status = status;
            this.view = view;
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            if (glassExtra.equalsIgnoreCase("true")) {
                params.put("ArgTripMCustId", "1");
                params.put("ArgExcludeCustId", "0");

            } else {
                params.put("ArgTripMCustId", "0");
                params.put("ArgExcludeCustId", "1");
            }

            params.put("ArgTripDDmId", sharedPreferences.getString(Constants.PREFS_USER_ID, "0"));
            params.put("ArgTripMID", "0");
            params.put("ArgTripDID", "0");
            params.put("ArgTripMStatus", "0");
            params.put("ArgTripDStatus", status);

            String BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterListCount";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterListCount";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        int count = 0;

                        try {
                            count = Integer.parseInt(jsonArray.getJSONObject(0).getString("Cnt"));
                        } catch (Exception ignored) {
                        }

                        if (count > 0) {
                            Animation blinkText = AnimationUtils.loadAnimation(CustomerTripActivity.this, R.anim.blink);
                            view.setVisibility(View.VISIBLE);
                            view.setText(String.format(Locale.getDefault(), "%d", count));
                            view.startAnimation(blinkText);
                            //viewList.add(view);
                        } else {
                            view.setVisibility(View.GONE);
                            view.clearAnimation();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //if (animate) {
                //    if (viewList.size() > 0) {
                //        i = 0;
                //        final Animation animationBlink = getBlinkAnimation();
                //
                //        while (i < viewList.size()) {
                //            viewList.get(i).startAnimation(animationBlink);
                //            i++;
                //        }
                //
                //        //viewList.get(i).startAnimation(animationBlink);
                //        //
                //        //animationBlink.setAnimationListener(new Animation.AnimationListener() {
                //        //    @Override
                //        //    public void onAnimationStart(Animation animation) {
                //        //    }
                //        //
                //        //    @Override
                //        //    public void onAnimationEnd(Animation animation) {
                //        //        animation.reset();
                //        //
                //        //        i++;
                //        //        if (i < viewList.size()) {
                //        //            viewList.get(i).startAnimation(animationBlink);
                //        //        } else {
                //        //            i = 0;
                //        //            viewList.get(i).startAnimation(animationBlink);
                //        //        }
                //        //    }
                //        //
                //        //    @Override
                //        //    public void onAnimationRepeat(Animation animation) {
                //        //    }
                //        //});
                //    }
                //}
            }
        }
    }

//    public Animation getBlinkAnimation(){
//        Animation animation = new AlphaAnimation(1, 0);          // Change alpha from fully visible to invisible
//        animation.setDuration(300);                              // duration - half a second
//        animation.setInterpolator(new LinearInterpolator());     // do not alter animation rate
//        animation.setRepeatCount(-1);                            // Repeat animation infinitely
//        animation.setRepeatMode(Animation.REVERSE);              // Reverse animation at the end so the button will fade back in
//
//        return animation;
//    }

    //class GetOrderHistory extends AsyncTask<Void, Void, JSONObject> {
    //    MyCircularProgressDialog progressDialog;
    //    JsonParser jsonParser;
    //
    //    @Override
    //    protected void onPreExecute() {
    //        super.onPreExecute();
    //        progressDialog = new MyCircularProgressDialog(CustomerTripActivity.this);
    //        progressDialog.setCancelable(false);
    //        progressDialog.show();
    //    }
    //
    //    protected JSONObject doInBackground(Void... param) {
    //        jsonParser = new JsonParser();
    //
    //        HashMap<String, String> params = new HashMap<>();
    //
    //        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    //
    //        params.put("ArgTripMCustId", "0");
    //        params.put("ArgTripDDmId", preferences.getString(Constants.PREFS_USER_ID, "0"));
    //        params.put("ArgTripMID", "0");
    //        params.put("ArgTripDID", "0");
    //        params.put("ArgTripMStatus", "0");
    //        params.put("ArgTripDStatus", "7");
    //
    //        JSONObject json;
    //
    //        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsMasterList", "POST", params);
    //
    //        } else {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsMasterList", "POST", params);
    //        }
    //        //params.put("ArgTripMID", "0");
    //        //params.put("ArgTripMCustId", "0");
    //        //params.put("ArgTripMStatus", "1");
    //        //params.put("ArgTripMVmoId", "0");
    //        //JSONObject json;
    //        //if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
    //        //    json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripMasterList", "POST", params);
    //        //} else {
    //        //    json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripMasterList", "POST", params);
    //        //}
    //        return json;
    //    }
    //
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
    //                            ordersJSONArray = response.getJSONArray("data");
    //
    //                            ordersArrayList = new ArrayList<>();
    //
    //                            homeRecyclerView.setHasFixedSize(true);
    //                            homeLayoutManager = new LinearLayoutManager(CustomerTripActivity.this);
    //                            homeRecyclerView.setLayoutManager(homeLayoutManager);
    //
    //                            if (ordersJSONArray.length() > 0) {
    //
    //                                for (int i = 0; i < ordersJSONArray.length(); i++) {
    //                                    Order order = new Order();
    //
    //                                    order.setTripId(ordersJSONArray.getJSONObject(i).getString("TripMID").trim());
    //                                    order.setTripNo(ordersJSONArray.getJSONObject(i).getString("TripMNo").trim());
    //                                    order.setTripFromAddress(ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim());
    //                                    order.setTripFromLat(ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim());
    //                                    order.setTripFromLng(ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim());
    //                                    try {
    //                                        order.setTripfromSelf(Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf")));
    //                                    } catch (Exception e) {
    //                                        order.setTripfromSelf(false);
    //                                    }
    //                                    order.setTripFromName(ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim());
    //                                    order.setTripFromMob(ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim());
    //                                    order.setTripToAddress(ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim());
    //                                    order.setTripToLat(ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim());
    //                                    order.setTripToLng(ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim());
    //                                    try {
    //                                        order.setTripToSelf(Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf")));
    //                                    } catch (Exception e) {
    //                                        order.setTripToSelf(false);
    //                                    }
    //                                    order.setTripToName(ordersJSONArray.getJSONObject(i).getString("TripMToName").trim());
    //                                    order.setTripToMob(ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim());
    //                                    order.setVehicleModel(ordersJSONArray.getJSONObject(i).getString("VMName").trim());
    //                                    order.setVehicleType(ordersJSONArray.getJSONObject(i).getString("VmoName").trim());
    //                                    order.setScheduleDate(ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim());
    //                                    order.setScheduleTime(ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim());
    //                                    order.setUserName(ordersJSONArray.getJSONObject(i).getString("UsrName").trim());
    //                                    order.setUserMobile(ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim());
    //                                    order.setTripFilter(ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim());
    //                                    order.setTripStatus(ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim());
    //                                    order.setTripSubject(ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim());
    //                                    order.setTripNotes(ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim());
    //                                    order.setVehicleImage(ordersJSONArray.getJSONObject(i).getString("VmoURL").trim());
    //                                    order.setTripdId(ordersJSONArray.getJSONObject(i).getString("TripDID").trim());
    //                                    order.setDistance(ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim());
    //
    //                                    ordersArrayList.add(order);
    //                                }
    //
    //                                mHomeAdapter = new MyOrdersAdapter();
    //                                homeRecyclerView.setAdapter(mHomeAdapter);
    //
    //                            } else {
    //                                snackbar = Snackbar.make(coordinatorLayout, response.getString("message"), Snackbar.LENGTH_LONG).setAction(R.string.ok, new View.OnClickListener() {
    //                                    @Override
    //                                    public void onClick(View view) {
    //                                    }
    //                                });
    //                                snackbar.show();
    //                            }
    //                        }
    //                    } catch (JSONException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //        }, 2000);
    //    }
    //}
    //
    //public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {
    //
    //    public class ViewHolder extends RecyclerView.ViewHolder {
    //        // each data item is just a string in this case
    //        public TextView orderNo;
    //        public TextView address;
    //        public TextView view1;
    //
    //        public ViewHolder(View v) {
    //            super(v);
    //            orderNo = (TextView) v.findViewById(R.id.id_text_view);
    //            address = (TextView) v.findViewById(R.id.id_text_from_to);
    //            view1 = (TextView) v.findViewById(R.id.show_details);
    //        }
    //    }
    //
    //    // Create new views (invoked by the layout manager)
    //    @Override
    //    public MyOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //        // create a new view
    //        View v = LayoutInflater.from(parent.getContext())
    //                .inflate(R.layout.layout_orders_card, parent, false);
    //        // set the view's size, margins, paddings and layout parameters
    //        ViewHolder vh = new ViewHolder(v);
    //        return vh;
    //    }
    //
    //    // Replace the contents of a view (invoked by the layout manager)
    //    @Override
    //    public void onBindViewHolder(final ViewHolder holder, int position) {
    //        // - get element from your dataset at this position
    //        // - replace the contents of the view with that element
    //        //View view = holder.itemView;
    //        final int positionPlace = position;
    //
    //        holder.orderNo.setTypeface(face);
    //        holder.address.setTypeface(face);
    //        holder.view1.setTypeface(face);
    //
    //        holder.orderNo.setText("#" + ordersArrayList.get(positionPlace).getTripNo());
    //        holder.address.setText(ordersArrayList.get(positionPlace).getTripFromAddress() + " - " + ordersArrayList.get(positionPlace).getTripToAddress());
    //
    //        holder.view1.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                Order order = ordersArrayList.get(positionPlace);
    //
    //                Intent intent = new Intent(CustomerTripActivity.this, ReplyActivity.class);
    //                intent.putExtra("order", order);
    //                intent.putExtra("alarm", false);
    //                startActivity(intent);
    //            }
    //        });
    //    }
    //
    //    // Return the size of your dataset (invoked by the layout manager)
    //    @Override
    //    public int getItemCount() {
    //        return ordersArrayList.size();
    //    }
    //
    //}
}
