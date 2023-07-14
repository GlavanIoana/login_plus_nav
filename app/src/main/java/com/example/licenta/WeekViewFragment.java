package com.example.licenta;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeekViewFragment extends Fragment{
    private RecyclerView[] recyclerViews;
    private LocalDate startDate;
    private RecyclerViewAdapter[] eventAdapters;
    private NestedScrollView nsvWeekView;
    private TextView[] tvDates=new TextView[7];
    private TextView tvMonthDay;

    public static WeekViewFragment newInstance(LocalDate date) {
        WeekViewFragment fragment = new WeekViewFragment();
        Bundle args = new Bundle();
        args.putSerializable("selectedDate", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_view, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey("selectedDate")) {
            LocalDate selectedDate = (LocalDate) args.getSerializable("selectedDate");
            startDate = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            // Use the selectedDate as needed
        }else {
            startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }

        initializeTextViewsDates(view);

        recyclerViews = new RecyclerView[7];
        eventAdapters = new RecyclerViewAdapter[7];
        nsvWeekView = view.findViewById(R.id.nsvWeekView);

        LinearLayout columnLayout = view.findViewById(R.id.llHoursColumn);

        // Adjust the height of each TextView in the column
        int durationInMinutes = 60; // Assuming each TextView represents one hour (you can adjust this as needed)
        int lineHeight = getResources().getDimensionPixelSize(R.dimen.line_height); // Replace with your desired line height

        for (int i = 1; i < columnLayout.getChildCount(); i+=2) {
            TextView hourTextView = (TextView) columnLayout.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) hourTextView.getLayoutParams();
            layoutParams.height = RecyclerViewAdapter.calculateBlockHeight(durationInMinutes)-lineHeight;
            hourTextView.setLayoutParams(layoutParams);
        }
        // Initialize RecyclerViews
        initializeRecyclerViews(view);

        // Set layout managers for RecyclerViews (e.g., LinearLayoutManager or GridLayoutManager)
        for (int i = 0; i < 7; i++) {
            eventAdapters[i] = new RecyclerViewAdapter(new ArrayList<>(), false,null,getParentFragmentManager());
            recyclerViews[i].setLayoutManager(new LinearLayoutManager(getContext()));
//            recyclerViews[i].setOnClickListener(v -> {
//                //TODO trimitere catre day view
//                Log.d("WeekViewCalendar setWeekView","recyclerView.setOnClickListener");
//            });
            recyclerViews[i].setAdapter(eventAdapters[i]);

        }

        Button btnPrev = view.findViewById(R.id.btnPrevWeek);
        Button btnNext = view.findViewById(R.id.btnNextWeek);

        btnPrev.setOnClickListener(v -> {
            startDate = startDate.minusWeeks(1);
            setWeekView();
        });

        btnNext.setOnClickListener(v -> {
            startDate = startDate.plusWeeks(1);
            setWeekView();
        });

//        setWeekView();

        return view;
    }

    private void initializeTextViewsDates(View view) {
        tvMonthDay=view.findViewById(R.id.tvMonthDay);
        tvDates[0]= view.findViewById(R.id.tvLuDate);
        tvDates[1]= view.findViewById(R.id.tvMaDate);
        tvDates[2]= view.findViewById(R.id.tvMiDate);
        tvDates[3]= view.findViewById(R.id.tvJoDate);
        tvDates[4]= view.findViewById(R.id.tvViDate);
        tvDates[5]= view.findViewById(R.id.tvSaDate);
        tvDates[6]= view.findViewById(R.id.tvDuDate);
    }

    @Override
    public void onResume() {
        super.onResume();
        setWeekView();
    }

    private void setWeekView() {
        // set the RecyclerView adapters and update data
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            tvDates[i].setText(String.valueOf(date.getDayOfMonth()));
            setRecyclerViewAdapter(eventAdapters[i], date);

        }

        tvMonthDay.setText(CalendarUtils.monthYearFromDate(startDate));

        int contentHeight = nsvWeekView.getChildAt(0).getHeight();

        // Calculate the desired scroll position (the middle of the content)
        int scrollToPosition = contentHeight / 2;

        // Scroll the NestedScrollView to the desired position
        nsvWeekView.smoothScrollTo(0, scrollToPosition,300);
    }


    private void setRecyclerViewAdapter(RecyclerViewAdapter eventAdapter, LocalDate date) {
        List<Object> blockList = generateBlockList(date);
        eventAdapter.setBlockList(blockList,date);
        eventAdapter.notifyDataSetChanged();
    }

    private List<Object> generateBlockList(LocalDate selectedDate) {
        List<Event> eventsForDate = Event.eventsForDate(selectedDate);
        Log.d("generateBlockList- No of events for date", String.valueOf(eventsForDate.size()));

        List<Object> blockList = new ArrayList<>();

        if (!eventsForDate.isEmpty()) {
            eventsForDate.sort(Comparator.comparing(Event::getTimeStart));

            Event firstEvent = eventsForDate.get(0);

            long durationBeforeFirstEvent = eventAdapters[0].calculateDuration(LocalTime.of(0, 0), firstEvent.getTimeStart());
            blockList.add(RecyclerViewAdapter.calculateBlockHeight(durationBeforeFirstEvent));
            blockList.add(firstEvent);
            for (int i = 1; i < eventsForDate.size(); i++) {
                Event previousEvent = eventsForDate.get(i - 1);
                Event currentEvent = eventsForDate.get(i);
                long duration = eventAdapters[0].calculateDuration(previousEvent.getTimeFinal(), currentEvent.getTimeStart());
                blockList.add(RecyclerViewAdapter.calculateBlockHeight(duration));
                blockList.add(currentEvent);
            }

            Event lastEvent = eventsForDate.get(eventsForDate.size() - 1);
            long durationAfterLastEvent = eventAdapters[0].calculateDuration(lastEvent.getTimeFinal(), LocalTime.of(23, 59));
            blockList.add(RecyclerViewAdapter.calculateBlockHeight(durationAfterLastEvent));
        } else {
            long duration = eventAdapters[0].calculateDuration(LocalTime.of(0, 0), LocalTime.of(23, 59));
            blockList.add(RecyclerViewAdapter.calculateBlockHeight(duration));
        }

        Log.d("generateBlockList- blockList.size", String.valueOf(blockList.size()));
        return blockList;
    }

    private void initializeRecyclerViews(View view) {
        recyclerViews[0] = view.findViewById(R.id.rvMonday);
        recyclerViews[1] = view.findViewById(R.id.rvTuesday);
        recyclerViews[2] = view.findViewById(R.id.rvWednesday);
        recyclerViews[3] = view.findViewById(R.id.rvThursday);
        recyclerViews[4] = view.findViewById(R.id.rvFriday);
        recyclerViews[5] = view.findViewById(R.id.rvSaturday);
        recyclerViews[6] = view.findViewById(R.id.rvSunday);
    }

}