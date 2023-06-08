package com.example.licenta.Eisenhower;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.licenta.Event;
import com.example.licenta.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class EisenhowerFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    protected static ArrayList<Event> listaEvenimente=new ArrayList<>();
    private static final String ARG_PARAM1 = "lista";

    public EisenhowerFragment() {
    }

    public static EisenhowerFragment newInstance(ArrayList<Event> lista) {
        EisenhowerFragment fragment = new EisenhowerFragment();
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
        Log.d("EisenhowerFragment", String.valueOf(listaEvenimente.size()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_eisenhower, container, false);

        tabLayout=view.findViewById(R.id.tabLayout);
        viewPager=view.findViewById(R.id.viewPager);

//        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Aplicare"));
        tabLayout.addTab(tabLayout.newTab().setText("Descriere"));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPagerAdapter.addFragment(new ApplicationEisenhowerFragment(),"Aplicare");
        viewPagerAdapter.addFragment(new DescriptionEisenhowerFragment(),"Descriere");
        viewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(viewPagerAdapter.getPageTitle(position))).attach();



        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
}