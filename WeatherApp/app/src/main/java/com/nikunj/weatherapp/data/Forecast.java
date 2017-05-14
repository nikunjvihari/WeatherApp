package com.nikunj.weatherapp.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by nnn on 10/20/2015.
 */
public class Forecast implements JSONPopulator {


    private int[] code=new int[5];

    public String[] getDay() {
        return day;
    }

    public void setCode(int[] code) {
        this.code = code;
    }

    public void setDay(String[] day) {
        this.day = day;
    }

    private String[] day=new String[5];

    private String[] text= new String[5];

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public int[] getCode() {
        return code;
    }

    @Override
    public void populate(JSONObject data) {


    }

    public void populate1(JSONArray data) {

        for (int i = 0; i < 5; i++) {
            JSONObject obj =  data.optJSONObject(i);

            code[i] = obj.optInt("code");
            day[i] = obj.optString("day");
            text[i]=obj.optString("text");
        }
    }
}
