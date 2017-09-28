package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.models.Ride;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class ShipmentDetailsActivity extends AppCompatActivity {
        //implements NavigationView.OnNavigationItemSelectedListener {

    Ride ride;

    Boolean editRide;

    static Typeface face;

    Snackbar snackbar;

    CoordinatorLayout coordinatorLayout;

    List<Order> orderList;

    Button bookVehicleButton;

    EditText subject;
    EditText shipment;

    TextView subTitleTextView;
    TextView shipTitleTextView;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

//    Spinner vehicleModelSpinner;
//    Spinner vehicleTypeSpinner;
//
//    JSONArray vehicleModelArray;
//    JSONArray vehicleTypeArray;
//
//    List<String> vehicleModelDataList;
//    List<String> vehicleModelIdList;
//    List<String> vehicleTypeDataList;
//    List<String> vehicleTypeIdList;
//
//    ArrayAdapter<String> vehicleModelAdapter;
//    ArrayAdapter<String> vehicleTypeAdapter;
//
//    TextView usernameTextView;
//    TextView modelTitleTextView;
//    TextView typeTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(ShipmentDetailsActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.shipment_details);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        ride = (Ride) getIntent().getSerializableExtra("ride");
        orderList = new ArrayList<>();

        if(ride == null) {
            editRide = false;
            ride = new Ride();
        } else {
            editRide = true;
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

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
//        usernameTextView.setText(sharedPreferences.getString(Constants.PREFS_USER_NAME, ""));
//
//        for (int i=0;i<menu.size();i++) {
//            MenuItem mi = menu.getItem(i);
//            SpannableString s = new SpannableString(mi.getTitle());
//            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            mi.setTitle(s);
//        }

        //if(preferences.getBoolean(Constants.PREFS_USER_AGENT, false)) {
        //    menu.findItem (R.id.nav_orders).setVisible(true);
        //    menu.findItem (R.id.nav_agent).setVisible(false);
        //
        //} else {
        //    menu.findItem (R.id.nav_orders).setVisible(false);
        //    menu.findItem (R.id.nav_agent).setVisible(true);
        //}

        bookVehicleButton = (Button) findViewById(R.id.btn_book_vehicle);
        bookVehicleButton.setTypeface(face);

        subTitleTextView = (TextView) findViewById(R.id.sub_title);
        subTitleTextView.setTypeface(face);

        shipTitleTextView = (TextView) findViewById(R.id.ship_title);
        shipTitleTextView.setTypeface(face);

//        modelTitleTextView = (TextView) findViewById(R.id.model_title);
//        modelTitleTextView.setTypeface(face);
//
//        typeTitleTextView = (TextView) findViewById(R.id.type_title);
//        typeTitleTextView.setTypeface(face);

        subject = (EditText) findViewById(R.id.edit_subject);
        subject.setTypeface(face);
        subject.requestFocus();

        shipment = (EditText) findViewById(R.id.edit_shipment);
        shipment.setTypeface(face);
        //shipment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        //    public void onFocusChange(View v, boolean hasFocus) {
        //        if (hasFocus) {
        //            if(subject.getText().toString().trim().length() == 0) {
        //                subject.setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
        //                subject.requestFocus();
        //            }
        //        }
        //    }
        //});

//        vehicleModelSpinner = (Spinner) findViewById(R.id.spnr_veh_model);
//        vehicleTypeSpinner = (Spinner) findViewById(R.id.spnr_veh_type);
//
//        vehicleModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Integer index = parent.getSelectedItemPosition();
//                ride.setVehicleModelId(vehicleModelIdList.get(index));
//                ride.setVehicleModelName(vehicleModelDataList.get(index));
//                ride.setVehicleModSpinnerId(index.toString().trim());
//
//                new GetVehicleType().execute();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Integer index = parent.getSelectedItemPosition();
//                ride.setVehicleTypeId(vehicleTypeIdList.get(index));
//                ride.setVehicleTypeName(vehicleTypeDataList.get(index));
//                ride.setVehicleTypeSpinnerId(index.toString().trim());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        bookVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (ConnectionDetector.isConnected(ShipmentDetailsActivity.this)) {
                        Intent intent = new Intent(ShipmentDetailsActivity.this, SenderDetailsActivity.class);
                        intent.putExtra("ride", ride);
                        intent.putExtra("editRide", editRide);
                        startActivity(intent);

                    } else {
                        ConnectionDetector.errorSnackbar(coordinatorLayout);
                    }
                }
            }
        });

//        if (ConnectionDetector.isConnected(getApplicationContext())) {
//            new GetVehicleModel().execute();
//        } else {
//            ConnectionDetector.errorSnackbar(coordinatorLayout);
//        }
//
//        vehicleTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(subject.getText().toString().trim().length() == 0) {
//                    subject.setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
//                    subject.requestFocus();
//
//                } else if(shipment.getText().toString().trim().length() == 0) {
//                    shipment.setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
//                    shipment.requestFocus();
//                }
//
//                hideSoftKeyboard();
//                return false;
//            }
//        });
//
//        vehicleModelSpinner.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(subject.getText().toString().trim().length() == 0) {
//                    subject.setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
//                    subject.requestFocus();
//
//                } else if(shipment.getText().toString().trim().length() == 0) {
//                    shipment.setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
//                    shipment.requestFocus();
//                }
//
//                hideSoftKeyboard();
//                return false;
//            }
//        });

        //ride.setVehicleTypeId(preferences.getString(Constants.PREFS_VMO_ID, "103"));
        ride.vehicleSizeId = sharedPreferences.getString(Constants.PREFS_VMS_ID, "0");

        if(editRide) {
            shipment.setText(ride.shipment);
            subject.setText(ride.subject);
//        } else {
//            ride.setVehicleModelId("0");
//            ride.setVehicleTypeId("0");
        }
    }

//    public void hideSoftKeyboard() {
//        if (getCurrentFocus() != null) {
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
//    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }
    }

    boolean validate() {
        String subjectText = subject.getText().toString();
        String shipmentText = shipment.getText().toString();
//        if(ride.getVehicleModelId().equalsIgnoreCase("0")) {
//            ((TextView)vehicleModelSpinner.getChildAt(0)).setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
//            vehicleModelSpinner.requestFocus();
//            return false;
//        }
//
//        if(ride.getVehicleTypeId().equalsIgnoreCase("0")) {
//            ((TextView)vehicleTypeSpinner.getChildAt(0)).setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
//            vehicleTypeSpinner.requestFocus();
//            return false;
//        }
        if(subjectText.trim().length() == 0) {
            subject.setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
            subject.requestFocus();
            return  false;
        } else {
            ride.subject = subjectText.trim();
            subject.setError(null);
        }

        if (shipmentText.trim().length() == 0) {
            shipment.setError(ShipmentDetailsActivity.this.getResources().getText(R.string.required));
            shipment.requestFocus();
            return  false;
        } else {
            ride.shipment = shipmentText.trim();
            shipment.setError(null);
        }

        return true;
    }

//    private class GetVehicleModel extends AsyncTask<Void, Void, JSONObject> {
//        JsonParser jsonParser;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        protected JSONObject doInBackground(Void... param) {
//            jsonParser = new JsonParser();
//
//            HashMap<String, String> params = new HashMap<>();
//
//            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//
//            JSONObject json;
//
//            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "ListVehicleMake", "POST", params);
//
//            } else {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "ListVehicleMake", "POST", params);
//            }
//
//            return json;
//        }
//
//
//        protected void onPostExecute(JSONObject response) {
//            if (response != null) {
//                try {
//                    // Parsing json object response
//                    // response will be a json object
//                    if (response.getBoolean("status")) {
//                        vehicleModelArray = response.getJSONArray("data");
//
//                        if (vehicleModelArray.length() > 0) {
//                            vehicleModelIdList = new ArrayList<>();
//                            vehicleModelDataList = new ArrayList<>();
//
//                            for (int i = 0; i < vehicleModelArray.length(); i++) {
//                                vehicleModelIdList.add(vehicleModelArray.getJSONObject(i).getString("VMId").trim());
//                                vehicleModelDataList.add(vehicleModelArray.getJSONObject(i).getString("VMName").trim());
//                            }
//                            vehicleModelAdapter = new MySpinnerAdapter(ShipmentDetailsActivity.this, android.R.layout.select_dialog_item, vehicleModelDataList);
//                            vehicleModelSpinner.setAdapter(vehicleModelAdapter);
//                        }
//
//                        if(editRide) {
//                            vehicleModelSpinner.setSelection(Integer.parseInt(ride.getVehicleModSpinnerId()));
//
//                        } else {
//                            ride.setVehicleModelId(vehicleModelArray.getJSONObject(0).getString("VMId").trim());
//                            ride.setVehicleModelName(vehicleModelArray.getJSONObject(0).getString("VMName").trim());
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private static class MySpinnerAdapter extends ArrayAdapter<String> {
//
//        // (In reality I used a manager which caches the Typeface objects)
//        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);
//
//        private MySpinnerAdapter(Context context, int resource, List<String> items) {
//            super(context, resource, items);
//        }
//
//        // Affects default (closed) state of the spinner
//        @NonNull
//        @Override
//        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//            TextView view = (TextView) super.getView(position, convertView, parent);
//            view.setTypeface(face);
//            view.setTextSize(15);
//            return view;
//        }
//
//        // Affects opened state of the spinner
//        @Override
//        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
//            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
//            view.setTypeface(face);
//            view.setTextSize(15);
//            return view;
//        }
//    }
//
//    private class GetVehicleType extends AsyncTask<Void, Void, JSONObject> {
//        JsonParser jsonParser;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        protected JSONObject doInBackground(Void... param) {
//            jsonParser = new JsonParser();
//
//            HashMap<String, String> params = new HashMap<>();
//
//            params.put("ArgVmoVMId", ride.getVehicleModelId()+"");
//
//            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//
//            JSONObject json;
//
//            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "ListVehicleModel", "POST", params);
//
//            } else {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "ListVehicleModel", "POST", params);
//            }
//
//            return json;
//        }
//
//
//        protected void onPostExecute(JSONObject response) {
//            if (response != null) {
//                try {
//                    // Parsing json object response
//                    // response will be a json object
//                    if (response.getBoolean("status")) {
//                        vehicleTypeArray = response.getJSONArray("data");
//
//                        if (vehicleTypeArray.length() > 0) {
//                            vehicleTypeDataList = new ArrayList<>();
//                            vehicleTypeIdList = new ArrayList<>();
//
//                            for (int i = 0; i < vehicleTypeArray.length(); i++) {
//                                vehicleTypeIdList.add(vehicleTypeArray.getJSONObject(i).getString("VmoId").trim());
//                                vehicleTypeDataList.add(vehicleTypeArray.getJSONObject(i).getString("VmoName").trim());
//                            }
//                            vehicleTypeAdapter = new MySpinnerAdapter(ShipmentDetailsActivity.this, android.R.layout.select_dialog_item, vehicleTypeDataList);
//                            vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
//                        }
//
//                        if(editRide) {
//                            vehicleTypeSpinner.setSelection(Integer.parseInt(ride.getVehicleTypeSpinnerId()));
//
//                        } else {
//                            ride.setVehicleTypeId(vehicleTypeArray.getJSONObject(0).getString("VmoId").trim());
//                            ride.setVehicleTypeName(vehicleTypeArray.getJSONObject(0).getString("VmoName").trim());
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
////        if (id == R.id.back_option) {
////            finish();
////        }
//
//        if (id == R.id.exit_option) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(ShipmentDetailsActivity.this);
//            LayoutInflater inflater = ShipmentDetailsActivity.this.getLayoutInflater();
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
//                    editor.putString(Constants.PREFS_CUST_ID, "0");
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
//                    Intent intent = new Intent(ShipmentDetailsActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ShipmentDetailsActivity.this);
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
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            startActivity(new Intent(ShipmentDetailsActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(ShipmentDetailsActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(ShipmentDetailsActivity.this);
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
//        //            startActivity(new Intent(ShipmentDetailsActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(ShipmentDetailsActivity.this, ChangeLanguageActivity.class));
//        }
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(ShipmentDetailsActivity.this);
//            LayoutInflater inflater = ShipmentDetailsActivity.this.getLayoutInflater();
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
//                    editor.putString(Constants.PREFS_CUST_ID, "0");
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
//                    Intent intent = new Intent(ShipmentDetailsActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ShipmentDetailsActivity.this);
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
//        //    ActivityCompat.finishAffinity(ShipmentDetailsActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

}
