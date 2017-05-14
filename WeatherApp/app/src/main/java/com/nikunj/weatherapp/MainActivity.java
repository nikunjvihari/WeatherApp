package com.nikunj.weatherapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filterable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Filter;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;
import com.nikunj.weatherapp.data.*;
import com.nikunj.weatherapp.service.AppLocationService;
import com.nikunj.weatherapp.service.LocationAddress;
import com.nikunj.weatherapp.service.WeatherServiceCallBack;
import com.nikunj.weatherapp.service.YahooWeatherService;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, WeatherServiceCallBack {


    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private TextView humidityTextView;
    private TextView enterCityTextView;
    private YahooWeatherService service;
    private AutoCompleteTextView inputTextView;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDZnvJu3EE9ytFIGFXN5mZoSnXXZ4R-4C8";
    AppLocationService appLocationService;
    public String savecity = null;
    public int errorCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterCityTextView = (TextView) findViewById(R.id.enterCityTextView);
        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        inputTextView = (AutoCompleteTextView) findViewById(R.id.inputTextView);
        inputTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        inputTextView.setOnItemClickListener(this);
        inputTextView.getText().clear();
        appLocationService = new AppLocationService(MainActivity.this);
        final ProgressDialog progress1 = new ProgressDialog(this);
        // Font path
        String fontPath = "fonts/font.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        temperatureTextView.setTypeface(tf);
        conditionTextView.setTypeface(tf);
        locationTextView.setTypeface(tf);
        inputTextView.setTypeface(tf);
        humidityTextView.setTypeface(tf);
        enterCityTextView.setTypeface(tf);

        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = preferences1.edit();
        editor.putInt("counter", errorCount);
        editor.apply();


        errorCount = preferences1.getInt("counter", 0);
        service = new YahooWeatherService(this);
        progress1.setMessage("Loading...");
        progress1.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progress1.dismiss();

            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 4000);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String strcity1 = preferences.getString("storedCity", "");
        savecity = strcity1;
        service.RefreshWeather(strcity1);


        Button B1 = (Button) findViewById(R.id.button);
        B1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String tempstr = inputTextView.getText().toString();
                savecity = tempstr;
                if (tempstr.length() > 3) {
                    service.RefreshWeather(tempstr);
                    inputTextView.setText(null);
                }
            }
        });

        Button B2 = (Button) findViewById(R.id.saveButton);
        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputTextView.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, "Enter City Name", Toast.LENGTH_SHORT).show();
                }

                if (inputTextView.getText().toString().length() > 3) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    String tempstr = inputTextView.getText().toString();
                    savecity = tempstr;
                    editor.putString("storedCity", savecity);
                    editor.apply();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    service.RefreshWeather(savecity);
                    inputTextView.setText(null);
                }
            }
        });


        Button B3 = (Button) findViewById(R.id.autoDetect);

        B3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (statusOfGPS == false)
                        showSettingsAlert();
                    else {
                        turnOnStatus();
                        progress1.setMessage("Auto Detecting...");
                        progress1.show();
                        Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                progress1.cancel();
                            }
                        };
                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 5000);


                    }
            }
        });


        Button B4 = (Button) findViewById(R.id.button2);

        B4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Forecast.class);
                i.putExtra("passedLocation", savecity);
                startActivity(i);

            }
        });

    }


    public void turnOnStatus() {

        Location gpsLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
        //GPS_PROVIDER
        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
            System.out.println(latitude);
            System.out.println(longitude);
            appLocationService.stopUsingGPS();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    System.out.println(locationAddress);
                    break;

                default:
                    locationAddress=null;
            }

            savecity = locationAddress;
            service.RefreshWeather(locationAddress);
        }
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("", "Error connecting to Places API", e);
            return resultList;
        } catch (NullPointerException e) {
            Log.e("", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("", "", e);
        }

        return resultList;
    }


    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        if (constraint.length() > 3)
                            resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }


    @Override
    public void serviceSuccess(Channel channel) {

        Item item = channel.getItem();

        int resourceId = getResources().getIdentifier("drawable/icon_" + channel.getItem().getCondition().getCode(), null, getPackageName());

        @SuppressWarnings("deprecation")
        Drawable weatherIconDrawable = getResources().getDrawable(resourceId);
        weatherIconImageView.setImageDrawable(weatherIconDrawable);
        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        humidityTextView.setText("Humidity: " + channel.getAtmosphere().getHumidity());
        String display[] = savecity.toString().split(",");
        locationTextView.setText(display[0]);


    }

    @Override
    public void serviceFailure(Exception exception) {
        errorCount++;
        if (errorCount > 1) {
            if (!(exception instanceof NullPointerException)) {
                Toast.makeText(this, "No Internet Connection, Application Ending", Toast.LENGTH_SHORT).show();
                this.finish();

            } else if (exception instanceof NullPointerException)
                Toast.makeText(this, "Weather Data not Available at Yahoo!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("Please set the GPS in High Accuracy mode. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Button B3 = (Button) findViewById(R.id.autoDetect);
                    B3.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Auto Detect Shall not Work!", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

}