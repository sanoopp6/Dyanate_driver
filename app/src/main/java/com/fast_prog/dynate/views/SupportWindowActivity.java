package com.fast_prog.dynate.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.utilities.Constants;

public class SupportWindowActivity extends AppCompatActivity {

    TextView supportNoTextView;
    TextView supportMailTextView;

    Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_window);

        face = Typeface.createFromAsset(SupportWindowActivity.this.getAssets(), Constants.FONT_URL);

        supportNoTextView = (TextView) findViewById(R.id.textview_supportno);
        supportNoTextView.setTypeface(face);

        supportMailTextView = (TextView) findViewById(R.id.textview_supportmail);
        supportMailTextView.setTypeface(face);

        supportNoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+966 5577 69868";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
                //String phone_no = "+966 5577 69868";
                //Intent callIntent = new Intent(Intent.ACTION_CALL);
                //callIntent.setData(Uri.parse("tel:" + phone_no));
                //callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //if (ActivityCompat.checkSelfPermission(SupportWindowActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //    // TODO: Consider calling
                //    //    ActivityCompat#requestPermissions
                //    // here to request the missing permissions, and then overriding
                //    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //    //                                          int[] grantResults)
                //    // to handle the case where the user grants the permission. See the documentation
                //    // for ActivityCompat#requestPermissions for more details.
                //    return;
                //}
                //startActivity(callIntent);
            }
        });

        supportMailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@fast-prog.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.new_registration));
                i.putExtra(Intent.EXTRA_TEXT   , "");
                try {
                    startActivity(Intent.createChooser(i, getResources().getString(R.string.send_mail)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SupportWindowActivity.this, getResources().getString(R.string.there_are_no_email_clients), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SupportWindowActivity.this, LoginActivity.class));
        finish();
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
            startActivity(new Intent(SupportWindowActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
