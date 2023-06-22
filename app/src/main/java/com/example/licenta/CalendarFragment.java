package com.example.licenta;

import static com.example.licenta.CalendarUtils.selectedDate;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private TextView tvMonthDay;
    private TextView tvDayOfWeek;
    private RecyclerView recyclerView;
    public static RecyclerViewAdapter eventAdapter;
    private View view;
    private LinearLayout llData,llOraStart,llOraSfarsit,llErrorDenumire,llErrorData;
    private TextView tvTitlu,tvData,tvOraStart,tvOraSfarsit,tvDurata,tvFrecventa,tvAux;
    private TextInputEditText tietDenumire,tietNrFrecventa;
    private Spinner spnCategory,spnDurata,spnFrecventa;
    private RadioGroup radioGroup;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_calendar, container, false);

        tvMonthDay=view.findViewById(R.id.tvMonthDay);
        tvDayOfWeek=view.findViewById(R.id.tvDayOfWeek);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Button btnNext = view.findViewById(R.id.btnNextDay);
        Button btnPrev = view.findViewById(R.id.btnPrevDay);
        Button btnAddEvent = view.findViewById(R.id.btnAddEvent);
        LinearLayout layoutOra = view.findViewById(R.id.layoutOra);
        TextView tvOra = view.findViewById(R.id.tvTime);

        CalendarUtils.selectedDate = LocalDate.now();
        setDayView();

        btnNext.setOnClickListener(v -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
            setDayView();
        });
        btnPrev.setOnClickListener(v -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
            setDayView();
        });
        btnAddEvent.setOnClickListener(v -> {
            Toast.makeText(getActivity(),"Add event",Toast.LENGTH_SHORT).show();
            createPopupWindow(null,null);
//                createAddEventDialog();
        });
//        layoutOra.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createPopupWindow();
//            }
//        });
        Log.d("CalendarFragment", "No of reminders: " + Reminder.reminders.size());

//        db.collection("event").whereEqualTo("userID",user.getUid())
//                .addSnapshotListener((value, error) -> {
//                    if (error!=null){
//                        Log.w("CalendarFragment","Listen failed.",error);
//                        return;
//                    }
//                    for (QueryDocumentSnapshot document:value){
//                        String denumire=document.getString("name");
//                        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                        String strData=document.getString("day");
//                        LocalDate data=LocalDate.parse(strData,formatter);
//                        DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
//                        String strOraSt=document.getString("time start");
//                        LocalTime oraStart=LocalTime.parse(strOraSt,timeFormatter);
//                        String strOraSf=document.getString("time final");
//                        LocalTime oraSfarsit=LocalTime.parse(strOraSf,timeFormatter);
//                        StatusEv statusEv=StatusEv.valueOf(document.getString("status"));
//                        Categories category= Categories.valueOf(document.getString("category"));
//
//                        Event newEvent=new Event(denumire,data,oraStart,oraSfarsit,statusEv, category);
//                        Event.eventsList.add(newEvent);
//                    }
////                        setDayView();
//                });
        return view;
    }

//    private void createAddEventDialog() {
//        dialogBuilder=new AlertDialog.Builder(getActivity());
////        final View popupView=getLayoutInflater().inflate(R.layout.popup_add_event,null);
//        LayoutInflater inflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View popupView=inflater.inflate(R.layout.popup_add_event,null);
//
//        Button btnsave=popupView.findViewById(R.id.btnSave);
//        TextInputEditText tietDenumire=popupView.findViewById(R.id.tietDenumire);
//
//        dialogBuilder.setView(popupView);
//        dialog=dialogBuilder.create();
//        dialog.show();
//
//        btnsave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String eventName = tietDenumire.getText().toString();
//                Event newEvent = new Event(eventName, CalendarUtils.selectedDate, LocalTime.now());
//                Event.eventsList.add(newEvent);
//                Toast.makeText(getActivity(),newEvent.toString(),Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//                onResume();
//            }
//        });
//    }

    protected void createPopupWindow(LocalDate date, LocalTime timeStart) {
        LayoutInflater inflater= (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView=inflater.inflate(R.layout.popup_add_event,null);

        PopupWindow popupWindow = showPopupWindow(popupView);

        initializeViews(popupView);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbProgramFix) {
                tvTitlu.setText(R.string.adauga_o_sarcina);
                llData.setVisibility(View.VISIBLE);
                llOraStart.setVisibility(View.VISIBLE);
                llOraSfarsit.setVisibility(View.VISIBLE);

                tvDurata.setVisibility(View.GONE);
                spnDurata.setVisibility(View.GONE);
                tvFrecventa.setVisibility(View.GONE);
                tietNrFrecventa.setVisibility(View.GONE);
                tvAux.setVisibility(View.GONE);
                spnFrecventa.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbProgramFlexibil) {
                tvTitlu.setText(R.string.seteaza_un_obiectiv);
                llData.setVisibility(View.GONE);
                llOraStart.setVisibility(View.GONE);
                llOraSfarsit.setVisibility(View.GONE);

                tvDurata.setVisibility(View.VISIBLE);
                spnDurata.setVisibility(View.VISIBLE);
                tvFrecventa.setVisibility(View.VISIBLE);
                tietNrFrecventa.setVisibility(View.VISIBLE);
                tvAux.setVisibility(View.VISIBLE);
                spnFrecventa.setVisibility(View.VISIBLE);
            }
        });
        llData.setOnClickListener(v -> datePickerDialog());
        llOraStart.setOnClickListener(v -> timePickerDialog(v));
        llOraSfarsit.setOnClickListener(v -> timePickerDialog(v));
//        if(date!=null){
//            tvData.setText(date.toString());
//        }
//        if(timeStart!=null){
//            tvOraStart.setText(timeStart.toString());
//        }
        Button btnsave=popupView.findViewById(R.id.btnSave);
        btnsave.setOnClickListener(v -> {
            DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");

            if (!checkInputs()) {
                return;
            }
            Event newEvent=createEventFromPopupView(dateFormatter,timeFormatter);
            if (newEvent==null){
                return;
            }
            Event.eventsList.add(newEvent);

            Map<String,Object> eventToAdd=new HashMap<>();
            createEventToAdd(dateFormatter, timeFormatter, newEvent, eventToAdd);
//                final String[] idDoc = new String[1];

            db.collection("event").add(eventToAdd).addOnSuccessListener(documentReference -> {
                String idDoc = documentReference.getId();
                Log.d("CalendarFragment", "New document added with ID: " + idDoc);

                db.collection("user").document(user.getUid())
                        .update("events",FieldValue.arrayUnion(idDoc))
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getActivity(),newEvent.toString(),Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                            onResume();
                        })
                        .addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding event id to the events list", e));

                long dateTime=CalendarUtils.toLong(newEvent.getDate(),newEvent.getTimeStart())- (10 * 60 * 1000);

                Map<String,Object> reminderToAdd=new HashMap<>();
                reminderToAdd.put("message",newEvent.getName());
                reminderToAdd.put("time millis", dateTime);
                reminderToAdd.put("eventID",idDoc);

                db.collection("reminder").add(reminderToAdd).addOnSuccessListener(documentReference1 -> {
                    Log.d("CalendarFragment", "New reminder added with ID: " + documentReference1.getId());
                    Reminder newReminder=new Reminder(newEvent.getName(),dateTime);
                    Reminder.reminders.add(newReminder);

                    AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(getContext(), NotificationReceiver.class);
                    intent.putExtra("message", newReminder.getMessage());
                    intent.putExtra("time", newReminder.getDateTime());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, newReminder.getDateTime(), pendingIntent);
                }).addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding reminder", e));
            }).addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding event", e));

        });
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
//                onResume();
            return true;
        });

    }

    private boolean checkInputs() {
        boolean isValid=true;
        llErrorDenumire.setVisibility(View.GONE);
        llErrorData.setVisibility(View.GONE);
        if(tvData.getText()==getString(R.string.data)){
            llErrorData.setVisibility(View.VISIBLE);
            isValid= false;
        }
        if (TextUtils.isEmpty(tietDenumire.getText())){
            llErrorDenumire.setVisibility(View.VISIBLE);
            isValid= false;
        }
        return isValid;
    }

    private void createEventToAdd(DateTimeFormatter dateFormatter, DateTimeFormatter timeFormatter, Event newEvent, Map<String, Object> eventToAdd) {
        eventToAdd.put("name", newEvent.getName());
        eventToAdd.put("day", newEvent.getDate().format(dateFormatter));
        eventToAdd.put("time start", newEvent.getTimeStart().format(timeFormatter));
        eventToAdd.put("time final", newEvent.getTimeFinal().format(timeFormatter));
        eventToAdd.put("status",StatusEv.NEINCEPUT);
        eventToAdd.put("category", newEvent.getCategory().name());
        eventToAdd.put("userID",user.getUid());
    }

    private Event createEventFromPopupView(DateTimeFormatter dateFormatter, DateTimeFormatter timeFormatter) {
        Categories strCategory=(Categories) spnCategory.getSelectedItem();
        String eventName = tietDenumire.getText().toString();
        String strData = tvData.getText().toString();
        String strOraStart = tvOraStart.getText().toString();
        String strOraSfarsit = tvOraSfarsit.getText().toString();
        LocalDate dataEv=LocalDate.parse(strData,dateFormatter);
        LocalTime oraStartEv,oraSfarsitEv;
        if (!strOraStart.equals(getString(R.string.ora_start)) && !strOraSfarsit.equals(getString(R.string.ora_sfarsit))){
            oraStartEv=LocalTime.parse(strOraStart,timeFormatter);
            oraSfarsitEv=LocalTime.parse(strOraSfarsit,timeFormatter);
            Event eventFromPopup=new Event(eventName, dataEv, oraStartEv,oraSfarsitEv,StatusEv.NEINCEPUT,strCategory);
            if (checkForOverlaps(eventFromPopup)){//the event can be created
                //TODO: time final< time start => doua evenimente
                return eventFromPopup;
            }else {
                Toast.makeText(getContext(), "Evenimentul se suprapune cu cele deja existente!" , Toast.LENGTH_SHORT).show();
                return null;
            }
        }else {
            return scheduleEvent(eventName,dataEv,strCategory);
        }
    }

    private boolean checkForOverlaps(Event eventFromPopup) {
        List<Event> dayEvents = getEventsForDate(eventFromPopup.getDate());

        if (isStartingHourNotOverlapping(eventFromPopup,dayEvents) && isFinishingHourNotOverlapping(eventFromPopup,dayEvents)){
            if (!isEventScheduledBetween(eventFromPopup, dayEvents)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEventScheduledBetween(Event eventFromPopup, List<Event> dayEvents) {
        for (Event event : dayEvents) {
            if (event.getTimeStart().isAfter(eventFromPopup.getTimeStart()) &&
                    event.getTimeFinal().isBefore(eventFromPopup.getTimeFinal())) {
                return true; // Event scheduled between the starting and finishing hours of eventFromPopup
            }
        }
        return false; // No event scheduled between the starting and finishing hours of eventFromPopup
    }

    private boolean isFinishingHourNotOverlapping(Event eventFromPopup, List<Event> dayEvents) {
        Event firstEventAfterPopup = null;

        for (Event event : dayEvents) {
            if (event.getTimeStart().isAfter(eventFromPopup.getTimeStart())) {
                firstEventAfterPopup = event;
                break;
            }
        }

        if (firstEventAfterPopup != null ){
            if(firstEventAfterPopup.getTimeStart().isBefore(eventFromPopup.getTimeFinal())){
                return false;
            }
        }
        return true;
    }

    private boolean isStartingHourNotOverlapping(Event eventFromPopup, List<Event> dayEvents) {
        Event lastEventBeforePopup = null;

        for (Event event : dayEvents) {
            if (event.getTimeStart().isBefore(eventFromPopup.getTimeStart())) {
                lastEventBeforePopup = event;
            } else {
                break;
            }
        }

        if (lastEventBeforePopup != null ){
            if(lastEventBeforePopup.getTimeFinal().isAfter(eventFromPopup.getTimeStart())){
                return false;
            }
        }
        return true;
    }

    private Event scheduleEvent(String eventName, LocalDate dataEv, Categories strCategory) {
        LocalTime startTime = LocalTime.of(8, 0); // Starting time: 8am

        List<Event> eventsForDate = getEventsForDate(dataEv);
        // Check if there is at least one hour available before the first event
        if (eventsForDate.isEmpty() || startTime.plusHours(1).isBefore(eventsForDate.get(0).getTimeStart())) {
            // Schedule the event using the specified start and end times
            return new Event(eventName, dataEv, startTime, startTime.plusHours(1), StatusEv.NEINCEPUT, strCategory);
        }

        // Iterate over the events to find a free hour between the events
        for (int i = 0; i < eventsForDate.size() - 1; i++) {
            Event currentEvent = eventsForDate.get(i);
            Event nextEvent = eventsForDate.get(i + 1);

            LocalTime currentEventEndTime = currentEvent.getTimeFinal();
            LocalTime nextEventStartTime = nextEvent.getTimeStart();

            // Check if there is at least one hour available between the current event's end time and the next event's start time
            if (currentEventEndTime.plusHours(1).isBefore(nextEventStartTime)) {
                // One hour is available, schedule the event
                return new Event(eventName, dataEv, currentEventEndTime, currentEventEndTime.plusHours(1), StatusEv.NEINCEPUT, strCategory);
            }
        }
        // Check if there is at least one hour available after the last event
        Event lastEvent = eventsForDate.get(eventsForDate.size() - 1);
        LocalTime lastEventEndTime = lastEvent.getTimeFinal();
        if (lastEventEndTime.isBefore(LocalTime.of(23, 1))) {
            // Schedule the event using the specified start and end times
            return new Event(eventName, dataEv, lastEventEndTime, lastEventEndTime.plusHours(1), StatusEv.NEINCEPUT, strCategory);
        }
        System.out.println("Ziua este plina! Nu se poate programa pe data de " + dataEv);
        Toast.makeText(getContext(), "Ziua este plina! Nu se poate programa pe data de " + dataEv, Toast.LENGTH_SHORT).show();
        return null;
    }

    private List<Event> getEventsForDate(LocalDate dataEv) {
//        List<Event> eventsForDate=new ArrayList<>();
//        for (Event ev:Event.eventsList) {
//            if(ev.getDate().equals(dataEv)){
//                eventsForDate.add(ev);
//            }
//        }
        List<Event> eventsForDate=Event.eventsForDate(dataEv);

        eventsForDate.sort((event1, event2) -> {
            LocalTime startTime1 = event1.getTimeStart();
            LocalTime startTime2 = event2.getTimeStart();
            return startTime1.compareTo(startTime2);
        });
        return eventsForDate;
    }

    private void initializeViews(View popupView) {
        tvTitlu=popupView.findViewById(R.id.textView);
        radioGroup=popupView.findViewById(R.id.radioGroup);
        spnCategory =popupView.findViewById(R.id.spnCategory);
        spnCategory.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, Categories.values()));

        tietDenumire=popupView.findViewById(R.id.tietDenumire);
        llData=popupView.findViewById(R.id.llData);
        llOraStart=popupView.findViewById(R.id.llOraStart);
        llOraSfarsit=popupView.findViewById(R.id.llOraSfarsit);

        tvData=popupView.findViewById(R.id.tvData);
        tvOraStart=popupView.findViewById(R.id.tvOraStart);
        tvOraSfarsit=popupView.findViewById(R.id.tvOraSfarsit);

        tvDurata=popupView.findViewById(R.id.tvDurata);
        spnDurata=popupView.findViewById(R.id.spnDurata);
        tvFrecventa=popupView.findViewById(R.id.tvFrecventa);
        tietNrFrecventa=popupView.findViewById(R.id.tietNrFrecventa);
        tvAux=popupView.findViewById(R.id.tvAux);
        spnFrecventa=popupView.findViewById(R.id.spnUnitateFrecventa);
        llErrorDenumire=popupView.findViewById(R.id.llErrorDenumire);
        llErrorData=popupView.findViewById(R.id.llErrorData);
    }

    @NonNull
    private PopupWindow showPopupWindow(View popupView) {
        int width=ViewGroup.LayoutParams.MATCH_PARENT;
        int height=ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable=true;
        PopupWindow popupWindow=new PopupWindow(popupView,width,height,focusable);
        LinearLayout layout=view.findViewById(R.id.linearLayout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.CENTER,0,0);
            }
        });
        return popupWindow;
    }

    private void timePickerDialog(View v) {
        TimePickerDialog dialog=new TimePickerDialog(requireContext(), (view, hour, minute) -> {
            if (llOraStart.equals(v)) {
                tvOraStart.setText((hour<10?"0"+hour:hour)+":"+(minute<10?"0"+minute:minute));
            } else if (llOraSfarsit.equals(v)){
                tvOraSfarsit.setText((hour<10?"0"+hour:hour)+":"+(minute<10?"0"+minute:minute));
            }
        },LocalTime.now().getHour(),LocalTime.now().getMinute(),true);
        dialog.show();
    }

    private void datePickerDialog() {
        DatePickerDialog dialog=new DatePickerDialog(requireContext(), (view, year, month, day) -> tvData.setText((day<10?"0"+day:day)+"/"+((month+1)<10?"0"+(month+1):(month+1))+"/"+year),selectedDate.getYear(),selectedDate.getMonthValue()-1,selectedDate.getDayOfMonth());
        dialog.show();
    }

    private List<Object> generateBlockList(LocalDate selectedDate) {
        List<Event> eventsForDate = Event.eventsForDate(selectedDate);
        Log.d("generateBlockList- No of events for date", String.valueOf(eventsForDate.size()));

        List<Object> blockList = new ArrayList<>();

        if (!eventsForDate.isEmpty()) {

            // Sort events by start time
            eventsForDate.sort(Comparator.comparing(Event::getTimeStart));

            Event firstEvent = eventsForDate.get(0);

            long durationBeforeFirstEvent = eventAdapter.calculateDuration(LocalTime.of(0, 0), firstEvent.getTimeStart());
            blockList.add(eventAdapter.calculateBlockHeight(durationBeforeFirstEvent));

            // Add the first event
            blockList.add(firstEvent);

            // Add the blank blocks and events
            for (int i = 1; i < eventsForDate.size(); i++) {
                Event previousEvent = eventsForDate.get(i - 1);
                Event currentEvent = eventsForDate.get(i);

                // Calculate the duration between the end time of the previous event and the start time of the current event
                long duration = eventAdapter.calculateDuration(previousEvent.getTimeFinal(), currentEvent.getTimeStart());

                // Add the blank block
                blockList.add(eventAdapter.calculateBlockHeight(duration));

                // Add the current event
                blockList.add(currentEvent);
            }
            Event lastEvent = eventsForDate.get(eventsForDate.size() - 1);
            long durationAfterLastEvent = eventAdapter.calculateDuration(lastEvent.getTimeFinal(), LocalTime.of(23, 59));
            blockList.add(eventAdapter.calculateBlockHeight(durationAfterLastEvent));
        }else {
            long duration = eventAdapter.calculateDuration(LocalTime.of(0,0), LocalTime.of(23,59));
            blockList.add(eventAdapter.calculateBlockHeight(duration));
        }

        return blockList;
    }

    @Override
    public void onResume() {
        super.onResume();
        setDayView();
    }

    private void setDayView() {
        tvMonthDay.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek=selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        tvDayOfWeek.setText(dayOfWeek);
        setRecyclerViewAdapter(new RecyclerViewAdapter(new ArrayList<>(),true));
    }

    private void setRecyclerViewAdapter(RecyclerViewAdapter recyclerViewAdapter) {
        eventAdapter = recyclerViewAdapter; // Initialize the adapter with an empty list
        List<Object> blockList = generateBlockList(selectedDate);
        eventAdapter.setBlockList(blockList);
        recyclerView.setAdapter(eventAdapter);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                long duration = Duration.between(LocalTime.of(0, 0), LocalTime.of(23, 59)).toMinutes();
                int totalBlockHeight = eventAdapter.calculateBlockHeight(duration);
                int centralPosition = totalBlockHeight / 2;
                int recyclerViewHeight = recyclerView.getHeight();
                int scrollToPosition = centralPosition - recyclerViewHeight / 2;

                recyclerView.scrollToPosition(scrollToPosition);
                recyclerView.smoothScrollBy(0, recyclerViewHeight / 2);
            }
        });
    }
}