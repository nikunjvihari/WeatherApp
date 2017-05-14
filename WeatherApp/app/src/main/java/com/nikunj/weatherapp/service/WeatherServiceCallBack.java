package com.nikunj.weatherapp.service;

import android.location.Address;

import com.nikunj.weatherapp.data.Channel;

import java.util.List;

/**
 * Created by nnn on 8/29/2015.
 */
public interface WeatherServiceCallBack {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}

