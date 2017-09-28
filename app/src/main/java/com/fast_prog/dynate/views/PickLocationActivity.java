package com.fast_prog.dynate.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.PlaceItem;
import com.fast_prog.dynate.utilities.Constants;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class PickLocationActivity extends AppCompatActivity {

    private ListView listView;

    private ArrayList<PlaceItem> m_parts = new ArrayList<PlaceItem>();

    private PlaceListCustomAdapter m_adapter;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_NEARBY = "/nearbysearch";

    private static final String OUT_JSON = "/json";

    private ArrayList<String> cityLatLngArray;

    private SharedPreferences sharedPreferences;

    Typeface face;

    Boolean bookMarked = true;

    //Boolean selectMap;
    //Button pickLocationButton;
    //private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(PickLocationActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getResources().getString(R.string.pick_location);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        //selectMap = getIntent().getBooleanExtra("selectMap", false);
        Spinner citySpinner = (Spinner) findViewById(R.id.city_spinner);
        listView = (ListView) findViewById(R.id.listView_results);
        EditText etEnterLocation = (EditText) findViewById(R.id.edEnterLocation);
        etEnterLocation.setTypeface(face);

        m_parts = new ArrayList<>();

        try {
            DB snappyDB = DBFactory.open(PickLocationActivity.this, Constants.DYNA_DB);
            PlaceItem[] placeItemArray = snappyDB.getObjectArray(Constants.DYNA_DB_KEY, PlaceItem.class);
            if (placeItemArray != null && placeItemArray.length > 0) {
                m_parts = new ArrayList<>(Arrays.asList(placeItemArray));
            }
            snappyDB.close();

        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        ArrayList<String> cityArray = new ArrayList<>();
        cityLatLngArray = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.city_array).length; i++) {
            cityArray.add(i, getResources().getStringArray(R.array.city_array)[i]);
        }

        for (int i = 0; i < getResources().getStringArray(R.array.city_location_array).length; i++) {
            cityLatLngArray.add(i, getResources().getStringArray(R.array.city_location_array)[i]);
        }

//        pickLocationButton = (Button) findViewById(R.id.btn_pick_address);
//
//        if(selectMap) {
//            pickLocationButton.setVisibility(View.VISIBLE);
//
//            pickLocationButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(PickLocationActivity.this, MapLocationPickerActivity.class);
//                    intent.putExtra("selectList", false);
//                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
//                }
//            });
//        }

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(PickLocationActivity.this, R.layout.city_spinner_item, cityArray, getResources());
        citySpinner.setAdapter(customSpinnerAdapter);

        m_adapter = new PlaceListCustomAdapter(PickLocationActivity.this, R.layout.place_item, m_parts);

        listView.setAdapter(m_adapter);

        listView.setTextFilterEnabled(true);

        etEnterLocation.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bookMarked = false;

                if (s != null) {
                    if (s.length() != 0) {
//                        pickLocationButton.setVisibility(View.GONE);
                        new GetPlaceNamesBackground(s.toString()).execute();

//                    } else {
//                        pickLocationButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        int Position = customSpinnerAdapter.getPosition(sharedPreferences.getString(Constants.PREFS_SEARCH_LOCATION_AREA, cityLatLngArray.get(0)));
        citySpinner.setSelection(Position);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.PREFS_SEARCH_LOCATION_AREA, cityLatLngArray.get(i));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PickLocationActivity.this.getSupportFragmentManager().popBackStack();
                PlaceItem selectedPlaceItem = m_parts.get(i);

                Intent intent = new Intent();
                intent.putExtra("PlaceItem", selectedPlaceItem);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

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

    private class GetPlaceNamesBackground extends AsyncTask<Void, Void, Void> {
        private String s;

        GetPlaceNamesBackground(String s) {
            this.s = s;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            m_parts = autocomplete(s);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (m_parts != null) {
                m_parts.clear();
            }
        }

        @Override
        protected void onPostExecute(Void jsonObject) {
            super.onPostExecute(jsonObject);
            if (m_parts != null) {
                m_adapter = new PlaceListCustomAdapter(PickLocationActivity.this, R.layout.place_item, m_parts);
                listView.setAdapter(m_adapter);
            }
        }
    }

    public ArrayList<PlaceItem> autocomplete(String input) {

        ArrayList<PlaceItem> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String sb = PLACES_API_BASE + TYPE_NEARBY + OUT_JSON + "?key=" + Constants.GOOGLE_API_KEY +
                    "&location=" + sharedPreferences.getString(Constants.PREFS_SEARCH_LOCATION_AREA, cityLatLngArray.get(0)) +
                    "&name=" + input.trim().replace(" ", "+") +
                    "&language=" + sharedPreferences.getString(Constants.PREFS_LANG, "ar") +
                    "&radius=" + 50000;

            URL url = new URL(sb);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            JSONArray resultsJsonArray = jsonObj.getJSONArray("results");

            if (resultsJsonArray != null) {
                resultList = new ArrayList<>();

                for (int i = 0; i < resultsJsonArray.length(); i++) {
                    PlaceItem placeItem = new PlaceItem();
                    placeItem.plName = resultsJsonArray.getJSONObject(i).getString("name");

                    if (resultsJsonArray.getJSONObject(i).getString("vicinity") != null)
                        placeItem.pVicinity = resultsJsonArray.getJSONObject(i).getString("vicinity");

                    placeItem.pLatitude = resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat");
                    placeItem.pLongitude = resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng");
                    resultList.add(placeItem);
                }
            }
        } catch (JSONException ignored) {
        }

        return resultList;
    }

    private class PlaceListCustomAdapter extends ArrayAdapter<PlaceItem> {
        private ArrayList<PlaceItem> objects;

        PlaceListCustomAdapter(Context context, int textViewResourceId, ArrayList<PlaceItem> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
        }

        @NonNull
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.place_item, null);
            }

            final PlaceItem placeItem = objects.get(position);

            if (placeItem != null) {
                TextView placeNameTextView = (TextView) v.findViewById(R.id.place_name_text_view);
                placeNameTextView.setTypeface(face);
                placeNameTextView.setText(placeItem.plName);

                TextView placeVicinityTextView = (TextView) v.findViewById(R.id.place_vicinity_text_view);
                placeVicinityTextView.setTypeface(face);
                if (placeItem.pVicinity != null) {
                    placeVicinityTextView.setText(placeItem.pVicinity);
                }

                final ImageView bookmarkLocationImageView = (ImageView) v.findViewById(R.id.bookmark_location_image_view);
                if (bookMarked) {
                    bookmarkLocationImageView.setColorFilter(Color.parseColor(Constants.FILTER_COLOR));
                    bookmarkLocationImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_parts.remove(position);
                            m_parts.add(null);

                            try {
                                DB snappyDB = DBFactory.open(PickLocationActivity.this, Constants.DYNA_DB);
                                snappyDB.del(Constants.DYNA_DB_KEY);
                                snappyDB.put(Constants.DYNA_DB_KEY, m_parts);
                                snappyDB.close();

                                m_parts.remove(m_parts.size() - 1);
                                m_adapter.notifyDataSetChanged();

                            } catch (SnappydbException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    bookmarkLocationImageView.setVisibility(View.GONE);
                }
            }
            return v;
        }
    }

    private class CustomSpinnerAdapter extends ArrayAdapter<String> {
        private ArrayList<String> data;
        public Resources res;
        LayoutInflater inflater;

        CustomSpinnerAdapter(Context context, int textViewResourceId, ArrayList objects, Resources resLocal) {
            super(context, textViewResourceId, objects);

            data = objects;
            res = resLocal;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        View getCustomView(int position, View convertView, ViewGroup parent) {
            View row = inflater.inflate(R.layout.city_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.city_name_text_view);
            label.setText(data.get(position));
            label.setTypeface(face);
            return row;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == RESULT_OK) {
//            PickLocationActivity.this.getSupportFragmentManager().popBackStack();
//
//            PlaceItem placeItem = new PlaceItem();
//            placeItem.setPlName(data.getStringExtra("location"));
//            placeItem.setpLatitude(String.valueOf(data.getDoubleExtra("latitude",0)));
//            placeItem.setpLongitude(String.valueOf(data.getDoubleExtra("longitude",0)));
//
//            Intent intent = new Intent();
//            intent.putExtra("PlaceItem", placeItem);
//            setResult(RESULT_OK,intent);
//            finish();
//        }
//    }

}
