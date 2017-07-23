package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.os.Bundle;
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
import android.widget.LinearLayout;
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

public class AllOrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        face = Typeface.createFromAsset(AllOrdersActivity.this.getAssets(), Constants.FONT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

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

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            SpannableString s = new SpannableString(mi.getTitle());
            s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s);
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back_option) {
            finish();
        }

        if (id == R.id.exit_option) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AllOrdersActivity.this);
            LayoutInflater inflater = AllOrdersActivity.this.getLayoutInflater();
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

                    Intent intent = new Intent(AllOrdersActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(AllOrdersActivity.this);
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
            startActivity(new Intent(AllOrdersActivity.this, HomeActivity.class));
        }

        //if (id == R.id.nav_orders) {
        //    startActivity(new Intent(AllOrdersActivity.this, MyOrdersActivity.class));
        //}
        //if (id == R.id.nav_agent) {
        //    final MyCircularProgressDialog progressDialog;
        //    progressDialog = new MyCircularProgressDialog(AllOrdersActivity.this);
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
        //            startActivity(new Intent(AllOrdersActivity.this, HomeActivity.class));
        //            finish();
        //        }
        //    }, 2000);
        //}

        if (id == R.id.nav_language) {
            startActivity(new Intent(AllOrdersActivity.this, ChangeLanguageActivity.class));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(AllOrdersActivity.this);
            LayoutInflater inflater = AllOrdersActivity.this.getLayoutInflater();
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
                    editor.putString(Constants.PREFS_USER_NAME, "0");
                    editor.putString(Constants.PREFS_USER_MOBILE, "");
                    editor.putString(Constants.PREFS_SHARE_URL, "");
                    editor.putString(Constants.PREFS_LATITUDE, "");
                    editor.putString(Constants.PREFS_LONGITUDE, "");
                    editor.putString(Constants.PREFS_USER_CONSTANT, "");
                    editor.putString(Constants.PREFS_IS_FACTORY, "");
                    editor.commit();

                    Intent intent = new Intent(AllOrdersActivity.this, LoginActivity.class);
                    ActivityCompat.finishAffinity(AllOrdersActivity.this);
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
        //    ActivityCompat.finishAffinity(AllOrdersActivity.this);
        //    finish();
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetOrderHistory extends AsyncTask<Void, Void, JSONObject> {
        MyCircularProgressDialog progressDialog;
        JsonParser jsonParserOne;
        //JsonParser jsonParserTwo;
        JSONObject jsonObjectOne;
        //JSONObject jsonObjectTwo;
        boolean isDialog;

        GetOrderHistory(boolean isDialog) {
            this.isDialog = isDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (isDialog) {
                progressDialog = new MyCircularProgressDialog(AllOrdersActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            jsonParserOne = new JsonParser();
            //jsonParserTwo = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

            if (glassExtra.equalsIgnoreCase("true")) {
                params.put("ArgTripMCustId", "1");
                params.put("ArgExcludeCustId", "0");

            } else {
                params.put("ArgTripMCustId", "0");
                params.put("ArgExcludeCustId", "1");
            }

            params.put("ArgTripDDmId", preferences.getString(Constants.PREFS_USER_ID, "0"));
            params.put("ArgTripMID", "0");
            params.put("ArgTripDID", "0");
            params.put("ArgTripMStatus", "0");
            params.put("ArgTripDStatus", modeExtra);

            if (preferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_AR + "TripDetailsMasterList", "POST", params);

            } else {
                jsonObjectOne = jsonParserOne.makeHttpRequest(Constants.BASE_URL_EN + "TripDetailsMasterList", "POST", params);
            }

            return jsonObjectOne;
        }

        protected void onPostExecute(final JSONObject jsonObject) {
            if (isDialog) {
                progressDialog.dismiss();
            }

            if (jsonObjectOne != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (jsonObjectOne.getBoolean("status")) {
                        ordersJSONArray = jsonObjectOne.getJSONArray("data");

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
                                order.setVehicleModel(ordersJSONArray.getJSONObject(i).getString("VMName").trim());
                                order.setVehicleType(ordersJSONArray.getJSONObject(i).getString("VmoName").trim());
                                order.setScheduleDate(ordersJSONArray.getJSONObject(i).getString("TripMScheduleDate").trim());
                                order.setScheduleTime(ordersJSONArray.getJSONObject(i).getString("TripMScheduleTime").trim());
                                order.setUserName(ordersJSONArray.getJSONObject(i).getString("UsrName").trim());
                                order.setUserMobile(ordersJSONArray.getJSONObject(i).getString("UsrMobNumber").trim());
                                order.setTripFilter(ordersJSONArray.getJSONObject(i).getString("TripMFilterName").trim());
                                order.setTripStatus(ordersJSONArray.getJSONObject(i).getString("TripMStatus").trim());
                                order.setTripSubject(ordersJSONArray.getJSONObject(i).getString("TripMSubject").trim());
                                order.setTripNotes(ordersJSONArray.getJSONObject(i).getString("TripMNotes").trim());
                                order.setVehicleImage(ordersJSONArray.getJSONObject(i).getString("VmoURL").trim());
                                order.setTripdId(ordersJSONArray.getJSONObject(i).getString("TripDID").trim());
                                order.setDistance(ordersJSONArray.getJSONObject(i).getString("TripMDistanceString").trim());

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