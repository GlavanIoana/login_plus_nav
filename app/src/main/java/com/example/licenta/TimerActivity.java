package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.licenta.Pomodoro.PomodoroActivity;

import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView countdownTextView;
    private CountDownTimer workCountdownTimer;
    private CountDownTimer breakCountdownTimer;
    private TimerState workTimerState = TimerState.INACTIVE;
    private TimerState breakTimerState = TimerState.INACTIVE;
    private Button startButton;
    private int noOfSessions=0,maxNoOfSessions,longBreakMinutes=30,breakMinutes;
    private long breakDurationInMillis, workDurationInMillis, minutesToMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(TimerActivity.this, R.color.teal_700));
        setContentView(R.layout.activity_timer);
        progressBar = findViewById(R.id.progressBarTimer);
        countdownTextView = findViewById(R.id.tvCountdown);

        int workMinutes = getIntent().getIntExtra("work_minutes", 25);
        breakMinutes = getIntent().getIntExtra("break_minutes", 5);
        maxNoOfSessions = getIntent().getIntExtra("no_of_sessions", 4);
        Log.d("TimerActivity maxNoOfSessions", String.valueOf(maxNoOfSessions));

        minutesToMillis=60000L;
        workDurationInMillis = workMinutes * minutesToMillis;
        breakDurationInMillis = breakMinutes * minutesToMillis;
        long interval = 1000;

        workCountdownTimer = createCountdownTimer(workDurationInMillis, interval);
        breakCountdownTimer = createCountdownTimer(breakDurationInMillis, interval);

        startButton = findViewById(R.id.btnStartTimer);
        startButton.setVisibility(View.INVISIBLE);
        startButton.setOnClickListener(v -> {
            if (breakTimerState == TimerState.FINISHED) {
                workCountdownTimer.start();
                workTimerState = TimerState.ACTIVE;
            } else if (workTimerState == TimerState.FINISHED) {
                breakCountdownTimer.start();
                breakTimerState = TimerState.ACTIVE;
            }
            startButton.setVisibility(View.INVISIBLE);
        });
        workCountdownTimer.start();
    }

    private CountDownTimer createCountdownTimer(long durationInMillis, long interval) {
        return new CountDownTimer(durationInMillis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (millisUntilFinished * 100 / durationInMillis));
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                countdownTextView.setText(formattedTime);
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);
                Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                MediaPlayer alarm = MediaPlayer.create(TimerActivity.this, defaultRingtoneUri);
                alarm.start();

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    alarm.stop();
                    alarm.release();
                    startButton.setVisibility(View.VISIBLE);
                }, 2000);

                if (this == workCountdownTimer) {
                    countdownTextView.setText("Break Time!");
                    workTimerState = TimerState.FINISHED;
                    breakTimerState=TimerState.INACTIVE;
                    saveTimeSpent(TimerActivity.this,workDurationInMillis);
                    noOfSessions++;
                    if (noOfSessions==maxNoOfSessions){
                        breakDurationInMillis = longBreakMinutes * minutesToMillis;
                    }
                } else if (this == breakCountdownTimer) {
                    countdownTextView.setText("Work Time!");
                    breakTimerState = TimerState.FINISHED;
                    workTimerState=TimerState.INACTIVE;
                    if (breakDurationInMillis == longBreakMinutes * minutesToMillis){
                        breakDurationInMillis = breakMinutes * minutesToMillis;
                        noOfSessions=0;
                    }
                }
                breakCountdownTimer = createCountdownTimer(breakDurationInMillis, interval);
            }
        };
    }

    private void saveTimeSpent(Context context, long durationInMillis) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TimeSpent", MODE_PRIVATE);
        String currentDate = LocalDate.now().toString();
        long totalTimeSpent = sharedPreferences.getLong(currentDate, 0) + durationInMillis;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(currentDate, totalTimeSpent);
        editor.apply();
    }

}