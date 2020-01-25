package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeparturesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final long serialVersionUID = 1L;
    public ArrayAdapter<String> adapter;
    private ArrayList<String> listItems= new  ArrayList<String> ();
    Spinner spin;
    TimePicker timePicker;
    Boolean load= false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView=(NavigationView)findViewById(R.id.navigation_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                switch (id){
                    case R.id.nav_Trips:
                        Intent intent2 = new Intent(DeparturesActivity.this,MainTickets.class);
                        startActivityForResult(intent2,0);
                        break;
                    case R.id.nav_departures:
                        Intent intent4 = new Intent(DeparturesActivity.this,DeparturesActivity.class);
                        startActivityForResult(intent4,0);
                        break;
                    case R.id.nav_map:
                        Intent intent3 = new Intent(DeparturesActivity.this,MapsActivity.class);
                        startActivityForResult(intent3,0);
                        break;
                    case R.id.nav_info:
                        Intent intent5 = new Intent(DeparturesActivity.this,MainInfo.class);
                        startActivityForResult(intent5,0);
                        break;
                }
                return false;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Departures");
        Intent intent = getIntent();


        spin = (Spinner)findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(DeparturesActivity.this);

        OnStart();

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,  listItems);
        spin.setAdapter(adapter);

        timePicker = (TimePicker)findViewById(R.id.time);

        Button loadStation= (Button) findViewById(R.id.loadStation);
        loadStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load = true;
                OnStart();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void OnStart(){
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }
    private class BackTask extends AsyncTask<Void,Void,Void> {

        ArrayList<String> list;
        ArrayList<String> listDep;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        Statement stmt1 = null;
        ResultSet rs1 = null;
        protected void onPreExecute(){
            super.onPreExecute();
            list=new ArrayList<>();
            listDep = new ArrayList<>();
        }
        protected Void doInBackground(Void...params){

            try{

                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                String sql = "SELECT name FROM station";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String StationName = rs.getString("name");
                    System.out.println(StationName + "\n");

                    list.add(StationName);

                }
                if(load==true) {
                    String fromStation = spin.getSelectedItem().toString();
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();

                    String fromTime = hour + ":" + min;
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date fromHourTime = (Date) dateFormat.parse(fromTime);

                    Calendar calMax = Calendar.getInstance();
                    calMax.setTime(fromHourTime);
                    calMax.add(Calendar.MINUTE, 60);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fromHourTime);

                    String add = dateFormat.format(cal.getTime());
                    String MaxTime = dateFormat.format(calMax.getTime());
                    add = fromTime;
                    while (!add.equals(MaxTime)) {

                        String sql1 = "select s.tostation id" +
                                "            ,d.name name " +
                                "            ,s.fromhour fromhour" +
                                "            ,s.tohour tohour " +
                                "            ,s.busid busid " +
                                "            ,s.price price" +
                                "        from schedule s,station d " +
                                "               where s.tostation = d.station_id " +
                                "               and s.fstation in (select st.station_id from station st where st.name = '" + fromStation + "') " +
                                "               and s.fromhour = '" + add + "' ";


                        stmt1 = conn.createStatement();
                        rs1 = stmt1.executeQuery(sql1);

                        while (rs1.next()) {
                            String price = rs1.getString("price");
                            String toStation = rs1.getString("name");
                            String fromh = rs1.getString("fromhour");
                            String toh = rs1.getString("tohour");
                            String bus = rs1.getString("busid");

                            listDep.add(price);
                            listDep.add(toStation);
                            listDep.add(fromh);
                            listDep.add(toh);
                            listDep.add(bus);


                            System.out.println("-------------------------------3" + listDep.toString());
                        }
                        cal.add(Calendar.MINUTE, 5);
                        add = dateFormat.format(cal.getTime());
                    }

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){

            listItems.addAll(list);
            adapter.notifyDataSetChanged();

            if(load==true) {

                for(int i = 0; i < listDep.size(); i += 5) {
                    String first = listDep.get(i);
                    String second = null;
                    String third = null;
                    String fourth = null;
                    String fifth = null;

                    if(listDep.size() > i + 1){
                        second = listDep.get(i + 1);
                        third = listDep.get(i + 2);
                        fourth = listDep.get(i + 3);
                        fifth = listDep.get(i + 4);
                    }
                    String res = "    Price:  " + first + " - To: " + second + " - time:  (" +third+ " - " + fourth + ")  -  Bus Nbr:  " +fifth ;
                    System.out.println(res);

                    TableLayout tl = (TableLayout) findViewById(R.id.table1);

                    TableRow tr = new TableRow(DeparturesActivity.this);
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    TextView t = new TextView(DeparturesActivity.this);
                    t.setText(res);
                    t.setTextColor(Color.BLACK);
                    t.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    tr.addView(t);

                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }



            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {


        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);


    }
}
