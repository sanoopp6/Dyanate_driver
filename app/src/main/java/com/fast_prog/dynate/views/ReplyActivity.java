package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.utilities.AlarmController;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.CustomTypefaceSpan;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.fast_prog.dynate.utilities.SetOffline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ReplyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Typeface face;
    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;

    Order order;

    private Button agreeButton;
    private Button disagreeButton;

    TextView usernameTextView;
    TextView subTitleTextView;
    TextView shipTitleTextView;
    TextView vehModelTextView;
    TextView vehTypeTextView;
    TextView senderTitleTextView;
    TextView distanceTextView;
    TextView fromNameTextView;
    TextView fromMobileTextView;
    TextView dateTitleTextView;
    TextView timeTitleTextView;
    TextView receiverTitleTextView;
    TextView toNameTextView;
    TextView toMobileTextView;
    TextView fromAddrTextView;
    TextView toAddrTextView;
    TextView priceTitleTextView;

    ImageView fromLocTextView;
    ImageView toLocTextView;

    EditText priceEditText;
    CheckBox negotiableCheckbox;

    String tripDId;
    double price;
    Boolean negotiable;
    double rate;

    AlertDialog alertDialog;
    private AlarmController alarmController;
    boolean alarmOn;
    boolean fromTripAdd;

    MyCircularProgressDialog myCircularProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        face = Typeface.createFromAsset(ReplyActivity.this.getAssets(), Constants.FONT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        String title = getResources().getString(R.string.reply);
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

        order = (Order) getIntent().getSerializableExtra("order");
        if(order == null) finish();

        alarmOn = getIntent().getBooleanExtra("alarm", false);
        fromTripAdd = getIntent().getBooleanExtra("fromTripAdd", false);

        if (fromTripAdd) {
            if(preferences.getBoolean(Constants.PREFS_IS_LOGIN, false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReplyActivity.this);
                LayoutInflater inflater = ReplyActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.alert_dialog, null);
                builder.setView(view);
                TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                txtAlert.setText(R.string.now_offline_change_to_online);
                final AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        startActivity(new Intent(ReplyActivity.this, HomeActivity.class));
                        finish();
                    }
                });
                view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
                        Date date = new Date();

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constants.PREFS_STATUS_TIME, simpleDateFormat.format(date));
                        editor.putString(Constants.PREFS_ONLINE_STATUS, "online");
                        editor.commit();
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

        if(alarmOn) {
            alarmController = new AlarmController(ReplyActivity.this);
            alarmController.playSound("android.resource://" + getPackageName() + "/" + R.raw.new_trip);
        }

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

        agreeButton = (Button) findViewById(R.id.btn_agree);
        agreeButton.setTypeface(face);

        disagreeButton = (Button) findViewById(R.id.btn_disagree);
        disagreeButton.setTypeface(face);

        subTitleTextView = (TextView) findViewById(R.id.sub_title);
        subTitleTextView.setTypeface(face);

        shipTitleTextView = (TextView) findViewById(R.id.ship_title);
        shipTitleTextView.setTypeface(face);

        vehModelTextView = (TextView) findViewById(R.id.model_title);
        vehModelTextView.setTypeface(face);

        vehTypeTextView = (TextView) findViewById(R.id.type_title);
        vehTypeTextView.setTypeface(face);

        senderTitleTextView = (TextView) findViewById(R.id.sender_det_title);
        senderTitleTextView.setTypeface(face, Typeface.BOLD);

        distanceTextView = (TextView) findViewById(R.id.text_distance);
        distanceTextView.setTypeface(face);

        fromNameTextView = (TextView) findViewById(R.id.text_from_name);
        fromNameTextView.setTypeface(face);

        fromMobileTextView = (TextView) findViewById(R.id.text_from_mobile);
        fromMobileTextView.setTypeface(face);

        dateTitleTextView = (TextView) findViewById(R.id.date_title);
        dateTitleTextView.setTypeface(face);

        timeTitleTextView = (TextView) findViewById(R.id.time_title);
        timeTitleTextView.setTypeface(face);

        receiverTitleTextView = (TextView) findViewById(R.id.receiver_det_title);
        receiverTitleTextView.setTypeface(face, Typeface.BOLD);

        toNameTextView = (TextView) findViewById(R.id.text_to_name);
        toNameTextView.setTypeface(face);

        toMobileTextView = (TextView) findViewById(R.id.text_to_mobile);
        toMobileTextView.setTypeface(face);

        fromAddrTextView = (TextView) findViewById(R.id.from_addr);
        fromAddrTextView.setTypeface(face);

        fromLocTextView = (ImageView) findViewById(R.id.from_loc);

        toAddrTextView = (TextView) findViewById(R.id.to_addr);
        toAddrTextView.setTypeface(face);

        toLocTextView = (ImageView) findViewById(R.id.to_loc);

        priceTitleTextView = (TextView) findViewById(R.id.price_title);
        priceTitleTextView.setTypeface(face);

        priceEditText = (EditText) findViewById(R.id.edit_price);
        priceEditText.setTypeface(face);

        negotiableCheckbox = (CheckBox) findViewById(R.id.chk_negotiable);
        negotiableCheckbox.setTypeface(face);

        String sourceString;

        sourceString = getResources().getString(R.string.subject) + " " + order.getTripSubject();
        subTitleTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.shipment) + " " + order.getTripNotes();
        shipTitleTextView.setText(Html.fromHtml(sourceString));

        //sourceString = "<b>" + getResources().getString(R.string.vehicle_type) + "</b> " + order.getVehicleModel();
        sourceString = getResources().getString(R.string.vehicle_type) + " " + order.getVehicleModel();
        vehModelTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.vehicle_model) + " " + order.getVehicleType();
        vehTypeTextView.setText(Html.fromHtml(sourceString));

        if (order.getDistance() != null) {
            sourceString = order.getDistance();
            distanceTextView.setText(Html.fromHtml(sourceString));

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                distanceTextView.setTextDirection(View.TEXT_DIRECTION_RTL);
            } else {
                distanceTextView.setTextDirection(View.TEXT_DIRECTION_LTR);
            }

        } else {
            distanceTextView.setVisibility(View.GONE);
        }

        sourceString = getResources().getString(R.string.name) + " " + order.getTripFromName();
        fromNameTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.date) + " " + order.getScheduleDate();
        dateTitleTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.time) + " " + order.getScheduleTime();
        timeTitleTextView.setText(Html.fromHtml(sourceString));

        sourceString = getResources().getString(R.string.name) + " " + order.getTripToName();
        toNameTextView.setText(Html.fromHtml(sourceString));

        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
            sourceString = getResources().getString(R.string.mobile) + " " + order.getTripFromMob().replace("+", "") + "+";
            fromMobileTextView.setText(Html.fromHtml(sourceString));

            sourceString = getResources().getString(R.string.mobile) + " " + order.getTripToMob().replace("+", "") + "+";
            toMobileTextView.setText(Html.fromHtml(sourceString));

        } else {
            sourceString = getResources().getString(R.string.mobile) + " " + order.getTripFromMob();
            fromMobileTextView.setText(Html.fromHtml(sourceString));

            sourceString = getResources().getString(R.string.mobile) + " " + order.getTripToMob();
            toMobileTextView.setText(Html.fromHtml(sourceString));
        }

        sourceString = getResources().getString(R.string.sender) + " " + order.getTripFromAddress();
        fromAddrTextView.setText(Html.fromHtml(sourceString));

        fromLocTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReplyActivity.this, RouteActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });

        sourceString = getResources().getString(R.string.receiver) + " " + order.getTripToAddress();
        toAddrTextView.setText(Html.fromHtml(sourceString));

        toLocTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReplyActivity.this, RouteActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });

        if (ConnectionDetector.isConnected(getApplicationContext())) {
            new GetOrderDetails().execute();

        } else {
            ConnectionDetector.errorSnackbar(coordinatorLayout);
        }

        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equalsIgnoreCase("0")) {
                    priceEditText.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmOn) {
                    if (alarmController != null) alarmController.stopSound();
                }

                String priceStr = priceEditText.getText().toString().trim();
                price = 0;

                try {
                    price = Double.parseDouble(priceStr);
                } catch (Exception e) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ReplyActivity.this);
                    LayoutInflater inflater1 = ReplyActivity.this.getLayoutInflater();
                    final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                    builder1.setView(view1);
                    TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                    txtAlert1.setText(R.string.you_must_enter_price);
                    final AlertDialog dialog1 = builder1.create();
                    dialog1.setCancelable(false);
                    Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                    btnOk.setText(getResources().getString(R.string.ok));
                    view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                    view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                    btnOk.setTypeface(face);
                    txtAlert1.setTypeface(face);
                    dialog1.show();
                }

                if (rate != price) {
                    negotiable = negotiableCheckbox.isChecked();

                    if (price <= 0) {
                        priceEditText.setError(getResources().getString(R.string.invalid_price));

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReplyActivity.this);
                        LayoutInflater inflater1 = ReplyActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.are_you_sure);
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();

                                if (tripDId != null)
                                    new UpdateTripDetails().execute();
                                //else
                                //new AddTripDetails(true).execute();
                            }
                        });
                        view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        btnCancel.setTypeface(face);
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();
                    }
                }
            }
        });

        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarmOn) {
                    if (alarmController != null) alarmController.stopSound();
                }

                AlertDialog.Builder builder1 = new AlertDialog.Builder(ReplyActivity.this);
                LayoutInflater inflater1 = ReplyActivity.this.getLayoutInflater();
                final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                builder1.setView(view1);
                TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                txtAlert1.setText(R.string.are_you_sure);
                final AlertDialog dialog1 = builder1.create();
                dialog1.setCancelable(false);
                Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
                Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();

                        if(tripDId != null)
                            new UpdateTripDetailStatus(true).execute();
                        //else
                        //new AddTripDetails(false).execute();
                    }
                });
                view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                btnCancel.setTypeface(face);
                btnOk.setTypeface(face);
                txtAlert1.setTypeface(face);
                dialog1.show();
            }
        });

        if (alarmOn && !fromTripAdd) {
            int SPLASH_TIME_OUT = 30000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (myCircularProgressDialog != null && myCircularProgressDialog.isShowing()) {
                        myCircularProgressDialog.dismiss();
                    }
//                    if (tripDId != null) {
//                        new UpdateTripDetailStatus(false).execute();
//                    } else {
                    finish();
//                    }
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }

        if (alarmOn) {
            if (alarmController != null) alarmController.stopSound();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (alarmOn) {
            if (alarmController != null) alarmController.stopSound();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        if (fromTripAdd) {
            MenuItem menuBack = menu.findItem(R.id.back_option);
            menuBack.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back_option) {
            finish();
        }

        if (id == R.id.exit_option) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReplyActivity.this);
            LayoutInflater inflater = ReplyActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(ReplyActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(ReplyActivity.this);
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
            startActivity(new Intent(ReplyActivity.this, HomeActivity.class));
        }

        //if (id == R.id.nav_orders) {
        //    startActivity(new Intent(ReplyActivity.this, MyOrdersActivity.class));
        //}
        //if (id == R.id.nav_agent) {
        //    final MyCircularProgressDialog progressDialog;
        //    progressDialog = new MyCircularProgressDialog(ReplyActivity.this);
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
        //            startActivity(new Intent(ReplyActivity.this, HomeActivity.class));
        //            finish();
        //        }
        //    }, 2000);
        //}

        if (id == R.id.nav_language) {
            startActivity(new Intent(ReplyActivity.this, ChangeLanguageActivity.class));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ReplyActivity.this);
            LayoutInflater inflater = ReplyActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(ReplyActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(ReplyActivity.this);
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
        //    ActivityCompat.finishAffinity(ReplyActivity.this);
        //    finish();
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private class AddTripDetails extends AsyncTask<Void, Void, JSONObject> {
//        JsonParser jsonParser;
//        boolean isAgree;
//
//        AddTripDetails(boolean isAgree) {
//            this.isAgree = isAgree;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        protected JSONObject doInBackground(Void... param) {
//            jsonParser = new JsonParser();
//
//            HashMap<String, String> params = new HashMap<>();
//
//            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//
//            params.put("ArgTripDMID", order.getTripId()+"");
//            params.put("ArgTripDDmId", preferences.getString(Constants.PREFS_USER_ID, ""));
//            params.put("ArgTripDRate", "0");
//            params.put("ArgTripDIsNegotiable", "false");
//
//            JSONObject json;
//
//            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "AddTripDetails", "POST", params);
//
//            } else {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "AddTripDetails", "POST", params);
//            }
//
//            return json;
//        }
//
//        protected void onPostExecute(final JSONObject response) {
//            if (response != null) {
//                try {
//                    // Parsing json object response
//                    // response will be a json object
//                    if (response.getBoolean("status")) {
//                        tripDId = ((int) Double.parseDouble(response.getString("data")))+"";
//
//                        if (isAgree)
//                            new UpdateTripDetails().execute();
//                        else
//                            new UpdateTripDetailStatus().execute();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private class GetOrderDetails extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParserOne;
        JsonParser jsonParserTwo;
        JSONObject jsonObjectOne;
        JSONObject jsonObjectTwo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myCircularProgressDialog = new MyCircularProgressDialog(ReplyActivity.this);
            myCircularProgressDialog.setCancelable(false);
            myCircularProgressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParserOne = new JsonParser();
            jsonParserTwo = new JsonParser();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripDID", "0");
            params.put("ArgTripDMID", order.getTripId()+"");
            params.put("ArgTripDDmId", preferences.getString(Constants.PREFS_USER_ID, "0"));

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsList", "POST", params);

            } else {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsList", "POST", params);
            }

            params = new HashMap<>();

            params.put("ArgTripDId", order.getTripdId()+"");
            params.put("ArgTripDIsNotified", "true");

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                jsonObjectTwo = jsonParserTwo.makeHttpRequest(Constants.BASE_URL_AR + "UpdateTripNotifiedStatus", "POST", params);

            } else {
                jsonObjectTwo = jsonParserTwo.makeHttpRequest(Constants.BASE_URL_EN + "UpdateTripNotifiedStatus", "POST", params);
            }

            return jsonObjectOne;
        }

        protected void onPostExecute(final JSONObject response1) {
            myCircularProgressDialog.dismiss();

            if (jsonObjectOne != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (jsonObjectOne.getBoolean("status")) {
                        JSONArray orderDetail = jsonObjectOne.getJSONArray("data");

                        if (orderDetail.length() > 0) {
                            try {
                                rate = Double.parseDouble(orderDetail.getJSONObject(0).getString("TripDRate"));
                            } catch (Exception e) {
                                rate = 0;
                            }

                            if (rate == 0) {
                                priceEditText.setText("");
                            } else {
                                priceEditText.setText(String.valueOf(rate));
                            }

                            try {
                                negotiableCheckbox.setChecked(Boolean.parseBoolean(orderDetail.getJSONObject(0).getString("TripDIsNegotiable")));
                            } catch (Exception ignored) {
                            }

                            tripDId = orderDetail.getJSONObject(0).getString("TripDID");
                            String status = orderDetail.getJSONObject(0).getString("TripDStatus");

                            if(!status.matches("1|7")) {
                                priceEditText.setEnabled(false);
                                negotiableCheckbox.setEnabled(false);
                                agreeButton.setVisibility(View.GONE);
                                disagreeButton.setVisibility(View.GONE);

                                getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                );

                            } else {
                                agreeButton.setVisibility(View.VISIBLE);
                                disagreeButton.setVisibility(View.VISIBLE);
                                priceEditText.setEnabled(true);
                                negotiableCheckbox.setEnabled(true);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UpdateTripDetails extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myCircularProgressDialog = new MyCircularProgressDialog(ReplyActivity.this);
            myCircularProgressDialog.setCancelable(false);
            myCircularProgressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgTripDID", tripDId);
            params.put("ArgTripDRate", price+"");
            params.put("ArgTripDIsNegotiable", negotiable+"");

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsUpdate", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsUpdate", "POST", params);
            }

            return json;
        }

        protected void onPostExecute(final JSONObject response) {
            myCircularProgressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReplyActivity.this);
                        LayoutInflater inflater1 = ReplyActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(getResources().getString(R.string.priced_succesfully));
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                                //Intent intent = new Intent(ReplyActivity.this, HomeActivity.class);
                                //ActivityCompat.finishAffinity(ReplyActivity.this);
                                //startActivity(intent);
                                if (fromTripAdd) {
                                    startActivity(new Intent(ReplyActivity.this, HomeActivity.class));
                                    ActivityCompat.finishAffinity(ReplyActivity.this);
                                    finish();

                                } else {
                                    finish();
                                }
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();
                    }  else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReplyActivity.this);
                        LayoutInflater inflater1 = ReplyActivity.this.getLayoutInflater();
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

    private class UpdateTripDetailStatus extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;
        boolean showDialog;

        UpdateTripDetailStatus(boolean showDialog) {
            this.showDialog = showDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myCircularProgressDialog = new MyCircularProgressDialog(ReplyActivity.this);
            myCircularProgressDialog.setCancelable(false);
            myCircularProgressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgTripDID", tripDId);
            params.put("ArgTripDStatus", "5");

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsStatusUpdate", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsStatusUpdate", "POST", params);
            }

            return json;
        }


        protected void onPostExecute(final JSONObject response) {
            myCircularProgressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        if (showDialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReplyActivity.this);
                            LayoutInflater inflater = ReplyActivity.this.getLayoutInflater();
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

                                    if (fromTripAdd) {
                                        startActivity(new Intent(ReplyActivity.this, HomeActivity.class));
                                        ActivityCompat.finishAffinity(ReplyActivity.this);
                                        finish();

                                    } else {
                                        finish();
                                    }
                                }
                            });
                            Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                            btnOK.setTypeface(face);
                            txtAlert.setTypeface(face);
                            alertDialog.show();
                        } else {
                            if (fromTripAdd) {
                                startActivity(new Intent(ReplyActivity.this, HomeActivity.class));
                                ActivityCompat.finishAffinity(ReplyActivity.this);
                                finish();

                            } else {
                                finish();
                            }
                        }

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyActivity.this);
                        LayoutInflater inflater = ReplyActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(response.getString("message"));
                        final AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        dialog.show();
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

}
