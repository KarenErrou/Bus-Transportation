package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

/**
 * Created by HP-USER on 4/30/2018.
 */


public class StatisticsActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public String [] items={"Number of Tickets Sold","Revenue"};// aDD LATER earnings
    public ArrayAdapter<String> adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        final Spinner spin=(Spinner)findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(StatisticsActivity.this);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,  items);
        spin.setAdapter(adapter);
        Button loadGraphs = (Button) findViewById(R.id.loadGraph);

        loadGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StatisticsActivity.this, GraphsActivity.class);
                String spinValue = spin.getItemAtPosition(spin.getSelectedItemPosition()).toString();

                DatePicker fromDate = (DatePicker) findViewById(R.id.fromDate);
                int fromDay = fromDate.getDayOfMonth();
                int fromMonth = fromDate.getMonth()+1;
                int fromYear = fromDate.getYear();

                StringBuffer sbFromDate = new StringBuffer();
                sbFromDate.append(fromYear);sbFromDate.append('/');
                sbFromDate.append(fromMonth);sbFromDate.append('/');
                sbFromDate.append(fromDay);

                DatePicker toDate = (DatePicker) findViewById(R.id.toDate);
                int toDay = toDate.getDayOfMonth();
                int toMonth = toDate.getMonth()+1;
                int toYear = toDate.getYear();

                StringBuffer sbToDate = new StringBuffer();
                sbToDate.append(toYear);sbToDate.append('/');
                sbToDate.append(toMonth);sbToDate.append('/');
                sbToDate.append(toDay);

                intent.putExtra("showStat", spinValue);
                intent.putExtra("fromDate", sbFromDate.toString());
                intent.putExtra("toDate", sbToDate.toString());
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
