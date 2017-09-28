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

public class ResetPasswordActivity extends AppCompatActivity {
    //EditText confPasswordEditText;

    public static EditText oTPEditText;

    EditText passwordEditText;

    Button updateButton;

    TextView resendButton;
    TextView timerTextView;
    TextView otpTextView2;

    String otp;
    String password;
    String mobNo;
    String otpExtra;
    String userIdExtra;

    boolean isRegistered;

    SMSReceiver receiver;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Typeface face;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    MyCircularProgressDialog myCircularProgressDialog;

    List<String> otpArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(ResetPasswordActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.reset_password);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        oTPEditText = (EditText) findViewById(R.id.edit_otp);
        oTPEditText.setTypeface(face);

        passwordEditText = (EditText) findViewById(R.id.edit_password1);
        passwordEditText.setTypeface(face);

//        confPasswordEditText = (EditText) findViewById(R.id.edit_conf_password1);
//        confPasswordEditText.setTypeface(face);

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

        otpExtra = getIntent().getStringExtra("OTP");
        userIdExtra = getIntent().getStringExtra("UserId");
        mobNo = getIntent().getStringExtra("MobNo");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        otpTextView2.setText(String.format("%s %s", ResetPasswordActivity.this.getResources().getString(R.string.we_have_sent_an_otp_via_sms), mobNo));

        new CountDownTimer(45000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.format(Locale.getDefault(), "00: %d", millisUntilFinished / 1000));
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
                    if (ConnectionDetector.isConnected(ResetPasswordActivity.this)) {
                        new UpdatePasswordDMBackground().execute();
                    } else {
                        ConnectionDetector.errorSnackbar(coordinatorLayout);
                    }
                }
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(ResetPasswordActivity.this)) {
                    new SendOTPDMBackground().execute();
                } else {
                    ConnectionDetector.errorSnackbar(coordinatorLayout);
                }
            }
        });

        if(!isRegistered) {
            receiver = new SMSReceiver("reset");
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(receiver, filter);
            isRegistered = true;
        }

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    password = passwordEditText.getText().toString().trim();

                    if (password.length() == 0) {
                        passwordEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.required));

                    } else if (password.length() < 6) {
                        passwordEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.password_min_6_char));
                    }
                }
            }
        });

        otpArray = new ArrayList<>();
        otpArray.add(otpExtra);
//        confPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    if (password != null && !password.equals(confPasswordEditText.getText().toString().trim())) {
//                        confPasswordEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.password_does_not_match));
//                    }
//                }
//            }
//        });
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
            receiver = new SMSReceiver("reset");
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
        password = passwordEditText.getText().toString().trim();
        //String conf = confPasswordEditText.getText().toString().trim();

        if (otp.length() == 0) {
            oTPEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.required));
            oTPEditText.requestFocus();
            return  false;
        } else {
            oTPEditText.setError(null);
        }

        if (!useLoop(otpExtra)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
            LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
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
            //oTPEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.invalid_otp));
            //oTPEditText.requestFocus();
            return  false;
        } else {
            oTPEditText.setError(null);
        }

        if (password.length() == 0) {
            passwordEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.required));
            passwordEditText.requestFocus();
            return false;
        } else if (password.length() < 6) {
            passwordEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.password_min_6_char));
            passwordEditText.requestFocus();
            return false;
            //} else if (!conf.equals(password)) {
            //    confPasswordEditText.setError(ResetPasswordActivity.this.getResources().getString(R.string.password_does_not_match));
            //    return false;
        } else {
            //confPasswordEditText.setError(null);
            passwordEditText.setError(null);
        }

        return true;
    }

    private class UpdatePasswordDMBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(ResetPasswordActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgUserId", userIdExtra);
            params.put("ArgOTP", otp);
            params.put("ArgNewPassword", password);

            String BASE_URL = Constants.BASE_URL_EN + "UpdatePasswordDM";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "UpdatePasswordDM";
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.password_reset_success);
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                ActivityCompat.finishAffinity(ResetPasswordActivity.this);
                                startActivity(intent);
                                finish();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        alertDialog.show();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
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

                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                ActivityCompat.finishAffinity(ResetPasswordActivity.this);
                                startActivity(intent);
                                finish();
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

    private class SendOTPDMBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(ResetPasswordActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgMobNo", "966"+mobNo);
            params.put("ArgIsDB", "true");

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
                                timerTextView.setText(String.format(Locale.getDefault(), "00: %d", millisUntilFinished / 1000));
                            }

                            public void onFinish() {
                                timerTextView.setVisibility(View.GONE);
                                resendButton.setEnabled(true);
                                resendButton.setPaintFlags(resendButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                resendButton.setTextColor(getResources().getColor(R.color.edit_text_focused_color));
                            }
                        }.start();

//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
//                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
//                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
//                        builder1.setView(view1);
//                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
//                        txtAlert1.setText(R.string.password_send);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
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
