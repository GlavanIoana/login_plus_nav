package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth auth;
    FirebaseUser user;
    TextView tvUserEmail;
    private DrawerLayout drawerLayout;

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
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Toast.makeText(getApplicationContext(), getString(R.string.show_option,item.getTitle()), Toast.LENGTH_SHORT).show();
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            }
//        });

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_calendar);
        }
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SetariFragment()).commit();
                break;
            case R.id.nav_statistici:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StatisticiFragment()).commit();
                break;
            case R.id.nav_study:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InvatareFragment()).commit();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }
}