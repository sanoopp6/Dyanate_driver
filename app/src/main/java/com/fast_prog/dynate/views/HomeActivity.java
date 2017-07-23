package com.fast_prog.dynate.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.CustomTypefaceSpan;
import com.fast_prog.dynate.utilities.GPSTracker;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.fast_prog.dynate.utilities.SetOffline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CoordinatorLayout coordinatorLayout;

    Typeface face;

    Snackbar snackbar;

    TextView usernameTextView;
    TextView makeOnlineButton;

    Button addNewTripButton;
    Button myBookedTripsButton;
    Button glassFactButton;
    Button otherCustButton;

    TextView myBookedTripsCount;
    TextView glassFactCount;
    TextView otherCustCount;

    Boolean online;

    Thread UploadLocationThread;

    GPSTracker gpsTracker;

    List<String> permissionsList;

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    AlertDialog alertDialog;

    List<View> viewList;

    int i;

    FrameLayout glassFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        face = Typeface.createFromAsset(HomeActivity.this.getAssets(), Constants.FONT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        String title = getResources().getString(R.string.home);
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

        final SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        usernameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        usernameTextView.setText(preferences.getString(Constants.PREFS_USER_NAME, ""));

        new CheckVersion().execute();

        int MyVersion = Build.VERSION.SDK_INT;

        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            permissionsList = new ArrayList<>();

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        for (int i=0;i<menu.size();i++) {
            MenuItem mi = menu.getItem(i);
            SpannableString s = new SpannableString(mi.getTitle());
            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s);
        }

        addNewTripButton = (Button) findViewById(R.id.add_new_button);
        addNewTripButton.setTypeface(face);

        myBookedTripsButton = (Button) findViewById(R.id.my_booked_button);
        myBookedTripsButton.setTypeface(face);

        glassFactButton = (Button) findViewById(R.id.glass_fact_button);
        glassFactButton.setTypeface(face);

        otherCustButton = (Button) findViewById(R.id.other_cus_button);
        otherCustButton.setTypeface(face);

        makeOnlineButton = (TextView) findViewById(R.id.make_online_button);
        makeOnlineButton.setTypeface(face);

        //if(preferences.getBoolean(Constants.PREFS_USER_AGENT, false)) {
        //    menu.findItem (R.id.nav_orders).setVisible(true);
        //    menu.findItem (R.id.nav_agent).setVisible(false);
        //
        //    addNewTripButton.setVisibility(View.VISIBLE);
        //    myBookedTripsButton.setVisibility(View.VISIBLE);
        //
        //} else {
        //    menu.findItem (R.id.nav_orders).setVisible(false);
        //    menu.findItem (R.id.nav_agent).setVisible(true);
        //
        //    addNewTripButton.setVisibility(View.GONE);
        //    myBookedTripsButton.setVisibility(View.GONE);
        //}

        viewList = new ArrayList<>();

        myBookedTripsCount = (TextView) findViewById(R.id.my_booked_count);
        myBookedTripsCount.setTypeface(face);
        //new TripDetailsMasterListCountCust().execute();

        glassFrame = (FrameLayout) findViewById(R.id.glass_fact_frame);
        glassFactCount = (TextView) findViewById(R.id.glass_fact_count);
        glassFactCount.setTypeface(face);

        //if(preferences.getBoolean(Constants.PREFS_IS_FACTORY, false)) {
        //    new TripDetailsMasterListCountByDate(true).execute();
        //
        //} else {
        //    glassFrame.setVisibility(View.GONE);
        //    glassFactButton.setVisibility(View.GONE);
        //    glassFactCount.setVisibility(View.GONE);
        //}

        otherCustCount = (TextView) findViewById(R.id.other_cus_count);
        otherCustCount.setTypeface(face);
        //new TripDetailsMasterListCountByDate(false).execute();

        addNewTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ShipmentDetailsActivity.class));
            }
        });

        myBookedTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyOrdersActivity.class));
            }
        });

        glassFactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CustomerTripActivity.class);
                intent.putExtra("glass", "true");
                startActivity(intent);
            }
        });

        otherCustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CustomerTripActivity.class);
                intent.putExtra("glass", "false");
                startActivity(intent);
            }
        });

        gpsTracker = new GPSTracker(HomeActivity.this);
        online = false;

        //if(preferences.getString(Constants.PREFS_ONLINE_STATUS, "offline").equalsIgnoreCase("online")) {
        //    online = true;
        //
        //    makeOnlineButton.setText(getResources().getString(R.string.make_offline));
        //    makeOnlineButton.setBackground(getResources().getDrawable(R.drawable.btn_ok));
        //    makeOnlineButton.startAnimation(getBlinkAnimation());
        //
        //} else {
        //    online = false;
        //}

        makeOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gpsTracker.canGetLocation()) {
                    gpsTracker.showSettingsAlert();
                    //}
                    //gpsTracker.getLocation();
                    //if(!gpsTracker.canGetLocation()) {
                    //    AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                    //    LayoutInflater inflater1 = HomeActivity.this.getLayoutInflater();
                    //    final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                    //    builder1.setView(view1);
                    //    TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                    //    txtAlert1.setText(R.string.allow_location);
                    //    final AlertDialog dialog1 = builder1.create();
                    //    dialog1.setCancelable(false);
                    //    Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                    //    view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                    //    view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    //        @Override
                    //        public void onClick(View v) {
                    //            dialog1.dismiss();
                    //        }
                    //    });
                    //    btnOk.setText(getResources().getString(R.string.ok));
                    //    btnOk.setTypeface(face);
                    //    txtAlert1.setTypeface(face);
                    //    dialog1.show();
                } else {
                    if (preferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
                        if (ConnectionDetector.isConnected(getApplicationContext())) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                            LayoutInflater inflater1 = HomeActivity.this.getLayoutInflater();
                            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                            builder1.setView(view1);
                            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                            txtAlert1.setText(R.string.are_you_sure);
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

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                                    editor.commit();

                                    makeOnlineButton.setText(getResources().getString(R.string.make_online));
                                    makeOnlineButton.setBackground(getResources().getDrawable(R.drawable.btn_not_agree_order));
                                    makeOnlineButton.clearAnimation();

                                    online = false;

                                    gpsTracker.getLocation();

                                    if (UploadLocationThread != null)
                                        UploadLocationThread.interrupt();

                                    new UpdateLocation("2").execute();
                                }
                            });
                            btnCancel.setTypeface(face);
                            btnOk.setTypeface(face);
                            txtAlert1.setTypeface(face);
                            alertDialog.show();
                        } else {
                            ConnectionDetector.errorSnackbar(coordinatorLayout);
                        }

                    } else {
                        if (ConnectionDetector.isConnected(getApplicationContext())) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                            LayoutInflater inflater1 = HomeActivity.this.getLayoutInflater();
                            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                            builder1.setView(view1);
                            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                            txtAlert1.setText(R.string.are_you_sure);
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

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
                                    Date date = new Date();

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(Constants.PREFS_STATUS_TIME, simpleDateFormat.format(date));
                                    editor.putString(Constants.PREFS_ONLINE_STATUS, "online");
                                    editor.commit();

                                    makeOnlineButton.setText(getResources().getString(R.string.make_offline));
                                    makeOnlineButton.setBackground(getResources().getDrawable(R.drawable.btn_ok));
                                    makeOnlineButton.startAnimation(getBlinkAnimation());

                                    online = true;

                                    //if (gpsTracker.canGetLocation()) {
                                    gpsTracker.getLocation();
                                    new UpdateLocation("1").execute();
                                    //}

                                    UploadLocationThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            while (online) {
                                                threadMsg("track");

                                                try {
                                                    Thread.sleep(5000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        private void threadMsg(String msg) {
                                            if (!msg.equals(null) && !msg.equals("")) {
                                                Message msgObj = handler.obtainMessage();
                                                Bundle b = new Bundle();
                                                b.putString("message", msg);
                                                msgObj.setData(b);
                                                handler.sendMessage(msgObj);
                                            }
                                        }

                                        Handler handler = new Handler() {
                                            @Override
                                            public void handleMessage(Message msg) {
                                                super.handleMessage(msg);

                                                updateTime();

                                                //if (gpsTracker.canGetLocation()) {
                                                //    gpsTracker.getLocation();
                                                //    if (ConnectionDetector.isConnected(getApplicationContext())) {
                                                new UpdateLocation("0").execute();
                                                //    }
                                                //}
                                            }
                                        };
                                    });

                                    UploadLocationThread.start();
                                }
                            });
                            btnCancel.setTypeface(face);
                            btnOk.setTypeface(face);
                            txtAlert1.setTypeface(face);
                            alertDialog.show();
                        } else {
                            ConnectionDetector.errorSnackbar(coordinatorLayout);
                        }
                    }
                }
            }
        });
    }

    public void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        Date startTime = null;
        try {
            startTime = sdf.parse(sharedPreferences.getString(Constants.PREFS_STATUS_TIME, ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endTime = new Date();

        if (startTime != null) {
            long different = endTime.getTime() - startTime.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            String timeStr = "" + (elapsedDays > 0 ? String.format("%sD ", elapsedDays) : "")
                    + (elapsedHours > 0 ? String.format("%sH ", elapsedHours) : "")
                    + (elapsedMinutes > 0 ? String.format("%sM ", elapsedMinutes) : "")
                    + (elapsedSeconds > 0 ? String.format("%sS ", elapsedSeconds) : "") + "";

            makeOnlineButton.setText(String.format("%s%s", getResources().getString(R.string.make_offline), timeStr.trim().length() > 0 ? " - ( " + timeStr + " )" : ""));
            makeOnlineButton.setSelected(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        if(preferences.getBoolean(Constants.PREFS_IS_FACTORY, false)) {
            glassFactCount.setTypeface(face);
            new TripDetailsMasterListCountByDate(true).execute();

        } else {
            glassFrame.setVisibility(View.GONE);
            glassFactButton.setVisibility(View.GONE);
            glassFactCount.setVisibility(View.GONE);
        }

        new TripDetailsMasterListCountByDate(false).execute();
        new TripDetailsMasterListCountCust().execute();

        if(preferences.getString(Constants.PREFS_ONLINE_STATUS, "offline").equalsIgnoreCase("online")) {
            if(!gpsTracker.canGetLocation()) {
                gpsTracker.showSettingsAlert();

                online = false;

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                editor.commit();

                makeOnlineButton.setText(getResources().getString(R.string.make_online));
                makeOnlineButton.setBackground(getResources().getDrawable(R.drawable.btn_not_agree_order));
                makeOnlineButton.clearAnimation();

                if (UploadLocationThread != null)
                    UploadLocationThread.interrupt();

                new UpdateLocation("2").execute();
                //}
                //
                //gpsTracker.getLocation();
                //
                //if(!gpsTracker.canGetLocation()) {
                //    AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                //    LayoutInflater inflater1 = HomeActivity.this.getLayoutInflater();
                //    final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                //    builder1.setView(view1);
                //    TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                //    txtAlert1.setText(R.string.allow_location);
                //    final AlertDialog dialog1 = builder1.create();
                //    dialog1.setCancelable(false);
                //    Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                //    view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                //    view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                //        @Override
                //        public void onClick(View v) {
                //            dialog1.dismiss();
                //
                //            online = false;
                //
                //            SharedPreferences.Editor editor = preferences.edit();
                //            editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                //            editor.commit();
                //
                //            makeOnlineButton.setText(getResources().getString(R.string.make_online));
                //            makeOnlineButton.setBackground(getResources().getDrawable(R.drawable.btn_not_agree_order));
                //            makeOnlineButton.clearAnimation();
                //
                //            if (UploadLocationThread != null)
                //                UploadLocationThread.interrupt();
                //
                //            new UpdateLocation("2").execute();
                //        }
                //    });
                //    btnOk.setText(getResources().getString(R.string.ok));
                //    btnOk.setTypeface(face);
                //    txtAlert1.setTypeface(face);
                //    dialog1.show();
            } else {
                online = true;

                makeOnlineButton.setText(getResources().getString(R.string.make_offline));
                makeOnlineButton.setBackground(getResources().getDrawable(R.drawable.btn_ok));
                makeOnlineButton.startAnimation(getBlinkAnimation());

                new UpdateLocation("1").execute();

                if (UploadLocationThread == null) {
                    UploadLocationThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (online) {
                                threadMsg("track");

                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        private void threadMsg(String msg) {
                            if (!msg.equals(null) && !msg.equals("")) {
                                Message msgObj = handler.obtainMessage();
                                Bundle b = new Bundle();
                                b.putString("message", msg);
                                msgObj.setData(b);
                                handler.sendMessage(msgObj);
                            }
                        }

                        Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);

                                updateTime();

                                //if (gpsTracker.canGetLocation()) {
                                //    gpsTracker.getLocation();
                                //    if (ConnectionDetector.isConnected(getApplicationContext())) {
                                new UpdateLocation("0").execute();
                                //    }
                                //}
                            }
                        };
                    });

                    UploadLocationThread.start();
                }
            }
        } else {
            online = false;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }
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
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.do_you_want_to_exit);
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

                    SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

                    if(preferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                        editor.commit();

                        new SetOffline(preferences.getString(Constants.PREFS_USER_ID, "")).execute();
                    }

                    ActivityCompat.finishAffinity(HomeActivity.this);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem menuBack = menu.findItem(R.id.back_option);
        menuBack.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back_option) {
            //finish();
        }

        if (id == R.id.exit_option) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(HomeActivity.this);
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
            //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
        }

        //if (id == R.id.nav_orders) {
        //    startActivity(new Intent(HomeActivity.this, MyOrdersActivity.class));
        //}
        //if (id == R.id.nav_agent) {
        //    final MyCircularProgressDialog progressDialog;
        //    progressDialog = new MyCircularProgressDialog(HomeActivity.this);
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
        //            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
        //            finish();
        //        }
        //    }, 2000);
        //}

        if (id == R.id.nav_language) {
            startActivity(new Intent(HomeActivity.this, ChangeLanguageActivity.class));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(HomeActivity.this);
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
        //    ActivityCompat.finishAffinity(HomeActivity.this);
        //    finish();
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class UpdateLocation extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;
        String status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        UpdateLocation(String status) {
            this.status = status;
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            gpsTracker.getLocation();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgUsrId", preferences.getString(Constants.PREFS_USER_ID, "en"));
            params.put("ArgLat", gpsTracker.getLatitude()+"");
            params.put("ArgLng", gpsTracker.getLongitude()+"");
            params.put("ArgTripStatus", status);
            params.put("ArgDmLoginToken", preferences.getString(Constants.PREFS_USER_CONSTANT, ""));

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "UpdateLatLongDM", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "UpdateLatLongDM", "POST", params);
            }

            return json;
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
                            count = Integer.parseInt(jsonArray.getJSONObject(0).getString("NoOfNewTrip"));
                        } catch (Exception ignored) {
                        }

                        if (count > 0 && (status.equals("1") || status.equals("0"))) {
                            new ShowTripRequests().execute();
                        }
                    } else {

                        online = false;
                        if(UploadLocationThread != null) UploadLocationThread.interrupt();

                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(response.getString("message"));
                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                if(prefs.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
                                    editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
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

                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                ActivityCompat.finishAffinity(HomeActivity.this);
                                startActivity(intent);
                                finish();
                            }
                        });
                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        btnOK.setText(getResources().getString(R.string.ok));
                        txtAlert.setTypeface(face);
                        alertDialog.show();
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

                                Intent intent = new Intent(HomeActivity.this, ReplyActivity.class);
                                intent.putExtra("alarm", true);
                                intent.putExtra("order", order);
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

    private boolean checkIfAlreadyhavePermission() {
        boolean result = true;

        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission5 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permission6 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        //int permission7 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (!(permission1 == PackageManager.PERMISSION_GRANTED  && permission2 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            result = false;
        }

        if (!(permission3 == PackageManager.PERMISSION_GRANTED && permission4 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            result = false;
        }

        if (!(permission5 == PackageManager.PERMISSION_GRANTED && permission6 == PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.READ_SMS);
            result = false;
        }

//        if (!(permission7 == PackageManager.PERMISSION_GRANTED)) {
//            permissionsList.add(Manifest.permission.CALL_PHONE);
//            result = false;
//        }

        return result;
    }

    private void requestForSpecificPermission() {

        String[] stringArr = permissionsList.toArray( new String[] {} );
        ActivityCompat.requestPermissions(HomeActivity.this, stringArr, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                switch (grantResults[0]) {
                    case PackageManager.PERMISSION_GRANTED:
                        //granted
                        break;
                    default:
                        finish();
                        //System.exit(0);
                        //not granted
                        break;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class TripDetailsMasterListCountByDate extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;
        boolean fromGlass;
        //boolean animate;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //TripDetailsMasterListCountByDate(boolean fromGlass, boolean animate) {
        TripDetailsMasterListCountByDate(boolean fromGlass) {
            this.fromGlass = fromGlass;
            //this.animate = animate;
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            if (fromGlass) {
                params.put("ArgTripMCustId", "1");
                params.put("ArgExcludeCustId", "0");

            } else {
                params.put("ArgTripMCustId", "0");
                params.put("ArgExcludeCustId", "1");
            }

            params.put("ArgTripDDmId", preferences.getString(Constants.PREFS_USER_ID, "0"));
            params.put("ArgTripMID", "0");
            params.put("ArgTripDID", "0");
            params.put("ArgTripMStatus", "0");
            params.put("ArgTripDStatus", "0");

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsMasterListCount", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsMasterListCount", "POST", params);
            }

            return json;
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

                        if (fromGlass && count > 0) {
                            glassFactCount.setVisibility(View.VISIBLE);
                            glassFactCount.setText(String.format(Locale.getDefault(), "%d", count));
                            glassFactCount.startAnimation(getBlinkAnimation());
                            //viewList.add(glassFactCount);
                        } else if (fromGlass && count <= 0 ) {
                            glassFactCount.setVisibility(View.GONE);
                            glassFactCount.clearAnimation();

                        } else if (!fromGlass && count > 0) {
                            otherCustCount.setVisibility(View.VISIBLE);
                            otherCustCount.setText(String.format(Locale.getDefault(), "%d", count));
                            otherCustCount.startAnimation(getBlinkAnimation());
                            //viewList.add(otherCustCount);
                        } else if (!fromGlass && count <= 0){
                            otherCustCount.setVisibility(View.GONE);
                            otherCustCount.clearAnimation();
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

    private class TripDetailsMasterListCountCust extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgTripMCustId", preferences.getString(Constants.PREFS_CUST_ID, "0"));
            params.put("ArgTripDDmId", "0");
            params.put("ArgTripMID", "0");
            params.put("ArgTripDID", "0");
            params.put("ArgTripMStatus", "0");
            params.put("ArgTripDStatus", "0");
            params.put("ArgExcludeCustId", "0");

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsMasterListCountCust", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsMasterListCountCust", "POST", params);
            }

            return json;
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
                            myBookedTripsCount.setVisibility(View.VISIBLE);
                            myBookedTripsCount.setText(String.format(Locale.getDefault(), "%d", count));
                            myBookedTripsCount.startAnimation(getBlinkAnimation());

                        } else {
                            myBookedTripsCount.setVisibility(View.GONE);
                            myBookedTripsCount.clearAnimation();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class CheckVersion extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyCircularProgressDialog(HomeActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgAppPackageName", Constants.APP_NAME);
            params.put("ArgAppVersionNo", Constants.APP_VERSION);

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "IsAppLive", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "IsAppLive", "POST", params);
            }

            return json;
        }

        protected void onPostExecute(JSONObject response) {
            progressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (!(response.getBoolean("status"))) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                        LayoutInflater inflater1 = HomeActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.update_available);
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();

                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                        dialog1.show();

                    } else if(!(response.getJSONArray("data").getJSONObject(0).getString("AppMsg").trim().equalsIgnoreCase(""))) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                        LayoutInflater inflater1 = HomeActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(response.getJSONArray("data").getJSONObject(0).getString("AppMsg").trim());
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();

                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                        dialog1.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
