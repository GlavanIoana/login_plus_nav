package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    private NotificationManagerCompat notificationManager;
    private static final String TAG = "MainActivity";
    FirebaseAuth auth;
    FirebaseUser user;
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
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

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_calendar);
        }

        db=FirebaseFirestore.getInstance();
        Map<String, Object> userobj = new HashMap<>();
        userobj.put("email", user.getEmail());
        db.collection("user").document(user.getUid()).set(userobj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("MainActivity", "DocumentSnapshot successfully written!");
            }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error writing document", e);
                    }
                });

        db.collection("event").whereEqualTo("userID",user.getUid()).orderBy("day").orderBy("time start").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            Event.eventsList.add(citireEvenimentBazaDeDate(documentSnapshot));
                        }
                    }
                });
        notificationManager = NotificationManagerCompat.from(this);

        createNotificationChannels();
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
        Event event=new Event(denumire,data,oraStart,oraSfarsit,statusEv,category==null?Categories.ALTELE:category);
        return event;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();
                break;
            case R.id.nav_lista:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,ListaFragment.newInstance(Event.eventsList)).commit();
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