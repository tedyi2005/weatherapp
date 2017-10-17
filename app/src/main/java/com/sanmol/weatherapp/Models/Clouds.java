package com.sanmol.weatherapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Teddy Kidanne on 10/17/2017.
 */

public class Clouds {

    @SerializedName("all")
    @Expose
    private Integer all;

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

}