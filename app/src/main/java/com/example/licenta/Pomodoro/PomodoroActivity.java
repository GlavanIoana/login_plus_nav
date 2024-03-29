package com.example.licenta.Pomodoro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.Eisenhower.EisenhowerFragment;
import com.example.licenta.Event;
import com.example.licenta.R;
import com.example.licenta.TimerActivity;

import java.util.ArrayList;
import java.util.List;

public class PomodoroActivity extends AppCompatActivity {

    private SeekBar skbTimpLucru,skbTimpPauza;
    private TextView tvTimpPauza,tvTimpLucru,tvNumarSesiuni;
    private ImageView imgMinus,imgPlus;
    private Spinner spnPomodoro;
    private Button btnStartPomodoro;
    private List<String> spnLista=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(PomodoroActivity.this, R.color.teal_700));
        setContentView(R.layout.activity_pomodoro);

        spnPomodoro=findViewById(R.id.spnPomodoro);
        for (Event ev: PomodoroFragment.listaEvenimente){
            spnLista.add(ev.getName());
        }
        Log.d("PomodoroActivitySpnListaSize", String.valueOf(spnLista.size()));
        spnPomodoro.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spnLista));

        skbTimpLucru=findViewById(R.id.skbTimpLucru);
        tvTimpLucru=findViewById(R.id.tvTimpLucru);

        skbTimpLucru.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTimpLucru.setText("00:"+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        skbTimpPauza=findViewById(R.id.skbTimpPauza);
        tvTimpPauza=findViewById(R.id.tvTimpPauza);

        skbTimpPauza.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTimpPauza.setText("00:"+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvNumarSesiuni=findViewById(R.id.tvNumarSesiuni);
        imgMinus=findViewById(R.id.imgMinus);
        imgPlus=findViewById(R.id.imgPlus);

        imgMinus.setOnClickListener(v -> {
            if (Integer.parseInt(tvNumarSesiuni.getText().toString())>1){
                tvNumarSesiuni.setText(String.valueOf(Integer.parseInt(tvNumarSesiuni.getText().toString())-1));
            }else {
                Toast.makeText(getApplicationContext(),"Ati atins limita minima!",Toast.LENGTH_SHORT).show();
            }
        });
        imgPlus.setOnClickListener(v -> {
            if (Integer.parseInt(tvNumarSesiuni.getText().toString())<=10){
                tvNumarSesiuni.setText(String.valueOf(Integer.parseInt(tvNumarSesiuni.getText().toString())+1));
            }else {
                Toast.makeText(getApplicationContext(),"Ati atins limita maxima!",Toast.LENGTH_SHORT).show();
            }
        });

        btnStartPomodoro=findViewById(R.id.btnStartPomodoro);
        btnStartPomodoro.setOnClickListener(v -> {
            int workMinutes = skbTimpLucru.getProgress();
            int breakMinutes = skbTimpPauza.getProgress();

            Intent intent = new Intent(PomodoroActivity.this, TimerActivity.class);
            intent.putExtra("work_minutes", workMinutes);
            intent.putExtra("break_minutes", breakMinutes);
            intent.putExtra("no_of_sessions", Integer.parseInt(tvNumarSesiuni.getText().toString()));
            startActivity(intent);
        });
    }
}