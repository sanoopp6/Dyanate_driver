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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class AllOrdersActivity extends AppCompatActivity {
        //implements NavigationView.OnNavigationItemSelectedListener {

    Typeface face;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    TextView usernameTextView;

    List<Order> ordersArrayList;

    JSONArray ordersJSONArray;

    RecyclerView homeRecyclerView;

    LinearLayoutManager homeLayoutManager;

    RecyclerView.Adapter mHomeAdapter;

    TextView searchTitleTextView;

    String glassExtra;
    String modeExtra;
    String modeTitle;

    Button allOrderButton;

    LinearLayout topLayout;

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(AllOrdersActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        glassExtra = getIntent().getStringExtra("glass");
        modeExtra = getIntent().getStringExtra("mode");
        modeTitle = getIntent().getStringExtra("modeStr");

        String title;
        if (glassExtra.equalsIgnoreCase("true")) {
            title = getResources().getString(R.string.glass_factory);

        } else {
            title = getResources().getString(R.string.other_customers);
        }

        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

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
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem mi = menu.getItem(i);
//            SpannableString s = new SpannableString(mi.getTitle());
//            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            mi.setTitle(s);
//        }
//
        //if (preferences.getBoolean(Constants.PREFS_USER_AGENT, false)) {
        //    menu.findItem(R.id.nav_orders).setVisible(true);
        //    menu.findItem(R.id.nav_agent).setVisible(false);
        //    ;
        //
        //} else {
        //    menu.findItem(R.id.nav_orders).setVisible(false);
        //    menu.findItem(R.id.nav_agent).setVisible(true);
        //}

        homeRecyclerView = (RecyclerView) findViewById(R.id.recycler_home);

        allOrderButton = (Button) findViewById(R.id.all_orders_button);
        allOrderButton.setTypeface(face);

        topLayout = (LinearLayout) findViewById(R.id.top_layout);

        allOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topLayout.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
                allOrderButton.setVisibility(View.GONE);
            }
        });

        searchTitleTextView = (TextView) findViewById(R.id.enter_port_priority_text_view);
        searchTitleTextView.setTypeface(face);
        searchTitleTextView.setText(modeTitle.toUpperCase());

        homeRecyclerView.setHasFixedSize(true);
        homeLayoutManager = new LinearLayoutManager(AllOrdersActivity.this);
        homeRecyclerView.setLayoutManager(homeLayoutManager);
        mHomeAdapter = new MyOrdersAdapter();
        homeRecyclerView.setAdapter(mHomeAdapter);

//        if (ConnectionDetector.isConnected(getApplicationContext())) {
//            new GetOrderHistory(true).execute();
//        } else {
//            ConnectionDetector.errorSnackbar(coordinatorLayout);
//        }
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

    @Override
    protected void onResume() {
        super.onResume();

        if (ConnectionDetector.isConnected(getApplicationContext())) {
            if (ordersArrayList != null) {
                new TripDetailsMasterListBackground(false).execute();

            } else {
                new TripDetailsMasterListBackground(true).execute();
            }
        } else {
            ConnectionDetector.errorSnackbar(coordinatorLayout);
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(AllOrdersActivity.this);
//            LayoutInflater inflater = AllOrdersActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(AllOrdersActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(AllOrdersActivity.this);
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
//            startActivity(new Intent(AllOrdersActivity.this, HomeActivity.class));
//        }
//
//        //if (id == R.id.nav_orders) {
//        //    startActivity(new Intent(AllOrdersActivity.this, MyOrdersActivity.class));
//        //}
//        //if (id == R.id.nav_agent) {
//        //    final MyCircularProgressDialog progressDialog;
//        //    progressDialog = new MyCircularProgressDialog(AllOrdersActivity.this);
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
//        //            startActivity(new Intent(AllOrdersActivity.this, HomeActivity.class));
//        //            finish();
//        //        }
//        //    }, 2000);
//        //}
//
//        if (id == R.id.nav_settings) {
//            startActivity(new Intent(AllOrdersActivity.this, ChangeLanguageActivity.class));
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
//            AlertDialog.Builder builder = new AlertDialog.Builder(AllOrdersActivity.this);
//            LayoutInflater inflater = AllOrdersActivity.this.getLayoutInflater();
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
//                    Intent intent = new Intent(AllOrdersActivity.this, LoginActivity.class);
//                    ActivityCompat.finishAffinity(AllOrdersActivity.this);
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
//        //    ActivityCompat.finishAffinity(AllOrdersActivity.this);
//        //    finish();
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private class TripDetailsMasterListBackground extends AsyncTask<Void, Void, JSONObject> {
        boolean isDialog;

        TripDetailsMasterListBackground(boolean isDialog) { this.isDialog = isDialog; }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (isDialog) {
                if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                    myCircularProgressDialog = new MyCircularProgressDialog(AllOrdersActivity.this);
                    myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    myCircularProgressDialog.setCancelable(false);
                    myCircularProgressDialog.show();
                }
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            if (glassExtra.equalsIgnoreCase("true")) {
                params.put("ArgTripMCustId", "1");
                params.put("ArgExcludeCustId", "0");

            } else {
                params.put("ArgTripMCustId", "0");
                params.put("ArgExcludeCustId", "1");
            }

            params.put("ArgTripDDmId", sharedPreferences.getString(Constants.PREFS_USER_ID, "0"));
            params.put("ArgTripMID", "0");
            params.put("ArgTripDID", "0");
            params.put("ArgTripMStatus", "0");
            params.put("ArgTripDStatus", modeExtra);

            String BASE_URL = Constants.BASE_URL_EN + "TripDetailsMasterList";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "TripDetailsMasterList";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(final JSONObject jsonObject) {
            if (isDialog) {
                myCircularProgressDialog.dismiss();
            }

            if (jsonObject != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (jsonObject.getBoolean("status")) {
                        ordersJSONArray = jsonObject.getJSONArray("data");

                        ordersArrayList = new ArrayList<>();

                        if (ordersJSONArray.length() > 0) {

                            for (int i = 0; i < ordersJSONArray.length(); i++) {
                                Order order = new Order();

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
                                order.distance = ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim();

                                ordersArrayList.add(order);
                            }
                        }
                        mHomeAdapter.notifyDataSetChanged();

                    } else {
                        ordersArrayList = new ArrayList<>();
                        mHomeAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (ordersArrayList == null|| ordersArrayList.size() <= 5) {
                allOrderButton.setVisibility(View.GONE);
            }
        }
    }

    class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView orderNo;
            public TextView address;
            public TextView view1;

            ViewHolder(View v) {
                super(v);
                orderNo = (TextView) v.findViewById(R.id.id_text_view);
                address = (TextView) v.findViewById(R.id.id_text_from_to);
                view1 = (TextView) v.findViewById(R.id.show_details);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_orders_card, parent, false);
            // set the view's size, margins, paddings and layout parameters
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

            holder.orderNo.setTypeface(face);
            holder.address.setTypeface(face);
            holder.view1.setTypeface(face);

            holder.orderNo.setText(ordersArrayList.get(positionPlace).tripNo);
            holder.address.setText(String.format("%s - %s", ordersArrayList.get(positionPlace).tripFromAddress, ordersArrayList.get(positionPlace).tripToAddress));

            holder.view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Order order = ordersArrayList.get(positionPlace);

                    Intent intent = new Intent(AllOrdersActivity.this, ReplyActivity.class);
                    intent.putExtra("order", order);
                    intent.putExtra("alarm", false);
                    startActivity(intent);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if (ordersArrayList != null)
                return ordersArrayList.size();
            else
                return 0;
        }

    }
}
