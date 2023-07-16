package com.example.licenta;

import static com.example.licenta.RecyclerViewAdapter.getCuloare;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    private int resource;
    private List<Event> events;
    private LayoutInflater inflater;
    private boolean selectionModeEnabled=false;
    private List<Event> selectedEvents;
    private FloatingActionButton fabChangeStatus,fabDelete;

    public EventAdapter(Context context, int resource, List<Event> objects, LayoutInflater inflater,FloatingActionButton fabChangeStatus,FloatingActionButton fabDelete) {
        this.context = context;
        this.resource = resource;
        this.events = objects;
        this.inflater = inflater;
        selectedEvents=new ArrayList<>();
        this.fabChangeStatus=fabChangeStatus;
        this.fabDelete=fabDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(resource, parent, false);
        return new ViewHolder(view,this,fabChangeStatus,fabDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        if (event != null) {
            holder.bind(event);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public List<Event> getSelectedEvents() {
        return selectedEvents;
    }

    public void clearSelection() {
        selectedEvents.clear();
        selectionModeEnabled = false;
        notifyDataSetChanged();
    }

    public void setEvents(List<Event> eventsToShow) {
        this.events=eventsToShow;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llItemData;
        private TextView tvItemTitle;
        private TextView tvItemDay;
        private TextView tvItemMonth;
        private TextView tvItemStartHour;
        private TextView tvItemFinishHour;
        private TextView tvItemStatus;
        private boolean isSelected = false; // Track the selected state

        public ViewHolder(@NonNull View itemView,EventAdapter adapter,FloatingActionButton fabChangeStatus,FloatingActionButton fabDelete) {
            super(itemView);
            llItemData=itemView.findViewById(R.id.llItemData);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemDay = itemView.findViewById(R.id.tvItemDay);
            tvItemMonth = itemView.findViewById(R.id.tvItemMonth);
            tvItemStartHour = itemView.findViewById(R.id.tvItemStartHour);
            tvItemFinishHour = itemView.findViewById(R.id.tvItemFinishHour);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            itemView.setOnLongClickListener(v -> {
                // Enable the selection mode
                selectionModeEnabled = true;
                fabChangeStatus.setVisibility(View.VISIBLE);
                fabDelete.setVisibility(View.VISIBLE);
                        // Get the position of the clicked item
                isSelected = !isSelected;
                // Update the UI to reflect the selection
                int position = getAdapterPosition();
                adapter.toggleSelection(position);
                if (adapter.selectedEvents.isEmpty()){
                    selectionModeEnabled=false;
                    fabChangeStatus.setVisibility(View.GONE);
                    fabDelete.setVisibility(View.GONE);
                }

                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), isSelected ? R.color.grey : android.R.color.transparent));
                return true; // Return true to consume the long-press event
            });
            itemView.setOnClickListener(v -> {
                if (selectionModeEnabled){
                    isSelected = !isSelected;
                    int position = getAdapterPosition();
                    // Toggle the selection state
                    adapter.toggleSelection(position);
                    if (adapter.selectedEvents.isEmpty()){
                        selectionModeEnabled=false;
                        fabChangeStatus.setVisibility(View.GONE);
                        fabDelete.setVisibility(View.GONE);
                    }
                    // Update the UI to reflect the selection
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), isSelected ? R.color.grey : android.R.color.transparent));
                }else if (isSelected){
                    Log.d("EventAdapter setOnCLickListener", "ViewHolder: "+getAdapterPosition()+" - "+isSelected);
                    isSelected = !isSelected;
                    Log.d("EventAdapter setOnCLickListener", "selectedEvents.size(): "+String.valueOf(selectedEvents.size()));
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), isSelected ? R.color.grey : android.R.color.transparent));

                }
            });
        }

        public void bind(Event event) {
            int culoare= getCuloare(event);
            llItemData.setBackgroundTintList(ColorStateList.valueOf(culoare));
            addTitle(event.getName());
            addDate(event.getDate());
            addStartingHour(event.getTimeStart());
            addFinishingHour(event.getTimeFinal());
            addStatus(event.getStatus());
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), isSelected ? R.color.grey : android.R.color.transparent));
        }

        private void addStatus(StatusEv status) {
            if (status != null && !status.name().isEmpty()) {
                tvItemStatus.setText(status.name());
            } else {
                tvItemStatus.setText(R.string.string_default);
            }
        }

        private void addDate(LocalDate date) {
            if (date != null || !date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).isEmpty()) {
                tvItemDay.setText(String.valueOf(date.getDayOfMonth()));
                tvItemMonth.setText(date.getMonth().name());
            } else {
                tvItemDay.setText(R.string.string_default);
                tvItemMonth.setText(R.string.string_default);
            }
        }

        private void addFinishingHour(LocalTime timeFinal) {
            if (timeFinal != null && !timeFinal.format(DateTimeFormatter.ofPattern("HH:mm")).trim().isEmpty()) {
                tvItemFinishHour.setText(timeFinal.format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                tvItemFinishHour.setText(R.string.string_default);
            }
        }

        private void addStartingHour(LocalTime timeStart) {
            if (timeStart != null && !timeStart.format(DateTimeFormatter.ofPattern("HH:mm")).trim().isEmpty()) {
                tvItemStartHour.setText(timeStart.format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                tvItemStartHour.setText(R.string.string_default);
            }
        }

        private void addTitle(String name) {
            if (name != null && !name.trim().isEmpty()) {
                tvItemTitle.setText(name);
            } else {
                tvItemTitle.setText(R.string.string_default);
            }
        }
    }

    private void toggleSelection(int position) {
        Event event = events.get(position);
        if (selectedEvents.contains(event)) {
            selectedEvents.remove(event);
        } else {
            selectedEvents.add(event);
        }
        Log.d("EventAdapter toggleSelection", String.valueOf(selectedEvents.size()));
    }
}
