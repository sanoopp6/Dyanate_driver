package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    static List<UploadFiles> uploadFiles;

    int custID;

    MyCircularProgressDialog myCircularProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        face = Typeface.createFromAsset(VerifyOTPActivity.this.getAssets(), Constants.FONT_URL);

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

        otpTextView2.setText(String.format("%s %s", VerifyOTPActivity.this.getResources().getString(R.string.we_have_sent_an_otp_via_sms), registerUserExtra.getMobile()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

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

        String title = getResources().getString(R.string.verify_otp);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (ConnectionDetector.isConnected(VerifyOTPActivity.this)) {
                        new RegisterBackground().execute();
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
                    new ResendOTPBackground().execute();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem menuLogout = menu.findItem(R.id.exit_option);
        menuLogout.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back_option) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

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

        if (!(otp.equals(otpExtra))) {
            oTPEditText.setError(VerifyOTPActivity.this.getResources().getText(R.string.invalid_otp));
            oTPEditText.requestFocus();
            return  false;
        } else {
            oTPEditText.setError(null);
        }

        return true;
    }

    private class RegisterBackground extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myCircularProgressDialog = new MyCircularProgressDialog(VerifyOTPActivity.this);
            myCircularProgressDialog.setCancelable(false);
            myCircularProgressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {

            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgDmName", registerUserExtra.getName());
            params.put("ArgDmNameAr", registerUserExtra.getNameArabic());
            params.put("ArgDmMobNumber", "966"+registerUserExtra.getMobile());
            params.put("ArgDmEmailId", registerUserExtra.getMail());
            params.put("ArgDmAddress", registerUserExtra.getAddress());
            params.put("ArgDmLatitude", registerUserExtra.getLatitide());
            params.put("ArgDmLongitude", registerUserExtra.getLongitude());
            params.put("ArgDmUserId", registerUserExtra.getUsername());
            params.put("ArgDmPassWord", registerUserExtra.getPassword());
            params.put("ArgDmVmoId", registerUserExtra.getvModelId()+"");
            params.put("ArgDmLicenseNo", registerUserExtra.getLicenseNo());
            params.put("ArgDmLicenseNoAr", registerUserExtra.getLicenseNoArabic());
            params.put("ArgDmIsFactory", registerUserExtra.isWithGlass()+"");
            params.put("ArgDmLoginType", registerUserExtra.getLoginMethod());

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "AddDriverMaster", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "AddDriverMaster", "POST", params);
            }

            return json;
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
                        //Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        //btnOk.setText(R.string.ok);
                        //view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
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

                        new UploadFilesNow().execute();

                    } else {
                        myCircularProgressDialog.dismiss();

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
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

    private class UploadFilesNow extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(Void... param) {

            jsonParser = new JsonParser();

            HashMap<String, String> params;

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            JSONObject jsonAll = null;
            JSONObject json;

            for (UploadFiles item: uploadFiles ){
                params = new HashMap<>();

                params.put("ArgBase64", item.getBase64Encoded());
                params.put("ArgRFCaption", item.getImageName());
                params.put("ArgRFDmId", custID+"");

                if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                    json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "AddRegFiles", "POST", params);

                } else {
                    json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "AddRegFiles", "POST", params);
                }

                try {
                    if (json.getBoolean("status") || jsonAll == null) {
                        jsonAll = json;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return jsonAll;
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
                        //Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        //btnOk.setText(R.string.ok);
                        //view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
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
    //                            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
    //                            btnOk.setText(R.string.ok);
    //                            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
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
    //                    Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
    //                    btnOk.setText(R.string.ok);
    //                    view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
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

    private class ResendOTPBackground extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyCircularProgressDialog(VerifyOTPActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {

            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgMobNo", "966"+registerUserExtra.getMobile());
            params.put("ArgIsDB", "false");

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "SendOTPDM", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "SendOTPDM", "POST", params);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.password_resend);
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                                resendButton.setVisibility(View.GONE);
                                try {
                                    otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOTPActivity.this);
                        LayoutInflater inflater1 = VerifyOTPActivity.this.getLayoutInflater();
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

}
