package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NoLoginActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;
    Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_login);

        face = Typeface.createFromAsset(NoLoginActivity.this.getAssets(), Constants.FONT_URL);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        if (ConnectionDetector.isConnected(getApplicationContext())) {

            new CheckVersion().execute();

            Button login = (Button) findViewById(R.id.loginButton);
            login.setTypeface(face);
            Button register = (Button) findViewById(R.id.registerButton);
            register.setTypeface(face);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(NoLoginActivity.this, LoginActivity.class));
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(NoLoginActivity.this, RegisterActivity.class));
                }
            });

        } else {
            snackbar = Snackbar.make(coordinatorLayout, R.string.network_error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoLoginActivity.this);
        LayoutInflater inflater = NoLoginActivity.this.getLayoutInflater();
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

                ActivityCompat.finishAffinity(NoLoginActivity.this);
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

    public class CheckVersion extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyCircularProgressDialog(NoLoginActivity.this);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(NoLoginActivity.this);
                        LayoutInflater inflater1 = NoLoginActivity.this.getLayoutInflater();
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(NoLoginActivity.this);
                        LayoutInflater inflater1 = NoLoginActivity.this.getLayoutInflater();
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
