package com.example.hyu13.weatherapp.Weather;

public class Forecast {
    private CurrentWeather mCurrentWeather;
    private HourByHour[] hourlyForecast;

    public CurrentWeather getCurrentWeather() {
        return mCurrentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        mCurrentWeather = currentWeather;
    }

    public HourByHour[] getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(HourByHour[] hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }
}
