package com.example.licenta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.databinding.PopupFilterEventsListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListaFragment extends Fragment {
    private RecyclerView recyclerView;
    private static String TAG="ListaFragment";
    private FloatingActionButton fabChangeStatus,fabDelete;
    private FloatingActionButton fabFilters;
    private EventAdapter eventAdapter;
    private AlertDialog dialog;
    private View view;
    private PopupFilterEventsListBinding binding;
    private List<Event> eventsToShow;
    private List<TextView> selectedFiltersCategory,selectedFiltersStatus;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lista, container, false);

        fabChangeStatus = view.findViewById(R.id.fabChangeStatus);
        fabDelete = view.findViewById(R.id.fabDelete);
        fabFilters = view.findViewById(R.id.fabFilters);

        eventsToShow =Event.eventsList;
        recyclerView = view.findViewById(R.id.rvEventList);
        eventAdapter = new EventAdapter(requireContext(), R.layout.lv_event_view, eventsToShow, inflater,fabChangeStatus,fabDelete);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fabChangeStatus.setVisibility(View.GONE);
        fabChangeStatus.setOnClickListener(v -> {
            List<Event> selectedEvents = eventAdapter.getSelectedEvents();

            showConfirmationDialog(selectedStatus -> {
                performDatabaseChanges(selectedEvents,selectedStatus);

                eventAdapter.clearSelection();
                eventAdapter.notifyDataSetChanged();
                fabChangeStatus.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
            });
        });

        fabDelete.setVisibility(View.GONE);
        fabDelete.setOnClickListener(v -> {
            List<Event> selectedEvents = eventAdapter.getSelectedEvents();

            showConfirmDeleteDialog(() -> {
                deleteEvents(selectedEvents);

                eventAdapter.clearSelection();
                eventAdapter.setEvents(eventsToShow);
                eventAdapter.notifyDataSetChanged();
                fabChangeStatus.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
            });
        });

        fabFilters.setOnClickListener(v -> createFiltersPopupWindow());
        return view;
    }

    private void showConfirmDeleteDialog(ConfirmDeleteCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirma stergerea")
                .setMessage("Esti sigur ca vrei sa stergi aceste evenimente?")
                .setPositiveButton("Sterge", (dialog, which) -> {
                    // Perform the deletion of events
                    callback.onConfirm();
                    dialog.dismiss();
                })
                .setNegativeButton("Anuleaza", (dialog, which) -> {
                    // Cancel the deletion
                    dialog.dismiss();

                    eventAdapter.clearSelection();
                    eventAdapter.setEvents(eventsToShow);
                    eventAdapter.notifyDataSetChanged();
                    fabChangeStatus.setVisibility(View.GONE);
                    fabDelete.setVisibility(View.GONE);
                });

        dialog = builder.create();
        dialog.show();

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
            fabDelete.setVisibility(View.GONE);
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }

    private void deleteEvents(List<Event> selectedEvents) {
        for (Event event:selectedEvents){
            Query query = db.collection("event")
                    .whereEqualTo("userID", user.getUid())
                    .whereEqualTo("day", CalendarUtils.formattedDate(event.getDate()))
                    .whereEqualTo("time start", CalendarUtils.formattedShortTime(event.getTimeStart()));
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    DocumentReference eventRef = documentSnapshot.getReference();
                    eventRef.delete();
                }
                Event.eventsList.remove(event);

//                eventsToShow.remove(event);
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
                Log.e("CalendarFragment deleteExistingEvent", "Failed to delete event", e);
            });

        }
    }

    private void createFiltersPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = PopupFilterEventsListBinding.inflate(inflater);
        View popupView = binding.getRoot();

        PopupWindow popupWindow = showPopupWindow(popupView);

        initializeSpinners();

        selectedFiltersCategory=new ArrayList<>();
        selectedFiltersStatus=new ArrayList<>();

        View.OnClickListener categoryClickListener = view -> {
            if (view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.filter_shape).getConstantState())) {
                Log.d("ListaFragment onclicklistener","s-a intrat pe ramura de filter_shape");
                view.setBackgroundResource(R.drawable.filter_selected_shape);
                String layoutIdString = getResources().getResourceEntryName(view.getId());
                String ivIdString=layoutIdString.replace("ll","iv");
                String tvIdString=layoutIdString.replace("ll","tv");
                int ivId = getResources().getIdentifier(ivIdString, "id", "com.example.licenta");
                int tvId = getResources().getIdentifier(tvIdString, "id", "com.example.licenta");
                ImageView iv = popupView.findViewById(ivId);
                TextView tv = popupView.findViewById(tvId);
                iv.setVisibility(View.VISIBLE);
                tv.setTextColor(getResources().getColor(R.color.teal_700));
                if (tvIdString.contains("Categor")){
                    selectedFiltersCategory.add(tv);
                }else if (tvIdString.contains("Status")){
                    selectedFiltersStatus.add(tv);
                }
            }else{
                Log.d("ListaFragment onclicklistener","s-a intrat pe ramura de filter_selected_shape");
                view.setBackgroundResource(R.drawable.filter_shape);
                String layoutIdString = getResources().getResourceEntryName(view.getId());
                String ivIdString=layoutIdString.replace("ll","iv");
                String tvIdString=layoutIdString.replace("ll","tv");
                //TODO: get the text view and the image view with these ids
                int ivId = getResources().getIdentifier(ivIdString, "id", "com.example.licenta");
                int tvId = getResources().getIdentifier(tvIdString, "id", "com.example.licenta");
                ImageView iv = popupView.findViewById(ivId);
                TextView tv = popupView.findViewById(tvId);
                iv.setVisibility(View.INVISIBLE);
                tv.setTextColor(getResources().getColor(R.color.lightgray));
                if (tvIdString.contains("Categor")){
                    selectedFiltersCategory.remove(tv);
                }else if (tvIdString.contains("Status")){
                    selectedFiltersStatus.remove(tv);
                }
            }
        };

        ConstraintLayout parentLayout = popupView.findViewById(R.id.llFilterPopupWindow);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);
            if (childView instanceof LinearLayout) {
                String layoutIdString = getResources().getResourceEntryName(childView.getId());
                if (layoutIdString.contains("Spinner")){
                    continue;
                }
                childView.setOnClickListener(categoryClickListener);
            }
        }

        Button applyFiltersButton=binding.btnApplyFilters;
        applyFiltersButton.setOnClickListener(v -> {
            applyFilters();
            popupWindow.dismiss();
        });

    }

    private void applyFilters() {
        eventsToShow =new ArrayList<>();
        String spnObiectivSelectedOption=binding.spnFilterObiectiv.getSelectedItem().toString();
        String spnDataSelectedOption=binding.spnFilterData.getSelectedItem().toString();

        Log.d("ListaFragment applyFilters()", String.valueOf(selectedFiltersCategory.size()+selectedFiltersStatus.size()));
        if (!selectedFiltersCategory.isEmpty() || !selectedFiltersStatus.isEmpty() || !spnObiectivSelectedOption.equals("-") || !spnDataSelectedOption.equals("-")){
            LocalDate currentDate = LocalDate.now();

            List<Event> eventsFromGoal = new ArrayList<>();
            List<Event> eventsFromCategory = new ArrayList<>();
            List<Event> eventsFromData = new ArrayList<>();
            List<Event> eventsFromStatus = new ArrayList<>();

            for (Event event:Event.eventsList){
                if (!spnObiectivSelectedOption.equals("-")){
                    Goal goal=Goal.getGoalByName(spnObiectivSelectedOption);
                    if (goal != null){
                        if(goal.getEvents().contains(event)){
                            eventsFromGoal.add(event);
                        }
                    }else {
                        Log.e("ListaFragment applyFilters()","Couldn't find the goal selected!");
                    }
                }

                if (!selectedFiltersCategory.isEmpty()){
                    for (TextView textView:selectedFiltersCategory){
                        if (event.getCategory().toString().equals(textView.getText().toString().toUpperCase())){
                            eventsFromCategory.add(event);
                        }
                    }
                }

                if (!selectedFiltersStatus.isEmpty()){
                    for (TextView textView:selectedFiltersStatus){
                        if (event.getStatus().toString().equals(textView.getText().toString().toUpperCase())){
                            eventsFromStatus.add(event);
                        }
                    }
                }

                if (!spnDataSelectedOption.equals("-")){
                    LocalDate eventDate = event.getDate();

                    switch (spnDataSelectedOption){
                        case "Trecut": if (eventDate.isBefore(currentDate)){
                            eventsFromData.add(event);
                        }
                        break;
                        case "Azi": if (eventDate.isEqual(currentDate)){
                            eventsFromData.add(event);
                        }
                            break;
                        case "Saptamana curenta":
                            LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                            LocalDate endOfWeek = currentDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                            if (eventDate.isEqual(currentDate) || (eventDate.isAfter(startOfWeek) && eventDate.isBefore(endOfWeek))) {
                                eventsFromData.add(event);
                            }
                            break;
                        case "Luna curenta":
                            LocalDate startOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
                            LocalDate endOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
                            if (eventDate.isEqual(currentDate) || (eventDate.isAfter(startOfMonth) && eventDate.isBefore(endOfMonth))) {
                                eventsFromData.add(event);
                            }
                            break;
                        case "Viitor":
                            if (eventDate.isAfter(currentDate)) {
                                eventsFromData.add(event);
                            }
                            break;
                        default:
                            break;

                    }

                }

                List<Event> commonEvents = new ArrayList<>();

                if (!eventsFromGoal.isEmpty()) {
                    commonEvents.addAll(eventsFromGoal);
                }

                if (!eventsFromCategory.isEmpty()) {
                    if (commonEvents.isEmpty()) {
                        commonEvents.addAll(eventsFromCategory);
                    } else {
                        commonEvents.retainAll(eventsFromCategory);
                    }
                }

                if (!eventsFromData.isEmpty()) {
                    if (commonEvents.isEmpty()) {
                        commonEvents.addAll(eventsFromData);
                    } else {
                        commonEvents.retainAll(eventsFromData);
                    }
                }

                if (!eventsFromStatus.isEmpty()) {
                    if (commonEvents.isEmpty()) {
                        commonEvents.addAll(eventsFromStatus);
                    } else {
                        commonEvents.retainAll(eventsFromStatus);
                    }
                }

                eventsToShow=commonEvents;
            }
        }

        eventAdapter.setEvents(eventsToShow);
        eventAdapter.notifyDataSetChanged();
    }

    private void initializeSpinners() {
        Spinner spinnerObiectiv = binding.spnFilterObiectiv;

        List<String> spinnerValuesObiectiv = new ArrayList<>();
        spinnerValuesObiectiv.add("-");

        for (Goal goal : Goal.goalsList) {
            spinnerValuesObiectiv.add(goal.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerValuesObiectiv);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObiectiv.setAdapter(spinnerAdapter);

        Spinner spinnerFilterData = binding.spnFilterData;
        spinnerFilterData.setSelection(0);
    }

    private PopupWindow showPopupWindow(View popupView) {
        int width=ViewGroup.LayoutParams.MATCH_PARENT;
        int height=ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable=true;
        PopupWindow popupWindow=new PopupWindow(popupView,width,height,focusable);
        ConstraintLayout layout=view.findViewById(R.id.layoutLista);
        layout.post(() -> popupWindow.showAtLocation(layout, Gravity.CENTER,0,0));
        return popupWindow;
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