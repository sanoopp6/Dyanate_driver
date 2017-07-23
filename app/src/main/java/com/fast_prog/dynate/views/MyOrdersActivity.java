package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.Order;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.CustomTypefaceSpan;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.fast_prog.dynate.utilities.SetOffline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Typeface face;

    Snackbar snackbar;

    CoordinatorLayout coordinatorLayout;

    JSONArray ordersJSONArray;

    List<Order> ordersArrayList;

    Order order;

    RecyclerView homeRecyclerView;

    LinearLayoutManager homeLayoutManager;

    RecyclerView.Adapter mHomeAdapter;

    TextView headerTextView;
    TextView usernameTextView;
    TextView orderNoTitle;
    TextView orderNoValue;
    TextView vehNoTitle;
    TextView vehNoValue;
    TextView fromAddrTitle;
    TextView fromAddrValue;
    TextView toAddrTitle;
    TextView toAddrValue;
    TextView dateTitle;
    TextView dateValue;
    TextView timeTitle;
    TextView timeValue;
    TextView subjectTitle;
    TextView subjectValue;
    TextView notesTitle;
    TextView notesValue;
    TextView statusTitle;
    TextView statusValue;

    Button showDetailButton;

    int selectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        face = Typeface.createFromAsset(MyOrdersActivity.this.getAssets(), Constants.FONT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

//        String title = getResources().getString(R.string.my_orders);
//        TextView titleTextView = new TextView(getApplicationContext());
//        titleTextView.setText(title);
//        titleTextView.setTextSize(16);
//        titleTextView.setAllCaps(true);
//        titleTextView.setTypeface(face, Typeface.BOLD);
//        titleTextView.setTextColor(Color.WHITE);
//        getSupportActionBar().setCustomView(titleTextView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        usernameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        usernameTextView.setText(preferences.getString(Constants.PREFS_USER_NAME, ""));

        for (int i=0;i<menu.size();i++) {
            MenuItem mi = menu.getItem(i);
            SpannableString s = new SpannableString(mi.getTitle());
            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s);
        }

        //if(preferences.getBoolean(Constants.PREFS_USER_AGENT, false)) {
        //    menu.findItem (R.id.nav_orders).setVisible(true);
        //    menu.findItem (R.id.nav_agent).setVisible(false);
        //
        //} else {
        //    menu.findItem (R.id.nav_orders).setVisible(false);
        //    menu.findItem (R.id.nav_agent).setVisible(true);
        //}

        homeRecyclerView = (RecyclerView) findViewById(R.id.recycler_home);

        headerTextView = (TextView) findViewById(R.id.enter_port_priority_text_view);
        headerTextView.setTypeface(face, Typeface.BOLD);
        headerTextView.setText(getResources().getString(R.string.my_orders).toUpperCase());

        orderNoTitle = (TextView) findViewById(R.id.order_no_title_text_view);
        orderNoTitle.setTypeface(face);

        orderNoValue = (TextView) findViewById(R.id.order_no_value_text_view);
        orderNoValue.setTypeface(face);

        vehNoTitle = (TextView) findViewById(R.id.veh_name_title_text_view);
        vehNoTitle.setTypeface(face);

        vehNoValue = (TextView) findViewById(R.id.veh_name_value_text_view);
        vehNoValue.setTypeface(face);

        fromAddrTitle = (TextView) findViewById(R.id.from_addr_title_text_view);
        fromAddrTitle.setTypeface(face);

        fromAddrValue = (TextView) findViewById(R.id.from_addr_value_text_view);
        fromAddrValue.setTypeface(face);

        toAddrTitle = (TextView) findViewById(R.id.to_addr_title_text_view);
        toAddrTitle.setTypeface(face);

        toAddrValue = (TextView) findViewById(R.id.to_addr_value_text_view);
        toAddrValue.setTypeface(face);

        dateTitle = (TextView) findViewById(R.id.date_title_text_view);
        dateTitle.setTypeface(face);

        dateValue = (TextView) findViewById(R.id.date_value_text_view);
        dateValue.setTypeface(face);

        timeTitle = (TextView) findViewById(R.id.time_title_text_view);
        timeTitle.setTypeface(face);

        timeValue = (TextView) findViewById(R.id.time_value_text_view);
        timeValue.setTypeface(face);

        subjectTitle = (TextView) findViewById(R.id.subject_title_text_view);
        subjectTitle.setTypeface(face);

        subjectValue = (TextView) findViewById(R.id.subject_value_text_view);
        subjectValue.setTypeface(face);

        notesTitle = (TextView) findViewById(R.id.notes_title_text_view);
        notesTitle.setTypeface(face);

        notesValue = (TextView) findViewById(R.id.notes_value_text_view);
        notesValue.setTypeface(face);

        statusTitle = (TextView) findViewById(R.id.status_title_text_view);
        statusTitle.setTypeface(face);

        statusValue = (TextView) findViewById(R.id.status_value_text_view);
        statusValue.setTypeface(face);

        showDetailButton = (Button) findViewById(R.id.show_button);
        showDetailButton.setTypeface(face);

        showDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyOrdersActivity.this, DetailActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });

        if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
            timeValue.setTextDirection(View.TEXT_DIRECTION_RTL);
            subjectValue.setTextDirection(View.TEXT_DIRECTION_RTL);
            notesValue.setTextDirection(View.TEXT_DIRECTION_RTL);
            fromAddrValue.setTextDirection(View.TEXT_DIRECTION_RTL);
            toAddrValue.setTextDirection(View.TEXT_DIRECTION_RTL);
            vehNoValue.setTextDirection(View.TEXT_DIRECTION_RTL);

        } else {
            timeValue.setTextDirection(View.TEXT_DIRECTION_LTR);
            subjectValue.setTextDirection(View.TEXT_DIRECTION_LTR);
            notesValue.setTextDirection(View.TEXT_DIRECTION_LTR);
            fromAddrValue.setTextDirection(View.TEXT_DIRECTION_LTR);
            toAddrValue.setTextDirection(View.TEXT_DIRECTION_LTR);
            vehNoValue.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        homeRecyclerView.setHasFixedSize(true);
        homeLayoutManager = new LinearLayoutManager(MyOrdersActivity.this);
        homeRecyclerView.setLayoutManager(homeLayoutManager);
        mHomeAdapter = new MyOrdersAdapter();
        homeRecyclerView.setAdapter(mHomeAdapter);

        selectedId = -1;

    }

//    private class UpdateTripNotifiedCustStatus extends AsyncTask<Void, Void, JSONObject> {
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
//            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
//
//            HashMap<String, String> params = new HashMap<>();
//
//            params.put("ArgTripDId", order.getTripdId()+"");
//            params.put("ArgTripDIsNotifiedCust", "true");
//
//            JSONObject json;
//
//            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "UpdateTripNotifiedCustStatus", "POST", params);
//
//            } else {
//                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "UpdateTripNotifiedCustStatus", "POST", params);
//            }
//
//            return json;
//        }
//
//        protected void onPostExecute(final JSONObject response) {
//            if (response != null) {
//                try {
//                    // Parsing json object response
//                    // response will be a json object
//                    if (response.getBoolean("status")) {
//                        //Log.e("success");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ConnectionDetector.isConnected(getApplicationContext())) {
            if (ordersArrayList != null) {
                new GetOrderHistory(false).execute();

            } else {
                new GetOrderHistory(true).execute();
            }
        } else {
            ConnectionDetector.errorSnackbar(coordinatorLayout);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back_option) {
            finish();
        }

        if (id == R.id.exit_option) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyOrdersActivity.this);
            LayoutInflater inflater = MyOrdersActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.are_you_sure);
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
                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if(prefs.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                        new SetOffline(prefs.getString(Constants.PREFS_USER_ID, "")).execute();
                    }
                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
                    //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
                    editor.putString(Constants.PREFS_USER_ID, "0");
                    editor.putString(Constants.PREFS_CUST_ID, "0");
                    editor.putString(Constants.PREFS_USER_NAME, "0");
                    editor.putString(Constants.PREFS_USER_MOBILE, "");
                    editor.putString(Constants.PREFS_SHARE_URL, "");
                    editor.putString(Constants.PREFS_LATITUDE, "");
                    editor.putString(Constants.PREFS_LONGITUDE, "");
                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
                    editor.putString(Constants.PREFS_IS_FACTORY, "");
                    editor.commit();

                    Intent intent = new Intent(MyOrdersActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(MyOrdersActivity.this);
                    startActivity(intent);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(MyOrdersActivity.this, HomeActivity.class));
        }

        //if (id == R.id.nav_orders) {
        //    startActivity(new Intent(MyOrdersActivity.this, MyOrdersActivity.class));
        //}
        //if (id == R.id.nav_agent) {
        //    final MyCircularProgressDialog progressDialog;
        //    progressDialog = new MyCircularProgressDialog(MyOrdersActivity.this);
        //    progressDialog.setCancelable(false);
        //    progressDialog.show();
        //
        //    Handler handler = new Handler();
        //    handler.postDelayed(new Runnable() {
        //        public void run() {
        //            progressDialog.dismiss();
        //
        //            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        //
        //            SharedPreferences.Editor editor = preferences.edit();
        //            editor.putBoolean(Constants.PREFS_USER_AGENT, true);
        //            editor.commit();
        //
        //            startActivity(new Intent(MyOrdersActivity.this, HomeActivity.class));
        //            finish();
        //        }
        //    }, 2000);
        //}

        if (id == R.id.nav_language) {
            startActivity(new Intent(MyOrdersActivity.this, ChangeLanguageActivity.class));
        }

        if (id == R.id.nav_share) {
            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.download_dynate) + " " + preferences.getString(Constants.PREFS_SHARE_URL, ""));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyOrdersActivity.this);
            LayoutInflater inflater = MyOrdersActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
            txtAlert.setText(R.string.are_you_sure);
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
                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if(prefs.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
                        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
                        new SetOffline(prefs.getString(Constants.PREFS_USER_ID, "")).execute();
                    }
                    editor.putBoolean(Constants.PREFS_IS_LOGIN, false);
                    //editor.putBoolean(Constants.PREFS_USER_AGENT, false);
                    editor.putString(Constants.PREFS_USER_ID, "0");
                    editor.putString(Constants.PREFS_CUST_ID, "0");
                    editor.putString(Constants.PREFS_USER_NAME, "0");
                    editor.putString(Constants.PREFS_USER_MOBILE, "");
                    editor.putString(Constants.PREFS_SHARE_URL, "");
                    editor.putString(Constants.PREFS_LATITUDE, "");
                    editor.putString(Constants.PREFS_LONGITUDE, "");
                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
                    editor.putString(Constants.PREFS_IS_FACTORY, "");
                    editor.commit();

                    Intent intent = new Intent(MyOrdersActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(MyOrdersActivity.this);
                    startActivity(intent);
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

        //if (id == R.id.nav_exit) {
        //    SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        //
        //    if(preferences.getString(Constants.PREFS_ONLINE_STATUS, "").equalsIgnoreCase("online")) {
        //        SharedPreferences.Editor editor = preferences.edit();
        //        editor.putString(Constants.PREFS_ONLINE_STATUS, "offline");
        //        editor.commit();
        //
        //        new SetOffline(preferences.getString(Constants.PREFS_USER_ID, "")).execute();
        //    }
        //
        //    ActivityCompat.finishAffinity(MyOrdersActivity.this);
        //    finish();
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetOrderHistory extends AsyncTask<Void, Void, JSONObject> {
        JsonParser jsonParser;
        MyCircularProgressDialog progressDialog;
        Boolean dialogSt;

        GetOrderHistory(Boolean dialogSt) {
            this.dialogSt = dialogSt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (dialogSt) {
                progressDialog = new MyCircularProgressDialog(MyOrdersActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            params.put("ArgTripMID", "0");
            params.put("ArgTripMCustId", preferences.getString(Constants.PREFS_CUST_ID, "0"));
            params.put("ArgTripMStatus", "0");

            JSONObject json;

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_AR + "TripMasterList", "POST", params);

            } else {
                json = jsonParser.makeHttpRequest(Constants.BASE_URL_EN + "TripMasterList", "POST", params);
            }

            return json;
        }

        protected void onPostExecute(final JSONObject response) {
            if (dialogSt) {
                progressDialog.dismiss();
            }

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        ordersJSONArray = response.getJSONArray("data");

                        ordersArrayList = new ArrayList<>();

                        if (ordersJSONArray.length() > 0) {

                            for (int i = 0; i < ordersJSONArray.length(); i++) {
                                Order order = new Order();

                                order.setTripId(ordersJSONArray.getJSONObject(i).getString("TripMID").trim());
                                order.setTripNo(ordersJSONArray.getJSONObject(i).getString("TripMNo").trim());
                                order.setTripFromAddress(ordersJSONArray.getJSONObject(i).getString("TripMFromAddress").trim());
                                order.setTripFromLat(ordersJSONArray.getJSONObject(i).getString("TripMFromLat").trim());
                                order.setTripFromLng(ordersJSONArray.getJSONObject(i).getString("TripMFromLng").trim());
                                try {
                                    order.setTripfromSelf(Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMFromIsSelf")));
                                } catch (Exception e) {
                                    order.setTripfromSelf(false);
                                }
                                order.setTripFromName(ordersJSONArray.getJSONObject(i).getString("TripMFromName").trim());
                                order.setTripFromMob(ordersJSONArray.getJSONObject(i).getString("TripMFromMob").trim());
                                order.setTripToAddress(ordersJSONArray.getJSONObject(i).getString("TripMToAddress").trim());
                                order.setTripToLat(ordersJSONArray.getJSONObject(i).getString("TripMToLat").trim());
                                order.setTripToLng(ordersJSONArray.getJSONObject(i).getString("TripMToLng").trim());
                                try {
                                    order.setTripToSelf(Boolean.parseBoolean(ordersJSONArray.getJSONObject(i).getString("TripMToIsSelf")));
                                } catch (Exception e) {
                                    order.setTripToSelf(false);
                                }
                                order.setTripToName(ordersJSONArray.getJSONObject(i).getString("TripMToName").trim());
                                order.setTripToMob(ordersJSONArray.getJSONObject(i).getString("TripMToMob").trim());
                                order.setVehicleModel(ordersJSONArray.getJSONObject(i).getString("VsName").trim());
                                //order.setVehicleType(ordersJSONArray.getJSONObject(i).getString("VmoName").trim());
                                order.setScheduleDate(ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim());
                                order.setScheduleTime(ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim());
                                order.setUserName(ordersJSONArray.getJSONObject(i).getString("UsrName").trim());
                                order.setUserMobile(ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim());
                                order.setTripFilter(ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim());
                                order.setTripStatus(ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim());
                                order.setTripSubject(ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim());
                                order.setTripNotes(ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim());
                                //order.setVehicleImage(ordersJSONArray.getJSONObject(i).getString("VmoURL").trim());

                                ordersArrayList.add(order);
                            }

                            if (selectedId != -1 && ordersArrayList.get(selectedId) != null) {
                                showDetailButton.setEnabled(true);

                                orderNoValue.setText(ordersArrayList.get(selectedId).getTripNo());
                                //vehNoValue.setText(ordersArrayList.get(selectedId).getVehicleModel() + " - " + ordersArrayList.get(selectedId).getVehicleType());
                                vehNoValue.setText(ordersArrayList.get(selectedId).getVehicleModel());
                                fromAddrValue.setText(ordersArrayList.get(selectedId).getTripFromAddress());
                                toAddrValue.setText(ordersArrayList.get(selectedId).getTripToAddress());
                                dateValue.setText(ordersArrayList.get(selectedId).getScheduleDate());
                                timeValue.setText(ordersArrayList.get(selectedId).getScheduleTime());
                                subjectValue.setText(ordersArrayList.get(selectedId).getTripSubject());
                                notesValue.setText(ordersArrayList.get(selectedId).getTripNotes());
                                statusValue.setText(ordersArrayList.get(selectedId).getTripFilter());
                            }

                        } else {
                            snackbar = Snackbar.make(coordinatorLayout, response.getString("message"), Snackbar.LENGTH_LONG).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });
                            snackbar.show();
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
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_orders_card, parent, false);
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

            holder.orderNo.setText(ordersArrayList.get(positionPlace).getTripNo());
            holder.address.setText(ordersArrayList.get(positionPlace).getTripFromAddress() + " - " + ordersArrayList.get(positionPlace).getTripToAddress());

            holder.view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedId = positionPlace;

                    order = ordersArrayList.get(positionPlace);
                    showDetailButton.setEnabled(true);

                    orderNoValue.setText(ordersArrayList.get(positionPlace).getTripNo());
                    vehNoValue.setText(ordersArrayList.get(positionPlace).getVehicleModel());
                    fromAddrValue.setText(ordersArrayList.get(positionPlace).getTripFromAddress());
                    toAddrValue.setText(ordersArrayList.get(positionPlace).getTripToAddress());
                    dateValue.setText(ordersArrayList.get(positionPlace).getScheduleDate());
                    timeValue.setText(ordersArrayList.get(positionPlace).getScheduleTime());
                    subjectValue.setText(ordersArrayList.get(positionPlace).getTripSubject());
                    notesValue.setText(ordersArrayList.get(positionPlace).getTripNotes());
                    statusValue.setText(ordersArrayList.get(positionPlace).getTripFilter());
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if(ordersArrayList != null)
                return ordersArrayList.size();
            else
                return 0;
        }

    }

}
