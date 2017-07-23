package com.fast_prog.dynate.views;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.HashMap;
import java.util.Locale;

public class ResetPasswordActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        face = Typeface.createFromAsset(ResetPasswordActivity.this.getAssets(), Constants.FONT_URL);

        oTPEditText = (EditText) findViewById(R.id.edit_otp);
        oTPEditText.setTypeface(face);

        passwordEditText = (EditText) findViewById(R.id.edit_password1);
        passwordEditText.setTypeface(face);

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

        otpTextView2.setText(ResetPasswordActivity.this.getResources().getString(R.string.we_have_sent_an_otp_via_sms) + " " + mobNo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

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

        String title = getResources().getString(R.string.reset_password);
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
                    if (ConnectionDetector.isConnected(ResetPasswordActivity.this)) {
                        new ResetPasswordBackground().execute();
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
                    new ResendOTPBackground().execute();
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
    }

    boolean validate() {
        otp = oTPEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        if (otp.length() == 0) {
            oTPEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.required));
            oTPEditText.requestFocus();
            return  false;
        } else {
            oTPEditText.setError(null);
        }

        if (!(otp.equals(otpExtra))) {
            oTPEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.invalid_otp));
            oTPEditText.requestFocus();
            return  false;
        } else {
            oTPEditText.setError(null);
        }

        if (password.length() == 0) {
            passwordEditText.setError(ResetPasswordActivity.this.getResources().getText(R.string.required));
            passwordEditText.requestFocus();
            return  false;
        } else {
            passwordEditText.setError(null);
        }

        return true;
    }

    private class ResetPasswordBackground extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyCircularProgressDialog(ResetPasswordActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            jsonParser = new JsonParser();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgUserId", userIdExtra);
            params.put("ArgOTP", otp);
            params.put("ArgNewPassword", password);

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "UpdatePasswordDM", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "UpdatePasswordDM", "POST", params);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.password_reset_success);
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();

                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                ActivityCompat.finishAffinity(ResetPasswordActivity.this);
                                startActivity(intent);
                                finish();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
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

                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                ActivityCompat.finishAffinity(ResetPasswordActivity.this);
                                startActivity(intent);
                                finish();
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

    private class ResendOTPBackground extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyCircularProgressDialog(ResetPasswordActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgMobNo", "966"+mobNo);
            params.put("ArgIsDB", "true");

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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.password_send);
                        final AlertDialog dialog1 = builder1.create();
                        dialog1.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResetPasswordActivity.this);
                        LayoutInflater inflater1 = ResetPasswordActivity.this.getLayoutInflater();
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
