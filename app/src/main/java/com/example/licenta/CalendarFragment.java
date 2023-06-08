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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private TextView tvMonthDay;
    private TextView tvDayOfWeek;
    private ListView lvHour;
    private View view;
    private LinearLayout layoutOra;
    private TextView tvOra;
    private LinearLayout llData,llOraStart,llOraSfarsit;
    private TextView tvData,tvOraStart,tvOraSfarsit;
    private TextInputEditText tietDenumire;
    private Spinner spinner;

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
        lvHour=view.findViewById(R.id.lvHour);
        Button btnNext = view.findViewById(R.id.btnNextDay);
        Button btnPrev = view.findViewById(R.id.btnPrevDay);
        Button btnAddEvent = view.findViewById(R.id.btnAddEvent);
        layoutOra=view.findViewById(R.id.layoutOra);
        tvOra=view.findViewById(R.id.tvTime);

        CalendarUtils.selectedDate = LocalDate.now();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
                setDayView();
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
                setDayView();
            }
        });
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Add event",Toast.LENGTH_SHORT).show();
                createPopupWindow(null,null);
//                createAddEventDialog();
            }
        });
//        layoutOra.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createPopupWindow();
//            }
//        });
        Log.d("CalendarFragment", "No of reminders: " + Reminder.reminders.size());

        db.collection("event").whereEqualTo("userID",user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            Log.w("CalendarFragment","Listen failed.",error);
                            return;
                        }
                        for (QueryDocumentSnapshot document:value){
                            String denumire=document.getString("name");
                            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            String strData=document.getString("day");
                            LocalDate data=LocalDate.parse(strData,formatter);
                            DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
                            String strOraSt=document.getString("time start");
                            LocalTime oraStart=LocalTime.parse(strOraSt,timeFormatter);
                            String strOraSf=document.getString("time final");
                            LocalTime oraSfarsit=LocalTime.parse(strOraSf,timeFormatter);
                            StatusEv statusEv=StatusEv.valueOf(document.getString("status"));
                            Categories category= Categories.valueOf(document.getString("category"));

                            Event newEvent=new Event(denumire,data,oraStart,oraSfarsit,statusEv,category==null?Categories.ALTELE:category);
                            Event.eventsList.add(newEvent);
                        }
                        setDayView();
                    }
                });
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

        PopupWindow popupWindow = createPopupWindow(popupView);

        initializeViews(popupView);

        llData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog();
            }
        });
        llOraStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog(v);
            }
        });
        llOraSfarsit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog(v);
            }
        });
//        if(date!=null){
//            tvData.setText(date.toString());
//        }
//        if(timeStart!=null){
//            tvOraStart.setText(timeStart.toString());
//        }
        Button btnsave=popupView.findViewById(R.id.btnSave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");

                Event newEvent=createEventFromPopupView(dateFormatter,timeFormatter);
                Event.eventsList.add(newEvent);

                Map<String,Object> eventToAdd=new HashMap<>();
                createEventToAdd(dateFormatter, timeFormatter, newEvent, eventToAdd);
//                final String[] idDoc = new String[1];

                db.collection("event").add(eventToAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String idDoc = documentReference.getId();
                        Log.d("CalendarFragment", "New document added with ID: " + idDoc);

                        db.collection("user").document(user.getUid())
                                .update("events",FieldValue.arrayUnion(idDoc))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(),newEvent.toString(),Toast.LENGTH_SHORT).show();
                                        popupWindow.dismiss();
                                        onResume();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("CalendarFragment", "Error adding event id to the events list", e);
                                    }
                                });

                        long dateTime=CalendarUtils.toLong(newEvent.getDate(),newEvent.getTimeStart())- (10 * 60 * 1000);

                        Map<String,Object> reminderToAdd=new HashMap<>();
                        reminderToAdd.put("message",newEvent.getName());
                        reminderToAdd.put("time millis", dateTime);
                        reminderToAdd.put("eventID",idDoc);

                        db.collection("reminder").add(reminderToAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("CalendarFragment", "New reminder added with ID: " + documentReference.getId());
                                Reminder newReminder=new Reminder(newEvent.getName(),dateTime);
                                Reminder.reminders.add(newReminder);

                                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

                                Intent intent = new Intent(getContext(), NotificationReceiver.class);
                                intent.putExtra("message", newReminder.getMessage());
                                intent.putExtra("time", newReminder.getDateTime());
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                alarmManager.set(AlarmManager.RTC_WAKEUP, newReminder.getDateTime(), pendingIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("CalendarFragment", "Error adding reminder", e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("CalendarFragment", "Error adding event", e);
                    }
                });

            }
        });
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                onResume();
                return true;
            }
        });

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
        Categories strCategory=(Categories) spinner.getSelectedItem();
        String eventName = tietDenumire.getText().toString();
        String strData = tvData.getText().toString();
        String strOraStart = tvOraStart.getText().toString();
        String strOraSfarsit = tvOraSfarsit.getText().toString();
        LocalDate dataEv=LocalDate.parse(strData,dateFormatter);
        LocalTime oraStartEv=LocalTime.parse(strOraStart,timeFormatter);
        LocalTime oraSfarsitEv=LocalTime.parse(strOraSfarsit,timeFormatter);
//                LocalTime time = LocalTime.now();
//                Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time);
        return new Event(eventName, dataEv, oraStartEv,oraSfarsitEv,StatusEv.NEINCEPUT,strCategory);
    }

    private void initializeViews(View popupView) {
        spinner=popupView.findViewById(R.id.spnCategory);
        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, Categories.values()));

        tietDenumire=popupView.findViewById(R.id.tietDenumire);
        llData=popupView.findViewById(R.id.llData);
        llOraStart=popupView.findViewById(R.id.llOraStart);
        llOraSfarsit=popupView.findViewById(R.id.llOraSfarsit);

        tvData=popupView.findViewById(R.id.tvData);
        tvOraStart=popupView.findViewById(R.id.tvOraStart);
        tvOraSfarsit=popupView.findViewById(R.id.tvOraSfarsit);
    }

    @NonNull
    private PopupWindow createPopupWindow(View popupView) {
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
        TimePickerDialog dialog=new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                if (llOraStart.equals(v)) {
                    tvOraStart.setText((hour<10?"0"+hour:hour)+":"+(minute<10?"0"+minute:minute));
                } else if (llOraSfarsit.equals(v)){
                    tvOraSfarsit.setText((hour<10?"0"+hour:hour)+":"+(minute<10?"0"+minute:minute));
                }
            }
        },LocalTime.now().getHour(),LocalTime.now().getMinute(),true);
        dialog.show();
    }

    private void datePickerDialog() {
        DatePickerDialog dialog=new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                tvData.setText((day<10?"0"+day:day)+"/"+((month+1)<10?"0"+(month+1):(month+1))+"/"+year);
            }
        },selectedDate.getYear(),selectedDate.getMonthValue(),selectedDate.getDayOfMonth());
        dialog.show();
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
        setHourAdapter();
    }

    private void setHourAdapter() {
        HourAdapter hourAdapter=new HourAdapter(getActivity().getApplicationContext(),hourEventList());
        lvHour.setAdapter(hourAdapter);
    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list=new ArrayList<>();
        for (int hour=0;hour<24;hour++){
            LocalTime time=LocalTime.of(hour,0);
            ArrayList<Event> events=Event.eventsForDateAndTime(selectedDate,time);
            HourEvent hourEvent=new HourEvent(time,events);
            list.add(hourEvent);
        }
//        Log.d("CalendarFragment", String.valueOf(Event.eventsForDateAndTime(selectedDate,LocalTime.now()).size()));
        return list;
    }
}