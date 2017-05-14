package com.nikunj.weatherapp.data;

import org.json.JSONObject;

/**
 * Created by nnn on 8/30/2015.
 */
public class Atmosphere implements JSONPopulator {
    private String humidity;

    public String getHumidity() {
        return humidity;
    }

    @Override
    public void populate(JSONObject data) {

        humidity = data.optString("humidity");

    }
}
