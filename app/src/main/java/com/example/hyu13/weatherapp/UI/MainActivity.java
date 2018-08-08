package com.example.hyu13.weatherapp.UI;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyu13.weatherapp.R;
import com.example.hyu13.weatherapp.Weather.CurrentWeather;
import com.example.hyu13.weatherapp.Weather.Forecast;
import com.example.hyu13.weatherapp.Weather.HourByHour;
import com.example.hyu13.weatherapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = MainActivity.class.getSimpleName();
    private Forecast mForecast;
    private ImageView icon; //in app weather icon
    private LocationManager mLocationManager;

    final double latitude = 35.2271;
    final double longitude = -80.8431;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        */

        getForecast(latitude, longitude);
        Log.d(TAG, "Main UI running");
    }

    private void getForecast(double latitude, double longitude) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        TextView darkSky = findViewById(R.id.darkSky);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        icon = findViewById(R.id.iconImageView);

        String apiKey = "5aa13f2fbcb46935d05b2d6f01e73c44";

        String forecastURL = "https://api.darksky.net/forecast/"
                + apiKey + "/" + latitude + "," + longitude;

        if(isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(forecastURL).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {   //executes asynchronous by queue provided by OkHttp
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) {
                   try {
                       String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForeCastData(jsonData);

                            CurrentWeather currentWeather = mForecast.getCurrentWeather();

                            final CurrentWeather displayWeather = new CurrentWeather(
                                    currentWeather.getLocationLabel(),
                                    currentWeather.getIcon(),
                                    currentWeather.getTime(),
                                    currentWeather.getTemperature(),
                                    currentWeather.getHumidity(),
                                    currentWeather.getPrecipchance(),
                                    currentWeather.getSummary(),
                                    currentWeather.getTimeZone()
                            );

                            binding.setWeather(displayWeather);

                            //for the in app weather icon
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconID());
                                    icon.setImageDrawable(drawable);
                                }
                            });


                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                    } catch (JSONException e){
                       Log.e(TAG, "JSON Exception caught: ", e);
                    }
                    }
                });
            }
    }

    private Forecast parseForeCastData(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrentWeather(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));

        return forecast;
    }

    //Connecting JSON array with Arrays in Java
    private HourByHour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        HourByHour[] hours = new HourByHour[data.length()];
        for(int i = 0; i<data.length(); i++){
            JSONObject jsonHour = data.getJSONObject(i);

            HourByHour hourByHour = new HourByHour();

            hourByHour.setSummary(jsonHour.getString("summary"));
            hourByHour.setIcon(jsonHour.getString("icon"));
            hourByHour.setTemperature(jsonHour.getDouble("temperature"));
            hourByHour.setTime(jsonHour.getLong("time"));
            hourByHour.setTimezone(timezone);

            hours[i]= hourByHour;
        }
        return hours;
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{ // moves tries catch to currentWeather = getCurrentDetails(jsonData);
        JSONObject forecast = new JSONObject(jsonData);

        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setLocationLabel("Charlotte, NC"); //hardcoded
        currentWeather.setPrecipchance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable=true;
        }
        else{
            Toast.makeText(this, R.string.network_offline_message, Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    public void refreshOnClick(View view){ //image to button, add this to image view android:onClick="refreshOnClick"
        getForecast(latitude, longitude);
        Toast.makeText(this, "Refreshing data", Toast.LENGTH_LONG).show();
    }

    public void hourlyOnClick(View view){
        List<HourByHour> hours = Arrays.asList(mForecast.getHourlyForecast());
        Intent intent = new Intent(this, HourlyActivity.class);

        //to save the value
        intent.putExtra("HourlyList", (Serializable) hours);
        startActivity(intent);
    }
    /*
    @Override
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    */
}