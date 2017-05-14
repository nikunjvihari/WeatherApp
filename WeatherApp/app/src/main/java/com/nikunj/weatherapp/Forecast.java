package com.nikunj.weatherapp;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nikunj.weatherapp.data.Channel;
import com.nikunj.weatherapp.data.Item;
import com.nikunj.weatherapp.service.WeatherServiceCallBack;
import com.nikunj.weatherapp.service.YahooWeatherService;

public class Forecast extends AppCompatActivity implements WeatherServiceCallBack {

    private TextView dayTextView1;
    private ImageView forecastImageView1;

    private TextView dayTextView2;
    private ImageView forecastImageView2;


    private TextView dayTextView3;
    private ImageView forecastImageView3;

    private TextView dayTextView4;
    private ImageView forecastImageView4;

    private TextView dayTextView5;
    private ImageView forecastImageView5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        dayTextView1=(TextView)findViewById(R.id.DayTextView1);
        forecastImageView1=(ImageView)findViewById(R.id.fweatherIconImageView1);

        dayTextView2=(TextView)findViewById(R.id.DayTextView2);
        forecastImageView2=(ImageView)findViewById(R.id.fweatherIconImageView2);

        dayTextView3=(TextView)findViewById(R.id.DayTextView3);
        forecastImageView3=(ImageView)findViewById(R.id.fweatherIconImageView3);

        dayTextView4=(TextView)findViewById(R.id.DayTextView4);
        forecastImageView4=(ImageView)findViewById(R.id.fweatherIconImageView4);

        dayTextView5=(TextView)findViewById(R.id.DayTextView5);
        forecastImageView5=(ImageView)findViewById(R.id.fweatherIconImageView5);

        YahooWeatherService service=new YahooWeatherService(this);

        String location = getIntent().getExtras().getString("passedLocation");
        service.RefreshWeather(location);
    }

    public void serviceSuccess(Channel channel) {

        Item item = channel.getItem();

        int [] code=item.getForecast().getCode().clone();
        String [] day=item.getForecast().getDay().clone();
        String [] text =item.getForecast().getText().clone();

        int resourceId = getResources().getIdentifier("drawable/icon_" + code[0], null, getPackageName());
        Drawable weatherIconDrawable = getResources().getDrawable(resourceId);

        forecastImageView1.setImageDrawable(weatherIconDrawable);
        dayTextView1.setText(day[0]+": "+text[0]);


        resourceId= getResources().getIdentifier("drawable/icon_" + code[1], null, getPackageName());
        weatherIconDrawable = getResources().getDrawable(resourceId);

        forecastImageView2.setImageDrawable(weatherIconDrawable);
        dayTextView2.setText(day[1]+": "+text[1]);

        resourceId= getResources().getIdentifier("drawable/icon_" + code[2], null, getPackageName());
        weatherIconDrawable = getResources().getDrawable(resourceId);

        forecastImageView3.setImageDrawable(weatherIconDrawable);
        dayTextView3.setText(day[2]+": "+text[2]);

        resourceId= getResources().getIdentifier("drawable/icon_" + code[3], null, getPackageName());
        weatherIconDrawable = getResources().getDrawable(resourceId);

        forecastImageView4.setImageDrawable(weatherIconDrawable);
        dayTextView4.setText(day[3]+": "+text[3]);

        resourceId= getResources().getIdentifier("drawable/icon_" + code[4], null, getPackageName());
        weatherIconDrawable = getResources().getDrawable(resourceId);

        forecastImageView5.setImageDrawable(weatherIconDrawable);
        dayTextView5.setText(day[4]+": "+text[4]);


    }

    @Override
    public void serviceFailure(Exception exception) {

        Toast.makeText(this, "Weather Data not Available at Yahoo!", Toast.LENGTH_SHORT).show();

    }
}
