package com.example.lbt.lbt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class GraphsActivity extends AppCompatActivity {

    private static final String TAG = "GraphsActivity";
    private SectionPageAdapter sectionPageAdapter;
    private ViewPager mviewPager;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();

        String spinValue = intent.getStringExtra("showStat");
        String fromDate = intent.getStringExtra("fromDate");
        String toDate = intent.getStringExtra("toDate");


        sectionPageAdapter=new SectionPageAdapter(getSupportFragmentManager());

        CategoryFragment categoryFragment = new CategoryFragment();
        TripsFragment tripsFragment = new TripsFragment();
        ZoneStationFragment zoneStationFragment = new ZoneStationFragment();
        mviewPager = (ViewPager)findViewById(R.id.viewpager);
        sectionPageAdapter.addFragment(categoryFragment ,"Categories");
        sectionPageAdapter.addFragment(tripsFragment,"Trips ");
        sectionPageAdapter.addFragment(zoneStationFragment,"Zones & Stations");
        mviewPager.setAdapter(sectionPageAdapter);

        Bundle bundle = new Bundle();
        String spinValuetofrag = spinValue;
        String fromDatetofrag = fromDate;
        String toDatetofrag = toDate;
        System.out.println("=============="+spinValuetofrag + "=============="+fromDatetofrag+"============"+toDatetofrag+"-====="+bundle);
        bundle.putString("spinValueToFrag", spinValuetofrag );
        bundle.putString("fromDateToFrag", fromDatetofrag );
        bundle.putString("toDateToFrag", toDatetofrag );

        categoryFragment.setArguments(bundle);
        tripsFragment.setArguments(bundle);
        zoneStationFragment.setArguments(bundle);
        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mviewPager);

    }

}
