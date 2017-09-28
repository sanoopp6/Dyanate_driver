package com.fast_prog.dynate.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;

    TextView forgotView;

    Button loginButton;
    Button registerButton;
    Button englishButton;
    Button arabicButton;

    String username;
    String password;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Typeface face;

    List<String> permissionsList;

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        face = Typeface.createFromAsset(LoginActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        usernameText = (EditText) findViewById(R.id.edt_username);
        usernameText.setTypeface(face);

        passwordText = (EditText) findViewById(R.id.edt_password);
        passwordText.setTypeface(face);

        forgotView = (TextView) findViewById(R.id.txt_forgot);
        forgotView.setTypeface(face);

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setTypeface(face);

        registerButton = (Button) findViewById(R.id.btn_register);
        registerButton.setTypeface(face);

        englishButton = (Button) findViewById(R.id.englishButton);
        englishButton.setTypeface(face);

        arabicButton = (Button) findViewById(R.id.arabicButton);
        arabicButton.setTypeface(face);

        //rememberCheckBox = (CheckBox) findViewById(R.id.chk_remember);
        //rememberCheckBox.setTypeface(face);

        //driverLoginTextView = (TextView) findViewById(R.id.txt_driver_login);
        //driverLoginTextView.setTypeface(face);

        //agentLoginTextView = (TextView) findViewById(R.id.txt_agent_login);
        //driverLoginTextView.setTypeface(face);

        //detailsLayout = (LinearLayout) findViewById(R.id.details_layout);

        password = "";
        //agent = false;

        new IsAppLiveBackground().execute();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);

        //String title = getResources().getString(R.string.title_activity_login);
        //TextView titleTextView = new TextView(getApplicationContext());
        //titleTextView.setText(title);
        //titleTextView.setTextSize(16);
        //titleTextView.setAllCaps(true);
        //titleTextView.setTypeface(face, Typeface.BOLD);
        //titleTextView.setTextColor(Color.WHITE);
        //getSupportActionBar().setCustomView(titleTextView);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        int MyVersion = Build.VERSION.SDK_INT;

        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            permissionsList = new ArrayList<>();

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        //driverLoginTextView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        driverLoginTextView.setBackgroundResource(R.drawable.login_background_driver);
        //        agentLoginTextView.setBackgroundResource(R.drawable.login_background_agent_selected);
        //        agent = false;
        //    }
        //});

        //agentLoginTextView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        driverLoginTextView.setBackgroundResource(R.drawable.login_background_driver_selected);
        //        agentLoginTextView.setBackgroundResource(R.drawable.login_background_agent);
        //        agent = true;
        //    }
        //});

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameText.getText().toString().trim();
                password = passwordText.getText().toString().trim();

                if (username.length() != 0 && !username.equals("")) {
                    //String mobNumber = username.replaceAll("[^0-9.]", "");
                    //
                    //if (mobNumber.equalsIgnoreCase(username)) {
                    //    if (username.length() < 12) {
                    //        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    //        LayoutInflater inflater1 = LoginActivity.this.getLayoutInflater();
                    //        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                    //        builder1.setView(view1);
                    //        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                    //        txtAlert1.setText(getResources().getString(R.string.please_enter_number_with_country_code));
                    //        final AlertDialog dialog1 = builder1.create();
                    //        dialog1.setCancelable(false);
                    //        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                    //        Button btnOk = (Button) view1.findViewById(R.id.btn_green_rounded);
                    //        btnOk.setText(R.string.ok);
                    //        view1.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
                    //            @Override
                    //            public void onClick(View v) {
                    //                dialog1.dismiss();
                    //
                    //                if (password.length() != 0 && !password.equals("")) {
                    //                    if (ConnectionDetector.isConnectedOrConnecting(getApplicationContext())) {
                    //                        new LoginBackground().execute();
                    //
                    //                    } else {
                    //                        ConnectionDetector.errorSnackbar(coordinatorLayout);
                    //                    }
                    //                } else {
                    //                    passwordText.setError(LoginActivity.this.getResources().getText(R.string.invalid_password));
                    //                    passwordText.requestFocus();
                    //                }
                    //            }
                    //        });
                    //        btnOk.setTypeface(face);
                    //        txtAlert1.setTypeface(face);
                    //        dialog1.show();
                    //    }
                    //} else {
                    if (password.length() != 0 && !password.equals("")) {
                        if (ConnectionDetector.isConnectedOrConnecting(getApplicationContext())) {
                            new CheckDriverLoginBackground().execute();

                        } else {
                            ConnectionDetector.errorSnackbar(coordinatorLayout);
                        }
                    } else {
                        passwordText.setError(LoginActivity.this.getResources().getText(R.string.invalid_password));
                        passwordText.requestFocus();
                    }
                    //}
                } else {
                    usernameText.setError(LoginActivity.this.getResources().getText(R.string.invalid_username));
                    usernameText.requestFocus();
                }
            }
        });

        forgotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPreferences.getString(Constants.PREFS_LANG, "").equalsIgnoreCase("ar")) {
                    reloadActivity("en");
                }
            }
        });

        arabicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString(Constants.PREFS_LANG, "").equalsIgnoreCase("en")) {
                    reloadActivity("ar");
                }
            }
        });
    }

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

    private void reloadActivity(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration confg = new Configuration();
        confg.locale = locale;
        getBaseContext().getResources().updateConfiguration(confg, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREFS_LANG, lang);
        editor.commit();

        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);
        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
        txtAlert.setText(R.string.do_you_want_to_exit);
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

                ActivityCompat.finishAffinity(LoginActivity.this);
                finish();
            }
        });
        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        btnOK.setTypeface(face);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setTypeface(face);
        txtAlert.setTypeface(face);
        alertDialog.show();
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.home, menu);
    //
    //    MenuItem menuLogout = menu.findItem(R.id.exit_option);
    //    menuLogout.setVisible(false);
    //
    //    return true;
    //}

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    int id = item.getItemId();
    //
    //    if (id == R.id.back_option) {
    //        finish();
    //    }
    //
    //    return super.onOptionsItemSelected(item);
    //}

    private class CheckDriverLoginBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(LoginActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgDmUserId", username);
            params.put("ArgDmPassWord", password);

            String BASE_URL = Constants.BASE_URL_EN + "CheckDriverLogin";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "CheckDriverLogin";
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
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(Constants.PREFS_USER_ID, response.getJSONArray("data").getJSONObject(0).getString("DmId"));
                        editor.putString(Constants.PREFS_VMO_ID, response.getJSONArray("data").getJSONObject(0).getString("DmVmoId"));
                        editor.putString(Constants.PREFS_VMS_ID, response.getJSONArray("data").getJSONObject(0).getString("VmoVsId"));
                        editor.putString(Constants.PREFS_CUST_ID, response.getJSONArray("data").getJSONObject(0).getString("DmCustId"));
                        editor.putString(Constants.PREFS_USER_MOBILE, response.getJSONArray("data").getJSONObject(0).getString("DmMobNumber"));
                        editor.putString(Constants.PREFS_LATITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLatitude"));
                        editor.putString(Constants.PREFS_LONGITUDE, response.getJSONArray("data").getJSONObject(0).getString("DmLongitude"));
                        editor.putString(Constants.PREFS_USER_NAME, response.getJSONArray("data").getJSONObject(0).getString("DmUserId"));
                        editor.putString(Constants.PREFS_USER_CONSTANT, response.getJSONArray("data").getJSONObject(0).getString("DmLoginToken"));
                        editor.putBoolean(Constants.PREFS_IS_FACTORY, response.getJSONArray("data").getJSONObject(0).getBoolean("DmIsFactory"));
                        editor.putString(Constants.PREFS_SHARE_URL, "https://goo.gl/i7Qasx");
                        editor.putBoolean(Constants.PREFS_IS_LOGIN, true);

                        //editor.putString(Constants.PREFS_SHARE_URL, response.getJSONArray("data").getJSONObject(0).getString("DmId"));
                        //editor.putBoolean(Constants.PREFS_USER_AGENT, agent);
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        ActivityCompat.finishAffinity(LoginActivity.this);
                        startActivity(intent);
                        finish();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        LayoutInflater inflater1 = LoginActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        //txtAlert1.setText(response.getString("message"));
                        txtAlert1.setText(R.string.invalid_login);
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
        ActivityCompat.requestPermissions(LoginActivity.this, stringArr, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
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

    public class IsAppLiveBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(LoginActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgAppPackageName", Constants.APP_NAME);
            params.put("ArgAppVersionNo", Constants.APP_VERSION);

            String BASE_URL = Constants.BASE_URL_EN + "IsAppLive";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "IsAppLive";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(JSONObject response) {
            myCircularProgressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (!(response.getBoolean("status"))) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        LayoutInflater inflater1 = LoginActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(R.string.update_available);
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                        alertDialog.show();

                    } else if(!(response.getJSONArray("data").getJSONObject(0).getString("AppMsg").trim().equalsIgnoreCase(""))) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        LayoutInflater inflater1 = LoginActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(response.getJSONArray("data").getJSONObject(0).getString("AppMsg").trim());
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                        alertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
