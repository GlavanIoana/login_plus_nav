package com.example.licenta;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StatisticiFragment extends Fragment {
    private View view;
    private PieChart pieChart;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private static final String ARG_PARAM1 = "nrNeinceput";
    private static final String ARG_PARAM2 = "nrInceput";
    private static final String ARG_PARAM3 = "nrFinalizat";
    private int nrNeinceput,nrInceput,nrFinalizat;
    private ProgressBar progressBar;


    public static StatisticiFragment newInstance(int nrNeinceput, int nrInceput, int nrFinalizat) {
        StatisticiFragment fragment = new StatisticiFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, nrNeinceput);
        args.putInt(ARG_PARAM2, nrInceput);
        args.putInt(ARG_PARAM3, nrFinalizat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nrNeinceput = getArguments().getInt(ARG_PARAM1);
            nrInceput = getArguments().getInt(ARG_PARAM2);
            nrFinalizat = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_statistici, container, false);

        user= FirebaseAuth.getInstance().getCurrentUser();
        db=FirebaseFirestore.getInstance();

        pieChart=view.findViewById(R.id.pieChart);
        ArrayList<PieEntry> entries=new ArrayList<>();

        db.collection("event").whereEqualTo("userID", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                switch (doc.getData().get("status").toString()) {
                                    case "NEINCEPUT":
                                        nrNeinceput++;
                                        break;
                                    case "INCEPUT":
                                        nrInceput++;
                                        break;
                                    case "FINALIZAT":
                                        nrFinalizat++;
                                        break;
                                }
                            }
                            Log.d("StatisticiFragment", nrNeinceput + " documents with status "+StatusEv.NEINCEPUT+" found");
                            Log.d("StatisticiFragment", nrInceput + " documents with status "+StatusEv.INCEPUT+" found");
                            Log.d("StatisticiFragment", nrFinalizat + " documents with status "+StatusEv.FINALIZAT+" found");

                            entries.add(new PieEntry(nrNeinceput,StatusEv.NEINCEPUT.toString()));
                            entries.add(new PieEntry(nrInceput,StatusEv.INCEPUT.toString()));
                            entries.add(new PieEntry(nrFinalizat,StatusEv.FINALIZAT.toString()));
                            Log.d("StatisticiFragment",entries.toString());

                            PieDataSet pieDataSet=new PieDataSet(entries,"Stari sarcini");
                            Log.d("StatisticiFragment",pieDataSet.toString());

                            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            pieDataSet.setValueTextColor(Color.BLACK);
                            pieDataSet.setValueTextSize(16f);
                            pieChart.setData(new PieData(pieDataSet));
                            pieChart.getDescription().setEnabled(false);
                            pieChart.setCenterText("Productivitate");
                            pieChart.animate();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                                }
                            }, 2000);
                            Log.d("StatisticiFragment","Delay finish");
                        } else {
                            Log.d("StatisticiFragment", "Error getting documents: ", task.getException());
                        }
                    }});

        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        Log.d("StatisticiFragment","Return");

        return view;
    }

}