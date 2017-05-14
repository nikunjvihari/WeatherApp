package com.nikunj.weatherapp.data;

import org.json.JSONObject;

/**
 * Created by nnn on 8/29/2015.
 */
public class Item implements JSONPopulator {
    private Condition condition;
    private Forecast forecast;

    public Forecast getForecast() {
        return forecast;
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public void populate(JSONObject data) {

        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));

        forecast=new Forecast();
        forecast.populate1(data.optJSONArray("forecast"));

    }


}
