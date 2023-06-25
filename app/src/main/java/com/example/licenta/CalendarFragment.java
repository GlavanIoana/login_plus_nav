package com.example.licenta;

import static com.example.licenta.CalendarUtils.selectedDate;
import static com.example.licenta.Scheduler.checkForOverlaps;
import static com.example.licenta.Scheduler.numWeeksToScheduleEventsAhead;
import static com.example.licenta.Scheduler.scheduleEvent;
import static com.example.licenta.Scheduler.scheduleEventsForGoal;

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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
    private TextView tvTitlu,tvData,tvOraStart,tvOraSfarsit,tvDurata,tvMinute,tvFrecventa,tvAux,tvIntervalPreferinta;
    private TextInputEditText tietDenumire,tietNrFrecventa,tietDurata;
    private Spinner spnCategory,spnFrecventa,spnIntervalPreferinta;
    private RadioGroup radioGroup;
    private RadioButton rbEvenUnic,rbObiectiv;
    private CheckBox cbAll;
    private RecyclerView rvAvailableEvents;
    private Button btnsave,btnSchedule,btnCancel;

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

        CalendarUtils.selectedDate = LocalDate.now();
//        setDayView();

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

        Log.d("CalendarFragment", "No of reminders: " + Notification.reminders.size());

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
            if (checkedId == R.id.rbEvenUnic) {
                setPopupViewForEvenUnic();
            } else if (checkedId == R.id.rbObiectiv) {
                setPopupViewForObiectiv();
            }
        });
        llData.setOnClickListener(v -> datePickerDialog());
        llOraStart.setOnClickListener(this::timePickerDialog);
        llOraSfarsit.setOnClickListener(this::timePickerDialog);

        btnsave=popupView.findViewById(R.id.btnSave);
        btnsave.setOnClickListener(v -> {
            DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");

            if (!checkInputs()) {
                return;
            }

            if (rbEvenUnic.isChecked()) {
                Event newEvent = createEventFromPopupView(dateFormatter, timeFormatter);
                if (newEvent == null) {
                    return;
                }
                Event.eventsList.add(newEvent);

                Map<String, Object> eventToAdd = new HashMap<>();
                updateMapWithEventsFields(eventToAdd,newEvent);
//                final String[] idDoc = new String[1];

                db.collection("event").add(eventToAdd).addOnSuccessListener(documentReference -> {
                    String idDoc = documentReference.getId();
                    Log.d("CalendarFragment", "New document added with ID: " + idDoc);

                    db.collection("user").document(user.getUid())
                            .update("events", FieldValue.arrayUnion(idDoc))
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getActivity(), newEvent.toString(), Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();
                                onResume();
                            })
                            .addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding event id to the events list", e));

                    createReminderInDatabase(newEvent, idDoc);
                }).addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding event", e));
            }else if (rbObiectiv.isChecked()){
                createGoalFromPopupView(popupWindow);

            }
        });
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
//                onResume();
            return true;
        });

    }

    private void setPopupViewForObiectiv() {
        tvTitlu.setText(R.string.seteaza_un_obiectiv);
        radioGroup.setVisibility(View.VISIBLE);
        spnCategory.setVisibility(View.VISIBLE);
        tietDenumire.setVisibility(View.VISIBLE);
        llData.setVisibility(View.GONE);
        llOraStart.setVisibility(View.GONE);
        llOraSfarsit.setVisibility(View.GONE);

        tvDurata.setVisibility(View.VISIBLE);
        tietDurata.setVisibility(View.VISIBLE);
        tvMinute.setVisibility(View.VISIBLE);
        tvFrecventa.setVisibility(View.VISIBLE);
        tietNrFrecventa.setVisibility(View.VISIBLE);
        tvAux.setVisibility(View.VISIBLE);
        spnFrecventa.setVisibility(View.VISIBLE);
        tvIntervalPreferinta.setVisibility(View.VISIBLE);
        spnIntervalPreferinta.setVisibility(View.VISIBLE);

        btnsave.setVisibility(View.VISIBLE);

        cbAll.setVisibility(View.GONE);
        rvAvailableEvents.setVisibility(View.GONE);
        btnSchedule.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    private void setPopupViewForEvenUnic() {
        tvTitlu.setText(R.string.adauga_o_sarcina);
        radioGroup.setVisibility(View.VISIBLE);
        spnCategory.setVisibility(View.VISIBLE);
        tietDenumire.setVisibility(View.VISIBLE);
        llData.setVisibility(View.VISIBLE);
        llOraStart.setVisibility(View.VISIBLE);
        llOraSfarsit.setVisibility(View.VISIBLE);

        tvDurata.setVisibility(View.GONE);
        tietDurata.setVisibility(View.GONE);
        tvMinute.setVisibility(View.GONE);
        tvFrecventa.setVisibility(View.GONE);
        tietNrFrecventa.setVisibility(View.GONE);
        tvAux.setVisibility(View.GONE);
        spnFrecventa.setVisibility(View.GONE);
        tvIntervalPreferinta.setVisibility(View.GONE);
        spnIntervalPreferinta.setVisibility(View.GONE);

        btnsave.setVisibility(View.VISIBLE);

        cbAll.setVisibility(View.GONE);
        rvAvailableEvents.setVisibility(View.GONE);
        btnSchedule.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    private void createGoalFromPopupView(PopupWindow popupWindow) {
        Categories category=(Categories) spnCategory.getSelectedItem();
        String name = tietDenumire.getText().toString();
        long duration= Integer.parseInt(tietDurata.getText().toString());
        int frequency= Integer.parseInt(tietNrFrecventa.getText().toString());
        String typeFrequency= (String) spnFrecventa.getSelectedItem();
        String intervalPref= (String) spnIntervalPreferinta.getSelectedItem();
        Goal goal=new Goal(name,category,duration,frequency,typeFrequency,intervalPref);

        LocalTime intervalStart = LocalTime.of(6, 0);
        LocalTime intervalEnd= LocalTime.of(23, 59);
        String[] intervalLabels = getResources().getStringArray(R.array.intervale_preferinta);

        if (intervalPref.equals(intervalLabels[1])) {
            intervalStart = LocalTime.of(6, 0); // Start of the morning
            intervalEnd = LocalTime.of(10, 0); // End of the morning
        } else if (intervalPref.equals(intervalLabels[2])) {
            intervalStart = LocalTime.of(10, 0); // Start of the afternoon
            intervalEnd = LocalTime.of(14, 0); // End of the afternoon
        } else if (intervalPref.equals(intervalLabels[3])) {
            intervalStart = LocalTime.of(14, 0); // Start of the evening
            intervalEnd = LocalTime.of(18, 0); // End of the evening
        } else if (intervalPref.equals(intervalLabels[4])) {
            intervalStart = LocalTime.of(18, 0); // Start of the night
            intervalEnd = LocalTime.of(23, 59); // End of the night
        }
        List<Event> eventsFound = scheduleEventsForGoal(getContext(),goal,intervalStart,intervalEnd);

        // Handle the scheduled events (e.g., display them to the user, save to database, etc.)
        handleScheduledEvents(popupWindow,goal,eventsFound);
    }

    private void handleScheduledEvents(PopupWindow popupWindow, Goal goal, List<Event> scheduledEvents) {
        if (scheduledEvents.size() < numWeeksToScheduleEventsAhead * goal.getFrequency()) {
            Toast.makeText(getContext(), "NU S-AU GASIT ATATEA INTERVALE" , Toast.LENGTH_SHORT).show();
            // Inform the user and offer the option to schedule available intervals
        }

        setPopupViewForShowingAvailableEvents();
        AvailableEventAdapter availableEventAdapter = new AvailableEventAdapter(scheduledEvents);
        rvAvailableEvents.setAdapter(availableEventAdapter);
        rvAvailableEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO: sincronizare cbAll cu checkboxuri

        btnSchedule.setOnClickListener(v -> {
            Goal.goalsList.add(goal);
            List<Event> selectedEvents = availableEventAdapter.getSelectedEvents();
            for (Event event : selectedEvents) {
                goal.addEvent(event);
            }
            addGoalToDatabase(popupWindow,goal);
            Toast.makeText(getContext(), selectedEvents.size()+" Selected events added to the goal", Toast.LENGTH_SHORT).show();
        });
        btnCancel.setOnClickListener(v -> {
            setPopupViewForObiectiv();
            Categories goalCategory = goal.getCategory();
            int categoryIndex = getCategoryIndex(goalCategory);
            spnCategory.setSelection(categoryIndex);
            tietDenumire.setText(goal.getName());
            tietDurata.setText(String.valueOf(goal.getDuration()));
            tietNrFrecventa.setText(String.valueOf(goal.getFrequency()));
            spnFrecventa.setSelection(getTypeFrequencyIndex(goal.getTypeFrequency()));
        });
    }
    private int getTypeFrequencyIndex(String typeFrequency) {
        SpinnerAdapter adapter = spnFrecventa.getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            Object item = adapter.getItem(i);
            if (item != null && item.toString().equals(typeFrequency)) {
                return i;
            }
        }
        return 0; // If the typeFrequency is not found in the adapter
    }

    private int getCategoryIndex(Categories category) {
        SpinnerAdapter adapter = spnCategory.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i) == category) {
                return i;
            }
        }
        return 0; // Default to the first item if category is not found
    }

    private void addGoalToDatabase(PopupWindow popupWindow, Goal newGoal) {
        Map<String, Object> goalToAdd = new HashMap<>();
        updateMapWithGoalFields(goalToAdd,newGoal);
        // Add the new goal to the database
        db.collection("goal").add(goalToAdd)
                .addOnSuccessListener(documentReference -> {
                    String goalId = documentReference.getId();
                    Log.d("CalendarFragment", "New goal added with ID: " + goalId);

                    // Create a list of event IDs for the goal's events
                    List<String> eventIds = new ArrayList<>();
                    for (Event event : newGoal.getEvents()) {
                        Map<String, Object> eventToAdd = new HashMap<>();
                        updateMapWithEventsFields(eventToAdd,event);

                        db.collection("event").add(eventToAdd)
                                .addOnSuccessListener(eventDocumentReference -> {
                                    String eventId = eventDocumentReference.getId();
                                    Log.d("CalendarFragment", "New event added with ID: " + eventId);
                                    eventIds.add(eventId);
                                    Event.eventsList.add(event);

                                    // Update the goal document with the event IDs
                                    db.collection("goal").document(goalId)
                                            .update("eventIds", FieldValue.arrayUnion(eventIds.toArray()))
                                            .addOnSuccessListener(unused -> {
//                                                Toast.makeText(getActivity(), newGoal.toString(), Toast.LENGTH_SHORT).show();
                                                if (popupWindow != null && popupWindow.isShowing()) {
                                                    popupWindow.dismiss();
                                                }
                                                onResume();
                                            })
                                            .addOnFailureListener(e -> Log.d("CalendarFragment", "Error updating goal document with event IDs", e));
                                }).addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding event", e));
                    }
                }).addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding goal", e));
    }

    private void updateMapWithGoalFields(Map<String, Object> goalToAdd, Goal newGoal) {
        goalToAdd.put("name", newGoal.getName());
        goalToAdd.put("category", newGoal.getCategory().name());
        goalToAdd.put("duration", newGoal.getDuration());
        goalToAdd.put("frequency", newGoal.getFrequency());
        goalToAdd.put("type frequency", newGoal.getTypeFrequency());
        goalToAdd.put("userID",user.getUid());
    }

    private void setPopupViewForShowingAvailableEvents() {
        tvTitlu.setText(R.string.alege_intervale);
        radioGroup.setVisibility(View.GONE);
        spnCategory.setVisibility(View.GONE);
        tietDenumire.setVisibility(View.GONE);

        llData.setVisibility(View.GONE);
        llOraStart.setVisibility(View.GONE);
        llOraSfarsit.setVisibility(View.GONE);

        tvDurata.setVisibility(View.GONE);
        tietDurata.setVisibility(View.GONE);
        tvMinute.setVisibility(View.GONE);
        tvFrecventa.setVisibility(View.GONE);
        tietNrFrecventa.setVisibility(View.GONE);
        tvAux.setVisibility(View.GONE);
        spnFrecventa.setVisibility(View.GONE);
        tvIntervalPreferinta.setVisibility(View.GONE);
        spnIntervalPreferinta.setVisibility(View.GONE);

        btnsave.setVisibility(View.GONE);

        cbAll.setVisibility(View.VISIBLE);
        rvAvailableEvents.setVisibility(View.VISIBLE);
        btnSchedule.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
    }

    private boolean checkInputs() {
        boolean isValid=true;
        llErrorDenumire.setVisibility(View.GONE);
        llErrorData.setVisibility(View.GONE);
        if (rbEvenUnic.isChecked()) {
            if(tvData.getText()==getString(R.string.data)){
                llErrorData.setVisibility(View.VISIBLE);
                isValid= false;
            }
        }
        if (TextUtils.isEmpty(tietDenumire.getText())){
            llErrorDenumire.setVisibility(View.VISIBLE);
            isValid= false;
        }
        return isValid;
    }

    private void updateMapWithEventsFields(Map<String, Object> eventToAdd,Event newEvent) {
        eventToAdd.put("name", newEvent.getName());
        eventToAdd.put("day", CalendarUtils.formattedDate(newEvent.getDate()));
        eventToAdd.put("time start", CalendarUtils.formattedShortTime(newEvent.getTimeStart()));
        eventToAdd.put("time final", CalendarUtils.formattedShortTime(newEvent.getTimeFinal()));
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
            return scheduleEvent(getContext(),eventName,dataEv,strCategory,60,LocalTime.of(6,0),LocalTime.of(23,59));
        }
    }

    private void initializeViews(View popupView) {
        tvTitlu=popupView.findViewById(R.id.textView);
        radioGroup=popupView.findViewById(R.id.radioGroup);
        rbEvenUnic=popupView.findViewById(R.id.rbEvenUnic);
        rbObiectiv=popupView.findViewById(R.id.rbObiectiv);
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
        tietDurata=popupView.findViewById(R.id.tietDurata);
        tvMinute = popupView.findViewById(R.id.tvMinute);
        tvFrecventa=popupView.findViewById(R.id.tvFrecventa);
        tietNrFrecventa=popupView.findViewById(R.id.tietNrFrecventa);
        tvAux=popupView.findViewById(R.id.tvAux);
        spnFrecventa=popupView.findViewById(R.id.spnUnitateFrecventa);
        tvIntervalPreferinta=popupView.findViewById(R.id.tvIntervalPreferinta);
        spnIntervalPreferinta=popupView.findViewById(R.id.spnIntervalPreferinta);
        llErrorDenumire=popupView.findViewById(R.id.llErrorDenumire);
        llErrorData=popupView.findViewById(R.id.llErrorData);

        cbAll=popupView.findViewById(R.id.cbAll);
        rvAvailableEvents=popupView.findViewById(R.id.rvAvailableEvents);
        btnSchedule=popupView.findViewById(R.id.btnSchedule);
        btnCancel=popupView.findViewById(R.id.btnCancel);
    }

    private void createReminderInDatabase(Event newEvent, String idDoc) {
        long dateTime = CalendarUtils.toLong(newEvent.getDate(), newEvent.getTimeStart()) - (10 * 60 * 1000);

        Map<String, Object> reminderToAdd = new HashMap<>();
        reminderToAdd.put("message", newEvent.getName());
        reminderToAdd.put("time millis", dateTime);
        reminderToAdd.put("eventID", idDoc);

        db.collection("reminder").add(reminderToAdd).addOnSuccessListener(documentReference1 -> {
            Log.d("CalendarFragment", "New reminder added with ID: " + documentReference1.getId());
            Notification newReminder = new Notification(newEvent.getName(), dateTime);
            Notification.reminders.add(newReminder);

            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(getContext(), NotificationReceiver.class);
            intent.putExtra("message", newReminder.getMessage());
            intent.putExtra("time", newReminder.getDateTime());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, newReminder.getDateTime(), pendingIntent);
        }).addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding reminder", e));
    }

    @NonNull
    private PopupWindow showPopupWindow(View popupView) {
        int width=ViewGroup.LayoutParams.MATCH_PARENT;
        int height=ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable=true;
        PopupWindow popupWindow=new PopupWindow(popupView,width,height,focusable);
        LinearLayout layout=view.findViewById(R.id.linearLayout);
        layout.post(() -> popupWindow.showAtLocation(layout, Gravity.CENTER,0,0));
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

            eventsForDate.sort(Comparator.comparing(Event::getTimeStart));

            Event firstEvent = eventsForDate.get(0);

            long durationBeforeFirstEvent = eventAdapter.calculateDuration(LocalTime.of(0, 0), firstEvent.getTimeStart());
            blockList.add(RecyclerViewAdapter.calculateBlockHeight(durationBeforeFirstEvent));

            blockList.add(firstEvent);

            for (int i = 1; i < eventsForDate.size(); i++) {
                Event previousEvent = eventsForDate.get(i - 1);
                Event currentEvent = eventsForDate.get(i);

                long duration = eventAdapter.calculateDuration(previousEvent.getTimeFinal(), currentEvent.getTimeStart());

                blockList.add(RecyclerViewAdapter.calculateBlockHeight(duration));

                blockList.add(currentEvent);
            }
            Event lastEvent = eventsForDate.get(eventsForDate.size() - 1);
            long durationAfterLastEvent = eventAdapter.calculateDuration(lastEvent.getTimeFinal(), LocalTime.of(23, 59));
            blockList.add(RecyclerViewAdapter.calculateBlockHeight(durationAfterLastEvent));
        }else {
            long duration = eventAdapter.calculateDuration(LocalTime.of(0,0), LocalTime.of(23,59));
            blockList.add(RecyclerViewAdapter.calculateBlockHeight(duration));
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
                int totalBlockHeight = RecyclerViewAdapter.calculateBlockHeight(duration);
                int centralPosition = totalBlockHeight / 2;
                int recyclerViewHeight = recyclerView.getHeight();
                int scrollToPosition = centralPosition - recyclerViewHeight / 2;

                recyclerView.scrollToPosition(scrollToPosition);
                recyclerView.smoothScrollBy(0, recyclerViewHeight / 2);
            }
        });
    }
}