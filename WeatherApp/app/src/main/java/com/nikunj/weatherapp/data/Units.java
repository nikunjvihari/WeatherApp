package com.nikunj.weatherapp.data;

import org.json.JSONObject;

/**
 * Created by nnn on 8/29/2015.
 */
public class Units implements JSONPopulator {
    private String temperature;

    public String getTemperature() {

        return temperature;
    }

    @Override
    public void populate(JSONObject data) {

        temperature = data.optString("temperature");
    }
}
