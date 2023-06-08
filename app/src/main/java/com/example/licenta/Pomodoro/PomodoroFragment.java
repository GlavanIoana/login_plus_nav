package com.example.licenta.Pomodoro;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.licenta.Eisenhower.EisenhowerFragment;
import com.example.licenta.Event;
import com.example.licenta.Pomodoro.PomodoroActivity;
import com.example.licenta.R;

import java.util.ArrayList;

public class PomodoroFragment extends Fragment {
    private View view;
    private Button btnAplicaPomodoro;

    protected static ArrayList<Event> listaEvenimente=new ArrayList<>();
    private static final String ARG_PARAM1 = "lista";

    public PomodoroFragment() {
    }

    public static PomodoroFragment newInstance(ArrayList<Event> lista) {
        PomodoroFragment fragment = new PomodoroFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, lista);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listaEvenimente = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
        Log.d("PomodoroFragment", String.valueOf(listaEvenimente.size()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_pomodoro, container, false);
        btnAplicaPomodoro=view.findViewById(R.id.btnAplicaPomodoro);
        btnAplicaPomodoro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(requireContext(), PomodoroActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}