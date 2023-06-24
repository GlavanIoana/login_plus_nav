package com.example.licenta;

import static com.example.licenta.CalendarUtils.selectedDate;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class StatisticiFragment extends Fragment {
    private PieChart pieChart;
    private TextView tvMonthDay;
    private TextView tvDayOfWeek;
    private TextView tvNoEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistici, container, false);

        tvMonthDay= view.findViewById(R.id.tvMonthDay);
        tvDayOfWeek= view.findViewById(R.id.tvDayOfWeek);
        Button btnNextDay = view.findViewById(R.id.btnNextDay);
        Button btnPrevDay = view.findViewById(R.id.btnPrevDay);
        pieChart= view.findViewById(R.id.pieChart);
        tvNoEvents = view.findViewById(R.id.tvNoEvents);

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
//
//        int nrNeinceput = 0;
//        int nrInceput = 0;
//        int nrFinalizat = 0;

        ArrayList<Event> events = Event.eventsForDate(selectedDate);

        if (events.isEmpty()) {
            tvNoEvents.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.INVISIBLE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);}

            int nrNeinceput = 0;
            int nrInceput = 0;
            int nrFinalizat = 0;

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
            Log.d("StatisticiFragment", nrNeinceput + " documents with status "+StatusEv.NEINCEPUT+" found");
            Log.d("StatisticiFragment", nrInceput + " documents with status "+StatusEv.INCEPUT+" found");
            Log.d("StatisticiFragment", nrFinalizat + " documents with status "+StatusEv.FINALIZAT+" found");

            ArrayList<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(nrNeinceput, StatusEv.NEINCEPUT.toString()));
            entries.add(new PieEntry(nrInceput, StatusEv.INCEPUT.toString()));
            entries.add(new PieEntry(nrFinalizat, StatusEv.FINALIZAT.toString()));
        Log.d("StatisticiFragment",entries.toString());

            PieDataSet pieDataSet = new PieDataSet(entries, "Stari sarcini");
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextColor(Color.BLACK);
            pieDataSet.setValueTextSize(16f);
            pieChart.setData(new PieData(pieDataSet));
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Productivitate");
            pieChart.animate();
            pieChart.invalidate();
    }

}