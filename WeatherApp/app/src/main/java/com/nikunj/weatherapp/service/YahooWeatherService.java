package com.nikunj.weatherapp.service;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nikunj.weatherapp.data.Channel;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by nnn on 8/29/2015.
 */
public class YahooWeatherService {

    private WeatherServiceCallBack callBack;
    private String location;
    private Exception error;

    public YahooWeatherService(WeatherServiceCallBack callBack) {

        this.callBack = callBack;
    }

    public String getLocation() {
        return location;
    }


    public void RefreshWeather(String l){
        this.location = l;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {

                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='c'", params[0]);
                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));

                try {
                    URL url = new URL(endpoint);

                    URLConnection connection = url.openConnection();

                    InputStream inputStream =connection.getInputStream();

                    BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream));

                    StringBuilder result = new StringBuilder();

                    String line;

                    while((line = reader.readLine()) != null)
                    {
                        result.append(line);
                    }

                    return result.toString();

                }

                catch (Exception e) {
                    error = e;

                }

                return null;

            }

            @Override
            protected void onPostExecute(String s) {

                if(s==null && error !=null)
                 callBack.serviceFailure(error);

                try {
                    JSONObject data = new JSONObject(s);
                    JSONObject queryResults = data.optJSONObject("query");
                    int count= queryResults.optInt("count");


                    if(count==0)
                    {
                        callBack.serviceFailure( new LocalWeatherException("City Not Found"));
                        return;
                    }


                    Channel channel = new Channel();
                    channel.populate(queryResults.optJSONObject("results").optJSONObject("channel"));

                    callBack.serviceSuccess(channel);

                }

                catch (JSONException e) {
                    callBack.serviceFailure(e);
                }

                catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                    callBack.serviceFailure(e);
                }

            }
        }.execute(location);

    }

    public class LocalWeatherException extends Exception{
        public LocalWeatherException(String detailMessage) {
            super(detailMessage);
        }
    }


}
