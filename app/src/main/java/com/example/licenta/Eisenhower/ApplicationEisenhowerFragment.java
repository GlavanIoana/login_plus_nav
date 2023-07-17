package com.example.licenta.Eisenhower;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.licenta.Event;
import com.example.licenta.R;

import java.util.HashSet;
import java.util.Set;


public class ApplicationEisenhowerFragment extends Fragment {
    private LinearLayout llUrgImp;
    private LinearLayout llNeurgImp;
    private LinearLayout llUrgNeimp;
    private LinearLayout llNeurgNeimp;
    private LinearLayout llClearMatrix;

    private AlertDialog dialog;
    private Set<String> spnSet;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_application_eisenhower, container, false);
        llUrgImp=view.findViewById(R.id.llUrgImp);
        llNeurgImp=view.findViewById(R.id.llNeurgImp);
        llUrgNeimp=view.findViewById(R.id.llUrgNeimp);
        llNeurgNeimp=view.findViewById(R.id.llNeurgNeimp);
        llClearMatrix=view.findViewById(R.id.llClearMatrix);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences("event_preferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        spnSet=new HashSet<>();
        for (Event ev:EisenhowerFragment.listaEvenimente){
            spnSet.add(ev.getName());
        }

        populateLayoutsWithSavedData();

        llUrgImp.setOnClickListener(v -> showPopupWindow(llUrgImp));
        llNeurgImp.setOnClickListener(v -> showPopupWindow(llNeurgImp));
        llUrgNeimp.setOnClickListener(v -> showPopupWindow(llUrgNeimp));
        llNeurgNeimp.setOnClickListener(v -> showPopupWindow(llNeurgNeimp));
        llClearMatrix.setOnClickListener(v -> confirmClearMatrixDialog());
    }

    private void confirmClearMatrixDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Confirma stergerea")
                .setMessage("Esti sigur ca vrei sa stergi prioritatile setate?")
                .setPositiveButton("Sterge", (dialog, which) -> {
                    clearMatrixAndSharedPrefs();
                    dialog.dismiss();
                })
                .setNegativeButton("Anuleaza", (dialog, which) -> {
                    dialog.dismiss();
                });

        dialog = builder.create();
        dialog.show();
    }

    private void clearMatrixAndSharedPrefs() {
        for (String selectedValue : spnSet) {
            String key=selectedValue + "_layout";
            if (sharedPreferences.contains(key)) {
                editor.remove(key);
                editor.apply();
            }
        }

        llUrgNeimp.removeAllViews();
        llUrgImp.removeAllViews();
        llNeurgNeimp.removeAllViews();
        llNeurgImp.removeAllViews();
    }

    private void populateLayoutsWithSavedData() {
        for (String selectedValue : spnSet) {
            String layoutIdentifier = sharedPreferences.getString(selectedValue + "_layout", "");

            LinearLayout layout = getLayoutFromIdentifier(layoutIdentifier);
            if (layout != null) {
                addTextView(layout, selectedValue,false);
            }
        }
    }

    private LinearLayout getLayoutFromIdentifier(String layoutIdentifier) {
        if (layoutIdentifier.equals(getLayoutIdentifier(llUrgImp))) {
            return llUrgImp;
        } else if (layoutIdentifier.equals(getLayoutIdentifier(llNeurgImp))) {
            return llNeurgImp;
        } else if (layoutIdentifier.equals(getLayoutIdentifier(llUrgNeimp))) {
            return llUrgNeimp;
        } else if (layoutIdentifier.equals(getLayoutIdentifier(llNeurgNeimp))) {
            return llNeurgNeimp;
        }
        return null;
    }

    private void showPopupWindow(LinearLayout layout) {
        View popupView=LayoutInflater.from(getContext()).inflate(R.layout.popup_eisenhower,null);
        Spinner spinner=popupView.findViewById(R.id.spnPopupEisenhower);

        Log.d("Eisenhower apl", String.valueOf(spnSet.size()));
        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spnSet.toArray()));
        Button btnPopupEisenhower=popupView.findViewById(R.id.btnPopupEisenhower);
        btnPopupEisenhower.setOnClickListener(v -> {
            String selectedValue= (String) spinner.getSelectedItem();
            addTextView(layout,selectedValue,true);
            dismissPopupDialog();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }

    private void addTextView(LinearLayout layout, String selectedValue, boolean saveToSharedPrefs) {
        TextView newTextView = new TextView(getContext());
        newTextView.setText(selectedValue);
        layout.addView(newTextView);

        if (saveToSharedPrefs){
            if (sharedPreferences.contains(selectedValue + "_layout")) {
                System.out.println("Cheia este deja salvata");
                return;
            }
            editor.putString(selectedValue + "_layout", getLayoutIdentifier(layout));  // Store the layout state
            editor.apply();
        }

    }

    private String getLayoutIdentifier(LinearLayout layout) {
        if (layout == llUrgImp) {
            return "llUrgImp";
        } else if (layout == llNeurgImp) {
            return "llNeurgImp";
        } else if (layout == llUrgNeimp) {
            return "llUrgNeimp";
        } else if (layout == llNeurgNeimp) {
            return "llNeurgNeimp";
        }
        return "";
    }

    private void dismissPopupDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}