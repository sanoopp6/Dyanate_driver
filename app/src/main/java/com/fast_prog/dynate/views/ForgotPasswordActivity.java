package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity {

    //IntlPhoneInput phoneInputView;
    EditText mobile;
    //ImageView mobileSelect;

    Button sendButton;

    String number;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Typeface face;
    //static final int PICK_CONTACT = 101;

    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        face = Typeface.createFromAsset(ForgotPasswordActivity.this.getAssets(), Constants.FONT_URL);

        sendButton = (Button) findViewById(R.id.btn_send);
        sendButton.setTypeface(face);

        //phoneInputView = (IntlPhoneInput) findViewById(R.id.edt_mobile);
        //phoneInputView.setEmptyDefault("sa");

        mobile = (EditText) findViewById(R.id.edt_mobile);
        mobile.setTypeface(face);

        //mobileSelect = (ImageView) findViewById(R.id.img_mobile);
        //mobileSelect.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //        startActivityForResult(intent, PICK_CONTACT);
        //    }
        //});

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidMobile(mobile.getText().toString().trim().replaceFirst("^0+(?!$)", ""))) {
                    number = mobile.getText().toString().trim().replaceFirst("^0+(?!$)", "");

                    if (number.length() != 0 && android.util.Patterns.PHONE.matcher(number).matches()) {
                        if (ConnectionDetector.isConnectedOrConnecting(getApplicationContext())) {
                            new ForgotPasswordBackground().execute();
                        } else {
                            ConnectionDetector.errorSnackbar(coordinatorLayout);
                        }
                    }
                } else {
                    mobile.setError(ForgotPasswordActivity.this.getResources().getText(R.string.invalid_mobile));
                    mobile.requestFocus();
                }
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        String title = getResources().getString(R.string.forgot_password);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);
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

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
    //
    //    if(requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
    //        Uri contactData = data.getData();
    //        Cursor c =  managedQuery(contactData, null, null, null, null);
    //        if (c.moveToFirst()) {
    //
    //            String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
    //            String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
    //
    //            if (hasPhone.equalsIgnoreCase("1")) {
    //                Cursor phones = getContentResolver().query(
    //                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
    //                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
    //                        null, null);
    //                phones.moveToFirst();
    //                String cNumber = phones.getString(phones.getColumnIndex("data1")).replaceAll("[^\\d]", "");
    //                mobile.setText(cNumber);
    //
    //            } else {
    //                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
    //                LayoutInflater inflater = ForgotPasswordActivity.this.getLayoutInflater();
    //                final View view = inflater.inflate(R.layout.alert_dialog, null);
    //                builder.setView(view);
    //                TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
    //                txtAlert.setText(R.string.no_mobile_number);
    //                final AlertDialog dialog = builder.create();
    //                dialog.setCancelable(false);
    //                view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    //                view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View v) {
    //                        dialog.dismiss();
    //                    }
    //                });
    //                Button btnOK = (Button) view.findViewById(R.id.btn_ok);
    //                btnOK.setText(R.string.ok);
    //                btnOK.setTypeface(face);
    //                txtAlert.setTypeface(face);
    //                dialog.show();
    //            }
    //        }
    //    }
    //}

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

    private class ForgotPasswordBackground extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyCircularProgressDialog(ForgotPasswordActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgMobNo", "966"+number);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        LayoutInflater inflater1 = ForgotPasswordActivity.this.getLayoutInflater();
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

                                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                try {
                                    intent.putExtra("MobNo", number);
                                    intent.putExtra("OTP", response.getJSONArray("data").getJSONObject(0).getString("OTP"));
                                    intent.putExtra("UserId", response.getJSONArray("data").getJSONObject(0).getString("DmId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ActivityCompat.finishAffinity(ForgotPasswordActivity.this);
                                startActivity(intent);
                                finish();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        dialog1.show();

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        LayoutInflater inflater1 = ForgotPasswordActivity.this.getLayoutInflater();
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
