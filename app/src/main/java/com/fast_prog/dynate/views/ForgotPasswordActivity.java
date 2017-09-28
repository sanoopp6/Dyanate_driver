package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText mobile;

    Button sendButton;

    String number;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Typeface face;

    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    //static final int PICK_CONTACT = 101;
    //IntlPhoneInput phoneInputView;
    //ImageView mobileSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(ForgotPasswordActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.forgot_password);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        sendButton = (Button) findViewById(R.id.btn_send);
        sendButton.setTypeface(face);

        mobile = (EditText) findViewById(R.id.edt_mobile);
        mobile.setTypeface(face);

        //phoneInputView = (IntlPhoneInput) findViewById(R.id.edt_mobile);
        //phoneInputView.setEmptyDefault("sa");
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
                            new SendOTPDMBackground().execute();
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
    //                view.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View v) {
    //                        dialog.dismiss();
    //                    }
    //                });
    //                Button btnOK = (Button) view.findViewById(R.id.btn_green_rounded);
    //                btnOK.setText(R.string.ok);
    //                btnOK.setTypeface(face);
    //                txtAlert.setTypeface(face);
    //                dialog.show();
    //            }
    //        }
    //    }
    //}

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

    private class SendOTPDMBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(ForgotPasswordActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgMobNo", "966"+number);
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
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("MobNo", number);
                        intent.putExtra("OTP", response.getJSONArray("data").getJSONObject(0).getString("OTP"));
                        intent.putExtra("UserId", response.getJSONArray("data").getJSONObject(0).getString("DmId"));
                        ActivityCompat.finishAffinity(ForgotPasswordActivity.this);
                        startActivity(intent);
                        finish();

//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgotPasswordActivity.this);
//                        LayoutInflater inflater1 = ForgotPasswordActivity.this.getLayoutInflater();
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        LayoutInflater inflater1 = ForgotPasswordActivity.this.getLayoutInflater();
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
