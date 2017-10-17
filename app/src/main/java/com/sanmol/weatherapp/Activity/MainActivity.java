package com.sanmol.weatherapp.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmol.weatherapp.Models.Weather;
import com.sanmol.weatherapp.Models.WeatherResponse;
import com.sanmol.weatherapp.Network.ApiClient;
import com.sanmol.weatherapp.Network.ApiInterface;
import com.sanmol.weatherapp.R;
import com.sanmol.weatherapp.Utils.Util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sanmol.weatherapp.Utils.Constants.API_KEY_VALUE;
import static com.sanmol.weatherapp.Utils.Constants.LAST_SEARCHED_CITY_KEY;
import static com.sanmol.weatherapp.Utils.Constants.SEARCHED_HISTORY_KEY;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.temperature)
    TextView tempreture;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.datetime)
    TextView datetime;
    @BindView(R.id.temperatureMax)
    TextView temperatureMax;
    @BindView(R.id.temperatureMin)
    TextView temperatureMin;
    @BindView(R.id.feelslike)
    TextView feelslike;
    @BindView(R.id.precipitation)
    TextView precipitation;
    @BindView(R.id.pressure)
    TextView pressure;
    @BindView(R.id.humidity)
    TextView humidity;
    @BindView(R.id.wind)
    TextView wind;
    @BindView(R.id.visibility)
    TextView visibility;
    @BindView(R.id.sunrise)
    TextView sunrise;
    @BindView(R.id.sunset)
    TextView sunset;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.ok)
    ImageView ok;
    @BindView(R.id.cancel)
    ImageView cancel;
    @BindView(R.id.progresbar)
    ProgressBar progresBar;
    @BindView(R.id.error)
    TextView error;
    @BindView(R.id.search)
    RelativeLayout search;
    @BindView(R.id.mianLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.result)
    LinearLayout result;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String lastSearchedCity;
    Double lat, lon;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather App");
        prefs = getSharedPreferences(SEARCHED_HISTORY_KEY, MODE_PRIVATE);
        ButterKnife.bind(this);
        if (API_KEY_VALUE.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY first from openweathermap.org", Toast.LENGTH_SHORT).show();
            return;
        }
        lastSearchedCity = prefs.getString(LAST_SEARCHED_CITY_KEY, null);
        if (lastSearchedCity != null) {
            getCityWeather(lastSearchedCity);
        } else {
            setLayoutVisibility("search");
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.INVISIBLE);
                if (!city.getText().toString().isEmpty() && city.getText().toString() != null) {
                    getCityWeather(city.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.city_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city.setText("");
            }
        });
    }

    private void getCityWeather(String lastSearchedCity) {
        editor = getSharedPreferences(SEARCHED_HISTORY_KEY, MODE_PRIVATE).edit();
        editor.putString(LAST_SEARCHED_CITY_KEY, lastSearchedCity);
        editor.apply();
        if (Util.isNetworkAvailable(MainActivity.this) == true) {
            Map<String, String> data = new HashMap<>();
            data.put("q", lastSearchedCity);
            data.put("APPID", API_KEY_VALUE);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            progresBar.setVisibility(View.VISIBLE);

            Call<WeatherResponse> call = apiService.getCurrentCityWeather(data);
            call.enqueue(new Callback<WeatherResponse>() {

                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    Log.d("URL", "" + response.raw().request().url());
                    search.setVisibility(View.INVISIBLE);
                    progresBar.setVisibility(View.INVISIBLE);
                    result.setVisibility(View.VISIBLE);
                    if (response.body() != null) {
                        setValues(response.body());
                    } else {
                        setLayoutVisibility("search");
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    progresBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.service_error), Toast.LENGTH_SHORT).show();
                    error.setVisibility(View.VISIBLE);
                    error.setText("Error : " + t.toString());
                }
            });
        } else {
            showDialog();
            //error.setText("Error : " + getResources().getString(R.string.network_error));
        }
    }

    public void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.retry));
        alertDialog.setMessage(getResources().getString(R.string.network_error));
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                lastSearchedCity = prefs.getString(LAST_SEARCHED_CITY_KEY, null);
                if (lastSearchedCity != null && !lastSearchedCity.isEmpty()) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                } else {
                    setLayoutVisibility("search");
                }
            }
        });

        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();
    }

    private void setValues(WeatherResponse body) {
        setTitle(body.getName() + ", " + body.getSys().getCountry());
        List<Weather> weathers = body.getWeather();
        if (weathers.get(0).getDescription() != null)
            description.setText(weathers.get(0).getDescription().substring(0, 1).toUpperCase() + weathers.get(0).getDescription().substring(1));
        if (weathers.get(0).getIcon() != null)
            icon.setBackgroundResource(Util.setIcon(weathers.get(0).getIcon()));
        if (body.getDt() != null)
            datetime.setText(Util.convertDate(body.getDt().toString()));
        if (body.getMain().getTemp() != null)
            tempreture.setText(Util.convertTemp(body.getMain().getTemp()) + "\u2109");
        if (body.getMain().getTempMax() != null)
            temperatureMax.setText("High " + Util.convertTemp(body.getMain().getTempMax()) + "\u2109");
        if (body.getMain().getTempMin() != null)
            temperatureMin.setText("Low " + Util.convertTemp(body.getMain().getTempMin()) + "\u2109");
        humidity.setText("Humidity " + body.getMain().getHumidity() + "%");
        if (body.getWind().getSpeed() != null)
            wind.setText("Wind " + body.getWind().getSpeed() + " mps");
        if (body.getSys().getSunrise() != null)
            sunrise.setText("Sunrise " + Util.convertTime(body.getSys().getSunrise().toString()));
        if (body.getSys().getSunset() != null)
            sunset.setText("Sunset " + Util.convertTime(body.getSys().getSunset().toString()));
        if (body.getMain().getPressure() != null)
            pressure.setText("Pressure " + body.getMain().getPressure());
        if (body.getClouds().getAll() != null)
            precipitation.setText("Clouds " + body.getClouds().getAll() + "%");
        if (body.getMain().getHumidity() != null && body.getMain().getTemp() != null && body.getWind().getSpeed() != null)
            feelslike.setText("Feelslike " + new DecimalFormat("##.##").format(Util.convertFeelsLike(body.getMain().getTemp(), body.getMain().getHumidity(), body.getWind().getSpeed())));
        if (body.getVisibility() != null)
            visibility.setText("Visibility " + Util.convertKm(body.getVisibility()));
        lat = body.getCoord().getLat();
        lon = body.getCoord().getLon();
        content = body.getName() + ", " + body.getSys().getCountry() + ", Temp " + Util.convertTemp(body.getMain().getTemp()) + "\u2109";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                city.setText("");
                setLayoutVisibility("search");
                break;
            case R.id.action_map:
                callMapView();
                break;
        }
        return true;
    }

    public void setLayoutVisibility(String layout) {
        if (layout.equals("search")) {
            search.setVisibility(View.VISIBLE);
            result.setVisibility(View.INVISIBLE);
        } else {
            search.setVisibility(View.INVISIBLE);
            result.setVisibility(View.VISIBLE);
        }
    }

    public void callMapView() {
        if (prefs.getString(LAST_SEARCHED_CITY_KEY, null) != null) {
            if (Util.isNetworkAvailable(MainActivity.this) == true) {
                Intent mapIntent = new Intent(MainActivity.this, WeatherMapActivity.class);
                mapIntent.putExtra("lat", lat);
                mapIntent.putExtra("lon", lon);
                mapIntent.putExtra("content", content);
                startActivity(mapIntent);
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.city_error), Toast.LENGTH_SHORT).show();
        }
    }
}
