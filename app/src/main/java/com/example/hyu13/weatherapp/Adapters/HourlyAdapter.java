package com.example.hyu13.weatherapp.Adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hyu13.weatherapp.R;
import com.example.hyu13.weatherapp.Weather.HourByHour;
import com.example.hyu13.weatherapp.databinding.HourlyListBinding;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private List<HourByHour> hours;
    private Context mContext;

    public HourlyAdapter(List<HourByHour> hours, Context context) {
        this.hours = hours;
        mContext = context;
    }

    @NonNull
    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        HourlyListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.hourly_list, viewGroup, false);

        return new ViewHolder(binding);
    }

    //which list item in our hourly data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HourByHour hour = hours.get(i);
        viewHolder.mHourlyListItemBinding.setHour(hour);
    }

    //return the size of data
    @Override
    public int getItemCount() {
        return hours.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public HourlyListBinding mHourlyListItemBinding;

        public ViewHolder(HourlyListBinding hourlyListBinding){
            super(hourlyListBinding.getRoot());
            mHourlyListItemBinding = hourlyListBinding;
        }
    }
}
