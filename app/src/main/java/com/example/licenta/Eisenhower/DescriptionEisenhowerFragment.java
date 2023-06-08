package com.example.licenta.Eisenhower;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.licenta.R;

import java.util.ArrayList;
import java.util.List;

public class DescriptionEisenhowerFragment extends Fragment {
    private GridView gridView;
    private List<String> tvTitles,tvDescriptions;
    private List<Integer> images;
    private List<Integer> colors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_description_eisenhower, container, false);
        gridView=view.findViewById(R.id.gridView);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitles=new ArrayList<>();
        tvTitles.add("DO");
        tvTitles.add("DECIDE");
        tvTitles.add("DELEGATE");
        tvTitles.add("DELETE");

        tvDescriptions=new ArrayList<>();
        tvDescriptions.add("Do it now!");
        tvDescriptions.add("Schedule a time to do it");
        tvDescriptions.add("Who can do it for you?");
        tvDescriptions.add("Eliminate it");

        images=new ArrayList<>();
        images.add(R.drawable.urgent_24);
        images.add(R.drawable.decide_24);
        images.add(R.drawable.ic_baseline_person_add_24);
        images.add(R.drawable.ic_baseline_delete_24);

        colors=new ArrayList<>();
        colors.add(R.color.red);
        colors.add(R.color.bleo);
        colors.add(R.color.green);
        colors.add(R.color.yellow);


        GridAdapter gridAdapter=new GridAdapter(requireContext(),tvTitles,tvDescriptions,images,colors);
        gridView.setAdapter(gridAdapter);
    }
}