package com.nikunj.weatherapp.data;

import org.json.JSONObject;

/**
 * Created by nnn on 8/29/2015.
 */
public class Condition implements JSONPopulator {

    private int code;
    private String description;
    private int temperature;

    public String getDescription() {

        return description;
    }

    public int getTemperature() {

        return temperature;
    }

    public int getCode() {
        return code;
    }

    @Override
    public void populate(JSONObject data) {

        code = data.optInt("code");
        temperature = data.optInt("temp");
        description= data.optString("text");

    }
}
