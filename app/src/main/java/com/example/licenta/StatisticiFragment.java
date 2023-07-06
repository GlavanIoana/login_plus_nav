package com.example.licenta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.licenta.Eisenhower.ApplicationEisenhowerFragment;
import com.example.licenta.Eisenhower.DescriptionEisenhowerFragment;
import com.example.licenta.Eisenhower.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class StatisticiFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_statistici, container, false);

        tabLayout=view.findViewById(R.id.tabLayout);
        viewPager=view.findViewById(R.id.viewPager);

//        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Zi"));
        tabLayout.addTab(tabLayout.newTab().setText("Săptămână"));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPagerAdapter.addFragment(new StatisticiZiFragment(),"Zi");
        viewPagerAdapter.addFragment(new StatisticiSaptamanaFragment(),"Săptămână");
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