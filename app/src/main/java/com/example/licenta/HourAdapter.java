package com.example.licenta;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
                HourEvent clickedHourEvent = getItem(position);
                // do something with the event
                Log.d("HourAdapter", "Clicked on event " + clickedHourEvent.getTime());
                ArrayList<Event> clickedEvents = clickedHourEvent.getEvents();

                // Create and configure the popup window
                PopupWindow popupWindow = createPopupWindow(clickedEvents, v);

                // Display the popup window
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//                ref.createPopupWindow(CalendarUtils.selectedDate,oraStartEv);
            }
        });

        return convertView;
    }

    private PopupWindow createPopupWindow(ArrayList<Event> clickedEvents, View v) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popupView = inflater.inflate(R.layout.popup_select_event, null);

        // Configure the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Find the ListView inside the popup layout
        ListView listView = popupView.findViewById(R.id.lvEvents);

        // Create an adapter for the events
        EventAdapter eventAdapter = new EventAdapter(getContext(),R.layout.lv_event_view, clickedEvents,inflater);

        // Set the adapter to the ListView
        listView.setAdapter(eventAdapter);

        // Set the item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventAdapter.getItem(position);

                // Perform actions with the selected event
                // For example, you can open a new activity to modify the event

                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });

        // Calculate the x and y offsets for the popup window
        int[] anchorLocation = new int[2];
        v.getLocationOnScreen(anchorLocation);
        int xOff = (int) (anchorLocation[0] - popupView.getWidth() / 2f + v.getWidth() / 2f);
        int yOff = anchorLocation[1] - popupView.getHeight();

        // Set the x and y offsets for the popup window
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, xOff, yOff);

        return popupWindow;
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
        int culoare;
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