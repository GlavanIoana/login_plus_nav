package com.example.licenta;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EventViewHolder> {
    private List<Object> blockList;
    private boolean showEventHours;
    private static CalendarFragment calendarFragment;

    public RecyclerViewAdapter(List<Object> blockList, boolean showEventHours,CalendarFragment calendar) {
        blockList = blockList;
        this.showEventHours = showEventHours;
        calendarFragment=calendar;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_time_block, parent, false);
        return new EventViewHolder(itemView,this);
    }

//    public void setOnEventClickListener(OnEventClickListener listener) {
//        this.onEventClickListener = listener;
//    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Object item = blockList.get(position);
        Log.d("RecyclerViewAdapter", "position of block list " + blockList.get(position));

        if (item instanceof Event) {
            Event event = (Event) item;

            Log.d("RecyclerViewAdapter", "Event Name: " + event.getName());
            Log.d("RecyclerViewAdapter", "Event Start Time: " + event.getTimeStart());
            Log.d("RecyclerViewAdapter", "Event End Time: " + event.getTimeFinal());

            holder.titleTextView.setText(event.getName());
            if (showEventHours) {
                holder.titleTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
                int color = ContextCompat.getColor(holder.titleTextView.getContext(), R.color.white);
                holder.titleTextView.setTextColor(color);
                holder.llBlock.setPadding(8,8,8,8);
                holder.startTimeTextView.setVisibility(View.VISIBLE);
                holder.endTimeTextView.setVisibility(View.VISIBLE);
                holder.startTimeTextView.setText(event.getTimeStart().format(DateTimeFormatter.ofPattern("HH:mm")));
                holder.endTimeTextView.setText(event.getTimeFinal().format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                holder.llBlock.setPadding(1,1,1,1);
                int color = ContextCompat.getColor(holder.titleTextView.getContext(), R.color.white);
                holder.titleTextView.setTextColor(color);
                holder.startTimeTextView.setVisibility(View.GONE);
                holder.endTimeTextView.setVisibility(View.GONE);
                holder.titleTextView.setTextAppearance(android.R.style.TextAppearance_Small);
            }
            int culoare = getCuloare(event);
            holder.llBlock.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),culoare));
            long duration = calculateDuration(event.getTimeStart(), event.getTimeFinal());
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = calculateBlockHeight(duration);
            holder.itemView.setLayoutParams(layoutParams);

//            holder.llBlock.setOnClickListener(v -> holder.clickOnEventBlock() );
        } else if (item instanceof Integer) {
            int blankBlockHeight = (Integer) item;
            holder.titleTextView.setText("");
            holder.startTimeTextView.setText("");
            holder.endTimeTextView.setText("");
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));

            if (blockList.size()>1){
                if (position > 0 && position < blockList.size() - 1) {
                    Event previousEvent = (Event) blockList.get(position - 1);
                    Event nextEvent = (Event) blockList.get(position + 1);
                    long duration = calculateDuration(previousEvent.getTimeFinal(), nextEvent.getTimeStart());
                    blankBlockHeight = calculateBlockHeight(duration);
                }else if (position==0){
                    Event nextEvent = (Event) blockList.get(position + 1);
                    long duration = calculateDuration(LocalTime.of(0, 0), nextEvent.getTimeStart());
                    blankBlockHeight = calculateBlockHeight(duration);
                }else if (position == blockList.size() - 1){
                    Event previousEvent = (Event) blockList.get(position - 1);
                    long duration = calculateDuration(previousEvent.getTimeFinal(), LocalTime.of(23, 59));
                    blankBlockHeight = calculateBlockHeight(duration);
                }
            }else {
                long duration = calculateDuration(LocalTime.of(0,0), LocalTime.of(23, 59));
                blankBlockHeight = calculateBlockHeight(duration);
            }
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = blankBlockHeight;
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    protected static int getCuloare(Event event) {
        int culoare;
        switch (event.getCategory()){
            case INTALNIRE:culoare=R.color.calpurple;break;
            case SEDINTA:culoare=R.color.caldarkblue;break;
            case MUNCA: culoare=R.color.calblue;break;
            case TEMA:culoare=R.color.calturqouse;break;
            case GOSPODARIT:culoare=R.color.calgreen;break;
            case RELAXARE:culoare=R.color.calyellow;break;
            case SPORT:culoare=R.color.calorange;break;
            case PROIECT:culoare=R.color.caldarkorange;break;
            case DEADLINE:culoare=R.color.calred;break;
            default:culoare=R.color.calpink;break;
        }

        return culoare;
    }

    public static int calculateBlockHeight(long duration) {
        return (int) (duration*2.0);
    }

    long calculateDuration(LocalTime timeStart, LocalTime timeFinal) {
        return Duration.between(timeStart,timeFinal).toMinutes();
    }

    @Override
    public int getItemCount() {
        Log.d("RecyclerViewAdapter getItemCount", String.valueOf(blockList.size()));
        return blockList.size();
    }

    public void setBlockList(List<Object> blockList) {
        this.blockList = blockList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;
        LinearLayout llBlock;
        RecyclerViewAdapter recyclerViewAdapter;
//        OnEventClickListener onEventClickListener;

        EventViewHolder(@NonNull View itemView,RecyclerViewAdapter adapter) {
            super(itemView);
            llBlock=itemView.findViewById(R.id.layout_time_block);
            titleTextView = itemView.findViewById(R.id.tvEventName);
            startTimeTextView = itemView.findViewById(R.id.tvEventStartTime);
            endTimeTextView = itemView.findViewById(R.id.tvEventEndTime);
            recyclerViewAdapter=adapter;
//            this.onEventClickListener=listener;

            itemView.setOnClickListener(v -> clickOnEventBlock());
        }

        private void clickOnEventBlock() {
            int position = getAdapterPosition();
            Object item = recyclerViewAdapter.blockList.get(position);
            if (calendarFragment!=null) {
                if (item instanceof Event) {
                    Event event = (Event) item;
                    Log.d("RecyclerViewAdapter onEventCLick", event.getName());
                    calendarFragment.createPopupWindow(event);
                }
            }else {
                //TODO trimitere catre day view
                Log.d("RecyclerViewAdapter onEventCLick","trimitere catre day view");
            }
        }
    }
}
