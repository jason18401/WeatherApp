package com.example.hyu13.weatherapp.UI;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.example.hyu13.weatherapp.Adapters.HourlyAdapter;
import com.example.hyu13.weatherapp.R;
import com.example.hyu13.weatherapp.Weather.HourByHour;
import com.example.hyu13.weatherapp.databinding.ActivityHourlyForecastBinding;

import java.util.ArrayList;
import java.util.List;

public class HourlyActivity extends AppCompatActivity {

    private HourlyAdapter mAdapter;
    private ActivityHourlyForecastBinding mbinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        List<HourByHour> hoursList = (ArrayList<HourByHour>) intent.getSerializableExtra("HourlyList");

        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_hourly_forecast);
        mAdapter = new HourlyAdapter(hoursList, this);

        mbinding.hourlyListItems.setAdapter(mAdapter);
        mbinding.hourlyListItems.setHasFixedSize(true);
        mbinding.hourlyListItems.setLayoutManager(new LinearLayoutManager(this));
    }

}
