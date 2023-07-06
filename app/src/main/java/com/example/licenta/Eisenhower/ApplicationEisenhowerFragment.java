package com.example.licenta.Eisenhower;

import android.app.AlertDialog;
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

import com.example.licenta.Event;
import com.example.licenta.ListaFragment;
import com.example.licenta.R;

import java.util.ArrayList;
import java.util.List;


public class ApplicationEisenhowerFragment extends Fragment {

    private LinearLayout llUrgImp,llUrgNeimp,llNeurgImp,llNeurgNeimp;
    private AlertDialog dialog;
    private List<String> spnLista=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_application_eisenhower, container, false);
        llUrgImp=view.findViewById(R.id.llUrgImp);
        llNeurgImp=view.findViewById(R.id.llNeurgImp);
        llUrgNeimp=view.findViewById(R.id.llUrgNeimp);
        llNeurgNeimp=view.findViewById(R.id.llNeurgNeimp);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for (Event ev:EisenhowerFragment.listaEvenimente){
            spnLista.add(ev.getName());
        }

        llUrgImp.setOnClickListener(v -> showPopupWindow(llUrgImp));
        llNeurgImp.setOnClickListener(v -> showPopupWindow(llNeurgImp));
        llUrgNeimp.setOnClickListener(v -> showPopupWindow(llUrgNeimp));
        llNeurgNeimp.setOnClickListener(v -> showPopupWindow(llNeurgNeimp));
    }

    private void showPopupWindow(LinearLayout layout) {
        View popupView=LayoutInflater.from(getContext()).inflate(R.layout.popup_eisenhower,null);
        Spinner spinner=popupView.findViewById(R.id.spnPopupEisenhower);

        Log.d("Eisenhower apl", String.valueOf(spnLista.size()));
        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spnLista));
        Button btnPopupEisenhower=popupView.findViewById(R.id.btnPopupEisenhower);
        btnPopupEisenhower.setOnClickListener(v -> {
            String selectedValue= (String) spinner.getSelectedItem();

            addCheckbox(layout,selectedValue);

            spnLista.remove(selectedValue);

            dismissPopupDialog();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }

    private void addCheckbox(LinearLayout layout, String selectedValue) {
        CheckBox newCheckBox=new CheckBox(getContext());
        newCheckBox.setText(selectedValue);
        layout.addView(newCheckBox);

        newCheckBox.setOnClickListener(v -> newCheckBox.setChecked(!newCheckBox.isChecked()));
    }

    private void dismissPopupDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}