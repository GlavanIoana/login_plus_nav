package com.example.licenta;

import static android.content.Context.MODE_PRIVATE;
import static com.example.licenta.CalendarUtils.selectedDate;
import static com.example.licenta.StatisticiZiFragment.CATEGORIES_COLORS;
import static com.example.licenta.StatisticiZiFragment.getCategoriesLongMap;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class StatisticiSaptamanaFragment extends Fragment {
    private BarChart barChartCategories, stackedBarChart;
    private TextView tvMonthWeek;
    private TextView tvNoEvents;
    private TextView tvWeekTimeSpentPomodoro;
    private TextView tvWeekAverageTimeSpentPomodoro;
    private LinearLayout llWeekTimeSpentPomodoro;
    private HashMap<Categories, Integer> categoryColors = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_statistici_saptamana, container, false);

        tvMonthWeek= view.findViewById(R.id.tvMonthWeek);
        Button btnNextWeek = view.findViewById(R.id.btnStatsNextWeek);
        Button btnPrevWeek = view.findViewById(R.id.btnStatsPrevWeek);
        barChartCategories = view.findViewById(R.id.barChartCategories);
        stackedBarChart = view.findViewById(R.id.stackedBarChart);
        tvNoEvents = view.findViewById(R.id.tvNoEvents);
        tvWeekTimeSpentPomodoro=view.findViewById(R.id.tvWeekTimeSpentPomodoro);
        tvWeekAverageTimeSpentPomodoro=view.findViewById(R.id.tvWeekAverageTimeSpentPomodoro);
        llWeekTimeSpentPomodoro=view.findViewById(R.id.llWeekTimeSpentPomodoro);

        categoryColors.put(Categories.MUNCA, ContextCompat.getColor(requireContext(),R.color.calblue));
        categoryColors.put(Categories.SEDINTA, ContextCompat.getColor(requireContext(), R.color.caldarkblue));
        categoryColors.put(Categories.TEMA, ContextCompat.getColor(requireContext(), R.color.calturqouse));
        categoryColors.put(Categories.INTALNIRE, ContextCompat.getColor(requireContext(), R.color.calpurple));
        categoryColors.put(Categories.GOSPODARIT, ContextCompat.getColor(requireContext(), R.color.calgreen));
        categoryColors.put(Categories.RELAXARE, ContextCompat.getColor(requireContext(), R.color.calyellow));
        categoryColors.put(Categories.SPORT, ContextCompat.getColor(requireContext(), R.color.calorange));
        categoryColors.put(Categories.PROIECT, ContextCompat.getColor(requireContext(), R.color.caldarkorange));
        categoryColors.put(Categories.DEADLINE, ContextCompat.getColor(requireContext(), R.color.calred));
        categoryColors.put(Categories.ALTELE, ContextCompat.getColor(requireContext(), R.color.calpink));

//        CalendarUtils.selectedDate = LocalDate.now();

        btnPrevWeek.setOnClickListener(v -> {
            selectedDate = selectedDate.minusWeeks(1);
            updateCharts();
        });

        btnNextWeek.setOnClickListener(v -> {
            selectedDate = selectedDate.plusWeeks(1);
            updateCharts();
        });

        updateCharts();

        return view;
    }

    private void updateCharts() {
        tvMonthWeek.setText(CalendarUtils.monthDayFromDate(selectedDate));

        long timeSpentForWeek=0;
        ArrayList<LocalDate> datesOfWeek=CalendarUtils.daysInWeekArray(selectedDate);
        for (LocalDate date:datesOfWeek){
            String currentDate = date.toString();
            timeSpentForWeek += getTimeSpentForDay(currentDate);
        }

        long hours = TimeUnit.MILLISECONDS.toHours(timeSpentForWeek);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeSpentForWeek) % 60;
        tvWeekTimeSpentPomodoro.setText("Total ore de concentrare: "+hours+"h"+minutes+"min.");

        long averageTimeSpentForWeek=timeSpentForWeek/7;
        hours = TimeUnit.MILLISECONDS.toHours(averageTimeSpentForWeek);
        minutes = TimeUnit.MILLISECONDS.toMinutes(averageTimeSpentForWeek) % 60;
        tvWeekAverageTimeSpentPomodoro.setText("Medie: "+hours+"h"+minutes+"min/zi.");

        ArrayList<Event> events = Event.eventsForWeek(selectedDate);

        tvNoEvents.setText(R.string.nu_exista_evenimente_saptamana);
        if (events.isEmpty()) {
            tvNoEvents.setVisibility(View.VISIBLE);
            barChartCategories.setVisibility(View.INVISIBLE);
            stackedBarChart.setVisibility(View.INVISIBLE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
            barChartCategories.setVisibility(View.VISIBLE);
            stackedBarChart.setVisibility(View.VISIBLE);
        }

        createBarChart(barChartCategories,events);
        createStackedBarChart(stackedBarChart,events);
    }

    private long getTimeSpentForDay(String date) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("TimeSpent", MODE_PRIVATE);
        return sharedPreferences.getLong(date, 0);
    }

    private void createStackedBarChart(BarChart stackedBarChart, ArrayList<Event> events) {
        Map<DayOfWeek, Map<Categories, Long>> categoryDurationMap = new HashMap<>();

        // Initialize the map with empty duration values for each category and day of the week
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            Map<Categories, Long> dayCategoryMap = new HashMap<>();
            for (Categories category : Categories.values()) {
                dayCategoryMap.put(category, 0L);
            }
            categoryDurationMap.put(dayOfWeek, dayCategoryMap);
        }

        // Calculate the duration for each category and day of the week
        for (Event event : events) {
            DayOfWeek eventDay = event.getDate().getDayOfWeek();
            Categories eventCategory = event.getCategory();
            long eventDuration = Duration.between(event.getTimeStart(), event.getTimeFinal()).toMinutes();

            // Update the category duration for the specific day of the week
            categoryDurationMap.get(eventDay).put(eventCategory,
                    categoryDurationMap.get(eventDay).get(eventCategory) + eventDuration);
        }

        // Create a list of BarEntry objects for each day of the week
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();  // Stores the X-axis labels
        int barIndex = 0;

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            Map<Categories, Long> dayCategoryMap = categoryDurationMap.get(dayOfWeek);
            float[] durations = new float[Categories.values().length];

            // Extract the duration values for each category
            for (int i = 0; i < Categories.values().length; i++) {
                durations[i] = dayCategoryMap.get(Categories.values()[i])==null?0:dayCategoryMap.get(Categories.values()[i]);
            }

            // Create a BarEntry object with the duration values and add it to the list
            barEntries.add(new BarEntry(barIndex, durations));
            xAxisLabels.add(dayOfWeek.toString());  // Add the day of the week as the X-axis label
            barIndex++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Category Durations");

        // Set colors for each category based on the assigned colors
        List<Integer> colors = new ArrayList<>();
        for (Categories category : Categories.values()) {
            int color = categoryColors.get(category);
            colors.add(color);
        }
        barDataSet.setColors(colors);

        String[] categoryLabels = new String[Categories.values().length];
        for (int i = 0; i < Categories.values().length; i++) {
            categoryLabels[i] = Categories.values()[i].toString();
        }
        barDataSet.setStackLabels(categoryLabels);

        // Create a BarData object and set the bar dataset
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f);

        // Configure the stacked bar chart
        stackedBarChart.setData(barData);
        stackedBarChart.getDescription().setEnabled(false);
        stackedBarChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < xAxisLabels.size()) {
                    return xAxisLabels.get(index);
                }
                return "";  // Return empty string for invalid indices
            }
        });
        stackedBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        stackedBarChart.getXAxis().setDrawLabels(false);
        stackedBarChart.getXAxis().setDrawAxisLine(false);
        stackedBarChart.getXAxis().setDrawGridLines(false);
        stackedBarChart.getAxisLeft().setDrawAxisLine(false);
        stackedBarChart.getAxisLeft().setDrawGridLines(false);
        stackedBarChart.getAxisRight().setEnabled(false);
        stackedBarChart.getLegend().setEnabled(true);
        stackedBarChart.animateY(500);
        stackedBarChart.invalidate();
    }

    private void createBarChart(BarChart barChartCategories, ArrayList<Event> events) {
        Map<Categories, Long> categoryDurationMap = getCategoriesLongMap(events);

        ArrayList<BarEntry> entries=new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int index = 0;
        for (Map.Entry<Categories, Long> entry : categoryDurationMap.entrySet()) {
            Categories category = entry.getKey();
            long duration = entry.getValue();
            Log.d("createBarChart",category+"-"+duration);
            entries.add(new BarEntry(index, duration));
            labels.add(category.toString());
            int color = categoryColors.get(category);
            colors.add(color);
            index++;
        }

//        List<Integer> colors = new ArrayList<>();
//        for (Categories category : Categories.values()) {
//            int color = categoryColors.get(category);
//            colors.add(color);
//        }
//        barDataSet.setColors(colors);
        BarDataSet barDataSet = new BarDataSet(entries, "Category Durations");
        barDataSet.setColors(colors);  // Set the colors based on the category
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        // Set the labels on the X-axis
        XAxis xAxis = barChartCategories.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        barChartCategories.getDescription().setEnabled(false);
        barChartCategories.getAxisRight().setEnabled(false);
        barChartCategories.getLegend().setEnabled(true);

        Description description = new Description();
        description.setText("Durata totala pentru fiecare categorie"); // Set the desired title
        description.setTextSize(18f); // Set the desired text size
        description.setPosition(0f, 0f);
        barChartCategories.setDescription(description);

        barChartCategories.setData(barData);
        barChartCategories.animateY(500);
        barChartCategories.invalidate();

    }
}