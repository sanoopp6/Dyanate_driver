package com.fast_prog.dynate.views;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.DatabaseHandler;

import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences prefs;
    Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        face = Typeface.createFromAsset(SplashScreenActivity.this.getAssets(), Constants.FONT_URL);
        prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        //Button english = (Button) findViewById(R.id.englishButton);
        //Button arabic = (Button) findViewById(R.id.arabicButton);
        //english.setTypeface(face);
        //arabic.setTypeface(face);

        DatabaseHandler dbHandler = new DatabaseHandler(SplashScreenActivity.this);
        dbHandler.truncateTable();

        //String lang = prefs.getString(Constants.PREFS_LANG, "").trim();
        //if(lang.length() == 0) {
        //    arabic.setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            Locale locale = new Locale("ar");
        //            Locale.setDefault(locale);
        //            Configuration confg = new Configuration();
        //            confg.locale = locale;
        //            getBaseContext().getResources().updateConfiguration(confg, getBaseContext().getResources().getDisplayMetrics());
        //
        //            SharedPreferences.Editor editor = prefs.edit();
        //            editor.putString(Constants.PREFS_LANG, "ar");
        //            editor.commit();
        //
        //            gotoNextActivity();
        //        }
        //    });
        //
        //    english.setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            Locale locale = new Locale("en");
        //            Locale.setDefault(locale);
        //            Configuration confg = new Configuration();
        //            confg.locale = locale;
        //            getBaseContext().getResources().updateConfiguration(confg, getBaseContext().getResources().getDisplayMetrics());
        //
        //            SharedPreferences.Editor editor = prefs.edit();
        //            editor.putString(Constants.PREFS_LANG, "en");
        //            editor.commit();
        //
        //            gotoNextActivity();
        //        }
        //    });
        //
        //} else {
        //    english.setVisibility(View.GONE);
        //    arabic.setVisibility(View.GONE);
        //
        //    Locale locale = new Locale(lang);
        //    Locale.setDefault(locale);
        //    Configuration confg = new Configuration();
        //    confg.locale = locale;
        //    getBaseContext().getResources().updateConfiguration(confg, getBaseContext().getResources().getDisplayMetrics());
        //
        //    int SPLASH_TIME_OUT = 3000;
        //    new Handler().postDelayed(new Runnable() {
        //        @Override
        //        public void run() {
        //            gotoNextActivity();
        //        }
        //    }, SPLASH_TIME_OUT);
        //}

        Locale locale = getCurrentLocale();
        String lang = "en";

        if (locale.getLanguage().equals("ar")) {
            lang = "ar";
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
        editor.putString(Constants.PREFS_LANG, lang);
        editor.commit();

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoNextActivity();
            }
        }, SPLASH_TIME_OUT);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }

    private void gotoNextActivity() {
        if (prefs.getBoolean(Constants.PREFS_IS_LOGIN, false)) {
            startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));

        } else {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        }
        finish();
    }
}
