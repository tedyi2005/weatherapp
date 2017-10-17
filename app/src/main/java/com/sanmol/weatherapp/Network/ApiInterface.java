package com.sanmol.weatherapp.Network;

import com.sanmol.weatherapp.Models.WeatherResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Teddy Kidanne on 10/17/2017.
 */

public interface ApiInterface {
    @GET("/data/2.5/weather")
    Call<WeatherResponse> getCurrentCityWeather(@QueryMap(encoded=true) Map<String, String> options);
}
