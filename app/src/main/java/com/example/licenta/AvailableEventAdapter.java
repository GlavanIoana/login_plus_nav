package com.example.licenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AvailableEventAdapter extends RecyclerView.Adapter<AvailableEventAdapter.AvailableEventViewHolder> {
    private List<Event> availableEvents; // List of Event objects
    private List<Boolean> selectedEvents; // List to keep track of selected events

    public AvailableEventAdapter(List<Event> events) {
        this.availableEvents = events;
        selectedEvents = new ArrayList<>(Collections.nCopies(availableEvents.size(), false));
    }

    public static class AvailableEventViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView textViewDateHours;

        public AvailableEventViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.cbAvailableEvent);
            textViewDateHours = itemView.findViewById(R.id.tvDateHoursAvailableEvent);
        }
    }

    @NonNull
    @Override
    public AvailableEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_available_event, parent, false);
        return new AvailableEventViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull AvailableEventAdapter.AvailableEventViewHolder holder, int position) {
        Event event = availableEvents.get(position);
        holder.checkbox.setChecked(selectedEvents.get(position));
        String dateHoursText = holder.itemView.getContext().getString(R.string.interval_date_hours, CalendarUtils.formattedDate(event.getDate()),event.getTimeStart(), event.getTimeFinal());
        holder.textViewDateHours.setText(dateHoursText);

        // Set a click listener for the checkbox to handle selection
        holder.checkbox.setOnClickListener(v -> {
            boolean isChecked = holder.checkbox.isChecked();
            selectedEvents.set(position, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return availableEvents.size();
    }

    public List<Event> getSelectedEvents() {
        List<Event> selected = new ArrayList<>();
        for (int i = 0; i < selectedEvents.size(); i++) {
            if (selectedEvents.get(i)) {
                selected.add(availableEvents.get(i));
            }
        }
        return selected;
    }
}
