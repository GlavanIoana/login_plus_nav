package com.example.licenta;

import static android.content.Context.MODE_PRIVATE;
import static com.example.licenta.CalendarUtils.selectedDate;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatisticiZiFragment extends Fragment {
    private PieChart pieChartStatus,pieChartCategorie;
    private TextView tvMonthDay;
    private TextView tvDayOfWeek;
    private TextView tvNoEvents;
    private TextView tvTimeSpentPomodoro;
    private LinearLayout llTimeSpentPomodoro;
    public static final int[] CATEGORIES_COLORS = {
            Color.rgb(230,230,230),//nedec
            Color.rgb(20,200,234), //munca
            Color.rgb(42,84,238),//sedinta
            Color.rgb(15,234,211),//tema
            Color.rgb(16,243,31),//gospod
            Color.rgb(255,250,17),//relax
            Color.rgb(255,182,17),//sport
            Color.rgb(255,106,17),//proiect
            Color.rgb(252,16,58),//deadline
            Color.rgb(129,33,237),//intalnire
            Color.rgb(246,16,123)//altele
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistici_zi, container, false);

        tvMonthDay= view.findViewById(R.id.tvMonthDay);
        tvDayOfWeek= view.findViewById(R.id.tvDayOfWeek);
        Button btnNextDay = view.findViewById(R.id.btnNextDay);
        Button btnPrevDay = view.findViewById(R.id.btnPrevDay);
        pieChartStatus = view.findViewById(R.id.pieChartStatus);
        pieChartCategorie = view.findViewById(R.id.pieChartCategorie);
        tvNoEvents = view.findViewById(R.id.tvNoEvents);
        tvTimeSpentPomodoro=view.findViewById(R.id.tvTimeSpentPomodoro);
        llTimeSpentPomodoro=view.findViewById(R.id.llTimeSpentPomodoro);

        CalendarUtils.selectedDate = LocalDate.now();

        btnPrevDay.setOnClickListener(v -> {
            selectedDate = selectedDate.minusDays(1);
            updateChart();
        });

        btnNextDay.setOnClickListener(v -> {
            selectedDate = selectedDate.plusDays(1);
            updateChart();
        });

        updateChart();

        return view;
    }

    private void updateChart() {
        tvMonthDay.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek=selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        tvDayOfWeek.setText(dayOfWeek);

        String currentDate = selectedDate.toString();
        long timeSpentToday = getTimeSpentForDay(currentDate);
        if (timeSpentToday==0){
            llTimeSpentPomodoro.setVisibility(View.GONE);
        }else {
            long hours = TimeUnit.MILLISECONDS.toHours(timeSpentToday);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeSpentToday) % 60;
            llTimeSpentPomodoro.setVisibility(View.VISIBLE);
            tvTimeSpentPomodoro.setText("Felicitari! Astazi te-ai concentrat "+(hours!=0?(hours+" ore si "):" ")+minutes+" minute.");
        }

        ArrayList<Event> events = Event.eventsForDate(selectedDate);

        tvNoEvents.setText(R.string.nu_sunt_evenimente_programate_in_aceasta_zi);
        if (events.isEmpty()) {
            tvNoEvents.setVisibility(View.VISIBLE);
            pieChartStatus.setVisibility(View.INVISIBLE);
            pieChartCategorie.setVisibility(View.INVISIBLE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
            pieChartStatus.setVisibility(View.VISIBLE);
            pieChartCategorie.setVisibility(View.VISIBLE);
        }

        createStatusChart(pieChartStatus,events);
        createCategoryChart(pieChartCategorie,events);
    }

    private void createCategoryChart(PieChart pieChartCategorie, ArrayList<Event> events) {
        Map<Categories, Long> categoryDurationMap = getCategoriesLongMap(events);

        long nrOreNedeclarate = Duration.between(LocalTime.of(0, 0), LocalTime.of(23, 59)).toMinutes() + 1;
        for (long duration : categoryDurationMap.values()) {
            nrOreNedeclarate -= duration;
        }
        System.out.println(nrOreNedeclarate + " undeclared hours");

        ArrayList<PieEntry> entries = getPieEntries(categoryDurationMap, nrOreNedeclarate);

        PieDataSet pieDataSet = new PieDataSet(entries, "Categorii");
        pieDataSet.setColors(CATEGORIES_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        pieChartCategorie.setData(new PieData(pieDataSet));
        pieChartCategorie.getDescription().setEnabled(false);
        pieChartCategorie.setCenterText("Categorizare (minute)");
        pieChartCategorie.animate();
        pieChartCategorie.invalidate();
    }

    @NonNull
    private ArrayList<PieEntry> getPieEntries(Map<Categories, Long> categoryDurationMap, long nrOreNedeclarate) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (nrOreNedeclarate >0){
            entries.add(new PieEntry(nrOreNedeclarate, "NEDECLARAT"));
        }
        for (Map.Entry<Categories, Long> entry : categoryDurationMap.entrySet()) {
            Categories category = entry.getKey();
            long duration = entry.getValue();

            if (duration > 0) {
                entries.add(new PieEntry(duration, category.toString()));
            }
        }
        return entries;
    }

    @NonNull
    protected static Map<Categories, Long> getCategoriesLongMap(ArrayList<Event> events) {
        Map<Categories,Long> categoryDurationMap=new HashMap<>();
        for (Event event : events) {
            Categories category = event.getCategory();
            categoryDurationMap.putIfAbsent(category, 0L); // Initialize the category if it doesn't exist
            long categoryDuration = categoryDurationMap.get(category);
            categoryDuration += Duration.between(event.getTimeStart(), event.getTimeFinal()).toMinutes();
            categoryDurationMap.put(category, categoryDuration);
        }
        return categoryDurationMap;
    }

    private void createStatusChart(PieChart pieChartStatus, ArrayList<Event> events) {
        int nrNeinceput = 0;
        int nrInceput = 0;
        int nrFinalizat = 0;
        int nrTotal=events.size();

        for (Event event : events) {
            switch (event.getStatus()) {
                case INCEPUT:
                    nrInceput++;
                    break;
                case FINALIZAT:
                    nrFinalizat++;
                    break;
                case NEINCEPUT:
                    nrNeinceput++;
                    break;
            }
        }
        float percentNeinceput = (nrNeinceput / (float) nrTotal) * 100;
        float percentInceput = (nrInceput / (float) nrTotal) * 100;
        float percentFinalizat = (nrFinalizat / (float) nrTotal) * 100;

        Log.d("StatisticiFragment", percentNeinceput + "% documents with status " + StatusEv.NEINCEPUT + " found");
        Log.d("StatisticiFragment", percentInceput + "% documents with status " + StatusEv.INCEPUT + " found");
        Log.d("StatisticiFragment", percentFinalizat + "% documents with status " + StatusEv.FINALIZAT + " found");

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (percentNeinceput > 0) {
            entries.add(new PieEntry(percentNeinceput, StatusEv.NEINCEPUT.toString()));
        }
        if (percentInceput > 0) {
            entries.add(new PieEntry(percentInceput, StatusEv.INCEPUT.toString()));
        }
        if (percentFinalizat > 0) {
            entries.add(new PieEntry(percentFinalizat, StatusEv.FINALIZAT.toString()));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Stari sarcini");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        pieChartStatus.setData(new PieData(pieDataSet));
        pieChartStatus.getDescription().setEnabled(false);
        pieChartStatus.setCenterText("Productivitate (%)");
        pieChartStatus.animate();
        pieChartStatus.invalidate();
    }

    private long getTimeSpentForDay(String date) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("TimeSpent", MODE_PRIVATE);
        return sharedPreferences.getLong(date, 0);
    }

}