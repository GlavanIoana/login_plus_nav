package com.example.licenta;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent>
{
//    CalendarFragment ref=new CalendarFragment();
    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents)
    {
        super(context, 0, hourEvents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        HourEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);

        setHour(convertView,event.time);
        setEvents(convertView,event.events);

        TextView timeTV=convertView.findViewById(R.id.tvTime);
        String str= String.valueOf(timeTV.getText());
        DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
        LocalTime oraStartEv=LocalTime.parse(str,timeFormatter);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the event at the clicked position
                HourEvent clickedEvent = getItem(position);
                // do something with the event
                Log.d("HourAdapter", "Clicked on event " + clickedEvent.getTime());

//                ref.createPopupWindow(CalendarUtils.selectedDate,oraStartEv);
            }
        });

        return convertView;
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV=convertView.findViewById(R.id.tvTime);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    private void setEvents(View convertView, ArrayList<Event> events) {
        TextView event1=convertView.findViewById(R.id.cell1);
        TextView event2=convertView.findViewById(R.id.cell2);
        TextView event3=convertView.findViewById(R.id.cell3);

        if (events.size()==0)
        {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
        }
        else if (events.size()==1){
            setEvent(event1,events.get(0));
            hideEvent(event2);
            hideEvent(event3);
        }else if (events.size()==2){
            setEvent(event1,events.get(0));
            setEvent(event2,events.get(1));
            hideEvent(event3);
        }else if (events.size()==3){
            setEvent(event1,events.get(0));
            setEvent(event2,events.get(1));
            setEvent(event3,events.get(2));
        }else{
            setEvent(event1,events.get(0));
            setEvent(event2,events.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown=String.valueOf(events.size()-2);
            eventsNotShown+=" More Events";
            event3.setText(eventsNotShown);
        }

    }

    private void setEvent(TextView textView, Event event) {
        textView.setText(event.getName());
        int culoare=R.color.calpink;
        GradientDrawable shape= (GradientDrawable) textView.getBackground().mutate();
        switch (event.getCategory()){
            case MUNCA: culoare=R.color.calblue;break;
            case SEDINTA:culoare=R.color.caldarkblue;break;
            case SPORT:culoare=R.color.calorange;break;
            case GOSPODARIT:culoare=R.color.calgreen;break;
            case RELAXARE:culoare=R.color.calyellow;break;
            case PROIECT:culoare=R.color.caldarkorange;break;
            case INTALNIRE:culoare=R.color.calpurple;break;
            case DEADLINE:culoare=R.color.calred;break;
            case TEMA:culoare=R.color.calturqouse;break;
            default:culoare=R.color.calpink;break;
        };
        shape.setColor(getContext().getResources().getColor(culoare));
        textView.setBackground(shape);
        textView.setVisibility(View.VISIBLE);
    }

    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }

}