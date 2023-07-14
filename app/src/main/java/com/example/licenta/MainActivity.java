package com.example.licenta;

import static com.example.licenta.CalendarFragment.updateMapWithEventsFields;
import static com.example.licenta.Scheduler.scheduleEventsForGoal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.work.Constraints;
//import androidx.work.ExistingPeriodicWorkPolicy;
//import androidx.work.NetworkType;
//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.licenta.Eisenhower.EisenhowerFragment;
import com.example.licenta.Pomodoro.PomodoroFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
//    private NotificationManagerCompat notificationManager;
//    private static final String TAG = "MainActivity";
    FirebaseAuth auth;
    FirebaseUser user;
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
//    private static final String WORK_TAG = "notification_work";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        Map<String, Object> userobj = new HashMap<>();
        userobj.put("email", user.getEmail());
        db.collection("user").document(user.getUid()).set(userobj)
                .addOnSuccessListener(unused -> Log.d("MainActivity", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("MainActivity", "Error writing document", e));

        Event.eventsList.clear();
        db.collection("event").whereEqualTo("userID", user.getUid()).orderBy("day").orderBy("time start").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Event.eventsList.add(citireEvenimentBazaDeDate(documentSnapshot));
                    }
//                    updateGoals(()->initializeViews(savedInstanceState));

                    Log.d("MainActivity","s-au citit evenimentele");
                    db.collection("goal").whereEqualTo("userID", user.getUid()).get()
                            .addOnSuccessListener(queryDocumentSnapshotsGoals -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshotsGoals) {
                                    Goal.goalsList.add(readGoalFromDatabase(documentSnapshot));
                                }
                                Log.d("MainActivity","s-au citit obiectivele");
                                updateGoals(() -> initializeViews(savedInstanceState));
                            });
                });

    }

//        notificationManager = NotificationManagerCompat.from(this);
//
//        createNotificationChannels();

//
//        // Create periodic work request
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
//
//        PeriodicWorkRequest notificationWorkRequest =
//                new PeriodicWorkRequest.Builder(NotificationWorker.class, 5, TimeUnit.MINUTES)
//                        .setConstraints(constraints)
//                        .build();
//
//        // Enqueue the work request
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//                WORK_TAG,
//                ExistingPeriodicWorkPolicy.KEEP,
//                notificationWorkRequest
//        );



    private Goal readGoalFromDatabase(QueryDocumentSnapshot documentSnapshot) {
        String goalId = documentSnapshot.getId();
        String name = documentSnapshot.getString("name");
        String category = documentSnapshot.getString("category");
        int duration = documentSnapshot.getLong("duration").intValue();
        int frequency = documentSnapshot.getLong("frequency").intValue();
        String typeFrequency = documentSnapshot.getString("type frequency");
        String intervalPref = documentSnapshot.getString("interval pref");

        Goal goal = new Goal(name, Categories.valueOf(category), duration, frequency, typeFrequency,intervalPref);
        goal.setId(goalId);

        //TODO: for each event id in eventIds array extract the event and add it to the goal's List<Event>
        List<String> eventIds = (List<String>) documentSnapshot.get("eventIds");
        if (eventIds != null) {
            for (String eventId : eventIds) {
                for (Event event:Event.eventsList){
                    if(Objects.equals(event.getId(), eventId)){
                        goal.addEvent(event);
                    }
                }
            }
        }

        return goal;
    }

    private void initializeViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        NavigationView navigationView=findViewById(R.id.nav_view);
        if (user==null){
            Intent intent=new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }
        else{
            View headerView=navigationView.getHeaderView(0);
            TextView tvUserEmail=headerView.findViewById(R.id.useremail);
            tvUserEmail.setText(user.getEmail());
        }

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState ==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_calendar);
        }
    }

    private void updateGoals(Runnable callback) {
        Log.d("MainActivity","s-a intrat in updateGoals");
        for (Goal goal:Goal.goalsList){
            LocalDate lastEventsDate=goal.getEvents().get(0).getDate();

            List<Event> eventsToRemoveFromGoal=new ArrayList<>();
            for (Event event:goal.getEvents()){
                if (event.getDate().isBefore(LocalDate.now())){
                    eventsToRemoveFromGoal.add(event);
                    //TODO: stergere din array ul goal ului
                }else if (event.getDate().isAfter(lastEventsDate)){
                    lastEventsDate=event.getDate();
                }
            }

            LocalDate sundayForLastScheduledEvent=lastEventsDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            LocalDate nextMondayForCurrentDate=LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

            if (nextMondayForCurrentDate.plusWeeks(1).isAfter(sundayForLastScheduledEvent)){
                //TODO: schedule new events
                LocalTime intervalStart = LocalTime.of(6, 0);
                LocalTime intervalEnd= LocalTime.of(23, 59);
                String[] intervalLabels = getResources().getStringArray(R.array.intervale_preferinta);

                if (goal.getIntervalPref().equals(intervalLabels[1])) {
                    intervalStart = LocalTime.of(6, 0); // Start of the morning
                    intervalEnd = LocalTime.of(10, 0); // End of the morning
                } else if (goal.getIntervalPref().equals(intervalLabels[2])) {
                    intervalStart = LocalTime.of(10, 0); // Start of the afternoon
                    intervalEnd = LocalTime.of(14, 0); // End of the afternoon
                } else if (goal.getIntervalPref().equals(intervalLabels[3])) {
                    intervalStart = LocalTime.of(14, 0); // Start of the evening
                    intervalEnd = LocalTime.of(18, 0); // End of the evening
                } else if (goal.getIntervalPref().equals(intervalLabels[4])) {
                    intervalStart = LocalTime.of(18, 0); // Start of the night
                    intervalEnd = LocalTime.of(23, 59); // End of the night
                }

                boolean isForUpdate=true;
                LocalDate startDateForUpdate;
                if (sundayForLastScheduledEvent.isAfter(LocalDate.now())){
                    startDateForUpdate=sundayForLastScheduledEvent;
                }else {
                    startDateForUpdate=LocalDate.now();
                    isForUpdate=false;
                }

                List<Event> eventsFound = scheduleEventsForGoal(goal,startDateForUpdate,intervalStart,intervalEnd,isForUpdate);

//                for (Event eventNew:eventsFound){
//                    Event.eventsList.add(eventNew);
//                    goal.addEvent(eventNew);
//
//                    Map<String, Object> eventToAdd = new HashMap<>();
//                    updateMapWithEventsFields(eventToAdd,eventNew);
//                    db.collection("event").add(eventToAdd).addOnSuccessListener(documentReference -> {
//                        String idDoc = documentReference.getId();
//                        eventNew.setId(idDoc);
//                        Log.d("CalendarFragment", "New document added with ID: " + idDoc);
//                        db.collection("user").document(user.getUid())
//                                .update("events", FieldValue.arrayUnion(idDoc))
//                                .addOnSuccessListener(unused -> System.out.println(eventNew))
//                                .addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding event id to the events list", e));
////                        createReminderInDatabase(eventNew, idDoc);
//                    }).addOnFailureListener(e -> Log.d("CalendarFragment", "Error adding event", e));
//                }
//                updateDatabaseForGoal(goal);

                addEventsToDatabase(eventsFound)
                        .addOnSuccessListener(documentReferences -> {
                            for (DocumentReference documentReference : documentReferences) {
                                String idDoc = documentReference.getId();
                                Event event = eventsFound.get(documentReferences.indexOf(documentReference));
                                event.setId(idDoc);
                                Log.d("CalendarFragment", "New document added with ID: " + idDoc);

                                // Update the goal with the event IDs
                                goal.addEvent(event);
                                Event.eventsList.add(event);
                            }

                            // Call the method to update the goal document in the database
                            updateGoalInDatabase(goal);
                        })
                        .addOnFailureListener(e -> {
                            Log.d("CalendarFragment", "Error adding events to database", e);
                            // Handle the failure case
                        });
            }
        }
        if (callback != null) {
            callback.run();
        }
    }

    private Task<List<DocumentReference>> addEventsToDatabase(List<Event> events) {
        List<Task<DocumentReference>> tasks = new ArrayList<>();
        for (Event event : events) {
            Map<String, Object> eventToAdd = new HashMap<>();
            updateMapWithEventsFields(eventToAdd, event);
            tasks.add(db.collection("event").add(eventToAdd));
        }
        return Tasks.whenAllSuccess(tasks);
    }

    private void updateGoalInDatabase(Goal goal) {
        Log.d("MainActivity",goal.getId());
        db.collection("goal").document(goal.getId())
                .update("eventIds", goal.getEventIds())
                .addOnSuccessListener(unused -> {
                    // Success case
                    Log.d("MainActivity", "Succes updating goal document "+goal.getId());
                })
                .addOnFailureListener(e -> {
                    Log.d("MainActivity", "Error updating goal document", e);
                    // Handle the failure case
                });
    }

    private void updateDatabaseForGoal(Goal goal) {
        String goalId = goal.getId();

        if (goalId != null) {
            DocumentReference goalRef = db.collection("goal").document(goalId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("eventIds", FieldValue.delete());

            // Update the goal document with the cleared eventIds field
            goalRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // Add the updated event IDs to the goal document
                        goalRef.update("eventIds", goal.getEventIds())
                                .addOnSuccessListener(aVoid1 -> {
                                    // Event IDs updated successfully
                                    // Handle any additional logic or UI updates here
                                })
                                .addOnFailureListener(e -> {
                                    // Error updating eventIds field
                                    Log.e("CalendarFragment", "Error updating eventIds field for goal", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Error clearing eventIds field
                        Log.e("CalendarFragment", "Error clearing eventIds field for goal", e);
                    });
        } else {
            Log.d("CalendarFragment", "Goal ID is null. Unable to update in the database.");
        }
    }

    private Event citireEvenimentBazaDeDate(QueryDocumentSnapshot document) {
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
        Event newEvent= new Event(denumire,data,oraStart,oraSfarsit,statusEv, category);
        newEvent.setId(document.getId());
        return newEvent;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();
                break;
            case R.id.nav_lista:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ListaFragment()).commit();
                break;
            case R.id.nav_setari:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new WeekViewFragment()).commit();
                break;
            case R.id.nav_statistici:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StatisticiFragment()).commit();
//                        StatisticiFragment.newInstance(nrNeinceput,nrInceput,nrFinalizat)).commit();
                break;
            case R.id.nav_pomodoro:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,PomodoroFragment.newInstance(Event.eventsList)).commit();
                break;
            case R.id.nav_eisenhower:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,EisenhowerFragment.newInstance(Event.eventsList)).commit();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNotificationChannels() {
        Log.d("App", "Creating notification channels");
        NotificationChannel channel1 = new NotificationChannel(
                CHANNEL_1_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel1.setDescription("This is Channel 1");

        NotificationChannel channel2 = new NotificationChannel(
                CHANNEL_2_ID,
                "Channel 2",
                NotificationManager.IMPORTANCE_LOW
        );
        channel2.setDescription("This is Channel 2");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel1);
        manager.createNotificationChannel(channel2);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }
}