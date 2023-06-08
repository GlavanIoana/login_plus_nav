package com.example.licenta;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ListaFragment extends Fragment {
    private View view;
    private static String TAG="ListaFragment";
    private LinearLayout layoutLista;
    private ArrayList<Event> listaEvenimente=new ArrayList<>();

    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    private static final String ARG_PARAM1 = "lista";

    public ListaFragment() {
    }

    public static ListaFragment newInstance(ArrayList<Event> lista) {
        ListaFragment fragment = new ListaFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_lista, container, false);

        layoutLista=view.findViewById(R.id.layoutLista);

        createCheckBoxes(listaEvenimente);

//        db.collection("event").whereEqualTo("userID",user.getUid()).orderBy("day").orderBy("time start").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
//                            listaEvenimente.add(citireEvenimentBazaDeDate(documentSnapshot));
//                        }
//                    }
//                });
                
                
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot snapshot=task.getResult();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, document.getId() + " => " + document.getData());
//                    }
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });
//                addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    // Count fetched successfully
//                    AggregateQuerySnapshot snapshot = task.getResult();
//                    Log.d(TAG, "Count: " + snapshot.getCount());
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, document.getId() + " => " + document.getData());
//                    }
//                    for(int i=0;i<snapshot.getCount();i++){
//                        CheckBox checkBox=new CheckBox(getContext());
//                        checkBox.setText(snapshot);
//                    }
//                } else {
//                    Log.d(TAG, "Count failed: ", task.getException());
//                }
//            }
//        });

        return view;
    }

//    private Event citireEvenimentBazaDeDate(QueryDocumentSnapshot document) {
//        String denumire=document.getString("name");
//        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        String strData=document.getString("day");
//        LocalDate data=LocalDate.parse(strData,formatter);
//        DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
//        String strOraSt=document.getString("time start");
//        LocalTime oraStart=LocalTime.parse(strOraSt,timeFormatter);
//        String strOraSf=document.getString("time final");
//        LocalTime oraSfarsit=LocalTime.parse(strOraSf,timeFormatter);
//        StatusEv statusEv=StatusEv.valueOf(document.getString("status"));
//        Event event=new Event(denumire,data,oraStart,oraSfarsit,statusEv);
//        return event;
//    }

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
            layoutLista.addView(checkBox);
        }
    }
}