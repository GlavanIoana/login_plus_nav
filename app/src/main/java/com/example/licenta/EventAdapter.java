package com.example.licenta;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private int resource;
    private List<Event> events;
    private LayoutInflater inflater;

    public EventAdapter(@NonNull Context context, int resource, @NonNull List<Event> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.events=objects;
        this.inflater=inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=inflater.inflate(resource,parent,false);
        Event event=events.get(position);
        if (event==null){
            return view;
        }
        addTitle(view,event.getName());
        addDate(view,event.getDate());
        addStartingHour(view,event.getTimeStart());
        addFinishingHour(view,event.getTimeFinal());
        addStatus(view,event.getStatus());
        return view;
    }

    private void addStatus(View view, StatusEv status) {
        TextView textView=view.findViewById(R.id.tvItemStatus);
        if (status!=null&& !status.name().isEmpty()){
            textView.setText(status.name());
        }else {
            textView.setText(R.string.string_default);
        }
    }

    private void addDate(View view, LocalDate date) {
        TextView tvDay=view.findViewById(R.id.tvItemDay);
        TextView tvMonth=view.findViewById(R.id.tvItemMonth);
        if (date!=null|| !date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).isEmpty()){
            Log.d("DayofMonth", String.valueOf(date.getDayOfMonth()));
            tvDay.setText(String.valueOf(date.getDayOfMonth()));
            tvMonth.setText(date.getMonth().name());
        }else {
            tvDay.setText(R.string.string_default);
            tvMonth.setText(R.string.string_default);
        }
    }

    private void addFinishingHour(View view, LocalTime timeFinal) {
        TextView textView=view.findViewById(R.id.tvItemFinishHour);
        if (timeFinal!=null&& !timeFinal.format(DateTimeFormatter.ofPattern("HH:mm")).trim().isEmpty()){
            textView.setText(timeFinal.format(DateTimeFormatter.ofPattern("HH:mm")));
        }else {
            textView.setText(R.string.string_default);
        }
    }

    private void addStartingHour(View view, LocalTime timeStart) {
        TextView textView=view.findViewById(R.id.tvItemStartHour);
        if (timeStart!=null&& !timeStart.format(DateTimeFormatter.ofPattern("HH:mm")).trim().isEmpty()){
            textView.setText(timeStart.format(DateTimeFormatter.ofPattern("HH:mm")));
        }else {
            textView.setText(R.string.string_default);
        }
    }

    private void addTitle(View view, String name) {
        TextView textView=view.findViewById(R.id.tvItemTitle);
        if (name!=null|| !name.trim().isEmpty()){
            textView.setText(name);
        }else {
            textView.setText(R.string.string_default);
        }
    }
}
