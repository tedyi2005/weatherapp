package com.sanmol.weatherapp.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.sanmol.weatherapp.R;
import com.sanmol.weatherapp.Utils.Constants;
import com.sanmol.weatherapp.Utils.TransparentTileOWM;

import java.net.MalformedURLException;
import java.net.URL;

public class WeatherMapActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String OWM_TILE_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png?appid=%s";
    private Spinner spinner;
    private String tileType = "clouds";
    private TileOverlay tileOver;
    ActionBar actionBar;
    Double lat, lon;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_map);
        spinner = (Spinner) findViewById(R.id.tileType);
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getDouble("lat", 0);
            lon = extras.getDouble("lon", 0);
            content = extras.getString("content", null);
            setTitle(content);
        }
        String[] tileName = new String[]{"Clouds", "Temperature", "Precipitations", "Snow", "Rain", "Wind", "Sea level press."};

        ArrayAdapter<String> adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tileName);

        spinner.setAdapter(adpt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Check click
                switch (position) {
                    case 0:
                        tileType = "clouds";
                        break;
                    case 1:
                        tileType = "temp";
                        break;
                    case 2:
                        tileType = "precipitation";
                        break;
                    case 3:
                        tileType = "snow";
                        break;
                    case 4:
                        tileType = "rain";
                        break;
                    case 5:
                        tileType = "wind";
                        break;
                    case 6:
                        tileType = "pressure";
                        break;

                }

                if (mMap != null) {
                    tileOver.remove();
                    setUpMap();
                }


            }
        });
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng city = new LatLng(lat, lon);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 12.0f));
        mMap.addMarker(new MarkerOptions().position(city).title(content));
        tileOver = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(createTransparentTileProvider()));
    }

    private TileProvider createTransparentTileProvider() {
        return new TransparentTileOWM(tileType);
    }


    private TileProvider createTilePovider() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String fUrl = String.format(OWM_TILE_URL, tileType == null ? "clouds" : tileType, zoom, x, y, Constants.API_KEY_VALUE);
                URL url = null;
                try {
                    url = new URL(fUrl);
                } catch (MalformedURLException mfe) {
                    mfe.printStackTrace();
                }

                return url;
            }
        };

        return tileProvider;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}