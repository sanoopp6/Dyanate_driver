package com.fast_prog.dynate.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.PlaceItem;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.GPSTracker;
import com.fast_prog.dynate.utilities.JsonParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MapLocationPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    GPSTracker gpsTracker;

    private LatLng latLng;

    private Button selectLocationButton;

    private static TextView youAreHereTextView;
    private static TextView typeLocationTextView;
    private static TextView provinceNameTextView;
    private static TextView addressTextView;

    private View locationSelectImageView;
    private View searchLocationImageView;

    private static Location userLocation;
    private static Location currentLocation;

    private ImageView changeMapView;
    private ImageView currentLocationView;

    private boolean mapViewSatellite;

    private PlaceItem currentPlaceItem;

    Typeface face;

    Boolean selectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location_picker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        face = Typeface.createFromAsset(MapLocationPickerActivity.this.getAssets(), Constants.FONT_URL);

        selectList = getIntent().getBooleanExtra("selectList", true);

        String title = getResources().getString(R.string.map_location_picker);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        mapViewSatellite = false;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        selectLocationButton = (Button) findViewById(R.id.btn_select_location);
        selectLocationButton.setTypeface(face);

        youAreHereTextView = (TextView) findViewById(R.id.you_are_here_text_view);
        youAreHereTextView.setTypeface(face);
        typeLocationTextView = (TextView) findViewById(R.id.type_location_text_view);
        typeLocationTextView.setTypeface(face);
        provinceNameTextView = (TextView) findViewById(R.id.text_view_province);
        provinceNameTextView.setTypeface(face);
        addressTextView = (TextView) findViewById(R.id.txt_address);
        addressTextView.setTypeface(face);

        locationSelectImageView = findViewById(R.id.location_select_gps_image_view);
        searchLocationImageView = findViewById(R.id.search_location_image_view);

        currentLocationView = (ImageView) findViewById(R.id.image_view_current_location_icon);
        changeMapView = (ImageView) findViewById(R.id.image_view_map_change_icon);

        if(selectList) {
            typeLocationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapLocationPickerActivity.this, PickLocationActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                }
            });

            locationSelectImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapLocationPickerActivity.this, PickLocationActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                }
            });

            searchLocationImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapLocationPickerActivity.this, PickLocationActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                }
            });
        }

        changeMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    if(!mapViewSatellite) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mapViewSatellite = true;
                        changeMapView.setImageResource(R.drawable.ic_earth1);

                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mapViewSatellite = false;
                        changeMapView.setImageResource(R.drawable.ic_earth2);
                    }

                    mMap.getUiSettings().setZoomControlsEnabled(false);

                    latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();

                    if (ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);
                    mMap.getUiSettings().setRotateGesturesEnabled(false);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        currentLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    gpsTracker.getLocation();

                    if (!gpsTracker.canGetLocation()) {
                        gpsTracker.showSettingsAlert();

                    } else {
                        latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(typeLocationTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.type_your_location)))) {
                    Intent intent = new Intent();
                    intent.putExtra("location", typeLocationTextView.getText());
                    intent.putExtra("latitude", userLocation.getLatitude());
                    intent.putExtra("longitude", userLocation.getLongitude());
                    setResult(RESULT_OK, intent);
                    finish();
                }
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

    @Override
    protected void onResume() {
        super.onResume();

        if (gpsTracker != null && mMap != null) {
            if (!gpsTracker.canGetLocation()) {
                gpsTracker.getLocation();

                latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        gpsTracker = new GPSTracker(MapLocationPickerActivity.this);

        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }

        latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        Location mLocation = new Location("");
        mLocation.setLatitude(latLng.latitude);
        mLocation.setLongitude(latLng.longitude);
        currentLocation = mLocation;
        userLocation = mLocation;

        if (ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                latLng = cameraPosition.target;

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(latLng.latitude);
                    mLocation.setLongitude(latLng.longitude);
                    userLocation = mLocation;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {
                new SetLocationNameBackground(userLocation.getLatitude(), userLocation.getLongitude()).execute();
            }
        });
    }

    private class SetLocationNameBackground extends AsyncTask<Void, Void, JSONArray> {
        private JsonParser locationNameParser;
        private SharedPreferences sharedPreferences;
        private Double latitude, longitude;

        SetLocationNameBackground(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            locationNameParser = new JsonParser();
            sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            HashMap<String, String> params = new HashMap<>();

            params.put("latlng", latitude + "," + longitude);
            params.put("sensor", "true");

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                params.put("language", "ar");
            }
            JSONObject locationNameObject = locationNameParser.makeHttpRequest(Constants.GOOGLE_LOCATION_NAME_URL, "GET", params);

            if (locationNameObject != null) {
                try {
                    JSONArray locationNameArray = locationNameObject.getJSONArray("results");
                    if (locationNameArray != null) {
                        return locationNameArray;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray locationArray) {
            super.onPostExecute(locationArray);
            if (locationArray != null) {
                try {
                    //String locationName = locationArray.getJSONObject(0).getString("formatted_address");
                    String provinceName = "";
                    String locationName = "";
                    JSONArray addressComponents = locationArray.getJSONObject(0).getJSONArray("address_components");

                    for (int i = 0; i < addressComponents.length(); i++) {
                        JSONArray types = addressComponents.getJSONObject(i).getJSONArray("types");

                        if ((types.getString(0).equalsIgnoreCase("route") || types.getString(0).equalsIgnoreCase("locality"))
                                || (types.length() > 1 && types.getString(1).equalsIgnoreCase("sublocality"))) {

                            if (locationName.trim().length() > 0) {
                                locationName = locationName + ", " + addressComponents.getJSONObject(i).getString("long_name");

                            } else {
                                locationName = addressComponents.getJSONObject(i).getString("long_name");
                            }

                            if (types.getString(0).equalsIgnoreCase("locality")) {
                                provinceName = addressComponents.getJSONObject(i).getString("long_name");
                            }
                        }
                    }

                    provinceNameTextView.setText(provinceName);
                    String regex = "\\d+";

                    if (!locationName.matches(regex)) {
                        typeLocationTextView.setText(locationName);
                        addressTextView.setText(locationName);

                    } else {
                        typeLocationTextView.setText(R.string.use_pin_location);
                    }
                    typeLocationTextView.setSelected(true);
                    addressTextView.setSelected(true);

                    if (ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MapLocationPickerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

//                    if (userLocation != null && currentLocation != null) {
//                        if (userLocation.distanceTo(currentLocation) > 50) {
//                            youAreHereTextView.setText(R.string.i_live_here);
//
//                        } else {
//                            youAreHereTextView.setText(R.string.you_are_here);
//                        }
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == RESULT_OK) {
            currentPlaceItem = (PlaceItem) data.getSerializableExtra("PlaceItem");

            Location mLocation = new Location("");
            try {
                mLocation.setLatitude(Double.parseDouble(currentPlaceItem.getpLatitude()));
                mLocation.setLongitude(Double.parseDouble(currentPlaceItem.getpLongitude()));
                userLocation = mLocation;
            } catch (Exception ignored){
            }

            latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

}
