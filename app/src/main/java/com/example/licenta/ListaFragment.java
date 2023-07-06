package com.example.licenta;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaFragment extends Fragment {
    private RecyclerView recyclerView;
    private static String TAG="ListaFragment";
    private FloatingActionButton fabChangeStatus;
    private EventAdapter eventAdapter;
    private AlertDialog dialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        fabChangeStatus = view.findViewById(R.id.fabChangeStatus);

        recyclerView = view.findViewById(R.id.rvEventList);
        eventAdapter = new EventAdapter(requireContext(), R.layout.lv_event_view, Event.eventsList, inflater,fabChangeStatus);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fabChangeStatus.setVisibility(View.GONE); // Initially hide the FloatingActionButton
        fabChangeStatus.setOnClickListener(v -> {
            List<Event> selectedEvents = eventAdapter.getSelectedEvents();

            showConfirmationDialog(selectedStatus -> {
                // Perform your desired action with the selected events
                // For example, change their status
                performDatabaseChanges(selectedEvents,selectedStatus);

                eventAdapter.clearSelection();
                eventAdapter.notifyDataSetChanged();
                fabChangeStatus.setVisibility(View.GONE);
            });
        });

        return view;
    }

    private void showConfirmationDialog(ConfirmationCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_view, null);

        // Customize the layout elements as needed
        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        Spinner spinner=dialogView.findViewById(R.id.spnOptiuniStatus);
        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, StatusEv.values()));
        Button yesButton = dialogView.findViewById(R.id.dialog_yes_button);
        Button noButton = dialogView.findViewById(R.id.dialog_no_button);

        titleTextView.setText("Schimba statusul");

        // Set the click listeners for the buttons
        yesButton.setOnClickListener(v -> {
            StatusEv selectedStatus = (StatusEv) spinner.getSelectedItem();
            // Invoke the callback when the user confirms
            callback.onConfirm(selectedStatus);
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            dialog.dismiss();

            eventAdapter.clearSelection();
            eventAdapter.notifyDataSetChanged();
            fabChangeStatus.setVisibility(View.GONE);
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }

    private void performDatabaseChanges(List<Event> selectedEvents, StatusEv selectedStatus) {
        for (Event event:selectedEvents){
            event.setStatus(selectedStatus);
            Query query = db.collection("event")
                    .whereEqualTo("userID", user.getUid())
                    .whereEqualTo("day", CalendarUtils.formattedDate(event.getDate()))
                    .whereEqualTo("time start", CalendarUtils.formattedShortTime(event.getTimeStart()));

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Update the status field in the retrieved event document
                    DocumentReference eventRef = documentSnapshot.getReference();
                    eventRef.update("status", selectedStatus);

                    // Notify the user about the successful update
//                    Toast.makeText(requireContext(), "Status updated successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Handle any potential errors during the query or update process
                Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to update status", e);
            });
        }


    }

    private void createCheckBoxes(ArrayList<Event> listaEvenimente) {
        for (Event ev:listaEvenimente){
            CheckBox checkBox=new CheckBox(getContext());
            checkBox.setText(ev.getName());
            checkBox.setTextSize(20);
            checkBox.setPadding(20,0,10,0);
            if (ev.getStatus()==StatusEv.FINALIZAT){
                checkBox.setChecked(true);
            }
            checkBox.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()){
                        checkBox.setTextColor(R.color.gray);
                    }else{
                        checkBox.setTextColor(R.color.black);
                    }
                }
            });
//            layoutLista.addView(checkBox);
        }
    }
}