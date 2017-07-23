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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.PlaceItem;
import com.fast_prog.dynate.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

    Boolean selectMap;

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

        face = Typeface.createFromAsset(PickLocationActivity.this.getAssets(), Constants.FONT_URL);

        String title = getResources().getString(R.string.pick_location);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        selectMap = getIntent().getBooleanExtra("selectMap", false);

        Spinner citySpinner = (Spinner) findViewById(R.id.city_spinner);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        listView = (ListView) findViewById(R.id.listView_results);
        EditText etEnterLocation = (EditText) findViewById(R.id.edEnterLocation);
        etEnterLocation.setTypeface(face);

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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

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
                    placeItem.setPlName(resultsJsonArray.getJSONObject(i).getString("name"));

                    if (resultsJsonArray.getJSONObject(i).getString("vicinity") != null)
                        placeItem.setpVicinity(resultsJsonArray.getJSONObject(i).getString("vicinity"));

                    placeItem.setpLatitude(resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat"));
                    placeItem.setpLongitude(resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng"));
                    resultList.add(placeItem);
                }
            }
        } catch (JSONException ignored) {
        }

        return resultList;
    }

    private class PlaceListCustomAdapter extends ArrayAdapter<PlaceItem> {

        // declaring our ArrayList of items
        private ArrayList<PlaceItem> objects;

        /* here we must override the constructor for ArrayAdapter
        * the only variable we care about now is ArrayList<Item> objects,
        * because it is the list of objects we want to display.
        */
        PlaceListCustomAdapter(Context context, int textViewResourceId, ArrayList<PlaceItem> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
        }

        /*
         * we are overriding the getView method here - this is what defines how each
         * list item will look.
         */
        @NonNull
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            // assign the view we are converting to a local variable
            View v = convertView;

            // first check to see if the view is null. if so, we have to inflate it.
            // to inflate it basically means to render, or show, the view.
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.place_item, null);
            }

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
            final PlaceItem placeItem = objects.get(position);

            if (placeItem != null) {

                // This is how you obtain a reference to the TextViews.
                // These TextViews are created in the XML files we defined.
                TextView placeNameTextView = (TextView) v.findViewById(R.id.place_name_text_view);
                placeNameTextView.setTypeface(face);
                TextView placeVicinityTextView = (TextView) v.findViewById(R.id.place_vicinity_text_view);
                placeVicinityTextView.setTypeface(face);

                // check to see if each individual textview is null.
                // if not, assign some text!
                placeNameTextView.setText(placeItem.getPlName());
                if (placeItem.getpVicinity() != null) {
                    placeVicinityTextView.setText(placeItem.getpVicinity());
                }
            }
            // the view must be returned to our activity
            return v;
        }
    }

    private class CustomSpinnerAdapter extends ArrayAdapter<String> {
        private ArrayList<String> data;
        public Resources res;
        LayoutInflater inflater;

        /*************
         * CustomAdapter Constructor
         *****************/
        CustomSpinnerAdapter(Context context, int textViewResourceId, ArrayList objects, Resources resLocal) {
            super(context, textViewResourceId, objects);
            /********** Take passed values **********/
            data = objects;
            res = resLocal;

            /***********  Layout inflator to call external xml layout () **********************/
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

        // This funtion called for each row ( Called data.size() times )
        View getCustomView(int position, View convertView, ViewGroup parent) {

            /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
            View row = inflater.inflate(R.layout.city_spinner_item, parent, false);

            /***** Get each Model object from Arraylist ********/
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
