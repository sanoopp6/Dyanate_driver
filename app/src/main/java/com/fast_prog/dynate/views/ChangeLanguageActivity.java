package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.utilities.Constants;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
        //implements NavigationView.OnNavigationItemSelectedListener {

//    RadioButton radioButtonEnglish;
//    RadioButton radioButtonArabic;
//    RadioGroup radioGroupLanguage;
//    TextView usernameTextView;
//    TextView emailTextView;

    CoordinatorLayout coordinatorLayout;

    Typeface face;

    SharedPreferences sharedPreferences;


    Button btnChangeLang;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(ChangeLanguageActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.settings);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

//        radioButtonArabic = (RadioButton) findViewById(R.id.radio_arabic);
//        radioButtonArabic.setTypeface(face);
//        radioButtonEnglish = (RadioButton) findViewById(R.id.radio_english);
//        radioButtonEnglish.setTypeface(face);
//        radioGroupLanguage = (RadioGroup) findViewById(R.id.switch_language);
//        if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equals("en")) {
//            radioButtonArabic.setChecked(false);
//            radioButtonEnglish.setChecked(true);
//        } else {
//            radioButtonArabic.setChecked(true);
//            radioButtonEnglish.setChecked(false);
//        }
//
//        radioButtonArabic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Locale locale = new Locale("ar");
//                Locale.setDefault(locale);
//                Configuration confg = new Configuration();
//                confg.locale = locale;
//                getBaseContext().getResources().updateConfiguration(confg, getBaseContext().getResources().getDisplayMetrics());
//
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(Constants.PREFS_LANG, "ar");
//                editor.commit();
//
//                ActivityCompat.finishAffinity(ChangeLanguageActivity.this);
//                startActivity(new Intent(ChangeLanguageActivity.this, HomeActivity.class));
//                finish();
//            }
//        });
//
//        radioButtonEnglish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Locale locale = new Locale("en");
//                Locale.setDefault(locale);
//                Configuration confg = new Configuration();
//                confg.locale = locale;
//                getBaseContext().getResources().updateConfiguration(confg, getBaseContext().getResources().getDisplayMetrics());
//
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(Constants.PREFS_LANG, "en");
//                editor.commit();
//
//                ActivityCompat.finishAffinity(ChangeLanguageActivity.this);
//                startActivity(new Intent(ChangeLanguageActivity.this, HomeActivity.class));
//                finish();
//            }
//        });

        btnChangeLang = (Button) findViewById(R.id.btn_change_lang);
        btnChangeLang.setTypeface(face);

        btnChangeLang.setText(getResources().getString(R.string.switch_lang));

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        Menu menu = navigationView.getMenu();
//        usernameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
//        emailTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
//        usernameTextView.setText(sharedPreferences.getString(Constants.PREFS_USER_NAME, ""));
//        emailTextView.setText(sharedPreferences.getString(Constants.PREFS_USER_MOBILE, ""));
//
//        for (int i=0;i<menu.size();i++) {
//            MenuItem mi = menu.getItem(i);
//            SpannableString s = new SpannableString(mi.getTitle());
//            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            mi.setTitle(s);
//        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        btnChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lang = "ar";

                if (sharedPreferences.getString(Constants.PREFS_LANG, "").equalsIgnoreCase("ar")) {
                    lang = "en";
                }

                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration confg = new Configuration();
                confg.locale = locale;
                getBaseContext().getResources().updateConfiguration(confg, getBaseContext().getResources().getDisplayMetrics());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.PREFS_LANG, lang);
                editor.commit();

                startActivity(new Intent(ChangeLanguageActivity.this, HomeActivity.class));
                ActivityCompat.finishAffinity(ChangeLanguageActivity.this);
                finish();
            }
        });

        //if(preferences.getBoolean(Constants.PREFS_USER_AGENT, false)) {
        //    menu.findItem (R.id.nav_orders).setVisible(true);
        //    menu.findItem (R.id.nav_agent).setVisible(false);
        //
        //} else {
        //    menu.findItem (R.id.nav_orders).setVisible(false);
        //    menu.findItem (R.id.nav_agent).setVisible(true);
        //}
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.home, menu);
    //
    //    return true;
    //}
    //
    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    int id = item.getItemId();
    //
    ////        if (id == R.id.back_option) {
    ////            finish();
    ////        }
    //
    //    if (id == R.id.exit_option) {
    //        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeLanguageActivity.this);
    //        LayoutInflater inflater = ChangeLanguageActivity.this.getLayoutInflater();
    //        final View view = inflater.inflate(R.layout.alert_dialog, null);
    //        builder.setView(view);
    //        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
    //        txtAlert.setText(R.string.are_you_sure);
    //        alertDialog = builder.create();
    //        alertDialog.setCancelable(false);
    //        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                alertDialog.dismiss();
    //            }
    //        });
    //        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                alertDialog.dismiss();
    //                SharedPreferences.Editor editor = sharedPreferences.edit();
    //
    //                if(sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
    //                    editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
    //                    new SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute();
    //                }
    //
    //                editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
    //                editor.putString(Constants.PREFS_USER_ID, "0");
    //                editor.putString(Constants.PREFS_CUST_ID, "0");
    //                editor.putString(Constants.PREFS_USER_NAME, "0");
    //                editor.putString(Constants.PREFS_USER_MOBILE, "");
    //                editor.putString(Constants.PREFS_SHARE_URL, "");
    //                editor.putString(Constants.PREFS_LATITUDE, "");
    //                editor.putString(Constants.PREFS_LONGITUDE, "");
    //                editor.putString(Constants.PREFS_USER_CONSTANT, "");
    //                editor.putString(Constants.PREFS_IS_FACTORY, "");
    //                //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
    //                editor.commit();
    //
    //                Intent intent = new Intent(ChangeLanguageActivity.this, LoginActivity.class);
    //                ActivityCompat.finishAffinity(ChangeLanguageActivity.this);
    //                startActivity(intent);
    //                finish();
    //            }
    //        });
    //        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
    //        btnOK.setTypeface(face);
    //        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
    //        btnCancel.setTypeface(face);
    //        txtAlert.setTypeface(face);
    //        alertDialog.show();
    //    }
    //
    //    return super.onOptionsItemSelected(item);
    //}

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            startActivity(new Intent(ChangeLanguageActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(ChangeLanguageActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(ChangeLanguageActivity.this);
//        //    progressDialog.setCancelable(false);
//        //    progressDialog.show();
//        //
//        //    Handler handler = new Handler();
//        //    handler.postDelayed(new Runnable() {
//        //        public void run() {
//        //            progressDialog.dismiss();
//        //
//        //            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//        //
//        //            SharedPreferences.Editor editor = preferences.edit();
//        //            editor.putBoolean(Constants.PREFS_USER_AGENT, true);
//        //            editor.commit();
//        //
//        //            startActivity(new Intent(ChangeLanguageActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//        //
//        //if (id == R.id.nav_language) {
//        //    startActivity(new Intent(ChangeLanguageActivity.this, ChangeLanguageActivity.class));
//        //}
//
//        if (id == R.id.nav_share) {
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.download_dynate) + " " + sharedPreferences.getString(Constants.PREFS_SHARE_URL, ""));
//            sendIntent.setType("text/plain");
//            startActivity(sendIntent);
//        }
//
//        if (id == R.id.nav_logout) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeLanguageActivity.this);
//            LayoutInflater inflater = ChangeLanguageActivity.this.getLayoutInflater();
//            final View view = inflater.inflate(R.layout.alert_dialog, null);
//            builder.setView(view);
//            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
//            txtAlert.setText(R.string.are_you_sure);
//            alertDialog = builder.create();
//            alertDialog.setCancelable(false);
//            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                    if(sharedPreferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
//                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
//                        new SetOffline(sharedPreferences.getString(Constants.PREFS_USER_ID, "")).execute();
//                    }
//
//                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
//                    editor.putString(Constants.PREFS_USER_ID, "0");
//                    editor.putString(Constants.PREFS_USER_NAME, "0");
//                    editor.putString(Constants.PREFS_USER_MOBILE, "");
//                    editor.putString(Constants.PREFS_SHARE_URL, "");
//                    editor.putString(Constants.PREFS_LATITUDE, "");
//                    editor.putString(Constants.PREFS_LONGITUDE, "");
//                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
//                    editor.putString(Constants.PREFS_IS_FACTORY, "");
//                    //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
//                    editor.commit();
//
//                    Intent intent = new Intent(ChangeLanguageActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ChangeLanguageActivity.this);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            Button btnOK = (Button) view.findViewById(R.id.btn_ok);
//            btnOK.setTypeface(face);
//            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
//            btnCancel.setTypeface(face);
//            txtAlert.setTypeface(face);
//            alertDialog.show();
//        }
//
//        //if (id == R.id.nav_exit) {
//        //    SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//        //
//        //    if(preferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
//        //        SharedPreferences.Editor editor = preferences.edit();
//        //        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
//        //        editor.commit();
//        //
//        //        new SetOffline(preferences.getString(Constants.PREFS_USER_ID, "")).execute();
//        //    }
//        //
//        //    ActivityCompat.finishAffinity(ChangeLanguageActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
