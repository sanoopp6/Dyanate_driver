package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Ride;
import com.fast_prog.dynate.models.RideTemp;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.CustomTypefaceSpan;
import com.fast_prog.dynate.utilities.DatabaseHandler;
import com.fast_prog.dynate.utilities.GPSTracker;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.SetOffline;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import net.alhazmy13.hijridatepicker.date.gregorian.GregorianDatePickerDialog;
import net.alhazmy13.hijridatepicker.date.hijri.HijriDatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SenderDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GregorianDatePickerDialog.OnDateSetListener, HijriDatePickerDialog.OnDateSetListener {

    Ride ride;

    Boolean editRide;

    static Typeface face;

    Snackbar snackbar;

    CoordinatorLayout coordinatorLayout;

    Button bookVehicleButton;

    //IntlPhoneInput fromMobile;
    //IntlPhoneInput toMobile;

    EditText fromName;
    EditText toName;
    EditText fromMobile;
    EditText toMobile;

    ImageView fromMobileSelect;
    ImageView toMobileSelect;

    Button fromDateEtxt1;
    Button fromDateEtxt2;
    Button fromTimeEtxt;

    TextView usernameTextView;
    TextView senderDetTitleTextView;
    TextView receiverDetTitleTextView;
    TextView dateTitleTextView;
    TextView timeTitleTextView;

    Thread dateTimeUpdate;

    Boolean runThread;

    GPSTracker gpsTracker;

    Animation animationToLeft1;
    Animation animationToLeft2;

    LinearLayout dateLayout;
    LinearLayout timeLayout;

    DatabaseHandler dbHandler;

    String timeString;

    static final int PICK_CONTACT = 101;

    String clickedImg;

    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd KK:mm", Locale.ENGLISH);
    SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("KK:mm", Locale.ENGLISH);
    SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("KK:mm aa", Locale.ENGLISH);

    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    AlertDialog alertFromMob;
    AlertDialog alertToMob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_details);

        face = Typeface.createFromAsset(SenderDetailsActivity.this.getAssets(), Constants.FONT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        final String title = getResources().getString(R.string.sender_and_recipient);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ride = (Ride) getIntent().getSerializableExtra("ride");
        editRide = getIntent().getBooleanExtra("editRide", false);

        final SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        dbHandler = new DatabaseHandler(this);
        final int count = dbHandler.getRideTempCount();

        if(count > 0) {
            RideTemp rideTemp = dbHandler.getRideTemp(1);

            ride.setFromself(false);
            ride.setFromName(rideTemp.getFromName());
            ride.setFromMobile(rideTemp.getFromMobile());
            ride.setFromISO(rideTemp.getFromISO());
            ride.setFromMobWithoutISO(rideTemp.getFromMobWithoutISO());
            ride.setDate(rideTemp.getDate());
            ride.setHijriDate(rideTemp.getHijriDate());
            ride.setTime(rideTemp.getTime());
            ride.setToself(false);
            ride.setToName(rideTemp.getToName());
            ride.setToMobile(rideTemp.getToMobile());
            ride.setToISO(rideTemp.getToISO());
            ride.setToMobWithoutISO(rideTemp.getToMobWithoutISO());
            ride.setMessage(Boolean.parseBoolean(rideTemp.getIsMessage()));
            timeString = rideTemp.getTimeString().trim();

            dbHandler.deleteRideTemp(rideTemp);
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

        bookVehicleButton = (Button) findViewById(R.id.btn_book_vehicle);
        bookVehicleButton.setTypeface(face);

        fromName = (EditText) findViewById(R.id.edit_from_name);
        fromName.setTypeface(face);

        //fromMobile = (IntlPhoneInput) findViewById(R.id.edit_from_mobile);
        //fromMobile.setEmptyDefault("sa");
        //fromMobile.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
        //    @Override
        //    public void done(View view, boolean isValid) {
        fromMobile = (EditText) findViewById(R.id.edit_from_mobile);
        fromMobile.setTypeface(face);
        fromMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (fromName.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromName.requestFocus();
                    }
                } else {
                    if (!(isValidMobile(fromMobile.getText().toString().trim()))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
                        LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
                        final View view1 = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view1);
                        TextView txtAlert = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.enter_sender_no);
                        alertFromMob = builder.create();
                        alertFromMob.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertFromMob.dismiss();
                            }
                        });
                        Button btnOK = (Button) view1.findViewById(R.id.btn_ok);
                        btnOK.setText(R.string.ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        alertFromMob.show();
                        //fromMobile.requestFocus();
                    }
                }
            }
        });

        fromMobileSelect = (ImageView) findViewById(R.id.img_from_mobile);
        fromMobileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedImg = "sender";

                //Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        toName = (EditText) findViewById(R.id.edit_to_name);
        toName.setTypeface(face);
        toName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (fromName.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromName.requestFocus();

                    } else if (!(isValidMobile(fromMobile.getText().toString().trim()))) {
                        v.clearFocus();
                        fromMobile.requestFocus();

                        if ((alertFromMob != null && !alertFromMob.isShowing()) || alertFromMob == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
                            LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
                            final View view = inflater.inflate(R.layout.alert_dialog, null);
                            builder.setView(view);
                            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                            txtAlert.setText(R.string.enter_sender_no);
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
                            btnOK.setText(R.string.ok);
                            btnOK.setTypeface(face);
                            txtAlert.setTypeface(face);
                            dialog.show();
                        }

                    } else if (fromDateEtxt1.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromDateEtxt1.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromDateEtxt1.requestFocus();

                    } else if (fromDateEtxt2.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromDateEtxt2.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromDateEtxt2.requestFocus();

                    } else if (fromTimeEtxt.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromTimeEtxt.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromTimeEtxt.requestFocus();
                    }
                }
            }
        });

        //toMobile = (IntlPhoneInput) findViewById(R.id.edit_to_mobile);
        //toMobile.setEmptyDefault("sa");
        //toMobile.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
        //    @Override
        //    public void done(View view, boolean isValid) {
        toMobile = (EditText) findViewById(R.id.edit_to_mobile);
        toMobile.setTypeface(face);
        toMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (fromName.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromName.requestFocus();

                    } else if (!(isValidMobile(fromMobile.getText().toString().trim()))) {
                        v.clearFocus();
                        fromMobile.requestFocus();

                        if ((alertFromMob != null && !alertFromMob.isShowing()) || alertFromMob == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
                            LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
                            final View view1 = inflater.inflate(R.layout.alert_dialog, null);
                            builder.setView(view1);
                            TextView txtAlert = (TextView) view1.findViewById(R.id.txt_alert);
                            txtAlert.setText(R.string.enter_sender_no);
                            final AlertDialog dialog = builder.create();
                            dialog.setCancelable(false);
                            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            Button btnOK = (Button) view1.findViewById(R.id.btn_ok);
                            btnOK.setText(R.string.ok);
                            btnOK.setTypeface(face);
                            txtAlert.setTypeface(face);
                            dialog.show();
                        }

                    } else if (fromDateEtxt1.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromDateEtxt1.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromDateEtxt1.requestFocus();

                    } else if (fromDateEtxt2.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromDateEtxt2.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromDateEtxt2.requestFocus();

                    } else if (fromTimeEtxt.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        fromTimeEtxt.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        fromTimeEtxt.requestFocus();

                    } else if (toName.getText().toString().trim().length() == 0) {
                        v.clearFocus();
                        toName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                        toName.requestFocus();
                    }
                } else {
                    if (!(isValidMobile(toMobile.getText().toString().trim()))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
                        LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
                        final View view1 = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view1);
                        TextView txtAlert = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.enter_sender_no);
                        alertToMob = builder.create();
                        alertToMob.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertToMob.dismiss();
                            }
                        });
                        Button btnOK = (Button) view1.findViewById(R.id.btn_ok);
                        btnOK.setText(R.string.ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        alertToMob.show();
                        //toMobile.requestFocus();
                    }
                }
            }
        });

        toMobileSelect = (ImageView) findViewById(R.id.img_to_mobile);
        toMobileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedImg = "receiver";

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        senderDetTitleTextView = (TextView) findViewById(R.id.sender_det_title);
        senderDetTitleTextView.setTypeface(face);

        receiverDetTitleTextView = (TextView) findViewById(R.id.receiver_det_title);
        receiverDetTitleTextView.setTypeface(face);

        fromDateEtxt1 = (Button) findViewById(R.id.txt_datepicker1);
        fromDateEtxt1.setTypeface(face);

        fromDateEtxt2 = (Button) findViewById(R.id.txt_datepicker2);
        fromDateEtxt2.setTypeface(face);

        fromTimeEtxt = (Button) findViewById(R.id.txt_timepicker);
        fromTimeEtxt.setTypeface(face);

        dateTitleTextView = (TextView) findViewById(R.id.date_title);
        dateTitleTextView.setTypeface(face);

        timeTitleTextView = (TextView) findViewById(R.id.time_title);
        timeTitleTextView.setTypeface(face);

        dateLayout = (LinearLayout) findViewById(R.id.date_layout);
        timeLayout = (LinearLayout) findViewById(R.id.time_layout);

        bookVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (ConnectionDetector.isConnected(SenderDetailsActivity.this)) {

                        gpsTracker = new GPSTracker(SenderDetailsActivity.this);

                        if (!gpsTracker.canGetLocation()) {
                            gpsTracker.showSettingsAlert();

                        } else {
                            ride.setMessage(false);

                            RideTemp rideTemp = new RideTemp();
                            rideTemp.setIsFromself(ride.getFromself()+"");
                            rideTemp.setFromName(ride.getFromName());
                            rideTemp.setFromMobile(ride.getFromMobile());
                            rideTemp.setFromISO(ride.getFromISO());
                            rideTemp.setFromMobWithoutISO(ride.getFromMobWithoutISO());
                            rideTemp.setDate(ride.getDate());
                            rideTemp.setHijriDate(ride.getHijriDate());
                            rideTemp.setTime(ride.getTime());
                            rideTemp.setIsToself(ride.getToself()+"");
                            rideTemp.setToName(ride.getToName());
                            rideTemp.setToMobile(ride.getToMobile());
                            rideTemp.setToISO(ride.getToISO());
                            rideTemp.setToMobWithoutISO(ride.getToMobWithoutISO());
                            rideTemp.setTimeString(timeString);
                            rideTemp.setIsMessage(ride.getMessage()+"");

                            dbHandler.truncateTable();
                            dbHandler.addRideTemp(rideTemp);

                            Intent intent = new Intent(SenderDetailsActivity.this, SenderLocationActivity.class);
                            intent.putExtra("ride", ride);
                            intent.putExtra("editRide", editRide);
                            startActivity(intent);
                        }

                    } else {
                        ConnectionDetector.errorSnackbar(coordinatorLayout);
                    }
                }
            }
        });

        if(editRide || count > 0) {
            fromDateEtxt1.setText(ride.getDate());
            fromDateEtxt2.setText(ride.getHijriDate());
            fromTimeEtxt.setText(ride.getTime());
        }

        final Calendar newCalendar = Calendar.getInstance();

        fromDateEtxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                if (fromName.getText().toString().trim().length() == 0) {
                    v.clearFocus();
                    fromName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                    fromName.requestFocus();
                    return;

                } else if (!(isValidMobile(fromMobile.getText().toString().trim()))) {
                    v.clearFocus();
                    fromMobile.requestFocus();

                    if ((alertFromMob != null && !alertFromMob.isShowing()) || alertFromMob == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
                        LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.enter_sender_no);
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
                        btnOK.setText(R.string.ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        dialog.show();
                    }
                    return;
                }

                Calendar now = Calendar.getInstance();
                GregorianDatePickerDialog gregorianDatePickerDialog =
                        GregorianDatePickerDialog.newInstance(SenderDetailsActivity.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH));
                gregorianDatePickerDialog.setMinDate(now);
                gregorianDatePickerDialog.setVersion(GregorianDatePickerDialog.Version.VERSION_2);
                //now.add(Calendar.YEAR, 100);
                //gregorianDatePickerDialog.setMaxDate(now);
                gregorianDatePickerDialog.show(getFragmentManager(), "GregorianDatePickerDialog");

//                new HijriCalendarDialog.Builder(SenderDetailsActivity.this)
//                    .setOnDateSetListener(new HijriCalendarView.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(int year, int month, int day) {
//                            String dateString;
//
//                            if(month < 10)
//                                dateString = year+"/0"+month;
//                            else
//                                dateString = year+"/"+month;
//
//                            if(day < 10)
//                                dateString += "/0"+day;
//                            else
//                                dateString += "/"+day;
//
//                            new GetDate(true, dateString).execute();
//                        }
//                    })
//                    .setMinMaxGregorianYear(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.YEAR) + 100)
//                    .setMode(HijriCalendarDialog.Mode.Gregorian)
//                    .setEnableScrolling(false)
//                    .show();
            }
        });

        fromDateEtxt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                if (fromName.getText().toString().trim().length() == 0) {
                    v.clearFocus();
                    fromName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                    fromName.requestFocus();
                    return;

                } else if (!(isValidMobile(fromMobile.getText().toString().trim()))) {
                    v.clearFocus();
                    fromMobile.requestFocus();

                    if ((alertFromMob != null && !alertFromMob.isShowing()) || alertFromMob == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
                        LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.enter_sender_no);
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
                        btnOK.setText(R.string.ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        dialog.show();
                    }
                    return;
                }

                UmmalquraCalendar now = new UmmalquraCalendar();
                HijriDatePickerDialog hijriDatePickerDialog =
                        HijriDatePickerDialog.newInstance(SenderDetailsActivity.this,
                                now.get(UmmalquraCalendar.YEAR),
                                now.get(UmmalquraCalendar.MONTH),
                                now.get(UmmalquraCalendar.DAY_OF_MONTH));
                hijriDatePickerDialog.setMinDate(now);
                hijriDatePickerDialog.setVersion(HijriDatePickerDialog.Version.VERSION_2);
                hijriDatePickerDialog.show(getFragmentManager(), "HijriDatePickerDialog");

//                new HijriCalendarDialog.Builder(SenderDetailsActivity.this)
//                    .setOnDateSetListener(new HijriCalendarView.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(int year, int month, int day) {
//                            month+=1;
//                            String dateString;
//
//                            if(month < 10)
//                                dateString = year+"/0"+month;
//                            else
//                                dateString = year+"/"+month;
//
//                            if(day < 10)
//                                dateString += "/0"+day;
//                            else
//                                dateString += "/"+day;
//
//                            new GetDate(false, dateString).execute();
//                        }
//                    })
//                    .setMinMaxGregorianYear(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.YEAR) + 100)
//                    .setMode(HijriCalendarDialog.Mode.Hijri)
//                    .setEnableScrolling(false)
//                    .show();
            }
        });

        fromTimeEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                if (fromName.getText().toString().trim().length() == 0) {
                    v.clearFocus();
                    fromName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                    fromName.requestFocus();
                    return;

                } else if (!(isValidMobile(fromMobile.getText().toString().trim()))) {
                    v.clearFocus();
                    fromMobile.requestFocus();

                    if ((alertFromMob != null && !alertFromMob.isShowing()) || alertFromMob == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
                        LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.enter_sender_no);
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
                        btnOK.setText(R.string.ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        dialog.show();
                    }
                    return;

                } else if (fromDateEtxt1.getText().toString().trim().length() == 0) {
                    v.clearFocus();
                    fromDateEtxt1.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                    fromDateEtxt1.requestFocus();
                    return;

                } else if (fromDateEtxt2.getText().toString().trim().length() == 0) {
                    v.clearFocus();
                    fromDateEtxt2.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
                    fromDateEtxt2.requestFocus();
                    return;
                }

                TimePickerDialog toDatePickerDialog = new TimePickerDialog(SenderDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String AM_PM ;
                        String time;

                        if (hourOfDay < 10) {
                            timeString = "0" + hourOfDay + ":";
                        } else {
                            timeString = hourOfDay + ":";
                        }

                        if(hourOfDay < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            hourOfDay -= 12;
                        }

                        if (hourOfDay < 10) {
                            time = "0" + hourOfDay + ":";
                        } else {
                            time = hourOfDay + ":";
                        }

                        if (minute < 10) {
                            timeString += "0" + minute;
                            time += "0" + minute + " " + AM_PM;
                        } else {
                            timeString += minute;
                            time += minute + " " + AM_PM;
                        }

                        fromTimeEtxt.setText(time);
                        ride.setTime(time);

                    }
                },newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);

                toDatePickerDialog.show();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
            animationToLeft1 = new TranslateAnimation(0, 260 - width, 0, 0);
            animationToLeft2 = new TranslateAnimation(0, 280 - width, 0, 0);

        } else {
            animationToLeft1 = new TranslateAnimation(0, width - 250, 0, 0);
            animationToLeft2 = new TranslateAnimation(0, width - 270, 0, 0);
        }

        animationToLeft1.setDuration(12000);
        animationToLeft1.setRepeatMode(Animation.RESTART);
        animationToLeft1.setRepeatCount(0);

        animationToLeft2.setDuration(12000);
        animationToLeft2.setRepeatMode(Animation.RESTART);
        animationToLeft2.setRepeatCount(0);

        senderDetTitleTextView.setAnimation(animationToLeft1);

        animationToLeft1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                animation.reset();
                receiverDetTitleTextView.startAnimation(animationToLeft2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animationToLeft2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                animation.reset();
                senderDetTitleTextView.startAnimation(animationToLeft1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        runThread = true;

        dateTimeUpdate = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted() && runThread) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dateTimeUpdateTextView();
                            }
                        });
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        dateTimeUpdate.start();
    }

    @Override
    public void onDateSet(HijriDatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        monthOfYear+=1;
        String dateString;

        if(monthOfYear < 10)
            dateString = year+"/0"+monthOfYear;
        else
            dateString = year+"/"+monthOfYear;

        if(dayOfMonth < 10)
            dateString += "/0"+dayOfMonth;
        else
            dateString += "/"+dayOfMonth;

        new GetDate(false, dateString).execute();
    }

    @Override
    public void onDateSet(GregorianDatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        monthOfYear+=1;
        String dateString;

        if(monthOfYear < 10)
            dateString = year+"/0"+monthOfYear;
        else
            dateString = year+"/"+monthOfYear;

        if(dayOfMonth < 10)
            dateString += "/0"+dayOfMonth;
        else
            dateString += "/"+dayOfMonth;

        new GetDate(true, dateString).execute();
    }

    public boolean isValidMobile(String mobile) {
        String iso = "SA";
        Phonenumber.PhoneNumber phoneNumber = null;

        try {
            phoneNumber = mPhoneUtil.parse(mobile, iso);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(dateTimeUpdate != null) {
            runThread = false;
            dateTimeUpdate.interrupt();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(dateTimeUpdate != null) {
            runThread = false;
            dateTimeUpdate.interrupt();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        runThread = true;

        dateTimeUpdate = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted() && runThread) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dateTimeUpdateTextView();
                            }
                        });
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        dateTimeUpdate.start();
    }

    private void dateTimeUpdateTextView() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        Date date = cal.getTime();
        String temp;
        String getMyDateTime = fromDateEtxt1.getText().toString().trim();

        if(getMyDateTime.length() > 0) {
            Date getMyDate = null;

            try {
                getMyDate = simpleDateFormat1.parse(getMyDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (getMyDate.before(date) || getMyDate.equals(date)) {
                timeString = simpleDateFormat3.format(date);

                temp = simpleDateFormat4.format(date);
                fromTimeEtxt.setText(temp);
                ride.setTime(temp);

                temp = simpleDateFormat1.format(date);
                new GetDate(true, temp).execute();
            }
        } else {
            timeString = simpleDateFormat3.format(date);

            temp = simpleDateFormat4.format(date);
            fromTimeEtxt.setText(temp);
            ride.setTime(temp);

            temp = simpleDateFormat1.format(date);
            new GetDate(true, temp).execute();
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    boolean validate() {
        ride.setFromself(false);
        ride.setToself(false);

        String fromNameText = fromName.getText().toString();
        String fromNumber = fromMobile.getText().toString().trim().replaceFirst("^0+(?!$)", "");
        String toNameText = toName.getText().toString();
        String toNumber = toMobile.getText().toString().trim().replaceFirst("^0+(?!$)", "");
        String dateText1 = fromDateEtxt1.getText().toString();
        String dateText2 = fromDateEtxt2.getText().toString();
        String timeText = fromTimeEtxt.getText().toString();

        if (fromNameText.trim().length() == 0) {
            fromName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
            fromName.requestFocus();
            return false;

        } else {
            ride.setFromName(fromNameText.trim());
            hideSoftKeyboard();
            fromName.setError(null);
        }

        if (isValidMobile(fromNumber)) {
            ride.setFromMobile(fromNumber);
            ride.setFromISO("sa");
            ride.setFromMobWithoutISO(fromNumber);

        } else {
            fromMobile.setError(SenderDetailsActivity.this.getResources().getText(R.string.enter_sender_no));
            fromMobile.requestFocus();
            return false;
            //fromMobile.setDefault();
            //fromMobile.requestFocus();
            //hideSoftKeyboard();
            //
            //AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            //LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
            //final View view = inflater.inflate(R.layout.alert_dialog, null);
            //builder.setView(view);
            //TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            //txtAlert.setText(R.string.enter_sender_no);
            //final AlertDialog dialog = builder.create();
            //dialog.setCancelable(false);
            //view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            //view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        dialog.dismiss();
            //    }
            //});
            //Button btnOK = (Button) view.findViewById(R.id.btn_ok);
            //btnOK.setText(R.string.ok);
            //btnOK.setTypeface(face);
            //txtAlert.setTypeface(face);
            //dialog.show();
            //return  false;
        }

        if (dateText1.trim().length() == 0){
            fromDateEtxt1.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
            fromDateEtxt1.requestFocus();
            return  false;
        } else {
            ride.setDate(dateText1.trim());
            hideSoftKeyboard();
            fromDateEtxt1.setError(null);
        }

        if (dateText2.trim().length() == 0){
            fromDateEtxt2.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
            fromDateEtxt2.requestFocus();
            return  false;
        } else {
            hideSoftKeyboard();
            fromDateEtxt2.setError(null);
        }

        if (timeText.trim().length() == 0) {
            fromTimeEtxt.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
            fromTimeEtxt.requestFocus();
            return  false;
        } else {
            ride.setTime(timeText.trim());
            hideSoftKeyboard();
            fromTimeEtxt.setError(null);
        }

        if (toNameText.trim().length() == 0) {
            toName.setError(SenderDetailsActivity.this.getResources().getText(R.string.required));
            toName.requestFocus();
            return  false;
        } else {
            ride.setToName(toNameText.trim());
            hideSoftKeyboard();
            toName.setError(null);
        }

        if(isValidMobile(toNumber)) {
            ride.setToMobile(toNumber);
            ride.setToISO("sa");
            ride.setToMobWithoutISO(toNumber);

        } else {
            toMobile.setError(SenderDetailsActivity.this.getResources().getText(R.string.enter_receiver_no));
            toMobile.requestFocus();
            return false;
            //toMobile.setDefault();
            //toMobile.requestFocus();
            //hideSoftKeyboard();
            //
            //AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            //LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
            //final View view = inflater.inflate(R.layout.alert_dialog, null);
            //builder.setView(view);
            //TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            //txtAlert.setText(R.string.enter_receiver_no);
            //final AlertDialog dialog = builder.create();
            //dialog.setCancelable(false);
            //view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            //view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        dialog.dismiss();
            //    }
            //});
            //Button btnOK = (Button) view.findViewById(R.id.btn_ok);
            //btnOK.setText(R.string.ok);
            //btnOK.setTypeface(face);
            //txtAlert.setTypeface(face);
            //dialog.show();
            //return  false;
        }

        if (ride.getFromMobile().trim().equalsIgnoreCase(ride.getToMobile().trim())) {
            //fromMobile.setDefault();
            fromMobile.setText("");
            fromMobile.requestFocus();
            hideSoftKeyboard();

            AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.sender_and_receiver_no_same);
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
            btnOK.setText(R.string.ok);
            btnOK.setTypeface(face);
            txtAlert.setTypeface(face);
            dialog.show();

            return  false;
        }

        String getMyDateTime = dateText1.trim() + " " + timeString.trim();
        Date getCurrentDate = null;
        Date getMyDate = null;

        try {
            getCurrentDate = simpleDateFormat2.parse(simpleDateFormat2.format(new Date()));
            getMyDate = simpleDateFormat2.parse(getMyDateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (getMyDate.before(getCurrentDate) || getMyDate.equals(getCurrentDate)) {
            hideSoftKeyboard();

            AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.wrong_trip_date);
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
            btnOK.setText(R.string.ok);
            btnOK.setTypeface(face);
            txtAlert.setTypeface(face);
            dialog.show();

            return  false;
        }

        return true;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(SenderDetailsActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(SenderDetailsActivity.this);
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
            startActivity(new Intent(SenderDetailsActivity.this, HomeActivity.class));
        }

        //if (id == R.id.nav_orders) {
        //    startActivity(new Intent(SenderDetailsActivity.this, MyOrdersActivity.class));
        //}
        //if (id == R.id.nav_agent) {
        //    final MyCircularProgressDialog progressDialog;
        //    progressDialog = new MyCircularProgressDialog(SenderDetailsActivity.this);
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
        //            startActivity(new Intent(SenderDetailsActivity.this, HomeActivity.class));
        //            finish();
        //        }
        //    }, 2000);
        //}

        if (id == R.id.nav_language) {
            startActivity(new Intent(SenderDetailsActivity.this, ChangeLanguageActivity.class));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(SenderDetailsActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(SenderDetailsActivity.this);
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
        //    ActivityCompat.finishAffinity(SenderDetailsActivity.this);
        //    finish();
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetDate extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;
        Boolean isHijri = false;
        String dateString = "";

        GetDate(Boolean isHijri, String dateString) {
            this.isHijri = isHijri;
            this.dateString = dateString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("data", dateString);

            JSONObject json;

            if (isHijri) {
                json = jsonParser.makeHttpRequest("https://dyanate.fast-prog.com/JBLCalendarWebService.asmx/GethijiriJson", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest("https://dyanate.fast-prog.com/JBLCalendarWebService.asmx/GetGregorianJson", "POST", params);
            }

            return json;
        }


        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        String date = response.getString("data");
                        Date curDate = simpleDateFormat1.parse(simpleDateFormat1.format(new Date()));
                        Date getMyDate = null;

                        if (isHijri) {
                            try {
                                getMyDate = simpleDateFormat1.parse(dateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (getMyDate.after(curDate) || getMyDate.equals(curDate)) {
                                fromDateEtxt1.setText(dateString);
                                fromDateEtxt2.setText(date);
                                ride.setHijriDate(date);
                                ride.setDate(dateString);
                            }

                        } else {
                            try {
                                getMyDate = simpleDateFormat1.parse(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (getMyDate.after(curDate) || getMyDate.equals(curDate)) {
                                fromDateEtxt1.setText(date);
                                fromDateEtxt2.setText(dateString);
                                ride.setHijriDate(dateString);
                                ride.setDate(date);
                            }
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            contactPicked(data);
            //Uri contactData = data.getData();
            //Cursor c =  managedQuery(contactData, null, null, null, null);
            //if (c.moveToFirst()) {
            //
            //    String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            //    String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            //
            //    if (hasPhone.equalsIgnoreCase("1")) {
            //        Cursor phones = getContentResolver().query(
            //                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
            //                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
            //                null, null);
            //        phones.moveToFirst();
            //
            //        String cNumber = phones.getString(phones.getColumnIndex("data1")).replaceAll("[^\\d]", "");
            //
            //        if (clickedImg.equalsIgnoreCase("sender")) {
            //            fromMobile.setText(cNumber);
            //        } else {
            //            toMobile.setText(cNumber);
            //        }
            //    } else {
            //        AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            //        LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
            //        final View view = inflater.inflate(R.layout.alert_dialog, null);
            //        builder.setView(view);
            //        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            //        txtAlert.setText(R.string.no_mobile_number);
            //        final AlertDialog dialog = builder.create();
            //        dialog.setCancelable(false);
            //        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            //        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            //            @Override
            //            public void onClick(View v) {
            //                dialog.dismiss();
            //            }
            //        });
            //        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
            //        btnOK.setText(R.string.ok);
            //        btnOK.setTypeface(face);
            //        txtAlert.setTypeface(face);
            //        dialog.show();
            //    }
            //    //String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //}
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            //name = cursor.getString(nameIndex);
            //fromMobile.setText(phoneNo.replaceAll("\\D+",""));

            if (clickedImg.equalsIgnoreCase("sender")) {
                fromMobile.setText(phoneNo.replaceAll("[^\\d]", ""));
            } else {
                toMobile.setText(phoneNo.replaceAll("[^\\d]", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();

            AlertDialog.Builder builder = new AlertDialog.Builder(SenderDetailsActivity.this);
            LayoutInflater inflater = SenderDetailsActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.no_mobile_number);
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
            btnOK.setText(R.string.ok);
            btnOK.setTypeface(face);
            txtAlert.setTypeface(face);
            dialog.show();
        }
    }

}
