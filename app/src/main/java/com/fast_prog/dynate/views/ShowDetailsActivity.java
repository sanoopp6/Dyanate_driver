package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowDetailsActivity extends AppCompatActivity {
        //implements NavigationView.OnNavigationItemSelectedListener {
        //TextView usenameTextView;

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Order order;

    String rate;
    String negotiable;

    List<Order> orderList;

    RecyclerView showDetailRecyclerView;

    LinearLayoutManager showDetailLayoutManager;

    RecyclerView.Adapter mShowDetailAdapter;

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(ShowDetailsActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.driver_replies);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        order = (Order) getIntent().getSerializableExtra("item");
        if(order == null) finish();

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
//        usenameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
//        usenameTextView.setText(sharedPreferences.getString(Constants.PREFS_USER_NAME, ""));
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

        showDetailRecyclerView = (RecyclerView) findViewById(R.id.recycler_show_details);
        showDetailRecyclerView.setHasFixedSize(true);
        showDetailLayoutManager = new LinearLayoutManager(ShowDetailsActivity.this);
        showDetailRecyclerView.setLayoutManager(showDetailLayoutManager);
        mShowDetailAdapter = new ShowDetailAdapter();
        showDetailRecyclerView.setAdapter(mShowDetailAdapter);

        if (ConnectionDetector.isConnected(getApplicationContext())) {
            new TripDetailsMasterListBackground(true).execute();
        } else {
            ConnectionDetector.errorSnackbar(coordinatorLayout);
        }
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
//            LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(ShowDetailsActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ShowDetailsActivity.this);
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
//            startActivity(new Intent(ShowDetailsActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(ShowDetailsActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(ShowDetailsActivity.this);
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
//        //            startActivity(new Intent(ShowDetailsActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(ShowDetailsActivity.this, ChangeLanguageActivity.class));
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
//            LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(ShowDetailsActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(ShowDetailsActivity.this);
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
//        //    ActivityCompat.finishAffinity(ShowDetailsActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private class TripDetailsMasterListBackground extends AsyncTask<Void, Void, JSONObject> {
        Boolean isdialog;

        TripDetailsMasterListBackground(boolean isdialog) {
            this.isdialog = isdialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isdialog) {
                if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                    myCircularProgressDialog = new MyCircularProgressDialog(ShowDetailsActivity.this);
                    myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    myCircularProgressDialog.setCancelable(false);
                    myCircularProgressDialog.show();
                }
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripMCustId", "0");
            params.put("ArgTripDDmId", "0");
            params.put("ArgTripMID", order.tripId);
            params.put("ArgTripDID", "0");
            params.put("ArgTripMStatus", "0");
            params.put("ArgTripDStatus", "0");
            params.put("ArgExcludeCustId", "0");

            String BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterList";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterList";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(final JSONObject response) {
            if (isdialog) {
                myCircularProgressDialog.dismiss();
            }

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        JSONArray ordersJSONArray = response.getJSONArray("data");
                        orderList = new ArrayList<>();

                        for (int i = 0; i < ordersJSONArray.length(); i++) {

                            if (!ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim().equalsIgnoreCase("7")) {

                                final Order order = new Order();
                                order.tripId = ordersJSONArray.getJSONObject(i).getString("TripMID").trim();
                                order.tripNo = ordersJSONArray.getJSONObject(i).getString("TripMNo").trim();
                                order.tripFromAddress = ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim();
                                order.tripFromLat = ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim();
                                order.tripFromLng = ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim();
                                try {
                                    order.tripFromSelf = Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf"));
                                } catch (Exception e) {
                                    order.tripFromSelf = false;
                                }
                                order.tripFromName = ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim();
                                order.tripFromMob = ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim();
                                order.tripToAddress = ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim();
                                order.tripToLat = ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim();
                                order.tripToLng = ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim();
                                try {
                                    order.tripToSelf = Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf"));
                                } catch (Exception e) {
                                    order.tripToSelf = false;
                                }
                                order.tripToName = ordersJSONArray.getJSONObject(i).getString("TripMToName").trim();
                                order.tripToMob = ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim();
                                order.vehicleModel = ordersJSONArray.getJSONObject(i).getString("VMName").trim();
                                order.vehicleType = ordersJSONArray.getJSONObject(i).getString("VmoName").trim();
                                order.scheduleDate = ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim();
                                order.scheduleTime = ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim();
                                order.userName = ordersJSONArray.getJSONObject(i).getString("UsrName").trim();
                                order.userMobile = ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim();
                                order.tripFilter = ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim();
                                order.tripStatus = ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim();
                                order.tripSubject = ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim();
                                order.tripNotes = ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim();
                                order.vehicleImage = ordersJSONArray.getJSONObject(i).getString("VmoURL").trim();
                                order.tripDId = ordersJSONArray.getJSONObject(i).getString("TripDID").trim();
                                order.tripDNo = ordersJSONArray.getJSONObject(i).getString("TripDNo").trim();
                                order.tripDStatus = ordersJSONArray.getJSONObject(i).getString("TripDStatus").trim();
                                order.tripDDmId = ordersJSONArray.getJSONObject(i).getString("TripDDmId").trim();
                                order.tripDIsNegotiable = ordersJSONArray.getJSONObject(i).getString("TripDIsNegotiable").trim();
                                order.tripDRate = ordersJSONArray.getJSONObject(i).getString("TripDRate").trim();
                                order.tripDDateTime = ordersJSONArray.getJSONObject(i).getString("TripDDateTime").trim();
                                order.tripDFilterName = ordersJSONArray.getJSONObject(i).getString("TripDFilterName").trim();
                                order.dmName = ordersJSONArray.getJSONObject(i).getString("DmName").trim();
                                order.dmMobNumber = ordersJSONArray.getJSONObject(i).getString("DmMobNumber").trim();
                                order.distanceKm = ordersJSONArray.getJSONObject(i).getString("DistanceKm").trim();
                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim();

                                orderList.add(order);
                            }
                        }

                        mShowDetailAdapter.notifyDataSetChanged();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
                        LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(response.getString("message"));
                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                finish();
                            }
                        });
                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        btnOK.setText(getResources().getString(R.string.ok));
                        txtAlert.setTypeface(face);
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ShowDetailAdapter extends RecyclerView.Adapter<ShowDetailAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView txtOrderNo;
            TextView txtDate;
            TextView titleLoc;
            TextView textDistance;
            TextView titleRate;
            TextView textRate;
            EditText edittextRate;
            TextView titleNegotiable;
            TextView textNegotiable;
            TextView titleStatus;
            TextView textStatus;
            TextView titleDriver;
            TextView textDriver;
            TextView textDriverNo;
            Button btnApprove;
            public Button btnCancel;

            ViewHolder(View v) {
                super(v);
                txtOrderNo = (TextView) v.findViewById(R.id.txt_order_no);
                txtDate = (TextView) v.findViewById(R.id.txt_date);
                titleLoc = (TextView) v.findViewById(R.id.title_location);
                textDistance = (TextView) v.findViewById(R.id.text_distance);
                titleRate = (TextView) v.findViewById(R.id.title_rate);
                textRate = (TextView) v.findViewById(R.id.text_rate);
                edittextRate = (EditText) v.findViewById(R.id.edittext_rate);
                titleNegotiable = (TextView) v.findViewById(R.id.title_negotiable);
                textNegotiable = (TextView) v.findViewById(R.id.text_negotiable);
                titleStatus = (TextView) v.findViewById(R.id.title_status);
                textStatus = (TextView) v.findViewById(R.id.text_status);
                titleDriver = (TextView) v.findViewById(R.id.title_driver);
                textDriver = (TextView) v.findViewById(R.id.text_driver);
                textDriverNo = (TextView) v.findViewById(R.id.text_driver_no);
                btnApprove = (Button) v.findViewById(R.id.btn_approve);
                btnCancel = (Button) v.findViewById(R.id.btn_cancel);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ShowDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_details, parent, false);
            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            //View view = holder.itemView;

            final int positionPlace = position;

            holder.setIsRecyclable(false);

            holder.txtOrderNo.setTypeface(face);
            holder.txtDate.setTypeface(face);
            holder.titleLoc.setTypeface(face);
            holder.textDistance.setTypeface(face);
            holder.titleRate.setTypeface(face);
            holder.textRate.setTypeface(face);
            holder.edittextRate.setTypeface(face);
            holder.titleNegotiable.setTypeface(face);
            holder.textNegotiable.setTypeface(face);
            holder.titleStatus.setTypeface(face);
            holder.textStatus.setTypeface(face);
            holder.titleDriver.setTypeface(face);
            holder.textDriver.setTypeface(face);
            holder.textDriverNo.setTypeface(face);
            holder.btnApprove.setTypeface(face);
            holder.btnCancel.setTypeface(face);

            holder.txtOrderNo.setText(orderList.get(positionPlace).tripDNo);
            holder.txtDate.setText(orderList.get(positionPlace).tripDDateTime);
            holder.textDistance.setText(orderList.get(positionPlace).distanceKm);
            holder.textRate.setText(orderList.get(positionPlace).tripDRate);
            holder.edittextRate.setText(orderList.get(positionPlace).tripDRate);
            holder.textNegotiable.setText(orderList.get(positionPlace).tripDIsNegotiable);
            holder.textStatus.setText(orderList.get(positionPlace).tripDFilterName);
            holder.textDriver.setText(orderList.get(positionPlace).dmName);
            holder.textDriverNo.setText(orderList.get(positionPlace).dmMobNumber);

            String tripDStatus = orderList.get(positionPlace).tripDStatus;

            if(orderList.get(positionPlace).tripDIsNegotiable.equalsIgnoreCase("true")) {
                holder.textNegotiable.setText(getResources().getString(R.string.yes));
                holder.edittextRate.setVisibility(View.GONE);
                holder.textRate.setVisibility(View.VISIBLE);
                if(tripDStatus.matches("1"))  {
                    holder.edittextRate.setVisibility(View.VISIBLE);
                    holder.textRate.setVisibility(View.GONE);
                }

            } else {
                holder.textNegotiable.setText(getResources().getString(R.string.no));
                holder.edittextRate.setVisibility(View.GONE);
                holder.textRate.setVisibility(View.VISIBLE);
            }

            if (!(tripDStatus.equalsIgnoreCase("1"))) {
                holder.btnApprove.setVisibility(View.GONE);
            }

            //if (tripDStatus.matches("3|4|5")) {
            //    holder.btnCancel.setVisibility(View.GONE);
            //}

            holder.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
                    LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
                    final View view = inflater.inflate(R.layout.alert_dialog, null);
                    builder.setView(view);
                    TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                    txtAlert.setText(R.string.are_you_sure);
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
                            rate = holder.edittextRate.getText().toString().trim();
                            negotiable = orderList.get(positionPlace).tripDIsNegotiable;
                            new TripDetailsBackground(orderList.get(positionPlace).tripDId).execute();
                        }
                    });
                    Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                    btnOK.setTypeface(face);
                    Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                    btnCancel.setTypeface(face);
                    txtAlert.setTypeface(face);
                    alertDialog.show();
                }
            });

            //holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
            //        LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
            //        final View view = inflater.inflate(R.layout.alert_dialog, null);
            //        builder.setView(view);
            //        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            //        txtAlert.setText(R.string.are_you_sure);
            //        final AlertDialog dialog = builder.create();
            //        dialog.setCancelable(false);
            //        view.findViewById(R.id.btn_red_rounded).setOnClickListener(new View.OnClickListener() {
            //            @Override
            //            public void onClick(View v) {
            //                dialog.dismiss();
            //            }
            //        });
            //        view.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
            //            @Override
            //            public void onClick(View v) {
            //                dialog.dismiss();
            //                new CancelTripDetail(orderList.get(positionPlace).getTripdId()).execute();
            //            }
            //        });
            //        Button btnOK = (Button) view.findViewById(R.id.btn_green_rounded);
            //        btnOK.setTypeface(face);
            //        Button btnCancel = (Button) view.findViewById(R.id.btn_red_rounded);
            //        btnCancel.setTypeface(face);
            //        txtAlert.setTypeface(face);
            //        dialog.show();
            //    }
            //});
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if(orderList != null) {
                return orderList.size();
            } else {
                return 0;
            }
        }
    }

    private class TripDetailsBackground extends AsyncTask<Void, Void, JSONObject> {
        JSONObject jsonStatus;
        JSONObject jsonDetails;

        String tripDId;

        TripDetailsBackground(String tripDId) {
            this.tripDId = tripDId.trim();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(ShowDetailsActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParserStatus = new JsonParser();
            JsonParser jsonParserDetails = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgTripDID", tripDId);
            params.put("ArgTripDRate", rate);
            params.put("ArgTripDIsNegotiable", negotiable);

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                jsonDetails = jsonParserDetails.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsUpdate", "POST", params);

            } else {
                jsonDetails = jsonParserDetails.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsUpdate", "POST", params);
            }

            params = new HashMap<>();

            params.put("ArgTripDID", tripDId);
            params.put("ArgTripDStatus", "2");

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                jsonStatus = jsonParserStatus.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsStatusUpdate", "POST", params);

            } else {
                jsonStatus = jsonParserStatus.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsStatusUpdate", "POST", params);
            }

            return jsonStatus;
        }

        protected void onPostExecute(final JSONObject jsonObject) {
            myCircularProgressDialog.dismiss();

            if (jsonDetails != null && jsonStatus != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (jsonDetails.getBoolean("status") && jsonStatus.getBoolean("status")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
                        LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.approved_succesfully);
                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                new TripDetailsMasterListBackground(false).execute();
                            }
                        });
                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        alertDialog.show();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
                        LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                        txtAlert.setText(R.string.approval_failed);
                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                        btnOK.setTypeface(face);
                        txtAlert.setTypeface(face);
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //private class CancelTripDetail extends AsyncTask<Void, Void, JSONObject> {
    //    MyCircularProgressDialog progressDialog;
    //    JsonParser jsonParser;
    //    String tripDId;
    //
    //    CancelTripDetail(String tripDId) {
    //        this.tripDId = tripDId.trim();
    //    }
    //
    //    @Override
    //    protected void onPreExecute() {
    //        super.onPreExecute();
    //        progressDialog = new MyCircularProgressDialog(ShowDetailsActivity.this);
    //        progressDialog.setCancelable(false);
    //        progressDialog.show();
    //    }
    //
    //    protected JSONObject doInBackground(Void... param) {
    //        jsonParser = new JsonParser();
    //
    //        HashMap<String, String> params = new HashMap<>();
    //
    //        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    //
    //        params.put("ArgTripDID", tripDId);
    //        params.put("ArgTripDStatus", "3");
    //
    //        JSONObject json;
    //
    //        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsStatusUpdate", "POST", params);
    //
    //        } else {
    //            json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsStatusUpdate", "POST", params);
    //        }
    //
    //        return json;
    //    }
    //
    //
    //    protected void onPostExecute(final JSONObject response) {
    //        Handler handler = new Handler();
    //        handler.postDelayed(new Runnable() {
    //            public void run() {
    //                progressDialog.dismiss();
    //
    //                if (response != null) {
    //                    try {
    //                        // Parsing json object response
    //                        // response will be a json object
    //                        if (response.getBoolean("status")) {
    //                            AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
    //                            LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
    //                            final View view = inflater.inflate(R.layout.alert_dialog, null);
    //                            builder.setView(view);
    //                            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
    //                            txtAlert.setText(R.string.canceled_succesfully);
    //                            final AlertDialog dialog = builder.create();
    //                            dialog.setCancelable(false);
    //                            view.findViewById(R.id.btn_red_rounded).setVisibility(View.GONE);
    //                            view.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
    //                                @Override
    //                                public void onClick(View v) {
    //                                    dialog.dismiss();
    //
    //                                    new GetShowDetails(false).execute();
    //                                }
    //                            });
    //                            Button btnOK = (Button) view.findViewById(R.id.btn_green_rounded);
    //                            btnOK.setTypeface(face);
    //                            txtAlert.setTypeface(face);
    //                            dialog.show();
    //
    //                        } else {
    //                            AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
    //                            LayoutInflater inflater = ShowDetailsActivity.this.getLayoutInflater();
    //                            final View view = inflater.inflate(R.layout.alert_dialog, null);
    //                            builder.setView(view);
    //                            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
    //                            txtAlert.setText(R.string.canceling_failed);
    //                            final AlertDialog dialog = builder.create();
    //                            dialog.setCancelable(false);
    //                            view.findViewById(R.id.btn_red_rounded).setVisibility(View.GONE);
    //                            view.findViewById(R.id.btn_green_rounded).setOnClickListener(new View.OnClickListener() {
    //                                @Override
    //                                public void onClick(View v) {
    //                                    dialog.dismiss();
    //                                }
    //                            });
    //                            Button btnOK = (Button) view.findViewById(R.id.btn_green_rounded);
    //                            btnOK.setTypeface(face);
    //                            txtAlert.setTypeface(face);
    //                            dialog.show();
    //                        }
    //                    } catch (JSONException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //        }, 2000);
    //    }
    //}

}
