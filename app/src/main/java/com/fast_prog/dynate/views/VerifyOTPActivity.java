package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.RegisterUser;
import com.fast_prog.dynate.models.UploadFiles;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.fast_prog.dynate.utilities.SMSReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VerifyOTPActivity extends AppCompatActivity {

    static EditText oTPEditText;

    Button updateButton;

    TextView resendButton;
    TextView timerTextView;

    String otp;

    TextView otpTextView2;

    boolean isRegistered;

    SMSReceiver receiver;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Typeface face;

    static String otpExtra;
    static RegisterUser registerUserExtra;
    static UploadFiles uploadFiles;

    int custID;

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    List<String> otpArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(VerifyOTPActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.verify_otp);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        oTPEditText = (EditText) findViewById(R.id.edit_otp);
        oTPEditText.setTypeface(face);

        updateButton = (Button) findViewById(R.id.btn_update);
        updateButton.setTypeface(face);

        resendButton = (TextView) findViewById(R.id.btn_resend_otp);
        resendButton.setTypeface(face);
        timerTextView = (TextView) findViewById(R.id.txt_timer);
        timerTextView.setTypeface(face);

        otpTextView2 = (TextView) findViewById(R.id.txt_otp2);
        otpTextView2.setTypeface(face);

        TextView t1 = (TextView) findViewById(R.id.txt_otp1);
        t1.setTypeface(face);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        //otpExtra = getIntent().getStringExtra("OTP");
        //uploadFiles = (List<UploadFiles>) getIntent().getSerializableExtra("uploadFiles");
        //registerUserExtra = (RegisterUser) getIntent().getSerializableExtra("registerUser");

        otpTextView2.setText(String.format("%s %s", VerifyOTPActivity.this.getResources().getString(R.string.we_have_sent_an_otp_via_sms), registerUserExtra.mobile));

        new CountDownTimer(45000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.format(Locale.getDefault() ,"00: %d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                timerTextView.setVisibility(View.GONE);
                resendButton.setEnabled(true);
                resendButton.setPaintFlags(resendButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                resendButton.setTextColor(getResources().getColor(R.color.edit_text_focused_color));
            }
        }.start();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (ConnectionDetector.isConnected(VerifyOTPActivity.this)) {
                        new AddDriverMasterBackground().execute();
                    } else {
                        ConnectionDetector.errorSnackbar(coordinatorLayout);
                    }
                }
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(VerifyOTPActivity.this)) {
                    new SendOTPDMBackground().execute();
                } else {
                    ConnectionDetector.errorSnackbar(coordinatorLayout);
                }
            }
        });

        if(!isRegistered) {
            receiver = new SMSReceiver("verify");
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(receiver, filter);
            isRegistered = true;
        }

        otpArray = new ArrayList<>();
        otpArray.add(otpExtra);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//
//        MenuItem menuLogout = menu.findItem(R.id.exit_option);
//        menuLogout.setVisible(false);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.back_option) {
//            finish();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public static void updateData(String otp) {
        oTPEditText.setText(otp);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(!isRegistered) {
            receiver = new SMSReceiver("verify");
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(receiver, filter);
            isRegistered = true;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(isRegistered) {
            unregisterReceiver(receiver);
            isRegistered = false;
        }

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }

        if (myCircularProgressDialog != null && myCircularProgressDialog.isShowing()) {
            myCircularProgressDialog.cancel();
        }
    }

    public boolean useLoop(String targetValue) {
        for (String s: otpArray) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }

    boolean validate() {
        otp = oTPEditText.getText().toString().trim();

        if (otp.length() == 0) {
            oTPEditText.setError(VerifyOTPActivity.this.getResources().getText(R.string.required));
            oTPEditText.requestFocus();
            return  false;
        } else {
            oTPEditText.setError(null);
        }

        if (!useLoop(otpExtra)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
            LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
            builder1.setView(view1);
            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
            txtAlert1.setText(R.string.invalid_otp);
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
            //oTPEditText.setError(VerifyOTPActivity.this.getResources().getText(R.string.invalid_otp));
            //oTPEditText.requestFocus();
            return  false;
        } else {
            oTPEditText.setError(null);
        }

        return true;
    }

    private class AddDriverMasterBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(VerifyOTPActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgDmName", registerUserExtra.name);
            params.put("ArgDmNameAr", registerUserExtra.nameArabic);
            params.put("ArgDmMobNumber", "966"+registerUserExtra.mobile);
            params.put("ArgDmEmailId", registerUserExtra.mail);
            params.put("ArgDmAddress", registerUserExtra.address);
            params.put("ArgDmLatitude", registerUserExtra.latitude);
            params.put("ArgDmLongitude", registerUserExtra.longitude);
            params.put("ArgDmUserId", registerUserExtra.username);
            params.put("ArgDmPassWord", registerUserExtra.password);
            params.put("ArgDmVmoId", registerUserExtra.vModelId);
            params.put("ArgDmLicenseNo", registerUserExtra.licenseNo);
            params.put("ArgDmLicenseNoAr", registerUserExtra.licenseNoArabic);
            params.put("ArgDmIsFactory", String.valueOf(registerUserExtra.withGlass));
            params.put("ArgDmLoginType", registerUserExtra.loginMethod);
            params.put("ArgVcId", registerUserExtra.vCompId);

            String BASE_URL = Constants.BASE_URL_EN + "AddDriverMaster";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "AddDriverMaster";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(final JSONObject response) {
            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        //AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        //LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
                        //final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        //builder1.setView(view1);
                        //TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        //txtAlert1.setText(R.string.registration_succesfull);
                        //final AlertDialog dialog1 = builder1.create();
                        //dialog1.setCancelable(false);
                        //view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        //Button btnOk = (Button) view1.findViewById(R.id.btn_green_rounded);
                        //btnOk.setText(R.string.ok);
                        //view1.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
                        //    @Override
                        //    public void onClick(View v) {
                        //        dialog1.dismiss();
                        //        new LoginBackground().execute();
                        //    }
                        //});
                        //btnOk.setTypeface(face);
                        //txtAlert1.setTypeface(face);
                        //dialog1.show();
                        try {
                            custID = (int) Double.parseDouble(response.getString("data"));
                        } catch (Exception ignored) {
                        }

                        new AddRegFilesBackground().execute();

                    } else {
                        myCircularProgressDialog.dismiss();

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
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
                    myCircularProgressDialog.dismiss();
                    e.printStackTrace();
                }
            } else {
                myCircularProgressDialog.dismiss();

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

    private class AddRegFilesBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            JSONObject json;

            String BASE_URL = Constants.BASE_URL_EN + "AddRegFiles";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "AddRegFiles";
            }

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgBase64", uploadFiles.base64Encoded1);
            params.put("ArgRFCaption", uploadFiles.imageName1);
            params.put("ArgRFDmId", custID+"");

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params);

            params = new HashMap<>();

            params.put("ArgBase64", uploadFiles.base64Encoded2);
            params.put("ArgRFCaption", uploadFiles.imageName2);
            params.put("ArgRFDmId", custID+"");

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params);

            params = new HashMap<>();

            params.put("ArgBase64", uploadFiles.base64Encoded3);
            params.put("ArgRFCaption", uploadFiles.imageName3);
            params.put("ArgRFDmId", custID+"");

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params);

            params = new HashMap<>();

            params.put("ArgBase64", uploadFiles.base64Encoded4);
            params.put("ArgRFCaption", uploadFiles.imageName4);
            params.put("ArgRFDmId", custID+"");

            json = jsonParser.makeHttpRequest(BASE_URL + "", "POST", params);


            return json;
        }

        protected void onPostExecute(final JSONObject response) {
            myCircularProgressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        startActivity(new Intent(VerifyOTPActivity.this, SupportWindowActivity.class));
                        ActivityCompat.finishAffinity(VerifyOTPActivity.this);

                        //AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        //LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
                        //final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        //builder1.setView(view1);
                        //TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        //txtAlert1.setText(getResources().getString(R.string.registration_successful_please_contact_office));
                        //final AlertDialog dialog1 = builder1.create();
                        //dialog1.setCancelable(false);
                        //view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        //Button btnOk = (Button) view1.findViewById(R.id.btn_green_rounded);
                        //btnOk.setText(R.string.ok);
                        //view1.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
                        //    @Override
                        //    public void onClick(View v) {
                        //        dialog1.dismiss();
                        //
                        //        startActivity(new Intent(VerifyOTPActivity.this, LoginActivity.class));
                        //        ActivityCompat.finishAffinity(VerifyOTPActivity.this);
                        //    }
                        //});
                        //btnOk.setTypeface(face);
                        //txtAlert1.setTypeface(face);
                        //dialog1.show();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
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

    //private class LoginBackground extends AsyncTask<Void, Void, JSONObject> {
    //    MyCircularProgressDialog progressDialog;
    //    JsonParser jsonParser;
    //
    //    @Override
    //    protected void onPreExecute() {
    //        super.onPreExecute();
    //        progressDialog = new MyCircularProgressDialog(VerifyOTPActivity.this);
    //        progressDialog.setCancelable(false);
    //        progressDialog.show();
    //    }
    //
    //    protected JSONObject doInBackground(Void... param) {
    //
    //        jsonParser = new JsonParser();
    //
    //        HashMap<String, String> params = new HashMap<>();
    //
    //        params.put("ArgDmUserId", registerUserExtra.getUsername());
    //        params.put("ArgDmPassWord", registerUserExtra.getPassword());
    //
    //        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    //
    //        JSONObject json;
    //
    //        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "CheckDriverLogin", "POST", params);
    //
    //        } else {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "CheckDriverLogin", "POST", params);
    //        }
    //
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
    //                            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    //                            SharedPreferences.Editor editor = prefs.edit();
    //                            editor.putString(Constants.PREFS_USER_ID, response.getJSONArray("data").getJSONObject(0).getString("DmId"));
    //                            editor.putString(Constants.PREFS_USER_MOBILE, response.getJSONArray("data").getJSONObject(0).getString("DmMobNumber"));
    //                            editor.putBoolean(Constants.PREFS_IS_LOGIN, true);
    //                            editor.putString(Constants.PREFS_LATITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLatitude"));
    //                            editor.putString(Constants.PREFS_LONGITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLongitude"));
    //                            editor.putString(Constants.PREFS_USER_NAME, response.getJSONArray("data").getJSONObject(0).getString("DmUserId"));
    //                            editor.commit();
    //
    //                            Intent intent = new Intent(VerifyOTPActivity.this, HomeActivity.class);
    //                            ActivityCompat.finishAffinity(VerifyOTPActivity.this);
    //                            startActivity(intent);
    //                            finish();
    //
    //                        } else {
    //                            AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
    //                            LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
    //                            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
    //                            builder1.setView(view1);
    //                            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
    //                            txtAlert1.setText(response.getString("message"));
    //                            final AlertDialog dialog1 = builder1.create();
    //                            dialog1.setCancelable(false);
    //                            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    //                            Button btnOk = (Button) view1.findViewById(R.id.btn_green_rounded);
    //                            btnOk.setText(R.string.ok);
    //                            view1.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
    //                                @Override
    //                                public void onClick(View v) {
    //                                    dialog1.dismiss();
    //
    //                                    Intent intent = new Intent(VerifyOTPActivity.this, CustomerTripActivity.class);
    //                                    ActivityCompat.finishAffinity(VerifyOTPActivity.this);
    //                                    startActivity(intent);
    //                                    finish();
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
    //                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
    //                    LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
    //                    final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
    //                    builder1.setView(view1);
    //                    TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
    //                    txtAlert1.setText(R.string.network_error);
    //                    final AlertDialog dialog1 = builder1.create();
    //                    dialog1.setCancelable(false);
    //                    view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    //                    Button btnOk = (Button) view1.findViewById(R.id.btn_green_rounded);
    //                    btnOk.setText(R.string.ok);
    //                    view1.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
    //                        @Override
    //                        public void onClick(View v) {
    //                            dialog1.dismiss();
    //
    //                            Intent intent = new Intent(VerifyOTPActivity.this, CustomerTripActivity.class);
    //                            ActivityCompat.finishAffinity(VerifyOTPActivity.this);
    //                            startActivity(intent);
    //                            finish();
    //                        }
    //                    });
    //                    btnOk.setTypeface(face);
    //                    txtAlert1.setTypeface(face);
    //                    dialog1.show();
    //                }
    //            }
    //        }, 2000);
    //    }
    //}

    private class SendOTPDMBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(VerifyOTPActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgMobNo", "966"+registerUserExtra.mobile);
            params.put("ArgIsDB", "false");

            String BASE_URL = Constants.BASE_URL_EN + "SendOTPDM";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "SendOTPDM";
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
                        otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP");
                        otpArray.add(otpExtra);
                        resendButton.setEnabled(false);
                        timerTextView.setVisibility(View.VISIBLE);

                        new CountDownTimer(45000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                timerTextView.setText(String.format(Locale.getDefault() ,"00: %d", millisUntilFinished / 1000));
                            }

                            public void onFinish() {
                                timerTextView.setVisibility(View.GONE);
                                resendButton.setEnabled(true);
                                resendButton.setPaintFlags(resendButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                resendButton.setTextColor(getResources().getColor(R.color.edit_text_focused_color));
                            }
                        }.start();

//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
//                        LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
//                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
//                        builder1.setView(view1);
//                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
//                        txtAlert1.setText(R.string.password_resend);
//                        alertDialog = builder1.create();
//                        alertDialog.setCancelable(false);
//                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
//                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
//                        btnOk.setText(R.string.ok);
//                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss();
//                            }
//                        });
//                        btnOk.setTypeface(face);
//                        txtAlert1.setTypeface(face);
//                        alertDialog.show();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
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

}
